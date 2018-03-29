package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class SQRY00020002 {

	/*
	 * @author xiangjun
	 * @createAt 2017年8月19日
	 * @version 1.0 交易请求报文检查及部分响应报文字段赋值
	 */
	public static int Chk() throws Exception
	{
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			BusiMsgProc.putCupRcverInfMsg(szTpID);
			BusiMsgProc.putCupSderInfMsg(szTpID);
			
			// 从报文获取签约信息
			String szIssrId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.MsgHeader[0].IssrId");// 发起方机构
			String szRcverAcctId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].RcverAcctId");// 账号
			String szMobNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].MobNo");// 手机号
			if (szIssrId == null || "".equals(szIssrId.trim()))
			{
				BusiPub.setCupMsg( "PB030X01","报文格式错误(发起方机构为空)","2" );
				SysPub.appLog("ERROR", "PB030X01:报文格式错误(发起方机构为空)");
				return -1;
			}
			
			if (szRcverAcctId == null || "".equals(szRcverAcctId.trim()))
			{
				BusiPub.setCupMsg( "PB030X01","报文格式错误(接收方账户为空)","2" );
				SysPub.appLog("ERROR",  "PB030X01:报文格式错误(接收方账户为空)");
				return -1;
			}
			
			if (szMobNo == null || "".equals(szMobNo.trim()))
			{
				BusiPub.setCupMsg( "PB030X01","报文格式错误(手机号为空)","2" );
				SysPub.appLog("ERROR", "PB030X01:报文格式错误(手机号为空)");
				return -1;
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR","校验处理异常");
			throw e;
		}

		return 0;	
	}
	
	/*
	 * @author xiangjun
	 * @createAt 2017年6月5日
	 * @version 1.0 检查账号有没有签约 返回值：0-未签约，1-已签约
	 */
	public static int SQRY00020002_check_sign() throws Exception 
	{
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			
			// 从报文获取签约信息
			String szIssrId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.MsgHeader[0].IssrId");// 发起方机构
			String szRcverAcctId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].RcverAcctId");// 账号
			String szMobNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].MobNo");// 手机号
			
			if (szIssrId == null || "".equals(szIssrId.trim()))
			{
				BusiPub.setCupMsg( "10100","发起方机构不能为空" ,"2" );
				EPOper.put(szTpID, "INIT._FUNC_RETURN", -1);
				return -1;
			}
			
			if (szRcverAcctId == null || "".equals(szRcverAcctId.trim()))
			{
				BusiPub.setCupMsg( "10100","账号不能为空" ,"2" );
				EPOper.put(szTpID, "INIT._FUNC_RETURN", -1);
				return -1;
			}
			
			if (szMobNo == null || "".equals(szMobNo.trim()))
			{
				BusiPub.setCupMsg( "10100","手机号不能为空","2" );
				EPOper.put(szTpID, "INIT._FUNC_RETURN", -1);
				return -1;
			}
			
			String szSql = " select * from t_ncp_sign where acct_no = ? and sign_type='1'";
			SysPub.appLog("DEBUG", szSql);
			Object[] value = { szRcverAcctId };
			DataBaseUtils.queryToElem(szSql, "T_NCP_SIGN", value);
			String szSignType = (String) EPOper.get(szTpID, "T_NCP_SIGN[0].SIGN_TYPE");
			if ( "1".equals(szSignType) )
			{
				BusiPub.setCupMsg( "10100","该账号已签约借记转账","2" );
				EPOper.put(szTpID, "INIT._FUNC_RETURN", -1);
				return -1;
			}
			
			EPOper.put(szTpID, "INIT._FUNC_RETURN", 0);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return 0;	
	}
	
	/*
	 * @author xiangjun
	 * @createAt 2017年6月5日
	 * @version 1.0 生成短信验证码和关联码（验证码为6位随机数，关联码为当前时间+平台流水号）
	 */
	public static int CreateLinkCode() throws Exception 
	{
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			
			// 从报文获取签约信息
			String szIssrId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.MsgHeader[0].IssrId");// 发起方机构
			String szRcverAcctIssrId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId");// 接收方机构
			String szRcverAcctId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].RcverAcctId");// 账号
			String szMobNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].MobNo");// 手机号
			String szTrxId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxId"); // 银联交易流水号
			
			// 取平台日期
			String szPlatDate = (String) EPOper.get(szTpID, "T_PLAT_PARA[0].PLAT_DATE");
			
			// 生成平台流水号
			int iPlatSeq = PubTool.sys_get_seq();
			
			// 生成6位随机数
			int iVrfyNo = PubTool.getId6();
			
			// 生成关联码
			String szDate14 = PubTool.getDate();
			String szLinkCode = szDate14 + String.valueOf(iPlatSeq);
			
			// 登记短信验证管理登记簿
			String szSql1 = "insert into t_sms_confirm values (?,?,?,?,?,?,?,?,'','','')";
			SysPub.appLog("DEBUG", szSql1);
			Object[] value1 = { szPlatDate, iPlatSeq, szIssrId, szTrxId, iVrfyNo, szLinkCode, szMobNo, szDate14 };
			DataBaseUtils.execute(szSql1, value1);
			//add by liangjr 20170918 不登记短信待发送表，直接发往短信前置
			SQRYPub.sendSmsPub(iVrfyNo);
			// end 
			// 登记短信发送表
			String szSql2 = "insert into t_sms_info values ('01',?,?,?,?,?,?,'0','','','','','','')";
			SysPub.appLog("DEBUG", szSql2);
			Object[] value2 = { szRcverAcctId, szRcverAcctIssrId, szDate14.substring(0, 8), szDate14.substring(8, 14), szMobNo, iVrfyNo };
			DataBaseUtils.execute(szSql2, value2);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/*
	 * @author xiangjun
	 * @createAt 2017年6月19日
	 * @version 1.0 上核心报文体赋值
	 */
	public static int SQRY00020002HostProc()
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.TranCode", "860009");
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.TermDate", Date);
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.TermTime", Time);
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.TermSeq", seq);
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.CallDate", EPOper.get(tpID, "T_PLAT_PARA.PLAT_DATE"));
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.TranType", "15");
//		EPOper.put(tpID, "FMT_HOST_CLI_860009_OUT.ChannelId", "40");
		
		EPOper.put(szTpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Rsp[0].AcctNo", EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId"));
		EPOper.put(szTpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Rsp.AcctName", EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm"));
		EPOper.put(szTpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Rsp.Brc", EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId"));
		return 0;
	}
	
	/*
	 * @author xiangjun
	 * @createAt 2017年6月19日
	 * @version 1.0 核心系统返回成功处理
	 */
	public static int SQRY00020002HostOkProc() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			String szDtaName = dtaInfo.getDrqInfo().getdDtaName();
			SysPub.appLog("DEBUG", "dtaName:----------%s", szDtaName);
			String szPlatDate = (String)EPOper.get(szTpID, "T_NCP_BOOK[0].PLAT_DATE");
			int iSeqNo = (Integer)EPOper.get(szTpID, "T_NCP_BOOK[0].SEQ_NO");
			Object[] value = new Object[6];
			value[0] = "1";
			value[1] = "0000000";
			value[2] = "交易成功";
			value[3] = PubTool.getDate();
			value[4] = szPlatDate;
			value[5] = iSeqNo;
		}
		catch( Exception e )
		{
			throw e;
		}
		return 0;
	}
	/*
	 * @author xiangjun
	 * @createAt 2017年6月19日
	 * @version 1.0 核心系统返回失败处理
	 */
	public static int SQRY00020002HostErrorProc() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			String szDtaName = dtaInfo.getDrqInfo().getdDtaName();
			SysPub.appLog("DEBUG", "dtaName--------:%s", szDtaName);
			String szPlatDate = (String)EPOper.get(szTpID, "T_NCP_BOOK[0].PLAT_DATE");
			int iSeqNo = (Integer)EPOper.get(szTpID, "T_NCP_BOOK[0].SEQ_NO");
			Object[] value = new Object[6];
			String szRspCode = "",szRspMsg = "";
			//处理响应码
			if( "HOST_CLI".equals(szDtaName) )
			{
				szRspCode = (String)EPOper.get(szTpID, "HOST_CLI_S805070_Rsp.RspCode");
				szRspMsg = (String)EPOper.get(szTpID, "HOST_CLI_S805070_Rsp.RspMsg");
			}
			else if( "CRED_CLI".equals(szDtaName) )
			{
				szRspCode = (String)EPOper.get(szTpID, "CREDIT_CLI_030517_Rsp.RspCode");
				szRspMsg = (String)EPOper.get(szTpID, "CREDIT_CLI_030517_Rsp.RspMsg");
			}
			//根据渠道更新流水表
			if( (szRspCode == null || "".equals(szRspCode)) && "HOST_CLI".equals(szDtaName) )//核心响应码为空，调度超时
			{
				value[0] = "3";
				value[1] = "PB528999";
				value[2] = "调用核心服务超时";
				
			}else if( (szRspCode == null || "".equals(szRspCode)) && "CRED_CLI".equals(szDtaName) )//贷记卡响应码为空，调度超时
			{
				value[0] = "3";
				value[1] = "PB528999";
				value[2] = "调用贷记卡服务超时";
			}
			else if ( !"000000".equals(szRspCode) )//交易失败
			{
				value[0] = "2";
				value[1] = szRspCode;
				value[2] = szRspMsg;
			}
			value[3] = PubTool.getDate();
			value[4] = szPlatDate;
			value[5] = iSeqNo;

		}
		catch( Exception e )
		{
			throw e;
		}
		return 0;
	}
	
	/*
	 * @author xiangjun
	 * @createAt 2017年6月23日
	 * @version 1.0 判断账户是否为2、3类账户
	 */
	public static int SQRY00020002_get_acct_type() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			String szDtaName = dtaInfo.getDrqInfo().getdDtaName();
			SysPub.appLog("DEBUG", "dtaName:----------%s", szDtaName);
			
			if("HOST_CLI".equals(szDtaName))
			{
				// 借记卡判断账户是否为2、3类账户
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0);
			}
			else
			{
				// 贷记卡判断账户是否为2、3类账户
				
			}
		}
		catch( Exception e )
		{
			throw e;
		}
		return 0;
	}
	
	/*
	 * @author xiangjun
	 * @createAt 2017年6月23日
	 * @version 1.0 判断账户信息是否一致
	 */
	public static int ChkAcctInfo() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			String szDtaName = dtaInfo.getDrqInfo().getdDtaName();
			SysPub.appLog("DEBUG", "dtaName:----------%s", szDtaName);
			
			// 获取银联上送的户名、证件类型、证件号码、手机号
			String szAcctNameIn = (String)EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm");
			String szCertTypeIn = (String)EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");
			String szCertNoIn = (String)EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");
			//String szPhoneIn = (String)EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo");
			
			String szAcctName;
			String szCertType;
			String szCertNo;
			if("HOST_CLI".equals(szDtaName))
			{
				// 取借记卡账户户名、证件类型、证件号码、手机号
				szAcctName = (String)EPOper.get(szTpID, "HOST_CLI_S805070_Rsp.CustomName");
				szCertType = (String)EPOper.get(szTpID, "HOST_CLI_S805070_Rsp.IdType");
				szCertNo = (String)EPOper.get(szTpID, "HOST_CLI_S805070_Rsp.IdNo");
			}
			else
			{
				// 取贷记卡账户户名、证件类型、证件号码、手机号
				szAcctName = (String)EPOper.get(szTpID, "CREDIT_CLI_030517_Rsp.NAME");
				szCertType = (String)EPOper.get(szTpID, "CREDIT_CLI_030517_Rsp.KEYTYPE");
				szCertNo = (String)EPOper.get(szTpID, "CREDIT_CLI_030517_Rsp.CUSTID");
			}
			
			// 判断账户户名、证件类型、证件号码、手机号与银联上送的是否一致
			if(!szAcctName.equals(szAcctNameIn))
			{
				EPOper.put(szTpID,"INIT[0]._FUNC_RETURN","1");
				EPOper.put(szTpID, "T_NCP_BOOK[0].STAT", "2");
				EPOper.put(szTpID, "T_NCP_BOOK[0].RET_CODE", "PB511017");
				EPOper.put(szTpID, "T_NCP_BOOK[0].RET_MSG", "签约人账户名称与接收方机构记录不符");
				//组响应报文
				BusiPub.setCupMsg( "PB511017","签约人账户名称与接收方机构记录不符" ,"2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511017","签约人账户名称与接收方机构记录不符");
				return -1;
			}
			
			if(!szCertType.equals(szCertTypeIn) || !szCertNo.equals(szCertNoIn))
			{
				EPOper.put(szTpID,"INIT[0]._FUNC_RETURN","1");
				EPOper.put(szTpID, "T_NCP_BOOK[0].STAT", "2");
				EPOper.put(szTpID, "T_NCP_BOOK[0].RET_CODE", "PB511019");
				EPOper.put(szTpID, "T_NCP_BOOK[0].RET_MSG", "签约人证件号与接收方机构记录不符");
				//组响应报文
				BusiPub.setCupMsg( "PB511019","签约人证件号与接收方机构记录不符","2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511019","签约人证件号与接收方机构记录不符");
				return -1;
			}
		}
		catch( Exception e )
		{
			throw e;
		}
		return 0;
	}
}


