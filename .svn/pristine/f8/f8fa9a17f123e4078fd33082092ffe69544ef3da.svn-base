package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.struct.dta.DtaInfo;

/********************************************************
 * 														*
 * @author dingjunbo									*
 *  协议支付签约触发短信									*
 *														*
 *******************************************************/
public class SQRY00020001 {
	
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupRcverInfMsg(tpID);
			BusiMsgProc.putCupSderInfMsg(tpID);	
		}catch( Exception e)
		{
			SysPub.appLog("ERROR","前处理检查失败");
			throw e;
		}
	}
}
