package com.adtec.ncps.busi.ncp.acct;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 协议支付处理类 * *
 *******************************************************/
public class SACCT0021001 {
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupChannelIssrInfMsg(tpID);
			BusiMsgProc.putCupPyerInfMsg(tpID);
			BusiMsgProc.putCupOrdrInfMsg(tpID);
			// BusiMsgProc.putCupBizInfMsg(tpID);
			// 协议号
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].ChannelIssrInf[0].SgnNo");
			// 收付标志
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].RPFlg",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].RPFlg");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "公共报文赋值处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月7日
	 * 
	 * @version 1.0 业务检查
	 */
	public static int chkBusi() throws BaseException, Exception {

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			String szProductTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ProductInf[0].ProductTp");// 产品类型
			String szSignNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo");// 协议号
			if (StringTool.isNullOrEmpty(szSignNo)) {
				BusiPub.setCupMsg("PB500023", "签约协议号不能为空", "2");
				SysPub.appLog("ERROR", "PB500023-签约协议号不能为空");
				return -1;
			}
			// 1.检查是否为二维码业务 0-二维码业务 1-非二维码业务 -1：异常
			iRet = ACCTPub.chkQRCdBusi(szProductTp, szSignNo);
			if (0 == iRet) {
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctId", 
						"T_NCP_SIGN[0].ACCT_NO");
				SysPub.appLog("INFO", "二维码业务不需要到签约表检查协议号");
				return 0;
			} else if (-1 == iRet) {
				SysPub.appLog("INFO", "检查二维码业务失败");
				return -1;
			}

			// 检查协议号是否正常
			iRet = BusiPub.chkSign(szSignNo, "ACCT");
			if (0 != iRet) {
				SysPub.appLog("WARN", "协议号非正常状态");
				return -1;
			}

			// 若账号不为空，检查账号是否和签约账号一致
			String szPyerAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctId");
			if (!StringTool.isNullOrEmpty(szPyerAcctId)) {
				String szAcctNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO");
				if (!szPyerAcctId.equals(szAcctNo)) {
					SysPub.appLog("INFO", "付款账号[%s]签约账号[%s]", szPyerAcctId, szAcctNo);
					BusiPub.setCupMsg("PB520097", "付款账号和签约账号不符", "2");
					SysPub.appLog("ERROR", "PB520097-付款账号和签约账号不符");
					return -1;
				}
			} else {
				// 需要返回付款人账号
				EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO",
						"fmt_CUP_SVR_OUT[0].Rsp_Body[0].PyerInf[0].PyerAcctId");
				// 登记付款人信息到登记簿
				EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO", "T_NCP_BOOK[0].PAY_ACCT_NO");
			}
			// 登记付款人信息到登记簿
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NAME", "T_NCP_BOOK[0].PAY_ACCT_NAME");
			// 需要校验渠道方机构标识是否为签约的发起方账户所属机构标识
			String szChannelIssrId = (String) EPOper.get(tpID,
					"fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].ChannelIssrId");
			String szSndBrch = BusiPub.findChnlBrch(szChannelIssrId, "01");
			String szSignBrch = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_BRCH");
			if (!szSndBrch.equals(szSignBrch)) {
				SysPub.appLog("INFO", "渠道方签约机构标识[%s]和签约表签约机构[%s]", szSndBrch, szSignBrch);
				BusiPub.setCupMsg("PB520097", "渠道方机构标识不符", "2");
				SysPub.appLog("ERROR", "PB520097-渠道方机构标识不符");
				return -1;
			}
			SysPub.appLog("INFO", "业务检查正常");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "checkSign 方法处理异常");
			throw e;
		}
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
			SysPub.appLog("INFO", "调用030105服务开始");
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
			//EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Desc2", "银联协议支付");
			//EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].MemoCode", "1124");
			BusiMsgProc.HostS801053ByCup(tpID);
			iRet = BusiPub.callHostSvc("S801053", "NOREV", "fmt_CUP_SVR");
			//for unionpay test Thread.sleep(50);
			//iRet = 0;
			if (0 == iRet) {
				SysPub.appLog("INFO", "主机记账成功");
				BusiPub.setCupMsg(SysPubDef.CUP_SUC_RET, SysPubDef.CUP_SUC_MSG, "1");
			}
			//Thread.sleep(40000);
			return iRet;

		} catch (Exception e) {
			SysPub.appLog("ERROR", "hostMsg 方法处理异常");
			throw e;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
