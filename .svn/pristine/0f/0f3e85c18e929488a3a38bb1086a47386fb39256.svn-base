package com.adtec.ncps.busi.ncp.sign;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * 														*
 * @author dingjunbo									*
 *  借记转账签约											*
 *														*
 *******************************************************/
public class SSIGN0020202 {
	
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
			BusiMsgProc.putCupRcverInfMsg(tpID);
			BusiMsgProc.putCupSderInfMsg(tpID);
		}catch( Exception e)
		{
			SysPub.appLog("ERROR","公共报文赋值处理异常");
			throw e;
		}
	}
	/*
	 * 检查签约信息
	 */
	public static String checkSign() throws Exception
	{
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			//发送机构标识 接收方账户 手机号银联报文规定必输项，不检查null情况
			String szSderAcctId = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctId");//发送账户
			String szRcverAcctId = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");//接收方账户
			String szIdNo = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");//证件号码
			String szIdTp = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");//证件类型
			String szMobNo = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo");//手机号
			String rtMsg = SSIGNPub.chkSign(szRcverAcctId, szSderAcctId, szIdTp, szIdNo, szMobNo);
			return rtMsg;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR","执行 checkSign 方法失败");
			e.printStackTrace();
			throw e;
		}
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月2日
	 * @version 1.0 检查短信关联码
	 */
	public static int checkSms() throws Exception
	{
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String Smskey = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].Smskey");//动态短信关联码
			EPOper.delete(tpID, "INIT[0]._FUNC_RETURN");
			if( StringTool.isNullOrEmpty(Smskey) )
			{
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( "PB511015","签约人未在付款行开通短信功能", "2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511015","签约人未在付款行开通短信功能");
				return 0;
			}
			String rtMsg = BusiPub.chkSMSVerify( Smskey );
			//短信校验失败
			if( !"0".equals(rtMsg) )
			{
				String []tmp = rtMsg.split("\\|");
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( tmp[0],tmp[1], "2" );
				return 0;
			}
			//检查账户信息
			String szRt = SSIGNPub.chkAcctInfo(Smskey);
			if( "-1".equals(szRt) )
			{
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				return 0;
			}
			EPOper.put(tpID,"INIT[0]._FUNC_RETURN","0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR","执行 checkSms 方法失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月1日
	 * @version 1.0 响应报文体赋值,更新签约信息
	 */
	public static int rspProc() throws Exception
	{
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szNewSignNo = "", szOldSignNo = "";
			String szSignStat = checkSign();
			szNewSignNo = SSIGNPub.crtSignNo();
			szOldSignNo = (String)EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
			/*未签约,插入新的签约协议号，已签约，产生新的协议号更新现有协议号，已解约，先删除旧签约信息再插入新的签约信息
			/*如果是已解约、信息变更失效，先删除签约记录再插入一条新的签约记录
			/*0-未签约，1-已签约.2-已解约,3-信息变更失效  协议支付签约使用*/
			int iRt = -1;
			if( "0".equals(szSignStat) || "1".equals(szSignStat) )
			{
				iRt = -1;
				if( "0".equals(szSignStat) )//未签约
				{
					iRt = SSIGNPub.signProc("",szNewSignNo,"");
				}else if("1".equals(szSignStat) )//已签约
				{
					iRt = SSIGNPub.signProc(szOldSignNo,szNewSignNo,"0");
				}
				//更新失败
				if( iRt < 0 )
				{
					BusiPub.setCupMsg( SysPubDef.CUP_ERR_RET,SysPubDef.CUP_ERR_MSG, "2" );
					EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo", szNewSignNo);//签约协议号
					SysPub.appLog("ERROR","更新协议号失败");
					return 0;
				}
				BusiPub.setCupMsg( SysPubDef.CUP_SUC_RET,SysPubDef.CUP_SUC_MSG, "1" );
				EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo", szNewSignNo);//签约协议号
				return 0;
			}else if( "2".equals(szSignStat) || "3".equals(szSignStat) )
			{
				iRt = SSIGNPub.signProc(szOldSignNo,szNewSignNo,"1");
				//更新失败
				if( iRt < 0 )
				{
					BusiPub.setCupMsg( SysPubDef.CUP_ERR_RET,SysPubDef.CUP_ERR_MSG, "2" );
					EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo", szNewSignNo);//签约协议号
					return 0;
				}
				BusiPub.setCupMsg( SysPubDef.CUP_SUC_RET,SysPubDef.CUP_SUC_MSG ,"1");
				//返回签约协议号
				EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo", szOldSignNo);//签约协议号
			}
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","执行 rspProc 方法失败");
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
