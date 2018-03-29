package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

/********************************************************
 * *
 * 
 * @author chenshx * 交易终态通知 * *
 *******************************************************/

public class SQRY00023002 {
	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年8月20日
	 * 
	 * @version 1.0
	 */
	public static int chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 返回报文赋值处理
			//BusiMsgProc.putCupPubMsg(tpID);
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizInf[0].OriTrxId", "T_NCP_BOOK[0].ORI_OTH_SEQ" );
			

		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年8月20日
	 * 
	 * @version 1.0
	 */
	public static int BusiDeal() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			String szOthSeq = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizInf[0].OriTrxId");
			String szIssrId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderIssrId");
			SysPub.appLog("INFO", "交易终态通知开始[%s][%s]", szIssrId, szOthSeq);
			// 查询原交易流水信息
			int iRet = BusiPub.qryCupBook(szIssrId, szOthSeq, "0");
			if (-1 == iRet) {
				SysPub.appLog("ERROR", "查询原交易流水信息失败");
				return -1;
			} else if (0 == iRet) {
				BusiPub.setCupMsg("PB531001", "原交易流水号不存在","2");
				SysPub.appLog("ERROR", "原交易流水号不存在");
				return -1;
			}
			SysPub.appLog("INFO", "交易终态通知开始[%s][%s]", szIssrId, szOthSeq);

			// 本地原交易状态
			String szStat = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].STAT");
			// 银联原交易状态
			String szCupStat = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizInf[0].TrxStatus");
			SysPub.appLog("INFO", "本地原交易状态[%s]银联原交易状态[%s]", szStat, szCupStat);
			// 本地成功，银联失败 需要冲正
			if ("1".equals(szStat) && "0".equals(szCupStat)) {
				// 登记自动冲正信息
				iRet = BusiPub.insHostRevData("T_NCP_BOOK_HIST[0]", "银联终态通知冲正");
				SysPub.appLog("INFO", "本地成功，银联失败 需要冲正[%d]", iRet);
				if (0 != iRet) {
					SysPub.appLog("ERROR", "登记自动冲正信息失败");
					return -1;
				}
				
				String szSqlStr = "UPDATE T_NCP_BOOK "//
						+ " SET stat =?, ret_code=?, ret_msg=?, ret_time = ? "//
						+ " WHERE plat_date=? and seq_no = ? ";
				szStat = "2"; // 本地失败
				String szRetCode = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SysRtnInf[0].SysRtnCd");
				String szRetMsg = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SysRtnInf[0].SysRtnDesc");
				String szRetTm = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SysRtnInf[0].SysRtnTm");
				String szPlatDate = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PLAT_DATE");
				int iSeqNo = (Integer) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].SEQ_NO");
				Object[] value = { szStat, szRetCode, szRetMsg, szRetTm, szPlatDate, iSeqNo };
				iRet = DataBaseUtils.execute(szSqlStr, value);
				SysPub.appLog("INFO", "更新原交易流水[%s][%d]状态和信息[%s][%s]", szPlatDate, iSeqNo, szRetCode, szRetMsg);
				if (0 >= iRet) {
					SysPub.appLog("ERROR", "更新原交易记录失败");
					return -1;
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return 0;
	}
}
