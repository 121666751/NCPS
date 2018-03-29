package com.adtec.ncps.busi.ncp.acct;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
/********************************************************
 * 														*
 * @author dingjunbo									*
 *  借记转账处理类										*
 *														*
 *******************************************************/
public class SACCT0021003 {
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupPubMsg(tpID);
		}catch( Exception e)
		{
			SysPub.appLog("ERROR","公共报文赋值处理异常");
			throw e;
		}
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月7日
	 * @version 1.0 检查协议号
	 */
	public static int checkSign() throws Exception
	{
		
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try{
			//清空业务判断标志
			EPOper.delete(tpID, "INIT[0]._FUNC_RETURN");
			String szSgnNo = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].SgnNo");//协议号
			if( StringTool.isNullOrEmpty(szSgnNo) )
			{
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( "PB512001","付款行查无此签约协议号","2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB512001","付款行查无此签约协议号");
				return 0;
			}
			//0-未签约，1-已签约.2-已解约,3-信息变更失效
			String szRt = BusiPub.chkSign(szSgnNo);
			if( !"1".equals( szRt ))
			{
				String szCode = "",szMsg = "";
				if( "0".equals(szRt) )
				{
					szCode = "PB521014";
					szMsg = "接收方机构查无此签约协议号";
				}else if( "2".equals(szRt) )
				{
					szCode = "PB521013";
					szMsg = "签约协议号对应支付协议已解约";
				}else if( "3".equals(szRt) )
				{
					szCode = "PB521016";
					szMsg = "签约协议号对应支付协议已失效（签约信息变更）";
				}else
				{
					szCode = "PB521096";
					szMsg = "除以上原因外的其他因协议状态原因导致的失败";
				}
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				//组响应报文
				BusiPub.setCupMsg( szCode,szMsg,"2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s",szCode,szMsg);
				return 0;
			}
			EPOper.put(tpID,"INIT[0]._FUNC_RETURN","0");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			SysPub.appLog("ERROR","checkSign 方法处理异常");
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月9日
	 * @version 1.0 DTA系统返回处理
	 */
	public static int rspProc() throws Exception
	{
		try{
			BusiMsgProc.gettMsgRet();
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","rspProc 方法处理异常");
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月23日
	 * @version 1.0 贷记报文赋值
	 */
	public static int credMsg() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.delete(tpID, "INIT[0].TxnCd");
			EPOper.put(tpID, "INIT[0].TxnCd", "030105");
			BusiMsgProc.headCred( "030105" );
			BusiMsgProc.msgBody030105();
			SysPub.appLog("INFO", "调用030105服务开始");
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","credMsg 方法处理异常");
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月23日
	 * @version 1.0 借记报文赋值
	 */
	public static int hostMsg() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.delete(tpID, "INIT[0].TxnCd");
			EPOper.put(tpID, "INIT[0].TxnCd", "S801053");
			BusiMsgProc.headHost( "S801053" );
			BusiMsgProc.msgBodyS801053();
			SysPub.appLog("INFO", "调用S801053服务开始");
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","hostMsg 方法处理异常");
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
