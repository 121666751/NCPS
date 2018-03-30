package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class SQRY00023101 {
	
	/*
	 * @author xiangjun
	 * @createAt 2017年8月19日
	 * @version 1.0 公共报文字段赋值
	 */
	public static int Chk() throws Exception
	{
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			BusiMsgProc.putCupRcverInfMsg(szTpID);
			BusiMsgProc.putCupSderInfMsg(szTpID);
			BusiMsgProc.putCupBizInfMsg(szTpID);
			
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR","前处理检查失败");
			throw e;
		}

		return 0;	
	}
	
	/*
	 * @author xiangjun	
	 * @createAt 2017年6月8日
	 * @version 1.0  返回银联报文处理
	 */
	public static int GetTxInfo() throws Exception 
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		int iRet = SysPub.ChkStep(szTpID);
		SysPub.appLog("DEBUG","iRet[%d]", iRet);
		if (-1 == iRet) {
			return -1;
		}
		try
		{
			// 返回银联报文头赋值
			//BusiPub.headUnionPay();
			
			// 通过机构号和流水号查询流水表
			String szBrch = (String)EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].SderInf[0].SderIssrId");
			String szOthSeq = (String)EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].OriTrxInf[0].OriTrxId");
			iRet = BusiPub.qryCupBook(szBrch, szOthSeq, "0");
			if(iRet == -1)
			{
				SysPub.appLog("ERROR", "查询流水表失败");
				return -1;
			}
			else if( 0 == iRet ){
				BusiPub.setCupMsg("PB531001", "找不到原交易信息", "2");
				SysPub.appLog("ERROR", "PB531001-找不到原交易信息");
				return -1;
			}
			
			// 从流水表中取值
			String szOldPlatDate = (String) EPOper.get(szTpID, "T_NCP_BOOK_HIST[0].PLAT_DATE");// 原平台日期
			int iOldSeqNo = (Integer) EPOper.get(szTpID, "T_NCP_BOOK_HIST[0].SEQ_NO");// 原平台流水号
//			String szRpFlag = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].RPFlg"); // 收付标识
			
//			if (szOldPlatDate == null || "".equals(szOldPlatDate.trim()))
//			{
//				if("5".equals(szRpFlag))
//				{
//					EPOper.put(szTpID, "fmt_CUP_SVR_OUT.Rsp_Body[0].SysRtnInf[0].SysRtnCd", "PB531001");
//				}
//				else if("6".equals(szRpFlag))
//				{
//					EPOper.put(szTpID, "fmt_CUP_SVR_OUT.Rsp_Body[0].SysRtnInf[0].SysRtnCd", "PB631001");
//				}
//				EPOper.put(szTpID, "fmt_CUP_SVR_OUT.Rsp_Body[0].SysRtnInf[0].SysRtnDesc", "找不到原交易信息");
//				return -1;
//			}
			
			// 原交易码
			String szTxCode = (String) EPOper.get(szTpID, "T_NCP_BOOK_HIST.TX_CODE");
			SysPub.appLog("INFO", "原交易码[%s]", szTxCode);
			if("SQRY00020001".equals(szTxCode) || "SQRY00020002".equals(szTxCode) || "SQRY00020003".equals(szTxCode))
			{
				// 触发短信类服务，查询t_sms_confirm，返回关联码；
				SysPub.appLog("INFO", "触发短信类服务，查询t_sms_confirm，返回关联码");
				String szSql = " select * from t_sms_confirm where plat_date = ? and seq_no= ? ";
				Object[] value = { szOldPlatDate, iOldSeqNo };
				DataBaseUtils.queryToElem(szSql, "T_SMS_CONFIRM", value);
			}
						
			// 给返回报文赋值
			// 交易信息
			EPOper.copy(szTpID,szTpID, "T_NCP_BOOK_HIST[0].CLEAR_DATE", "fmt_CUP_SVR_OUT.Rsp_Body[0].TrxInf[0].SettlmtDt"  ); // 清算日期
			
			// 业务响应信息
			EPOper.copy(szTpID,szTpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].RPFlg", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].RPFlg"); // 收付标志
			Double dTxAmt=(Double)EPOper.get(szTpID, "T_NCP_BOOK_HIST[0].TX_AMT");
			String szTxAmt="CNY"+String.format("%.2f", dTxAmt);
			SysPub.appLog("INFO", "金额字段[CNY%s]", szTxAmt);
			EPOper.put(szTpID, "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].TrxAmt", szTxAmt ); // 交易金额
			EPOper.copy(szTpID,szTpID, "T_NCP_BOOK_HIST[0].PAY_ACCT_NO", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].RcverAcctId"); //接收方账户
			//EPOper.copy(tpID,tpID, "T_NCP_BOOK_HIST[0].RP_FLAG",  "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].AcctLvl"); // 持卡人账户等级
			//EPOper.copy(tpID,tpID, "T_NCP_BOOK_HIST[0].RP_FLAG" ,"fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].ChkStat" ); // 柜面核身状态
			EPOper.copy(szTpID,szTpID,"T_SMS_CONFIRM[0].VRFY_NO", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].Smskey"); // 动态短信关联码 
			EPOper.copy(szTpID,szTpID, "T_NCP_BOOK_HIST[0].SIGN_NO", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].SgnNo"); // 签约协议号
			EPOper.copy(szTpID,szTpID, "T_NCP_BOOK_HIST[0].BUSI_TYPE", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].OriBizTp"); // 原业务种类
			EPOper.copy(szTpID,szTpID, "T_NCP_BOOK_HIST[0].OTH_SEQ", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].OriTrxId"); // 原交易流水号
			EPOper.copy(szTpID,szTpID, "T_NCP_BOOK_HIST[0].RET_CODE", "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].OriSysRtnCd"); // 原交易系统返回码
			String szStat=(String)EPOper.get(szTpID, "T_NCP_BOOK_HIST[0].STAT");
			String szCupStat="";
			if("1".equals(szStat)||"9".equals(szStat)){
				szCupStat="0";
			}else if("0".equals(szStat)){
				szCupStat="2";
			}else if("2".equals(szStat)){
				szCupStat="1";
			}else if("3".equals(szStat)){
				//超时，需要上核心查询
				BusiMsgProc.headHost("S801008"); //公共处理
				EPOper.put(szTpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801008_Req[0].Srvstan", iOldSeqNo);
				String szPlatdate= szOldPlatDate.substring(0,4) + "-" + szOldPlatDate.substring(4,6) + "-" + szOldPlatDate.substring(6,8);
				EPOper.put(szTpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801008_Req[0].PrimTranDate", szPlatdate);
				
				SysPub.appLog("INFO", "调用S801008服务开始");
				DtaTool.call("HOST_CLI", "S801008");
				String szRetCd = (String)EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].HOST_CLI_S801008_Rsp[0].RspCode");
				SysPub.appLog("INFO", "S801008响应码[%s]", szRetCd);
				if(!"000000".equals(szRetCd)){
					//交易失败返回银联交易处理中
					szCupStat = "2";
				}else{
					String szValue = (String)EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].HOST_CLI_S801008_Rsp[0].Value");
					if("0".equals(szValue)){
						//更新流水表原流水记录
						String szSql_Str = " update t_ncp_book set stat = '1' where plat_date = ?"
								+ " and seq_no = ?";
						Object[] value = { szOldPlatDate, iOldSeqNo };
						DataBaseUtils.execute(szSql_Str, value);
						szCupStat = "0";
					}else if("1".equals(szValue) || "2".equals(szValue) || "3".equals(szValue)){
						//更新流水表原流水记录
						String szSql_Str = " update t_ncp_book set stat = '2' where plat_date = ?"
								+ " and seq_no = ?";
						Object[] value = { szOldPlatDate, iOldSeqNo };
						DataBaseUtils.execute(szSql_Str, value);
						szCupStat = "1";
					}
				}
			}else{
				szCupStat = "1";
			}
			EPOper.put(szTpID, "fmt_CUP_SVR_OUT.Rsp_Body[0].BizInf[0].TrxStatus", szCupStat ); // 原交易状态

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

}
