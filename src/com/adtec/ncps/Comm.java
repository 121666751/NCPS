package com.adtec.ncps;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DrqMsgInfo;
import com.adtec.starring.struct.dta.DtaInfo;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;


public class Comm {

	/*
	 * @author dingjunbo
	 * @createAt 2017年6月7日
	 * @version 1.0 根据渠道转换服务码和服务响应信息
	 */
	public static void putRetMsg() throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String dtaName = dtaInfo.getDtaName();
		String szfmtMsg = "",szfmtRet = "";//报文返回码，返回信息对象
		String szErrMsg = "",szErrRet = "";
		try{
			String szlogicSvcName = BusiPub.getLogicSvcName();
			szErrRet = (String)EPOper.get(tpID, "__GDTA_FORMAT[0].__ERR_RET");
			szErrMsg = (String)EPOper.get(tpID, "__GDTA_FORMAT[0].__ERR_MSG");
			SysPub.appLog("DEBUG", "szlogicSvcName:%s", szlogicSvcName);
			if( "CUP_SVR".equals(dtaName) )
			{
				szfmtRet = szlogicSvcName + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnCd";
				szfmtMsg = szlogicSvcName + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnDesc";
				SysPub.appLog("DEBUG", "szfmtMsg:%s,szfmtRet:%s,szfmtRet:%s,szfmtMsg:%s", szfmtMsg,szfmtRet,szfmtRet,szfmtMsg);
				EPOper.put(tpID, szfmtRet, szErrRet);
				EPOper.put(tpID, szfmtMsg, szErrMsg);
			}
		}
		catch( Exception e )
		{
			throw e;
		}	
	}
	
	/*
	 * @author dingjunbo 
	 * @createAt 2017年6月7日
	 * ChnlNo:渠道名称
	 * @version 初始化
	 */
	public static void initData( String ChnlNo ) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		//逻辑服务名称
		String busiFlow = BusiPub.getLogicSvcName();
		//根据逻辑服务名称，拼接数据属性
		String tmp = busiFlow + "_Req[0].MsgHeader[0].BkData[0].ChnlNo";
		EPOper.put( tpID, tmp, ChnlNo);
	}
	
	/**
	 * 
	 * @return true 应用错误，false应用处理成功
	 * @throws Exception 
	 */
	public static boolean appIsErr() throws Exception{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szlogicSvcName = BusiPub.getLogicSvcName();
		String szfmtRet = szlogicSvcName + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnCd";
		String szErrRet = (String)EPOper.get(tpID, szfmtRet);
		if( !"00000".equals(szErrRet) && !"00000000".equals(szErrRet))
			return true;
		else
			return false;
	}
	/*
	 * @author dingjunbo 
	 * @createAt 2017年6月7日
	 * _iFlag:0-成功 1-应用处理失败 2-内部请求失败 3-内部响应失败 4-并发数超限
	 * @version 返回处理
	 */
	public static void retDeal( int _iFlag ) throws Exception{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		printXML();
		String szlogicSvcName = BusiPub.getLogicSvcName();
		String szfmtRet = szlogicSvcName + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnCd";
		//String szfmtMsg = szlogicSvcName + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnDesc";
		if( _iFlag == 0 )
		{
			//EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_RET", "00000000");
			//EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_MSG", "交易成功");
			return;
		}
		else if( _iFlag == 2 )
		{
			return;
		}
		else if( _iFlag ==  3 )
		{
			String msg = (String)EPOper.get(tpID, szfmtRet);
			if( msg == null || msg.length() == 0 )
			{
				EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_RET", "PB500099");
				EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_MSG", "内部响应超时");
			}
		}
		else if( _iFlag ==  4 )
		{
			EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_RET", "PS500001");
			EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_MSG", "并发数超限");
		}
	}
	
	public static void print()
	{
		TrcLog.log("epdata.log",EPOper.epToJSON(DtaInfo.getInstance().getTpId(), "utf-8"));
	}
	
	public static void printXML()
	{
		TrcLog.log("epdataXML.log",EPOper.epToXml(DtaInfo.getInstance().getTpId(), "utf-8"));
	}
	/*
	 * @author dingjunbo 
	 * 检查目的DTA通讯状态，返回true-接收超时
	 */
	public static boolean dtaComStat() throws Exception
	{
		boolean bFlag = false;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		DrqMsgInfo drqMsgInfo = dtaInfo.getDrqInfo();
		String szvcName = (String) EPOper.get(tpID,"__GDTA_FORMAT.__GDTA_SVCNAME");
		//SysPub.appLog("DEBUG","DTA超时检查");
		if( drqMsgInfo.isNetErrFlag() )
		{
			bFlag = true;
			SysPub.appLog("ERROR","DTA接收 %s服务超时!",szvcName);
			EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_RET", "PB500099");
			EPOper.put( tpID, "__GDTA_FORMAT[0].__ERR_MSG", "接收报文超时");
		}
		return bFlag;
	}
	
	public static void getReqHttpHead() throws Exception
	{
		
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		HttpExchange httpExchange = (HttpExchange)dtaInfo.getReqHeaders();
		Headers head = httpExchange.getRequestHeaders();
		String szMsgTp = head.get("MsgTp").get(0);
		EPOper.put(tpID, "__GDTA_FORMAT[0]__GDTA_SVCNAME", szMsgTp);
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
