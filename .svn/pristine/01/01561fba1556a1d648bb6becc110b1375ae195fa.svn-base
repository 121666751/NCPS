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
 *  借记转账解约											*
 *														*
 *******************************************************/
public class SSIGN0020302 {
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
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].SgnNo", "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo" );
		}catch( Exception e)
		{
			SysPub.appLog("ERROR","公共报文赋值处理异常");
			throw e;
		}
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月6日
	 * @version 1.0 检查协议号
	 */
	public static int checkSign() throws Exception
	{
		
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try{
			EPOper.delete(tpID, "INIT[0]._FUNC_RETURN");
			String szSgnNo = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].SgnNo");
			if( StringTool.isNullOrEmpty(szSgnNo) )
			{
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( "PB512001","接收方机构查无此签约协议号", "2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB512001","接收方机构查无此签约协议号");
				return 0;
			}
			//0-未签约，1-已签约.2-已解约,3-信息变更失效
			String szRt = BusiPub.chkSign(szSgnNo,"SIGN");
			if( !"1".equals( szRt ))
			{
				String szCode = "",szMsg = "";
				if( "0".equals(szRt) )
				{
					szCode = "PB512001";
					szMsg = "接收方机构查无此签约协议号";
				}else if( "2".equals(szRt) )
				{
					szCode = "PB512002";
					szMsg = "协议状态为已解约";
				}else if( "3".equals(szRt) )
				{
					szCode = "PB512098";
					szMsg = "除以上原因外的其他因协议状态原因而导致的失败";
				}else
				{
					szCode = "PB512099";
					szMsg = "除以上原因外其他不允许解约的情况";
				}
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				//组响应报文
				BusiPub.setCupMsg( szCode,szMsg,"2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s",szCode,szMsg);
				return 0;
			}
			//账户检查
			checkBusiLogic();
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","执行 checkSign方法失败");
			throw e;
		}
		return 0;
	}
	
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月6日
	 * @version 1.0账户验证
	 */
	public static int checkBusiLogic() throws Exception
	{
		try{
			//比较证件类型和号码
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String cert_type = (String)EPOper.get(tpID, "T_NCP_SIGN[0].CERT_TYPE");
			String cert_no = (String)EPOper.get(tpID, "T_NCP_SIGN[0].CERT_NO");
			String IDTp = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");
			String IDNo = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");
			if( StringTool.isNullOrEmpty(IDTp) || StringTool.isNullOrEmpty(IDNo))
			{
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( "PB511015","接收方机构不支持此类证件验证", "2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511022","接收方机构不支持此类证件验证");
				return 0;
			}
			else if ( !IDTp.equals( cert_type ) )
			{

				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( "PB511015","签约人证件类型与银行记录不符","2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511021","签约人证件类型与银行记录不符");
				return 0;
			}
			else if ( !IDNo.equals( cert_no ) )
			{
				EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
				BusiPub.setCupMsg( "PB511015","签约人证件类型与银行记录不符","2" );
				SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511019","签约人证件号与接收方机构记录不符");
				return 0;
			}
			//更新记录
			EPOper.put(tpID,"INIT[0]._FUNC_RETURN","0");
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","执行 checkBusiLogic 方法失败");
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月1日
	 * @version 1.0 响应报文体赋值, 更新签约表
	 */
	public static int rspProc() throws Exception
	{
		try{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			//流水表对象赋值，更新流水时用到
			EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "0");
			EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE", SysPubDef.CUP_SUC_RET);
			EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG", SysPubDef.CUP_SUC_MSG);
			//插入数据完成后，再赋值为成功
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd", SysPubDef.CUP_ERR_RET);
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnDesc", SysPubDef.CUP_ERR_MSG);
			//更新签约信息
			updateSign();
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","执行 rspProc 方法失败");
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * @createAt 2017年6月6日
	 * @version 1.0 更新表T_NCP_SIGN_HIST，T_NCP_SIGN
	 */
	public static int updateSign() throws Exception
	{
		try{
			SSIGNPub.update_t_ncp_sign( "N" );
			SSIGNPub.Init_t_ncp_sign_hist( "NCP" );	
		}
		catch( Exception e )
		{
			SysPub.appLog("ERROR","执行 updateSign 方法失败");
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
