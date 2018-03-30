package com.adtec.ncps.busi.ncp.chk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.ncps.ftp.FTPToolkit;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

import it.sauronsoftware.ftp4j.FTPClient;

/********************************************************
 * *
 * 
 * @author chenshx * 无卡支付对账类 * *
 *******************************************************/

public class NcpChk {
	/**
	 * 装载本地流水
	 * 
	 * @throws Exception
	 */
	public static int GetLocJrnl() throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
		String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");// 对账日期
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].LOC_STAT");// 装载流水状态
		if (szClrDate == null || "".equals(szClrDate)) {
			return 0;
		}
		// 机器时间必须大于清算时间才可以对账
		String szLocDate = PubTool.getDate10();
		if (szLocDate.compareTo(szClrDate) <= 0) {
			SysPub.appLog("ERROR", "szClrDate[%s]szEntrNo[%s]对账时间未到 ", szClrDate, szEntrNo);
			return -1;
		}

		// 装载流水状态不等于0，退出
		if (!"0".equals(szStat)) {
			return 0;
		}
		SysPub.appLog("INFO", "[%s][%s]开始装载流水 ", szClrDate, szEntrNo);
		try {
			SysPub.appLog("INFO", "清理[%s]差错流水信息 ", szClrDate);
			String szWhere = " Where clear_date = '" + szClrDate + "'";
			iRet = BusiPub.clrTabData("t_chk_err", szWhere);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "清理[%s]差错流水信息 失败", szClrDate);
				return -1;
			}
			// 执行SHELL脚本--创建临时表
			String szShell = "creat_chk_temp.sh " + szEntrNo + " T_LOC";
			SysPub.appLog("INFO", "执行SHELL脚本[%s] ", szShell);
			iRet = SysPub.callShell(szShell);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "执行SHELL脚本失败");
				return -1;
			}
			// 连接数据库
			DBExecuter executer = DataBaseUtils.conn();
			// 把流水表的数据插入本地流水临时表中
			String szSqlStr = "INSERT INTO t_loc_" + szEntrNo //
					+ "            (plat_date,seq_no,clear_date,tx_code,open_brch,chk_act_no,oth_uniq,rel_oth_uniq,tx_amt,stat,tx_flag)" //
					+ " SELECT plat_date,seq_no,clear_date,tx_code,open_brch,chk_act_no,rpad(snd_brch_no,11,' ')||oth_seq,snd_brch_no||ori_oth_seq,tx_amt,stat, " //
					+ "        (CASE stat WHEN '0' then '00001'" //
					+ "                 WHEN '1' then '00111'" //
					+ "                 WHEN '2' then '00001'" //
					+ "                 WHEN '9' then '00111'" //
					+ "                   ELSE '00001' END )" //
					+ " FROM t_ncp_book WHERE clear_date= ? "//
					+ " AND substr(tx_code,1,5)='SACCT' ";
			Object[] value = { szClrDate };
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, value);
			if (0 > iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "插入本地临时流水表失败");
				return -1;
			}
			SysPub.appLog("INFO", "把流水表的数据插入本地流水临时表中[%d]", iRet);
			EPOper.put(tpID, "T_CHK_SYS[0].LOC_STAT", "S"); // S—装载成功
			iRet = ChkPub.UptChkSys(executer);
			SysPub.appLog("INFO", "更新对账控制表[%d]", iRet);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);
			// 提交事务
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 下载核心流水文件方法
	 * 
	 * @throws Exception
	 */
	public static int DownLoadHostFile() throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].HOST_STAT");// 主机文件状态
		// 主机文件状态不等于0，退出
		if (!"0".equals(szStat)) {
			return 0;
		}
		try {
			SysPub.appLog("INFO", "下载核心流水开始");
			iRet = ChkPub.CallHostChkFile();
			if (0 != iRet) {
				SysPub.appLog("ERROR", "下载核心流水失败[%d]", iRet);
				return -1;
			}
			SysPub.appLog("INFO", "调度核心对账文件下载成功");
			// 下载文件到本地

			File file = new File(SysPubDef.HOST_CHK_DIR);
			if (!file.exists()) {
				SysPub.appLog("INFO", "[%s]目录不存在，需要创建", SysPubDef.HOST_CHK_DIR);
				if (!file.mkdirs()) {
					SysPub.appLog("ERROR", "创建[%s]目录失败", SysPubDef.HOST_CHK_DIR);
					return -1;
				}
			}
			String szRecNum = (String) EPOper.get(tpID, "ChkIn[0].ISO_8583[0].iso_8583_053");

			// String filePath = SysDef.WORK_DIR +
			// ResPool.configMap.get("FilePath");
			String fileName = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
			if (fileName != null && !fileName.isEmpty() && !"0000-00-00".equals(fileName) && 10 == fileName.length()) {
				fileName = fileName.replace("-", "");

			}
			if ( !StringTool.isNullOrEmpty(szRecNum)) {
				String szHostPath = ResPool.configMap.get("HOST_CLI_Path");
				String szRemoteName = szHostPath + "NCPS_" + fileName;

				TrcLog.log("te.log", "szLocalName==" + szRemoteName, new Object[0]);
				
				String szHost = ResPool.configMap.get("HOST_CLI_ipAddress");

				int port = Integer.valueOf(ResPool.configMap.get("HOST_CLI_port"));

				String szUser = ResPool.configMap.get("HOST_CLI_userName");

				String szPwd = ResPool.configMap.get("HOST_CLI_userPassword");

				boolean bPass = true;
				
				TrcLog.log("ftp.log","host" + szHost + "port" + port + "szUser" + szUser + "szPwd" + szPwd + "bPass" + bPass, new Object[0]);

				FTPClient client = FTPToolkit.makeFtpConnection(szHost, port, szUser, szPwd);

				FTPToolkit.download(client, szRemoteName, SysPubDef.HOST_CHK_DIR);
				TrcLog.log("ftp.log", "host" + szRemoteName, new Object[0]);

				FTPToolkit.closeConnection(client);

			} else {
				new File(SysPubDef.HOST_CHK_DIR+"NCPS_"+fileName).createNewFile();
			}
			
			// String szFileName = (String) EPOper.get(tpID,
			// "ChkIn[0].ISO_8583[0].FileHead");
			DBExecuter executer = DataBaseUtils.conn();
			String szFileName = "NCPS_" + fileName;
			EPOper.put(tpID, "T_CHK_SYS[0].FILE_NAME", szFileName); // 1—对账文件名
			EPOper.put(tpID, "T_CHK_SYS[0].HOST_STAT", "1"); // 1—下载文件成功
			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "核心流水下载失败");
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/**
	 * 装载核心流水
	 * 
	 * @throws Exception
	 */
	public static int LoadHostJrnl() throws Exception {
		int iRet = 0;
		int iNum = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].HOST_STAT");// 核心状态
		String szFile = (String) EPOper.get(tpID, "T_CHK_SYS[0].FILE_NAME");// 核心对账文件
		String szFileName = SysPubDef.HOST_CHK_DIR + szFile;
		// 状态不等于1-下载文件成功，退出
		if (!"1".equals(szStat)) {
			return 0;
		}
		// 执行SHELL脚本--创建临时表
		String szShell = "creat_chk_temp.sh " + szEntrNo + " T_HOST ";
		SysPub.appLog("INFO", "执行SHELL脚本[%s] ", szShell);
		iRet = SysPub.callShell(szShell);
		if (0 != iRet) {
			SysPub.appLog("ERROR", "执行SHELL脚本失败");
			return -1;
		}
		// 连接数据库
		DBExecuter executer = DataBaseUtils.conn();
		// 把流水表的数据插入本地流水临时表中
		String szSqlStr = " insert into t_host_" + szEntrNo
				+ " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			SysPub.appLog("INFO", "解析核心对账文件[%s]", szFileName);
			File file = new File(szFileName);
			if (!file.exists()) {
				SysPub.appLog("ERROR", "[%s]不存在", szFileName);
				return -1;
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(szFileName), "GBK"));
			// br=new BufferedReader(new InputStreamReader(new
			// FileInputStream(fileName),"UTF-8")); )

			String szOneLine = null;
			while ((szOneLine = br.readLine()) != null) {
				// 获取每一条记录的卡账号信息
				String[] szTmp = szOneLine.split("\\|");
				String szHostDate = szTmp[1];
				if (szHostDate.length() == 8) {
					szHostDate = szHostDate.substring(0, 4) + "-" + szHostDate.substring(4, 6) + "-"
							+ szHostDate.substring(6, 8);
				}
				for (int k = 0; k < 14; k++) {
					TrcLog.log("ftp.log", "host" + szTmp[k], new Object[0]);
				}
				String szHostSeq = szTmp[3]; // 核心流水号
				String szTeller = szTmp[7]; // 币种
				// String szBrchNo = szTmp[3];
				String szPlatDate = szTmp[12]; // 平台日期
				String szPlatTm = szTmp[2]; // 核心时间
				String szSeqNo = szTmp[13]; // 平台流水
				String szFrtNo = ""; // szTmp[11]; // 交易类型
				String szTxCode = szTmp[4]; // 借贷方向
				String szClrDate = "";
				String szOldSeq = "";
				String szOthSeq = "";
				String szAcctNo1 = szTmp[0]; // 交易账号
				String szOpenBrchNo1 = "00110"; // 发起机构
				String szAcctNo2 = szTmp[10]; // 对方账号
				String szOpenBrchNo2 = "";
				String szTxAmt = szTmp[5]; // 交易金额
				String szFlag = "1";
				String szHostCd = "";

				String szOpenBrchNo;
				if (szOpenBrchNo2.length() > 0) {
					szOpenBrchNo = szOpenBrchNo2;
				} else {
					szOpenBrchNo = szOpenBrchNo1;
				}

				TrcLog.log("ftp.log", "host" + szPlatDate, new Object[0]);

				double dTxAmt = Double.valueOf(szTxAmt);

				// 插入流水表
				Object[] value = { szPlatDate, szPlatTm, szSeqNo, szEntrNo, szHostDate, szHostSeq, szHostCd, szTeller,
						szOpenBrchNo, szFrtNo, szAcctNo1, szAcctNo2, szTxCode, szOthSeq, szClrDate, szOldSeq, dTxAmt,
						szFlag };
				iRet = DataBaseUtils.executenotr(szSqlStr, value);
				if (0 >= iRet) {
					DataBaseUtils.rollback(executer);
					br.close();
					fr.close();
					SysPub.appLog("ERROR", "把流水表的数据插入本地流水临时表中失败[%d]", iRet);
					return -1;
				}
				iNum++;
			}
			br.close();
			fr.close();
			SysPub.appLog("INFO", "完成t_HOST的插入");

			SysPub.appLog("INFO", "插入主机流水临时表成功[%d]", iNum);
			EPOper.put(tpID, "T_CHK_SYS[0].HOST_STAT", "S"); // S—装载文件成功
			EPOper.put(tpID, "T_CHK_SYS[0].CHK_STAT", "0"); // 0—未处理

			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			SysPub.appLog("INFO", "完成更新对账控制表[%d]", iRet);
			iRet = DataBaseUtils.commit(executer);
			SysPub.appLog("INFO", "提交事务[%d]", iRet);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 下载银联文件
	 * 
	 * @throws Exception
	 */
	public static int DownLoadCupFile() throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CUP_STAT");// 装载流水状态
		// 装载流水状态不等于0，退出
		if (!"0".equals(szStat)) {
			return 0;
		}
		try {
			// 执行SHELL脚本--下载银联文件
			String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
			// YYYY-MM-DD清算日期转换为YYYYMMDD
			String szDate = szClrDate.substring(0, 4) + szClrDate.substring(5, 7) + szClrDate.substring(8, 10);
			String szShell = "ftp_cup_file.sh " + szDate;
			File file = new File(SysPubDef.NCP_CHK_DIR);
			if (!file.exists()) {
				SysPub.appLog("INFO", "[%s]目录不存在，需要创建", SysPubDef.NCP_CHK_DIR);
				if (!file.mkdirs()) {
					SysPub.appLog("ERROR", "创建[%s]目录失败", SysPubDef.NCP_CHK_DIR);
					return -1;
				}
			}
			SysPub.appLog("INFO", "执行SHELL脚本[%s] ", szShell);
			iRet = SysPub.callShell(szShell);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "下载银联文件失败，执行SHELL脚本失败");
				return -1;
			}
			EPOper.put(tpID, "T_CHK_SYS[0].CUP_STAT", "1"); // 1—装载成功
			// 连接数据库
			DBExecuter executer = DataBaseUtils.conn();
			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);
			SysPub.appLog("INFO", "更新对账控制cup_stat状态为1");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行SHELL脚本-下载银联文件 ftp_cup_file.sh 失败");
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/**
	 * 装载银联流水方法
	 * 
	 * @throws Exception
	 */
	public static int LoadCupJrnl() throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CUP_STAT");// 装载流水状态
		String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");// 对账日期
		// 装载流水状态不等于1，退出
		if (!"1".equals(szStat)) {
			return 0;
		}
		try {

			SysPub.appLog("INFO", "==1.清理清算日期为当天的记录");
			String szWhere = " Where clear_date = '" + szClrDate + "'";
			iRet = BusiPub.clrTabData("t_ncp_sett_tot", szWhere);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "清理t_ncp_sett_tot失败");
				return -1;
			}
			iRet = BusiPub.clrTabData("t_ncp_sett_det", szWhere);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "清理t_ncp_sett_det失败");
				return -1;
			}
			iRet = BusiPub.clrTabData("t_ncp_err_detail", szWhere);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "清理t_ncp_err_detail失败");
				return -1;
			}
			szWhere = " Where sett_date = '" + szClrDate + "'";
			iRet = BusiPub.clrTabData("t_ncp_fund_sett", szWhere);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "清理t_ncp_fund_sett失败");
				return -1;
			}

			SysPub.appLog("INFO", "==2.读取银联文件");
			// 汇总文件
			iRet = LoadCupSUM("SUM", szClrDate);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "读取SUM文件");
				return -1;
			}
			// 发卡机构一般交易明细文件
			iRet = LoadCupCOM("IS_COMTRX", szClrDate);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "解析IS_COMTRX失败");
				return -1;
			}
			// TODO 收单暂时不做
			// 发卡机构差错交易明细文件
			iRet = LoadCupERR("IS_ERRTRX", szClrDate);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "解析IS_ERRTRX失败");
				return -1;
			}
			// TODO 备付金暂时不做

			SysPub.appLog("INFO", "==3.创建临时表");
			String szShell = "creat_chk_temp.sh " + szEntrNo + " T_OTH ";
			SysPub.appLog("INFO", "执行SHELL脚本[%s] ", szShell);
			iRet = SysPub.callShell(szShell);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "执行SHELL脚本失败");
				return -1;
			}

			// 连接数据库
			DBExecuter executer = DataBaseUtils.conn();
			SysPub.appLog("INFO", "==4.转移银联流水表到对账临时表");
			String szSqlStr = "INSERT INTO t_oth_" + szEntrNo //
					+ " SELECT sett_date, tx_code, rpad(snd_brch_no,11,' ')||oth_seq, snd_brch_no||ori_oth_seq, "//
					+ "     case when out_amt>0.005 then out_amt else in_amt end "//
					+ " FROM t_ncp_fund_sett " //
					+ " WHERE sett_date= ? AND substr(tx_code,1,5)='SACCT' ";
			Object[] value = { szClrDate };
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, value);
			if (iRet < 0) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "装载银联流水方法[%d]", iRet);
				return -1;
			}

			SysPub.appLog("INFO", "完成转移银联流水表到对账临时表[%d]", iRet);
			EPOper.put(tpID, "T_CHK_SYS[0].CUP_STAT", "S"); // S-装载文件成功

			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);
			SysPub.appLog("INFO", "==5.更新对账控制cup_stat状态为S-装载文件成功");

		} catch (Exception e) {
			SysPub.appLog("ERROR", "装载银联流水方法失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 装载银联流水SUM
	 * 
	 * @throws Exception
	 */
	public static int LoadCupSUM(String _szType, String _szClrDate) throws Exception {
		int iRet = 0;
		try {
			iRet = SumXmlParse.sumXmlParseDo(SysPubDef.NCP_CHK_DIR, _szClrDate);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "解析银联SUM文件失败");
				return -1;
			}
			SysPub.appLog("INFO", "插入SUM表成功");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "装载银联SUM表失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 装载银联流水COM
	 * 
	 * @throws Exception
	 */
	public static int LoadCupCOM(String _szType, String _szClrDate) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		int iBeg = 0;
		int iEnd = 0;
		int iLen = 0;
		int iCyc = 0;
		int iRet = 0;
		String szTrxtyp = null;
		String szTrxId = null;
		String szTrxAmt = null;
		String szBizTp = null;
		String szTmp = null;
		String szSttlDate = null;
		String szOriTrxId = null;
		String szOriTrxAmt = null;
		String szSderIssrId = null;
		String szMrchntNo = null;
		String szMrchntTpId = null;
		String szChangeFee = null;
		String szLogoFee = null;
		String szRecActFee = null;
		String szPayActFee = null;
		String szTxCode = null;
		String szPyerAcctId = null;
		String szPyeeAcctId = null;
		double dTrxAmt = 0.00;
		double dOriTrxAmt = 0.00;
		double dChangeFee = 0.00;
		double dLogoFee = 0.00;
		double dRecActFee = 0.00;
		double dPayActFee = 0.00;
		double dOutAmt = 0.00;
		double dInAmt = 0.00;
		double dCustFee = 0.00;
		String szFileName = "";
		String szOpenBrch = "NULL";

		String szDate = _szClrDate.substring(0, 4) + _szClrDate.substring(5, 7) + _szClrDate.substring(8, 10);
		szFileName = SysPubDef.NCP_CHK_DIR + szDate + "/" + szDate + "_01_" + _szType;
		try {
			SysPub.appLog("INFO", "解析银联文件[%s]", szFileName);
			File file = new File(szFileName);
			if (!file.exists()) {
				SysPub.appLog("INFO", "[%s]不存在", szFileName);
				return -1;
			}
			FileInputStream fs = new FileInputStream(file);
			InputStreamReader read = new InputStreamReader(fs, "GBK");
			BufferedReader bufferedReader = new BufferedReader(read);

			String szOneLine = null;
			byte[] byteLine = new byte[1500];
			while ((szOneLine = bufferedReader.readLine()) != null) {
				if (0 == iCyc) {
					SysPub.testLog("INFO", "清算文件版本号为[%s]", szOneLine);
					iCyc++;
					continue;
				}
				byteLine = szOneLine.getBytes("GBK");
				iLen = byteLine.length;
				if ((iCyc % 1000) == 1) {
					SysPub.testLog("INFO", "处理第[%d]条明细信息", iCyc);
					SysPub.testLog("TRACE", "字符串长度[%d]字节流长度[%d]", szOneLine.length(), iLen);
				}
				iCyc++;
				if (iLen < 700) {
					SysPub.testLog("TRACE", "该记录长度[%d]小于700，过滤掉[%s]", iLen, szOneLine);
					continue;
				}

				iCyc++;
				iBeg = 0;
				iLen = 4;
				iEnd = iLen;
				szTrxtyp = arraycopyStr(byteLine, iBeg, iLen); // 交易类型
				// SysPub.appLog("TRACE", "szTrxtyp[%s]iBeg[%d]iEnd[%d]",
				// szTrxtyp, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 16;
				iEnd = iEnd + 1 + iLen;
				szTrxId = arraycopyStr(byteLine, iBeg, iLen); // 交易流水号
				// SysPub.appLog("TRACE", "szTrxId[%s]iBeg[%d]iEnd[%d]",
				// szTrxId, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szTrxAmt = arraycopyStr(byteLine, iBeg, iLen); // 交易金额
				dTrxAmt = SysPub.tranStrToD(szTrxAmt, "NUM");
				// SysPub.appLog("TRACE", "dTrxAmt[%s]iBeg[%d]iEnd[%d]",
				// dTrxAmt, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 6;
				iEnd = iEnd + 1 + iLen;
				szBizTp = arraycopyStr(byteLine, iBeg, iLen); // 业务种类
				// SysPub.appLog("TRACE", "szBizTp[%s]iBeg[%d]iEnd[%d]",
				// szBizTp, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 8;
				iEnd = iEnd + 1 + iLen;
				szTmp = arraycopyStr(byteLine, iBeg, iLen); // 清算日期
				szSttlDate = szTmp.substring(0, 4) + "-" + szTmp.substring(4, 6) + "-" + szTmp.substring(6, 8);
				iBeg = iBeg + 1 + iLen;
				iLen = 16;
				iEnd = iEnd + 1 + iLen;
				szOriTrxId = arraycopyStr(byteLine, iBeg, iLen); // 原交易流水号
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szOriTrxAmt = arraycopyStr(byteLine, iBeg, iLen); // 原支付交易金额
				dOriTrxAmt = SysPub.tranStrToD(szOriTrxAmt, "NUM");
				iBeg = iBeg + 1 + iLen;
				iLen = 40;
				iEnd = iEnd + 1 + iLen;
				// String szOrdrId = arraycopyStr(byteLine,iBeg,iLen);订单号
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				szSderIssrId = arraycopyStr(byteLine, iBeg, iLen); // 发送机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				// szPyerAcctIssrId =
				// arraycopyStr(byteLine,iBeg,iLen);付款方账户所属机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				// szPyerAcctTp = arraycopyStr(byteLine,iBeg,iLen);付款方账户类型
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				szPyerAcctId = arraycopyStr(byteLine, iBeg, iLen);// 付款方账户
				iBeg = iBeg + 1 + iLen;
				iLen = 69;
				iEnd = iEnd + 1 + iLen;
				// szChannelIssrId = arraycopyStr(byteLine,iBeg,iLen);渠道方机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 55;
				iEnd = iEnd + 1 + iLen;
				// szSgnNo = arraycopyStr(byteLine,iBeg,iLen); 签约协议号
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				// szPyeeAcctIssrId = arraycopyStr(byteLine,iBeg,iLen);
				// 收款方账户所属机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				// szPyeeAcctTp = arraycopyStr(byteLine,iBeg,iLen);收款方账户类型
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				szPyeeAcctId = arraycopyStr(byteLine, iBeg, iLen);// 收款方账户
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				// szResfdAcctIssrId = arraycopyStr(byteLine,iBeg,iLen);
				// 备付金银行机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				// String szInstgAcctId = arraycopyStr(byteLine,iBeg,iLen);
				// 备付金银行账户
				iBeg = iBeg + 1 + iLen;
				iLen = 8;
				iEnd = iEnd + 1 + iLen;
				// szProductTp = arraycopyStr(byteLine,iBeg,iLen);// 产品类型
				iBeg = iBeg + 1 + iLen;
				iLen = 120;
				iEnd = iEnd + 1 + iLen;
				// szProductAssInformation =
				// arraycopyStr(byteLine,iBeg,iLen);产品辅助信息
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szMrchntNo = arraycopyStr(byteLine, iBeg, iLen); // 商户编码
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				szMrchntTpId = arraycopyStr(byteLine, iBeg, iLen); // 商户类别
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				// szSubMrchntNo = arraycopyStr(byteLine, iBeg, iLen); 二级商户编码
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				// szSubMrchntTpId = arraycopyStr(byteLine, iBeg, iLen); 二级商户类别
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				// szTrxTrmTp = arraycopyStr(byteLine,iBeg,iLen);// 交易终端类型
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;

				szChangeFee = arraycopyStr(byteLine, iBeg, iLen); // 网络服务费
				dChangeFee = SysPub.tranStrToD(szChangeFee, "DC");
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;
				szLogoFee = arraycopyStr(byteLine, iBeg, iLen); // 品牌费
				dLogoFee = SysPub.tranStrToD(szLogoFee, "DC");
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szRecActFee = arraycopyStr(byteLine, iBeg, iLen); // 应付业务参与价
				dRecActFee = SysPub.tranStrToD(szRecActFee, "NUM");
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szPayActFee = arraycopyStr(byteLine, iBeg, iLen); // 应收业务参与价
				dPayActFee = SysPub.tranStrToD(szPayActFee, "NUM");

				// 记录只有签约协议号，没有账号时，进行处理
				if ((szPyerAcctId == null || "".equals(szPyerAcctId))
						&& (szPyeeAcctId == null || "".equals(szPyeeAcctId))) {
					// 查询签约表，取出账号
					String szSql_Str = " select * from t_ncp_book where plat_date = ? and oth_seq = ?";
					Object[] value = { szTmp, szTrxId };
					iRet = DataBaseUtils.queryToElem(szSql_Str, "T_NCP_BOOK", value);
					if (0 > iRet) {
						SysPub.appLog("ERROR", "查询流水表失败");
						bufferedReader.close();
						read.close();
						fs.close();
						return -1;
					}

					// 贷记付款、退货（只有协议支付和直接支付）时为收款方账号，其他为付款方账号
					if ("1101".equals(szTrxtyp) || "2001".equals(szTrxtyp)) {
						szPyeeAcctId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_NO");
					} else {
						szPyerAcctId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_NO");
					}
				}

				dOutAmt = 0.00;
				dInAmt = 0.00;
				szTxCode = BusiPub.transCupCode(szTrxtyp);
				if ("1001".equals(szTrxtyp) || "1002".equals(szTrxtyp) || "1003".equals(szTrxtyp)) {
					// 借记交易-为借方金额
					dOutAmt = dTrxAmt;
				} else if ("1101".equals(szTrxtyp) || "2001".equals(szTrxtyp)) {
					// 贷记交易-为贷方金额
					dInAmt = dTrxAmt;
				}
				SysPub.appLog("DEBUG", "szTrxId[%s]dOutAmt[%s]dInAmt[%s]", szTrxId, dOutAmt, dInAmt);

				String szSqlStr = " insert into t_ncp_fund_sett values "//
						+ " ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'1','0','A','1','0', ?,0.00,0.00,'','','')";
				// SysPub.appLog("TRACE", szSqlStr);
				// 插入fund流水表 注意应付参与价在前面，应收参与价在后面
				Object[] valueFund = { _szClrDate, szTxCode, szTrxtyp, szOpenBrch, szPyerAcctId, szPyeeAcctId, szTrxtyp,
						szSderIssrId, szTrxId, szBizTp, szSttlDate, szOriTrxId, dOriTrxAmt, szMrchntNo, szMrchntTpId,
						dOutAmt, dInAmt, dCustFee, dChangeFee, dLogoFee, dRecActFee, dPayActFee, _szType };
				iRet = DataBaseUtils.execute(szSqlStr, valueFund);
				if (0 >= iRet) {
					SysPub.appLog("ERROR", "把银联清算数据插入fund失败");
					bufferedReader.close();
					read.close();
					fs.close();
					return -1;
				}
			}
			bufferedReader.close();
			read.close();
			fs.close();
			SysPub.appLog("INFO", "插入FUND流水表成功[%d]", iCyc - 1);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "装载银联流水COM失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 装载银联差错流水ERR
	 * 
	 * @throws Exception
	 */
	public static int LoadCupERR(String _szType, String _szClrDate) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		int iBeg = 0;
		int iEnd = 0;
		int iLen = 0;
		int iCyc = 0;
		int iRet = 0;
		String szTrxtyp = null;
		String szTrxId = null;
		String szTrxAmt = null;
		String szBizTp = null;
		String szTmp = null;
		String szSttlDate = null;
		String szErrCode = null;
		String szErrRsn = null;
		String szOriTrxId = null;
		String szOriTrxAmt = null;
		String szSderIssrId = null;
		String szPyerAcctIssrId = null;
		String szPyerAcctTp = null;
		String szPyerAcctId = null;
		String szChannelIssrId = null;
		String szSgnNo = null;
		String szPyeeAcctIssrId = null;
		String szPyeeAcctTp = null;
		String szPyeeAcctId = null;
		String szResfdAcctIssrId = null;
		String szInstgAcctId = null;
		String szProductTp = null;
		String szProductAssInformation = null;
		String szMrchntNo = null;
		String szMrchntTpId = null;
		String szSubMrchntNo = null;
		String szSubMrchntTpId = null;
		String szTrxTrmTp = null;
		String szChangeFee = null;
		String szLogoFee = null;
		String szErrTrxFee = null;
		String szRecActFee = null;
		String szPayActFee = null;
		double dTrxAmt = 0.00;
		double dOriTrxAmt = 0.00;
		double dChangeFee = 0.00;
		double dLogoFee = 0.00;
		double dErrTrxFee = 0.00;
		double dRecActFee = 0.00;
		double dPayActFee = 0.00;
		double dOutAmt = 0.00;
		double dInAmt = 0.00;
		double dCustFee = 0.00;
		String szFileName = "";
		String szOpenBrch = "NULL";

		String szDate = _szClrDate.substring(0, 4) + _szClrDate.substring(5, 7) + _szClrDate.substring(8, 10);
		szFileName = SysPubDef.NCP_CHK_DIR + szDate + "/" + szDate + "_01_" + _szType;
		try {
			SysPub.appLog("INFO", "解析银联差错文件[%s]", szFileName);
			File file = new File(szFileName);
			if (!file.exists()) {
				SysPub.appLog("INFO", "[%s]不存在", szFileName);
				return -1;
			}
			FileInputStream fs = new FileInputStream(file);
			InputStreamReader read = new InputStreamReader(fs, "GBK");
			BufferedReader bufferedReader = new BufferedReader(read);
			String szOneLine = null;
			byte[] byteLine = new byte[1500];
			while ((szOneLine = bufferedReader.readLine()) != null) {
				if (0 == iCyc) {
					SysPub.testLog("INFO", "清算文件版本号为[%s]", szOneLine);
					iCyc++;
					continue;
				}
				byteLine = szOneLine.getBytes("GBK");
				iLen = byteLine.length;
				if ((iCyc % 10) == 1) { // TODO TEST 上线后修改为1000
					SysPub.testLog("INFO", "处理第[%d]条明细信息", iCyc);
					SysPub.testLog("TRACE", "字符串长度[%d]字节流长度[%d]", szOneLine.length(), iLen);
				}
				iCyc++;
				if (iLen < 700) {
					SysPub.testLog("TRACE", "该记录长度[%d]小于700，过滤掉[%s]", iLen, szOneLine);
					continue;
				}
				iCyc++;
				// 获取每一条记录信息
				iBeg = 0;
				iLen = 4;
				iEnd = iLen;
				szTrxtyp = arraycopyStr(byteLine, iBeg, iLen); // 交易类型
				iBeg = iBeg + 1 + iLen;
				iLen = 16;
				iEnd = iEnd + 1 + iLen;
				szTrxId = arraycopyStr(byteLine, iBeg, iLen); // 交易流水号
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szTrxAmt = arraycopyStr(byteLine, iBeg, iLen); // 交易金额
				dTrxAmt = SysPub.tranStrToD(szTrxAmt, "NUM");
				iBeg = iBeg + 1 + iLen;
				iLen = 6;
				iEnd = iEnd + 1 + iLen;
				szBizTp = arraycopyStr(byteLine, iBeg, iLen); // 业务种类
				iBeg = iBeg + 1 + iLen;
				iLen = 8;
				iEnd = iEnd + 1 + iLen;
				szTmp = arraycopyStr(byteLine, iBeg, iLen); // 清算日期
				szSttlDate = szTmp.substring(0, 4) + "-" + szTmp.substring(4, 6) + "-" + szTmp.substring(6, 8);
				iBeg = iBeg + 1 + iLen;
				iLen = 3;
				iEnd = iEnd + 1 + iLen;
				szErrCode = arraycopyStr(byteLine, iBeg, iLen); // 差错交易标识
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				szErrRsn = arraycopyStr(byteLine, iBeg, iLen); // 差错原因
				iBeg = iBeg + 1 + iLen;
				iLen = 16;
				iEnd = iEnd + 1 + iLen;
				szOriTrxId = arraycopyStr(byteLine, iBeg, iLen); // 原交易流水号
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szOriTrxAmt = arraycopyStr(byteLine, iBeg, iLen); // 原支付交易金额
				dOriTrxAmt = SysPub.tranStrToD(szOriTrxAmt, "NUM");
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				szSderIssrId = arraycopyStr(byteLine, iBeg, iLen); // 发送机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				szPyerAcctIssrId = arraycopyStr(byteLine, iBeg, iLen); // 付款方账户所属机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				szPyerAcctTp = arraycopyStr(byteLine, iBeg, iLen); // 付款方账户类型
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				szPyerAcctId = arraycopyStr(byteLine, iBeg, iLen); // 付款方账户
				iBeg = iBeg + 1 + iLen;
				iLen = 69;
				iEnd = iEnd + 1 + iLen;
				szChannelIssrId = arraycopyStr(byteLine, iBeg, iLen); // 渠道方机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 55;
				iEnd = iEnd + 1 + iLen;
				szSgnNo = arraycopyStr(byteLine, iBeg, iLen); // 签约协议号
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				szPyeeAcctIssrId = arraycopyStr(byteLine, iBeg, iLen); // 收款方账户所属机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				szPyeeAcctTp = arraycopyStr(byteLine, iBeg, iLen); // 收款方账户类型
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				szPyeeAcctId = arraycopyStr(byteLine, iBeg, iLen); // 收款方账户
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				szResfdAcctIssrId = arraycopyStr(byteLine, iBeg, iLen); // 备付金银行机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				szInstgAcctId = arraycopyStr(byteLine, iBeg, iLen); // 备付金银行账户
				iBeg = iBeg + 1 + iLen;
				iLen = 8;
				iEnd = iEnd + 1 + iLen;
				szProductTp = arraycopyStr(byteLine, iBeg, iLen); // 产品类型
				iBeg = iBeg + 1 + iLen;
				iLen = 120;
				iEnd = iEnd + 1 + iLen;
				szProductAssInformation = arraycopyStr(byteLine, iBeg, iLen); // 产品辅助信息
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szMrchntNo = arraycopyStr(byteLine, iBeg, iLen); // 商户编码
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				szMrchntTpId = arraycopyStr(byteLine, iBeg, iLen); // 商户类别
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szSubMrchntNo = arraycopyStr(byteLine, iBeg, iLen); // 二级商户编码
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				szSubMrchntTpId = arraycopyStr(byteLine, iBeg, iLen); // 二级商户类别
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				szTrxTrmTp = arraycopyStr(byteLine, iBeg, iLen); // 交易终端类型
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;
				szChangeFee = arraycopyStr(byteLine, iBeg, iLen); // 网络服务费
				dChangeFee = SysPub.tranStrToD(szChangeFee, "DC");
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;
				szLogoFee = arraycopyStr(byteLine, iBeg, iLen); // 品牌费
				dLogoFee = SysPub.tranStrToD(szLogoFee, "DC");
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;
				szErrTrxFee = arraycopyStr(byteLine, iBeg, iLen); // 差错处理费
				dErrTrxFee = SysPub.tranStrToD(szErrTrxFee, "DC");
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szRecActFee = arraycopyStr(byteLine, iBeg, iLen); // 应收业务参与价
				dRecActFee = SysPub.tranStrToD(szRecActFee, "NUM");
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				szPayActFee = arraycopyStr(byteLine, iBeg, iLen); // 应付业务参与价
				dPayActFee = SysPub.tranStrToD(szPayActFee, "NUM");

				// 记录只有签约协议号，没有账号时，进行处理
				if ((szPyerAcctId == null || "".equals(szPyerAcctId))
						&& (szPyeeAcctId == null || "".equals(szPyeeAcctId))) {
					// 查询签约表，取出账号
					String szSql_Str = " select * from t_ncp_book where plat_date = ? and oth_seq = ?";
					Object[] value = { szTmp, szTrxId };
					iRet = DataBaseUtils.queryToElem(szSql_Str, "T_NCP_BOOK", value);
					if (0 > iRet) {
						SysPub.appLog("ERROR", "查询流水表失败");
						bufferedReader.close();
						read.close();
						fs.close();
						return -1;
					}

					// 贷记付款、退货（只有协议支付和直接支付）时为收款方账号，其他为付款方账号
					if ("1101".equals(szTrxtyp) || "2001".equals(szTrxtyp)) {
						szPyeeAcctId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_NO");
					} else {
						szPyerAcctId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_NO");
					}
				}

				String szTxCode = BusiPub.transCupCode(szTrxtyp);
				dOutAmt = 0.00;
				dInAmt = 0.00;
				// 目前支持发卡差错处理
				if ("IS_ERRTRX".equals(_szType)) {
					// 收款
					if ("E30".equals(szErrCode)// 付费
							|| "E29".equals(szErrCode)// 受理调单回复
							|| "E23".equals(szErrCode) || "E25".equals(szErrCode)// 发卡退单
							|| "E32".equals(szErrCode)// 贷记调整
							|| "E74".equals(szErrCode) || "E84".equals(szErrCode)// 手工退货
							|| "E81".equals(szErrCode)// 发卡请款
							|| "E73".equals(szErrCode)) {// 受理例外长款
						// 贷记交易-为贷记金额
						dInAmt = dTrxAmt;
					} else if ("E20".equals(szErrCode)// 收费
							|| "E05".equals(szErrCode)// 发卡调单回复
							|| "E82".equals(szErrCode)// 受理退单
							|| "E80".equals(szErrCode)// 发卡贷调
							|| "E31".equals(szErrCode)// 发卡例外长款
							|| "E22".equals(szErrCode) || "E24".equals(szErrCode)) {// 请款
						// 借记交易-为借记金额
						dOutAmt = dTrxAmt;
					}
				}

				// 插入fund流水表
				String szSqlStr = " insert into t_ncp_err_detail values ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'N','00','',0,'','','NULL','','','','',0.00,0.00,'','','')";
				Object[] value = { _szClrDate, _szType, szTxCode, szTrxtyp, szTrxId, dTrxAmt, szBizTp, szSttlDate,
						szErrCode, szErrRsn, szOriTrxId, dOriTrxAmt, szSderIssrId, szPyerAcctIssrId, szPyerAcctTp,
						szPyerAcctId, szChannelIssrId, szSgnNo, szPyeeAcctIssrId, szPyeeAcctTp, szPyeeAcctId,
						szResfdAcctIssrId, szInstgAcctId, szProductTp, szProductAssInformation, szMrchntNo,
						szMrchntTpId, szSubMrchntNo, szSubMrchntTpId, szTrxTrmTp, dOutAmt, dInAmt, dChangeFee, dLogoFee,
						dErrTrxFee, dPayActFee, dRecActFee };
				iRet = DataBaseUtils.execute(szSqlStr, value);
				if (0 >= iRet) {
					bufferedReader.close();
					read.close();
					fs.close();
					SysPub.appLog("ERROR", "把银联清算数据插入t_ncp_err_detail失败");
					return -1;
				}

				// 差错插入资金清算表
				/*
				 * 广西银联差错不用插入t_ncp_fund_sett szSqlStr =
				 * " insert into t_ncp_fund_sett values "// +
				 * " ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'1','1','A','0','0', ?,0.00,0.00,'','','')"
				 * ; // 插入fund流水表 Object[] valueFund = { _szClrDate, szTxCode,
				 * szTrxtyp, szOpenBrch,szPyerAcctId,szPyeeAcctId, szTrxtyp,
				 * szSderIssrId, szTrxId, szBizTp, szSttlDate, szOriTrxId,
				 * dOriTrxAmt, szMrchntNo, szMrchntTpId, dOutAmt, dInAmt,
				 * dCustFee, dChangeFee, dLogoFee, dPayActFee, dRecActFee,
				 * _szType }; iRet = DataBaseUtils.execute(szSqlStr, valueFund);
				 * if (0 >= iRet) { SysPub.appLog("ERROR",
				 * "把银联差错清算数据插入t_ncp_fund_sett失败"); bufferedReader.close();
				 * read.close(); fs.close(); return -1; }
				 */
			}
			bufferedReader.close();
			read.close();
			fs.close();
			SysPub.appLog("INFO", "插入ERR流水表成功[%d]", iCyc - 1);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "装载银联流水失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 装载本地流水
	 * 
	 * @throws Exception
	 */
	public static String arraycopyStr(byte[] src, int srcPos, int length) throws Exception {
		byte[] byteDest = new byte[length + 1];
		System.arraycopy(src, srcPos, byteDest, 0, length);
		// int iLenTot = src.length;
		// int iLen = byteDest.length;
		String sObj = new String(byteDest, "GBK").trim();

		// SysPub.testLog("INFO",
		// "iLenTot=[%d]srcPos=[%d]length=[%d]sObj=[%s]iLen=[%d]",iLenTot,
		// srcPos, length, sObj,iLen);

		return sObj;
	}

	public static int testLoadCupCOM(String _szType, String _szClrDate) throws Exception {
		int iBeg = 0;
		int iEnd = 0;
		int iLen = 0;
		int iCyc = 0;
		String szTmp = null;
		double dTrxAmt = 0.00;
		double dOriTrxAmt = 0.00;
		double dChangeFee = 0.00;
		double dLogoFee = 0.00;
		double dRecActFee = 0.00;
		double dPayActFee = 0.00;
		double dOutAmt = 0.00;
		double dInAmt = 0.00;
		String szFileName = "";

		szFileName = "E:\\20170925_01_IS_COMTRX.gbk";
		try {
			SysPub.testLog("INFO", "解析银联文件[%s]", szFileName);
			File file = new File(szFileName);
			if (!file.exists()) {
				SysPub.testLog("INFO", "[%s]不存在", szFileName);
				return -1;
			}

			FileInputStream fs = new FileInputStream(file);
			InputStreamReader read = new InputStreamReader(fs, "GBK");
			BufferedReader bufferedReader = new BufferedReader(read);
			String szOneLine = null;
			byte[] byteLine = new byte[1500];
			while ((szOneLine = bufferedReader.readLine()) != null) {
				if (0 == iCyc) {
					SysPub.testLog("INFO", "清算文件版本号为[%s]", szOneLine);
					iCyc++;
					continue;
				}
				byteLine = szOneLine.getBytes("GBK");
				iLen = byteLine.length;
				if ((iCyc % 10) == 1) { // TODO TEST 上线后修改为1000
					SysPub.testLog("INFO", "处理第[%d]条明细信息", iCyc);
					SysPub.testLog("TRACE", "字符串长度[%d]字节流长度[%d]", szOneLine.length(), iLen);
				}
				iCyc++;
				if (iLen < 700) {
					SysPub.testLog("TRACE", "该记录长度[%d]小于700，过滤掉[%s]", iLen, szOneLine);
					continue;
				}
				iBeg = 0;
				iLen = 4;
				iEnd = iLen;
				String szTrxtyp = arraycopyStr(byteLine, iBeg, iLen); // 交易类型
				// SysPub.appLog("TRACE", "szTrxtyp[%s]iBeg[%d]iEnd[%d]",
				// szTrxtyp, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 16;
				iEnd = iEnd + 1 + iLen;
				String szTrxId = arraycopyStr(byteLine, iBeg, iLen); // 交易流水号
				// SysPub.appLog("TRACE", "szTrxId[%s]iBeg[%d]iEnd[%d]",
				// szTrxId, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				String szTrxAmt = arraycopyStr(byteLine, iBeg, iLen); // 交易金额
				dTrxAmt = SysPub.tranStrToD(szTrxAmt, "NUM");
				iBeg = iBeg + 1 + iLen;
				iLen = 6;
				iEnd = iEnd + 1 + iLen;
				String szBizTp = arraycopyStr(byteLine, iBeg, iLen); // 业务种类
				// SysPub.appLog("TRACE", "szBizTp[%s]iBeg[%d]iEnd[%d]",
				// szBizTp, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 8;
				iEnd = iEnd + 1 + iLen;
				szTmp = arraycopyStr(byteLine, iBeg, iLen); // 清算日期
				String szSttlDate = szTmp.substring(0, 4) + "-" + szTmp.substring(4, 6) + "-" + szTmp.substring(6, 8);
				iBeg = iBeg + 1 + iLen;
				iLen = 16;
				iEnd = iEnd + 1 + iLen;
				String szOriTrxId = arraycopyStr(byteLine, iBeg, iLen); // 原交易流水号
				SysPub.testLog("TRACE", "szOriTrxId[%s]iBeg[%d]iEnd[%d]", szOriTrxId, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				String szOriTrxAmt = arraycopyStr(byteLine, iBeg, iLen); // 原支付交易金额
				dOriTrxAmt = SysPub.tranStrToD(szOriTrxAmt, "NUM");
				SysPub.testLog("TRACE", "dOriTrxAmt[%s]iBeg[%d]iEnd[%d]", dOriTrxAmt, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 40;
				iEnd = iEnd + 1 + iLen;
				String szOrdrId = arraycopyStr(byteLine, iBeg, iLen);// 订单号
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				String szSderIssrId = arraycopyStr(byteLine, iBeg, iLen); // 发送机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				String szPyerAcctIssrId = arraycopyStr(byteLine, iBeg, iLen); // 付款方账户所属机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				String szPyerAcctTp = arraycopyStr(byteLine, iBeg, iLen);// 付款方账户类型
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				String szPyerAcctId = arraycopyStr(byteLine, iBeg, iLen);// 付款方账户
				SysPub.testLog("TRACE", "szPyerAcctId[%s]iBeg[%d]iEnd[%d]", szPyerAcctId, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 69;
				iEnd = iEnd + 1 + iLen;
				String szChannelIssrId = arraycopyStr(byteLine, iBeg, iLen); // 渠道方机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 55;
				iEnd = iEnd + 1 + iLen;
				String szSgnNo = arraycopyStr(byteLine, iBeg, iLen); //
				// 签约协议号
				SysPub.testLog("TRACE", "szSgnNo[%s]iBeg[%d]iEnd[%d]", szSgnNo, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				String szPyeeAcctIssrId = szOneLine.substring(iBeg, iEnd).trim(); // 收款方账户所属机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				String szPyeeAcctTp = arraycopyStr(byteLine, iBeg, iLen);// 收款方账户类型
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				String szPyeeAcctId = arraycopyStr(byteLine, iBeg, iLen);// 收款方账户
				iBeg = iBeg + 1 + iLen;
				iLen = 11;
				iEnd = iEnd + 1 + iLen;
				String szResfdAcctIssrId = arraycopyStr(byteLine, iBeg, iLen); // 备付金银行机构标识
				iBeg = iBeg + 1 + iLen;
				iLen = 34;
				iEnd = iEnd + 1 + iLen;
				String szInstgAcctId = arraycopyStr(byteLine, iBeg, iLen); // 备付金银行账户
				SysPub.testLog("TRACE", "szInstgAcctId[%s]iBeg[%d]iEnd[%d]", szInstgAcctId, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 8;
				iEnd = iEnd + 1 + iLen;
				String szProductTp = arraycopyStr(byteLine, iBeg, iLen);// 产品类型
				iBeg = iBeg + 1 + iLen;
				iLen = 120;
				iEnd = iEnd + 1 + iLen;
				String szProductAssInformation = arraycopyStr(byteLine, iBeg, iLen); // 产品辅助信息
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				String szMrchntNo = arraycopyStr(byteLine, iBeg, iLen); // 商户编码
				SysPub.testLog("TRACE", "szMrchntNo[%s]iBeg[%d]iEnd[%d]", szMrchntNo, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				String szMrchntTpId = arraycopyStr(byteLine, iBeg, iLen); // 商户类别
				SysPub.testLog("TRACE", "szMrchntTpId[%s]iBeg[%d]iEnd[%d]", szMrchntTpId, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				String szSubMrchntNo = arraycopyStr(byteLine, iBeg, iLen); // 二级商户编码
				SysPub.testLog("TRACE", "szSubMrchntNo[%s]iBeg[%d]iEnd[%d]", szSubMrchntNo, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 4;
				iEnd = iEnd + 1 + iLen;
				String szSubMrchntTpId = arraycopyStr(byteLine, iBeg, iLen); // 二级商户类别
				iBeg = iBeg + 1 + iLen;
				iLen = 2;
				iEnd = iEnd + 1 + iLen;
				String szTrxTrmTp = arraycopyStr(byteLine, iBeg, iLen);// 交易终端类型
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;
				String szChangeFee = arraycopyStr(byteLine, iBeg, iLen); // 网络服务费
				dChangeFee = SysPub.tranStrToD(szChangeFee, "DC");
				SysPub.testLog("TRACE", "dChangeFee[%s]iBeg[%d]iEnd[%d]", dChangeFee, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 12;
				iEnd = iEnd + 1 + iLen;
				String szLogoFee = arraycopyStr(byteLine, iBeg, iLen); // 品牌费
				dLogoFee = SysPub.tranStrToD(szLogoFee, "DC");
				SysPub.testLog("TRACE", "dLogoFee[%s]iBeg[%d]iEnd[%d]", dLogoFee, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				String szRecActFee = arraycopyStr(byteLine, iBeg, iLen); // 应付业务参与价
				dRecActFee = SysPub.tranStrToD(szRecActFee, "NUM");
				SysPub.testLog("TRACE", "dRecActFee[%s]iBeg[%d]iEnd[%d]", dRecActFee, iBeg, iEnd);
				iBeg = iBeg + 1 + iLen;
				iLen = 15;
				iEnd = iEnd + 1 + iLen;
				String szPayActFee = arraycopyStr(byteLine, iBeg, iLen); // 应收业务参与价
				dPayActFee = SysPub.tranStrToD(szPayActFee, "NUM");
				SysPub.testLog("TRACE", "dPayActFee[%s]iBeg[%d]iEnd[%d]", dPayActFee, iBeg, iEnd);

				String szTxCode = BusiPub.transCupCode(szTrxtyp);
				if ("1001".equals(szTrxtyp) || "1002".equals(szTrxtyp) || "1003".equals(szTrxtyp)) {
					// 借记交易-为借方金额
					dOutAmt = dTrxAmt;
				} else if ("1101".equals(szTrxtyp) || "2001".equals(szTrxtyp)) {
					// 贷记交易-为贷方金额
					dInAmt = dTrxAmt;
				} else {
					SysPub.testLog("TRACE", "szTxCode[%s]szTrxtyp=[%s]dTrxAmt[%s]", szTxCode, szTrxtyp, dTrxAmt);
				}
				SysPub.testLog("TRACE", "dInAmt[%s]dOutAmt[%s]", dInAmt, dOutAmt);

				// String szSqlStr = " insert into t_ncp_fund_sett values "//
				// + " (
				// ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'1','0','A','1','0',
				// ?,0.00,0.00,'','','')";
				// SysPub.appLog("TRACE", szSqlStr);
				// 插入fund流水表
			}
			bufferedReader.close();
			read.close();
			fs.close();
			SysPub.testLog("INFO", "插入FUND流水表成功[%d]", iCyc - 1);
		} catch (Exception e) {
			SysPub.testLog("ERROR", "装载银联流水COM失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String szLocDate = "2017-09-26";
		String szClrDate = "2017-09-26";
		if (szLocDate.compareTo(szClrDate) >= 0) {
			SysPub.testLog("INFO", "szLocDate>=szClrDate");
		} else {
			SysPub.testLog("INFO", "szLocDate<szClrDate");
		}

		// testLoadCupCOM("IS_COMTRX", "2017-09-25");
		return;
	}

}
