package com.adtec.ncps.busi.ncp.chk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.TuxedoJoltComm;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.struct.dta.DtaInfo;

/********************************************************
 * *
 * 
 * @author chenshx * 清算处理类 * *
 *******************************************************/

public class ClearDo {

	/**
	 * @根据交易码返回交易名称
	 *
	 */
	public static String QryTxName(String _szTxCode) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szSqlStr = "SELECT *  FROM t_tx " //
				+ " WHERE tx_code	='" + _szTxCode + "' ";
		try {
			// SysPub.appLog("TRACE", "查询交易名称[%s]", szSqlStr);
			DataBaseUtils.queryToElem(szSqlStr, "T_TX", null);
			String szTxName = (String) EPOper.get(tpID, "T_TX[0].TX_NAME");
			return szTxName;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * @清算汇总处理
	 */
	public static int qryClearSum() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			// 查询待处理的清算记录
			iRet = ChkPub.QryChkSys("035001", "S", "0");
			if (iRet == 0) {
				SysPub.appLog("INFO", "无待处理的清算任务");
				return 0;
			}
			SysPub.appLog("INFO", "清算汇总处理开始");
			String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");// 对账日期
			SysPub.appLog("INFO", "%s清算开始", szClrDate);

			// 勾兑发卡方正常交易清算记录
			/*
			 * String szSqlStr = "UPDATE t_ncp_fund_sett a " // +
			 * " SET (a.sett_flag,a.open_brch) = "// +
			 * "     (SELECT 1,b.open_brch FROM t_loc_035001 b " // +
			 * "              WHERE a.snd_brch_no||a.oth_seq = b.oth_uniq  "// +
			 * "              AND a.clear_date = '" + szClrDate + "' "// +
			 * "              AND b.tx_flag='11111' ) " // +
			 * " where exists (SELECT 1,b.open_brch FROM t_loc_035001 b " // +
			 * "              WHERE rpad(a.snd_brch_no,11,' ')||a.oth_seq = b.oth_uniq  "
			 * // + "              AND a.clear_date = '" + szClrDate + "' "// +
			 * "              AND b.tx_flag='11111' )";
			 */

			String szSqlStr = " MERGE INTO t_ncp_fund_sett a "//
					+ " USING t_loc_035001 b  "//
					+ " ON ( a.clear_date = ? AND b.tx_flag='11111' "//
					+ "      AND rpad(a.snd_brch_no,11,' ')||a.oth_seq = b.oth_uniq ) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.sett_flag='1', a.open_brch=b.open_brch ";
			Object[] valueLoc = { szClrDate };
			iRet = DataBaseUtils.execute(szSqlStr, valueLoc);
			SysPub.appLog("INFO", "勾兑发卡方正常交易清算记录[%d]", iRet);
			if (iRet < 0) {
				SysPub.appLog("ERROR", "勾兑发卡方正常交易清算记录失败[%d]", iRet);
				return -1;
			}

			// 更新非账务类的开户机构，清算标志
			szSqlStr = " MERGE INTO t_ncp_fund_sett a "//
					+ " USING t_ncp_book b  "//
					+ " ON ( a.clear_date = ? AND b.clear_date=? and substr(a.tx_code,1,5)<>'SACCT' "//
					+ "      AND a.snd_brch_no=b.snd_brch_no and a.oth_seq = b.oth_seq ) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.sett_flag='1',a.open_brch=b.open_brch ";
			Object[] valueBook = { szClrDate, szClrDate };
			iRet = DataBaseUtils.execute(szSqlStr, valueBook);
			SysPub.appLog("INFO", "更新非账务类的开户机构，清算标志[%d]", iRet);
			if (iRet < 0) {
				SysPub.appLog("ERROR", "更新非账务类的开户机构，清算标志失败[%d]", iRet);
				return -1;
			}

			// 因没有做事务控制，为防止第一次失败，第二次成功，结果更新t_ncp_fund_sett记录未0，
			// 需要查询一次是否存在清算记录
			szSqlStr = "SELECT clear_date FROM t_ncp_fund_sett " //
					+ " WHERE clear_date='" + szClrDate + "' "//
					+ " and sett_flag='1' ";
			iRet = DataBaseUtils.queryToCount(szSqlStr, null);
			SysPub.appLog("INFO", "发卡方正常交易清算记录[%d]", iRet);
			if (0 == iRet) {
				SysPub.appLog("WARN", "请注意：待清算记录为0");
				// return -1;
			} else if (0 > iRet) {
				SysPub.appLog("ERROR", "查询清算记录失败");
				return -1;
			}

			// 生成本地汇总文件
			// SysPub.appLog("INFO", "创建本地无卡支付汇总文件");
			// iRet = crtLocSettSum(szClrDate);
			// if (0 == iRet) {
			// SysPub.appLog("WARN", "请注意：汇总记录为0");
			// //return -1;
			// }
			// else if(0 > iRet){
			// SysPub.appLog("ERROR", "请注意：生成本地汇总文件失败");
			// return -1;
			// }

			// 生成核心清算文件
			SysPub.appLog("INFO", "创建核心机构无卡支付汇总文件");
			iRet = crtHostBrchSum(szClrDate);
			if (0 == iRet) {
				SysPub.appLog("WARN", "请注意：清算记录为0");
				// return -1;
			} else if (0 > iRet) {
				SysPub.appLog("ERROR", "请注意：生成核心清算文件失败败");
				return -1;
			}

			// 更新对账控制表记录为
			EPOper.put(tpID, "T_CHK_SYS[0].CLEAR_STAT", "1"); // 1-生成清分文件
			SysPub.appLog("INFO", "更新CLEAR_STAT 1-生成清分文件");

			DBExecuter executer = DataBaseUtils.conn();
			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);

		} catch (Exception e) {
			SysPub.appLog("ERROR", "汇总查询失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * @创建本地无卡支付汇总文件
	 *
	 */
	public static int crtLocSettSum(String _szClrDate) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int iRet = 0;
		// 汇总格式： 交易码,笔数,应收本金,应付本金,应收业务参与价,应付业务参与价,
		// 网络服务费,品牌费,应收手续费（总）,应付手续费（总）
		/*
		 * String szSqlStr =
		 * "SELECT tx_code,count(sett_date) REC,sum(in_amt) IN_AMT, sum(out_amt) OUT_AMT, "
		 * +
		 * " sum(in_fee) IN_FEE, sum(out_fee) OUT_FEE,sum(charge_fee) CHARGE_FEE,sum(LOGO_FEE) LOGO_FEE ,"
		 * +
		 * " sum(in_fee+charge_fee+Logo_fee) TOT_IN_AMT, sum(out_fee+charge_fee+Logo_fee) TOT_OUT_AMT ,"
		 * + " FROM t_ncp_fund_sett " + " WHERE sett_date = '" + _szClrDate +
		 * "' " + " AND sett_flag = '1' AND open_brch <> 'NULL' " +
		 * " GROUP BY tx_code ORDER BY tx_code";
		 */
		String szSqlStr = "SELECT tx_code,count(sett_date) REC,sum(in_amt) IN_AMT, sum(out_amt) OUT_AMT, "
				+ " sum(in_fee) - sum(out_fee) + sum(charge_fee) + sum(Logo_fee) TOT_IN_AMT " + " FROM t_ncp_fund_sett "
				+ " WHERE sett_date = '" + _szClrDate + "' " + " AND sett_flag = '1' AND open_brch <> 'NULL' "
				+ " GROUP BY tx_code ORDER BY tx_code";

		try {
			// 放到NCPS_SETT_SUM对象中去
			int iCount = DataBaseUtils.queryToElem(szSqlStr, "NCPS_SETT_SUM", null);
			SysPub.appLog("INFO", "查询无卡支付汇总明细[%s][%d]", szSqlStr, iRet);
			if (0 > iCount) {
				SysPub.appLog("ERROR", "数据库错误");
				return -1;
			}
			String szDate = _szClrDate.substring(0, 4) + _szClrDate.substring(5, 7) + _szClrDate.substring(8, 10);
			String szFileName = SysPubDef.SUM_CLR_DIR + szDate + "/NCPS_SETT_SUM_" + szDate;
			FileReader fr = null;
			BufferedReader br = null;
			FileWriter fw = null;

			File file = new File(szFileName);
			if (!file.getParentFile().exists()) {
				SysPub.appLog("INFO", "[%s]目录不存在，需要创建", szFileName);
				if (!file.getParentFile().mkdirs()) {
					SysPub.appLog("ERROR", "创建[%s]目录失败", szFileName);
					return -1;
				}
			}

			if (!file.exists()) {
				if (!file.createNewFile()) {
					SysPub.appLog("ERROR", "创建[%s]文件失败", szFileName);
					return -1;
				}
			}

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String szFile = "";
			String szPre = "";
			String szTxCode = "";
			String szTxName = "";
			int iRec = 0;
			double dInAmt = 0.00;
			double dOutAmt = 0.00;

			// 汇总格式： 交易码,笔数,应收本金,应付本金,应收业务参与价,应付业务参与价,
			// 网络服务费,品牌费,应收手续费（总）,应付手续费（总）
			for (int iNum = 0; iNum < iCount; iNum++) {
				szPre = "NCPS_SETT_SUM[" + iNum + "].";
				szTxCode = (String) EPOper.get(tpID, szPre + "TX_CODE"); // 交易码
				szTxName = QryTxName(szTxCode);
				iRec = (Integer) EPOper.get(tpID, szPre + "REC"); // 笔数
				dInAmt = (Double) EPOper.get(tpID, szPre + "IN_AMT"); // 应收本金
				dOutAmt = (Double) EPOper.get(tpID, szPre + "OUT_AMT"); // 应付本金
				double dTxFee = 0.00;
				BigDecimal dTxInFee = new BigDecimal(0.00);
				BigDecimal dTxOutFee = new BigDecimal(0.00);
				dTxFee = (Double) EPOper.get(tpID, szPre + "TOT_IN_AMT"); // 银行支出手续费(为负)或收入手续费(为正)
				if (dTxFee > 0) {
					dTxInFee = new BigDecimal(dTxFee).setScale(2, BigDecimal.ROUND_HALF_UP);
					dTxOutFee = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					dTxInFee = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
					dTxOutFee = new BigDecimal(-dTxFee).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				/*
				 * szFile = szTxName + "|" + iRec + "|" + dInAmt + "|" + dOutAmt
				 * + "|" + dInFee + "|" + dOutFee// + "|" + dChangeFee + "|" +
				 * dBrandFee + "|" + dTotInFee + "|" + dTotOutFee + "\n" +
				 * szFile;
				 */
				/* 交易名称|交易笔数|应收本金|应付本金|应收手续费|应付手续费 */
				szFile = szTxName + "|" + iRec + "|" + dInAmt + "|" + dOutAmt + "|" + dTxInFee + "|" + dTxOutFee + "\n"
						+ szFile;
			}
			fw = new FileWriter(new File(szFileName));
			fw.write(szFile);
			fw.close();
			br.close();
			fr.close();

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "创建本地无卡支付汇总文件失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @创建核心机构无卡支付汇总文件
	 *
	 */
	public static int crtHostBrchSum(String _szClrDate) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int iRet = 0;
		// 汇总格式： 机构,交易码,笔数,交易金额,银行支出手续费(为负)或收入手续费(为正)
		String szSqlStr = "SELECT trim(open_brch) open_brch,tx_type_cup,count(sett_date) REC, "//
				+ " sum(case when in_amt <> 0 then in_amt else out_amt end) TX_AMT, "//
				+ " sum(in_fee)-sum(out_fee)+sum(charge_fee)+sum(Logo_fee) OUT_AMT"//
				+ " FROM t_ncp_fund_sett " //
				+ " WHERE sett_date = ? AND sett_flag = '1' AND open_brch <> 'NULL' " //
				+ " GROUP BY open_brch,tx_type_cup ";
		try {
			// 放到NCPS_BRCH_SUM对象中去
			Object[] value = { _szClrDate };
			int iCount = DataBaseUtils.queryToElem(szSqlStr, "NCPS_BRCH_SUM", value);
			SysPub.appLog("INFO", "查询核心机构无卡支付汇总明细[%s][%d]", szSqlStr, iRet);
			if (0 > iCount) {
				SysPub.appLog("ERROR", "数据库错误");
				return -1;
			}
			String szDate = _szClrDate.substring(0, 4) + _szClrDate.substring(5, 7) + _szClrDate.substring(8, 10);
			String szFileName = SysPubDef.SUM_CLR_DIR + szDate + "/NCPS_BRCH_FEE_" + szDate;
			EPOper.put(tpID, "T_CHK_SYS[0].FILE_NAME", szFileName); // 清分文件--更新到数据库中

			FileReader fr = null;
			BufferedReader br = null;
			FileWriter fw = null;

			File file = new File(szFileName);
			if (!file.getParentFile().exists()) {
				SysPub.appLog("INFO", "[%s]目录不存在，需要创建", szFileName);
				if (!file.getParentFile().mkdirs()) {
					SysPub.appLog("ERROR", "创建[%s]目录失败", szFileName);
					return -1;
				}

			}
			if (!file.exists()) {
				if (!file.createNewFile()) {
					SysPub.appLog("ERROR", "创建[%s]文件失败", szFileName);
					return -1;
				}
			}
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String szFile = "";
			String szPre = "";
			String szBrch = "";
			String szHostCode = "";
			int iRec = 0;
			double dTxAmt = 0.00;
			BigDecimal zero = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
			// 汇总格式： 交易码,笔数,应收本金,应付本金,应收业务参与价,应付业务参与价,
			// 网络服务费,品牌费,应收手续费（总）,应付手续费（总）
			for (int iNum = 0; iNum < iCount; iNum++) {
				szPre = "NCPS_BRCH_SUM[" + iNum + "].";
				szBrch = (String) EPOper.get(tpID, szPre + "OPEN_BRCH"); // 机构号
				szHostCode = (String) EPOper.get(tpID, szPre + "TX_TYPE_CUP"); // 交易码
				iRec = (Integer) EPOper.get(tpID, szPre + "REC"); // 笔数
				dTxAmt = (Double) EPOper.get(tpID, szPre + "TX_AMT"); // 交易金额
				BigDecimal txAmt = new BigDecimal(dTxAmt).setScale(2, BigDecimal.ROUND_HALF_UP);
				double dTxFee = 0.00;
				BigDecimal dTxInFee = new BigDecimal(0.00); // 初始化
				BigDecimal dTxOutFee = new BigDecimal(0.00);
				dTxFee = (Double) EPOper.get(tpID, szPre + "OUT_AMT"); // 银行支出手续费(为负)或收入手续费(为正)
				if (dTxFee > 0) {
					dTxInFee = new BigDecimal(dTxFee).setScale(2, BigDecimal.ROUND_HALF_UP);
					dTxOutFee = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					dTxInFee = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
					dTxOutFee = new BigDecimal(-dTxFee).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				szFile = szDate + "~" + szBrch + "~" + szHostCode + "~" + iRec + "~" + txAmt + "~" + zero + "~" + zero
						+ "~"//
						+ zero + "~" + dTxInFee + "~" + dTxOutFee + "~" + zero + "~" + zero + "~" + "\n" + szFile;
			}
			fw = new FileWriter(new File(szFileName));
			fw.write(szFile);
			fw.close();
			br.close();
			fr.close();

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "创建核心机构无卡支付汇总文件失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @调度核心清分处理
	 */
	public static int ClearDoHost() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int iRet = 0;
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			// 查询待处理的清算记录
			iRet = ChkPub.QryChkSys("035001", "S", "1");
			if (iRet == 0) {
				SysPub.appLog("INFO", "没有需要调度核心清分处理的任务");
				return 0;
			}
			SysPub.appLog("INFO", "核心清分处理");
			String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");// 对账日期
			String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
			// String szFileNme = (String) EPOper.get(tpID,
			// "T_CHK_SYS[0].FILE_NAME"); // 文件名
			SysPub.appLog("INFO", "清算日期[%s]", szClrDate);

			// 上传清分文件
			// String szDate = szClrDate.substring(0, 4) +
			// szClrDate.substring(5, 7) + szClrDate.substring(8, 10);
			// String szFileName = "NCPS_BRCH_FEE_" + szDate;
			// String szlocalPath = SysPubDef.SUM_CLR_DIR + szDate + "/";
			// TuxedoJoltComm.upLoadFile(szFileName, szlocalPath);
			//
			// BusiMsgProc.headHost("ChkOut", "S215072");
			// EPOper.put(tpID, "ChkOut[0].HOST_CLI_S215072_Req[0].Desc2",
			// "上传银联清分文件");
			// EPOper.put(tpID, "ChkOut[0].HOST_CLI_S215072_Req[0].FileName",
			// szFileName); // 文件名
			// // 调度 核心 S215072服务
			// SysPub.appLog("INFO", "调用S215072服务开始");
			// DtaTool.call("HOST_CLI", "S215072");
			// String szRetCd = (String) EPOper.get(tpID,
			// "ChkIn[0].HOST_CLI_PUB_Rsp[0].RspCode"); // 响应代码
			// SysPub.appLog("INFO", "S215072响应码[%s]", szRetCd);
			// if (!"000000".equals(szRetCd)) {
			// String szRetMsg = (String) EPOper.get(tpID,
			// "ChkIn[0].HOST_CLI_PUB_Rsp[0].RspMsg"); // 响应信息
			// SysPub.appLog("ERROR", "提交核心清算流水失败[%s][%s]", szRetCd, szRetMsg);
			// return -1;
			// }
			SysPub.appLog("INFO", "调用S215072服务成功");

			// 更新对账控制表记录为
			EPOper.put(tpID, "T_CHK_SYS[0].CLEAR_STAT", "S"); // S-清算成功
			DBExecuter executer = DataBaseUtils.conn();
			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}

			// 插入下一天对账控制信息
			String szNextDate = PubTool.calDateAdd(szClrDate, "yyyy-MM-dd", 86400);
			SysPub.appLog("INFO", "插入下一天[%s]对账控制信息", szNextDate);

			String szSql = "select * from t_chk_sys where chk_date=? ";
			SysPub.appLog("DEBUG", szSql);
			Object[] value1 = { szNextDate };
			int iTotCount = DataBaseUtils.queryToCount(szSql, value1);

			if (iTotCount == 0) {
				String szSqlStr = " INSERT INTO t_chk_sys values"//
						+ " (?,?,'0001','','','0','0','0','0','0','',0,'','')";
				Object[] value = { szNextDate, szEntrNo };
				iRet = DataBaseUtils.executenotr(executer, szSqlStr, value);
				if (iRet < 0) {
					iRet = DataBaseUtils.rollback(executer);
					SysPub.appLog("ERROR", "插入下一天对账控制信息失败[%d]", iRet);
					return -1;
				}
			}

			iRet = DataBaseUtils.commit(executer);

		} catch (Exception e) {
			SysPub.appLog("ERROR", "清分处理失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		String szOthFlag = "0";
		String szHostFlag = "3";
		if ((("0".equals(szOthFlag) || "2".equals(szOthFlag))
				&& ("1".equals(szHostFlag) || "3".equals(szHostFlag) || "9".equals(szHostFlag)))
				|| ("9".equals(szOthFlag) && ("1".equals(szHostFlag) || "3".equals(szHostFlag)))) {
			/* 第三方少'0','2', 主机多'1','3', 冲正 */
			/* 第三方少'0','2', 主机对账一致辞'9', 冲正 */
			/* 第三方对账一致'9', 主机多'1','3', 冲正 */
			SysPub.testLog("INFO", "自动冲账处理开始");
		} else if ((("1".equals(szOthFlag) || "3".equals(szOthFlag))
				&& ("0".equals(szHostFlag) || "2".equals(szHostFlag)))
				|| ("9".equals(szOthFlag) && ("0".equals(szHostFlag) || "2".equals(szHostFlag)))) {
			/* 第三方多'1','3', 主机少'0','2', 补账 */
			/* 第三方对账一致'9', 主机少'0','2', 补账 */
			SysPub.testLog("INFO", "自动补账处理开始");
		}
	}
}
