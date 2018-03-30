package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.struct.dta.DtaInfo;

public class SQRY00020003 {

	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年8月20
	 * 
	 * @version 1.0 直接支付触发短信检查处理
	 */
	public static int Chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			BusiMsgProc.putCupRcverInfMsg(szTpID);
			BusiMsgProc.putCupSderInfMsg(szTpID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR", "前处理检查失败");
			throw e;
		}

		return 0;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int iInt = 1212;
		String szString = "12" + iInt;
		System.out.println(szString);
	}

}