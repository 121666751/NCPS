package com.adtec.ncps.busi.ncp.sign;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 协议支付签约 * *
 *******************************************************/
public class SSIGN0020201 {
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupRcverInfMsg(tpID);
			BusiMsgProc.putCupSderInfMsg(tpID);
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
	 * @version 1.0 签约处理
	 */
	public static int signDeal() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			// 发起方账户所属机构标识 接收方账户 手机号银联报文规定必输项，不检查null情况
			String SderAcctIssrId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctIssrId");// 发起方账户所属机构标识
			String SderAcctInf = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctInf");// 支付账号信息
			String RcverAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");// 接收方账户
			iRet = SSIGNPub.qrySignByPay(RcverAcctId, SderAcctIssrId, SderAcctInf);
			if (0 == iRet) {
				SysPub.appLog("INFO", "未签约-插入新的签约信息");
				// 未签约 插入新的签约信息
				iRet=SSIGNPub.signInst("","INS");
			} else if (1 == iRet) {
				// 1-已签约 返回相同的签约协议号
				SysPub.appLog("INFO", "1-已签约 返回相同的签约协议号");
				EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_NO", "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo");// 签约协议号
				EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_NO", "T_NCP_BOOK[0].SIGN_NO");// 签约协议号
				return 0;
			} else {
				SysPub.appLog("INFO", "签约无效-删除原签约信息，重新新的登记签约信息");
				// 签约无效 删除原签约信息，重新新的登记签约信息
				String szSignNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
				iRet=SSIGNPub.signInst(szSignNo,"DEL");
			}
			String Smskey = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].Smskey");// 动态短信关联码
			BusiPub.uptSMSInvl(tpID,Smskey);  //短信校验码更新为失效状态
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 signDeal 方法失败");
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

			String Smskey = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].Smskey");// 动态短信关联码
			if (StringTool.isNullOrEmpty(Smskey)) {
				BusiPub.setCupMsg("PS500023", "动态短信关联码为空", "2");
				SysPub.appLog("ERROR", "PS500023-动态短信关联码为空");
				return 0;
			}

			// 验证短信校验码
			String vrfyNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].AuthMsg");// 验证码
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
