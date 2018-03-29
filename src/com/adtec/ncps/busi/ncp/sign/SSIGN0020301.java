package com.adtec.ncps.busi.ncp.sign;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

/********************************************************
 * *
 * 
 * @author dingjunbo * 协议支付解约 * *
 *******************************************************/
public class SSIGN0020301 {
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
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].SgnNo",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "公共报文赋值处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月6日
	 * 
	 * @version 1.0 签约检查
	 */
	public static int chkSign() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}

			String szSignNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].SgnNo");
			// 0-未签约，1-已签约.2-已解约,3-信息变更失效
			iRet = BusiPub.chkSign(szSignNo,"SIGN");
			if( 0!=iRet ){
				SysPub.appLog("WARN", "协议号非正常状态");
				return -1;
			}
			// 验证身份信息
			SSIGNPub.chkCustInfo();
			//Thread.sleep(40000);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 checkSign 方法失败");
			throw e;
		}
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
