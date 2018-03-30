package com.adtec.ncps.busi.ncp.acct;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class ACCTPub {
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月7日
	 * 
	 * @version 1.0 检查是否为二维码存量业务
	 * 
	 * @返回值 0-二维码业务 1-非二维码业务 -1：异常
	 */
	public static int chkQRCdBusi(String _szProductTp, String _szSignNo) throws Exception {
		try {
			/*
			 * QR030000-光学码读取（被扫，含芯片信息） QR040000-光学码读取（被扫，无卡）
			 * QR930000-光学码读取（主扫，含芯片信息） QR940000-光学码读取（主扫，无卡）
			 */
			if ("QR030000".equals(_szProductTp) || "QR040000".equals(_szProductTp) //
					|| "QR930000".equals(_szProductTp) || "QR940000".equals(_szProductTp)) {
				/*
				 * 判断协议号规则UPQR111111+签约发起机构标识码（8字节）+账户类型（2字节，默认填写00）+生成渠道类别（“3”）
				 * +（填写卡号长度（2字节）+卡号+不足34位补0
				 */
				if ("UPQR".equals(_szSignNo.substring(0, 4)) && "3".equals(_szSignNo.substring(20, 21))) {
					SysPub.appLog("INFO", "[%s][%s]是二维码业务", _szProductTp, _szSignNo);
					return 0;
				} else {
					SysPub.appLog("INFO", "[%s][%s]非二维码业务", _szProductTp, _szSignNo);
					//BusiPub.setCupMsg("PB523099", "签约协议号非二维码协议号", "2");
					//SysPub.appLog("ERROR", "PB523099-签约协议号非二维码协议号");
					return 1;
				}
			}
			SysPub.appLog("INFO", "非二维码业务");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "chkQRCdBusi 方法处理异常");
			throw e;
		}
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年9月5日
	 * 
	 * @version 1.0 检查银联限额控制前处理
	 * 
	 * @返回值
	 * 
	 * @注意：需要事先调度busiPub.cupGetAcctType方法得到卡bin
	 */
	public static int chkCupLmtBef() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			// 业务种类
			EPOper.copy(tpID, tpID, "T_NCP_BOOK[0].BUSI_TYPE", "T_BUSI_LMT[0].BUSI_TYPE");
			// 付款人账号
			EPOper.copy(tpID, tpID, "T_NCP_BOOK[0].PAY_ACCT_NO", "T_BUSI_LMT_TOT[0].ACCT_NO");

			SysPub.appLog("DEBUG", "银联限额控制前处理-赋值完成");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "chkCupLmtBef 方法处理异常");
			throw e;
		}
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年9月5日
	 * 
	 * @version 1.0 业务限额检查
	 * 
	 * @para _szBusiType 业务种类
	 * 
	 * @返回值 0-检查通过 -1-检查失败
	 * 
	 * @注意：需要事先调度busiPub.cupGetAcctType方法得到卡bin
	 */
	public static int chkBusiLmt() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			String szBusiType = (String) EPOper.get(tpID, "T_BUSI_LMT[0].BUSI_TYPE");
			String szCardType = (String) EPOper.get(tpID, "INIT[0]._CARD_TYPE");

			String szSqlStr = "SELECT * FROM t_busi_lmt "//
					+ " WHERE busi_type = ? and card_type = ? 	";
			Object[] value = { szBusiType, szCardType };
			iRet = DataBaseUtils.queryToElem(szSqlStr, "T_BUSI_LMT", value);
			if (0 == iRet) {
				EPOper.put(tpID, "INIT[0]._LMT_BUSI_FLG", "N");
				SysPub.appLog("INFO", "无业务限额控制");
				return 0;
			}
			String szStat = (String) EPOper.get(tpID, "T_BUSI_LMT[0].STAT");
			if (!"Y".equals(szStat)) {
				EPOper.put(tpID, "INIT[0]._LMT_BUSI_FLG", "N");
				SysPub.appLog("INFO", "[%s][%s]不做限额控制", szBusiType, szCardType);
				return 0;
			}
			// 交易金额
			double dTxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].TX_AMT");
			// 单笔限额
			double dLmtAmt = (Double) EPOper.get(tpID, "T_BUSI_LMT[0].LMT_AMT");
			// 日累计限额
			double dDayLmtAmt = (Double) EPOper.get(tpID, "T_BUSI_LMT[0].LMT_AMT_DAY");
			// 日累计交易限制次数
			int iLmtNum = (Integer) EPOper.get(tpID, "T_BUSI_LMT[0].LMT_NUM_DAY");

			// 1.是否超过单笔限额
			if (dTxAmt > dLmtAmt && dLmtAmt > 0) {
				SysPub.appLog("INFO", "交易金额[%s]超过单笔限额[%s]", dTxAmt, dLmtAmt);
				BusiPub.setCupMsg("PB521023", "接收方账户单笔交易金额超过接收方机构限额", "2");
				SysPub.appLog("ERROR", "PB521023-接收方账户单笔交易金额超过接收方机构限额");
				return -1;
			}

			String szAcctNo = (String) EPOper.get(tpID, "T_BUSI_LMT_TOT[0].ACCT_NO");
			szSqlStr = "SELECT * FROM t_busi_lmt_tot "//
					+ " WHERE busi_type = ? and acct_no = ? 	";
			Object[] valueTot = { szBusiType, szAcctNo };
			iRet = DataBaseUtils.queryToElem(szSqlStr, "T_BUSI_LMT_TOT", valueTot);
			if (0 == iRet) {
				szSqlStr = " INSERT INTO T_BUSI_LMT_TOT "//
						+ " (busi_type,acct_no,amt_day,num_day,amt1,num1,amt2,num2,rmrk,rmrk1,dac) "//
						+ " VALUES (?,?,0.00,0,0.00,0,0.00,0,'','','')";
				Object[] valueIns = { szBusiType, szAcctNo };
				DataBaseUtils.execute(szSqlStr, valueIns);
				SysPub.appLog("INFO", "[%s][%s]无累计限额控制,插入一条累计信息", szBusiType, szAcctNo);
				return 0;
			}
			// 日累计发生额
			double dDayAmt = (Double) EPOper.get(tpID, "T_BUSI_LMT_TOT[0].AMT_DAY");
			// 日累计交易次数
			int iDayNum = (Integer) EPOper.get(tpID, "T_BUSI_LMT_TOT[0].NUM_DAY");

			// 2.是否超过日累计限额
			if (dDayAmt + dTxAmt > dDayLmtAmt && dDayLmtAmt > 0) {
				SysPub.appLog("INFO", "累计金额[%s][%s]超过日交易限额[%s]", dDayAmt, dTxAmt, dDayLmtAmt);
				BusiPub.setCupMsg("PB521024", "接收方账户单日累计交易金额超过接收方机构限制", "2");
				SysPub.appLog("ERROR", "PB521024-接收方账户单日累计交易金额超过接收方机构限制");
				return -1;
			}

			// 3.是否超过交易限制次数
			if (iDayNum >= iLmtNum && iLmtNum > 0) {
				BusiPub.setCupMsg("PB521021", "接收方账户当日交易次数超过接收方机构限制", "2");
				SysPub.appLog("ERROR", "PB521021-接收方账户当日交易次数超过接收方机构限制");
				return -1;
			}

			SysPub.appLog("INFO", "业务限额检查完成");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "chkBusiLmt 方法处理异常");
			throw e;
		}
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年9月5日
	 * 
	 * @version 1.0 更新业务限额统计表
	 * 
	 * @返回值
	 * 
	 * @注意：需要事先调度busiPub.cupGetAcctType方法得到卡bin
	 */
	public static int uptLmtTot() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			String szFlag = (String) EPOper.get(tpID, "INIT[0]._LMT_BUSI_FLG");
			if( "N".equals(szFlag) ){
				SysPub.appLog("DEBUG", "不控制限额-不更新限额");
				return 0;
			}

			// 交易金额
			double dTxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].TX_AMT");
			String szBusiType = (String) EPOper.get(tpID, "T_BUSI_LMT[0].BUSI_TYPE");

			String szAcctNo = (String) EPOper.get(tpID, "T_BUSI_LMT_TOT[0].ACCT_NO");
			String szSqlStr = "UPDATE t_busi_lmt_tot "//
					+ " SET amt_day=amt_day+?, num_day=num_day+1 " + " WHERE busi_type = ? and acct_no = ? 	";
			Object[] value = { dTxAmt, szBusiType, szAcctNo };
			DataBaseUtils.execute(szSqlStr, value);

			SysPub.appLog("INFO", "更新业务限额完成");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "uptLmtTot 方法处理异常");
			throw e;
		}
	}

}
