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
 * @author dingjunbo * 贷记付款处理类 * *
 *******************************************************/
public class SACCT0022001 {
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupChannelIssrInfMsg(tpID);
			BusiMsgProc.putCupRcverInfMsg(tpID);
			BusiMsgProc.putCupPyeeInfMsg(tpID);
			BusiMsgProc.putCupOrdrInfMsg(tpID);
			BusiMsgProc.putCupBizInfMsg(tpID);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "公共报文赋值处理异常");
			throw e;
		}
	}
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月14日
	 * 
	 * @version 1.0 检查协议
	 */
	/*
	 * public static int checkSign() throws Exception {
	 * 
	 * DtaInfo dtaInfo = DtaInfo.getInstance(); String tpID = dtaInfo.getTpId();
	 * try{ //根据手机号和发起机构号查询短信验证管理登记簿 String PyerAcctIssrId =
	 * (String)EPOper.get(tpID,
	 * "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctIssrId"); String MobNo
	 * = (String)EPOper.get(tpID,
	 * "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].MobNo"); String sql =
	 * "select * from t_sms_confirm where sign_no= ? and phn = ?"; if(
	 * PyerAcctIssrId == null || MobNo == null || "".equals(MobNo) ||
	 * "".equals(PyerAcctIssrId) ) {
	 * EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1"); EPOper.put(tpID,
	 * "T_NCP_BOOK[0].STAT", "2"); EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE",
	 * "PB511013"); EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG",
	 * "请求中预留手机号与签约人在接收方机构预留的协议支付手机号不符"); BusiPub.setMsg(
	 * "PB511013","请求中预留手机号与签约人在接收方机构预留的协议支付手机号不符" );
	 * SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511013",
	 * "请求中预留手机号与签约人在接收方机构预留的协议支付手机号不符");
	 * 
	 * return 0; } Object []value = {PyerAcctIssrId,MobNo};
	 * DataBaseUtils.queryToElem(sql, "T_SMS_CONFIRM",value); String phn =
	 * (String)EPOper.get(tpID, "T_SMS_CONFIRM[0].CERT_TYPE"); //清空业务判断标志
	 * EPOper.delete(tpID, "INIT[0]._FUNC_RETURN"); if( phn == null ||
	 * "".equals(phn.trim())) { EPOper.put(tpID,"INIT[0]._FUNC_RETURN","1");
	 * EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "2"); EPOper.put(tpID,
	 * "T_NCP_BOOK[0].RET_CODE", "PB511013"); EPOper.put(tpID,
	 * "T_NCP_BOOK[0].RET_MSG", "请求中预留手机号与签约人在付款行预留的协议支付手机号不符"); BusiPub.setMsg(
	 * "PB511013","请求中预留手机号与签约人在付款行预留的协议支付手机号不符" );
	 * SysPub.appLog("ERROR","错误码：%s,错误信息:%s","PB511013",
	 * "请求中预留手机号与签约人在付款行预留的协议支付手机号不符"); return 0; }
	 * EPOper.put(tpID,"INIT[0]._FUNC_RETURN","0"); } catch( Exception e ) {
	 * e.printStackTrace(); SysPub.appLog("ERROR","checkSign 方法处理异常"); throw e;
	 * } return 0; }
	 */

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
			/*if (StringTool.isNullOrEmpty(szSignNo)) {
				BusiPub.setCupMsg("PB500023", "签约协议号不能为空", "2");
				SysPub.appLog("ERROR", "PB500023-签约协议号不能为空");
				return -1;
			}
			*/
			// 1.检查是否为二维码业务 0-二维码业务 1-非二维码业务 -1：异常
			iRet = ACCTPub.chkQRCdBusi(szProductTp, szSignNo);
			if (0 == iRet) {
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctId", 
						"T_NCP_SIGN[0].ACCT_NO");
				SysPub.appLog("INFO", "二维码业务不需要到签约表检查协议号");
				return 0;
			} else if (-1 == iRet) {
				SysPub.appLog("INFO", "检查二维码业务失败");
				return -1;
			}
			
			// 若有协议号信息，则需要检查协议号是否正确
			String szAcctNo="";
			String szCupSignNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo");
			String szCupAcctNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctId");
			if (!StringTool.isNullOrEmpty(szCupSignNo)) {
				SysPub.appLog("INFO", "检查签约协议号");
				iRet = BusiPub.qrySignBySignNo(szCupSignNo);
				if (0 != iRet) {
					szAcctNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO");
					if (!StringTool.isNullOrEmpty(szCupAcctNo)) {
						// 当银联签约协议号和账号都不为空时，需要校验协议号和账号信息是否匹配
						if (!szCupAcctNo.equals(szAcctNo)) {
							SysPub.appLog("ERROR", "PB014X02-收款账号与签约账号不符");
							BusiPub.setCupMsg("PB014X02", "收款账号与签约账号不符", "2");
							return -1;
						}
					}
					
					//户名判断
					String szName = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyeeInf[0].PyeeNm");
					String szSignName = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NAME");
					if(!StringTool.isNullOrEmpty(szName) ){
						if (!szSignName.equals(szName)) {
							SysPub.appLog("ERROR", "PB005203-收款户名与签约户名不符");
							BusiPub.setCupMsg("PB005203", "收款户名与签约户名不符", "2");
							return -1;
						}
					}
					//身份证号码
					String szCrdNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyeeInf[0].IDNo");
					String szSignCrdNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].CERT_NO");
					if(!StringTool.isNullOrEmpty(szCrdNo) ){
						if (!szSignCrdNo.equals(szCrdNo)) {
							SysPub.appLog("ERROR", "PB005203-收款证件信息与签约证件信息不符");
							BusiPub.setCupMsg("PB005203", "收款证件信息与签约证件信息不符", "2");
							return -1;
						}
					}
				}
				else{
					SysPub.appLog("ERROR", "PB005212-身份认证失败（签约协议号错误）");
					BusiPub.setCupMsg("PB005212", "身份认证失败（签约协议号错误）", "2");
					return -1;
				}
			} else {
				//协议号为空的情况 检查收款账号是否为空
				if (StringTool.isNullOrEmpty(szCupAcctNo)) {
					SysPub.appLog("ERROR", "PB030X01-协议号和账号不能同时为空");
					BusiPub.setCupMsg("PB030X01", "协议号和账号不能同时为空", "2");
					return -1;
				}
			}
			//因银联可能不送收款账号，就使用T_NCP_SIGN[0].ACCT_NO作为收款账号赋值
			if(!StringTool.isNullOrEmpty(szCupAcctNo)){
				EPOper.put(tpID, "T_NCP_SIGN[0].ACCT_NO", szCupAcctNo );
			}
			SysPub.appLog("INFO", "业务检查完成");
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
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Desc2", "银联贷记付款");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].MemoCode", "1127");
			BusiMsgProc.HostS801003ByCup(tpID);
			iRet = BusiPub.callHostSvc("S801003", "NOREV", "fmt_CUP_SVR");
			if (0 == iRet) {
				SysPub.appLog("INFO", "主机记账成功");
				BusiPub.setCupMsg(SysPubDef.CUP_SUC_RET, SysPubDef.CUP_SUC_MSG, "1");
			}
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
