package com.adtec.ncps.busi.ncp.acct;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 直接支付处理类 * *
 *******************************************************/
public class SACCT0021002 {
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupOrdrInfMsg(tpID);
			BusiMsgProc.putCupBizInfMsg(tpID);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "公共报文赋值处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月31日
	 * 
	 * @version 1.0 检查短信验证码和账户信息
	 */
	public static int checkSms() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			String Smskey = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].Smskey");// 动态短信关联码
			if (StringTool.isNullOrEmpty(Smskey)) {
				BusiPub.setCupMsg("PS500023", "动态短信关联码为空", "2");
				SysPub.appLog("ERROR", "PS500023-动态短信关联码为空");
				return 0;
			}

			// 验证短信校验码
			String vrfyNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].AuthMsg");// 验证码
			iRet = BusiPub.chkSMSVrfy(Smskey, vrfyNo);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "验证短信校验码失败");
				return -1;
			}

			// 检查原交易信息--若未登记原交易信息则不检查
			iRet = BusiPub.chkOriInfo(Smskey);
			if (-1 == iRet) {
				SysPub.appLog("ERROR", "验证短信校验码失败");
				return -1;
			}

		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 checkSms 方法失败");
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 贷记报文赋值
	 */
	public static int credMsg() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.delete(tpID, "INIT[0].TxnCd");
			EPOper.put(tpID, "INIT[0].TxnCd", "030105");
			BusiMsgProc.headCred("030105");
			BusiMsgProc.msgBody030105();
		} catch (Exception e) {
			SysPub.appLog("ERROR", "credMsg 方法处理异常");
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 借记报文赋值
	 */
	public static int callHost() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Desc2", "银联直接支付");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].MemoCode", "1125");
			BusiMsgProc.HostS801053ByCup(tpID);
			iRet = BusiPub.callHostSvc("S801053", "NOREV", "fmt_CUP_SVR");
			if (0 == iRet) {
				SysPub.appLog("INFO", "主机记账成功");
				BusiPub.setCupMsg(SysPubDef.CUP_SUC_RET, SysPubDef.CUP_SUC_MSG, "1");

				// 短信校验码更新为失效状态
				SysPub.appLog("INFO", "短信校验码更新为失效状态");
				String Smskey = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].Smskey");// 动态短信关联码
				BusiPub.uptSMSInvl(tpID, Smskey);
			}
			//Thread.sleep(40000);
			return iRet;

		} catch (Exception e) {
			SysPub.appLog("ERROR", "hostMsg 方法处理异常");
			throw e;
		}
	}

}
