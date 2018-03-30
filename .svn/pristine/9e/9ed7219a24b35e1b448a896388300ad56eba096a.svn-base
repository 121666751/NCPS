package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.struct.dta.DtaInfo;

public class SQRY00020101 {

	/*
	 * @author xiangjun
	 * @createAt 
	 * @version  账户信息验证   
	 */
	public static int Chk() throws Exception
	{
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			BusiMsgProc.putCupRcverInfMsg(szTpID);
			BusiMsgProc.putCupSderInfMsg(szTpID);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR","账户信息验证前处理失败");
			throw e;
		}

		return 0;	
	}
}