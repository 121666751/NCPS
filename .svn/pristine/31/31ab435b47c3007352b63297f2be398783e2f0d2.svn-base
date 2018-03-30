package com.adtec.ncps.busi.qrps;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 报文体赋值 * *
 *******************************************************/
public class QrBusiMsgProc {

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 发送核心系统报文 查询类交易
	 */
	public static int msgBodyS805070() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].RcverAcctId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Req[0].AcctNo1");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Req[0].CheckFlag", "0");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Req[0].IoFlag", "0");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].SettlmtDt",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805070_Req[0].ClearDate");
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 发送核心系统报文 直接支付
	 */
	public static int HostS801053ByCup(String tpID) throws Exception {
		try {
			//EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Desc2", "4");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Flag1", "4");
			String acctno = (String)EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO");
			if(StringTool.isNullOrEmpty(acctno)){
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyerInf[0].PyerAcctId",
						"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].AcctNo1");
			}else{
				EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO", "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].AcctNo1" );
			}
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].AcctNo1", 
					"T_NCP_BOOK[0].PAY_ACCT_NO");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].AcctNo2", "0000000000000000000000");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].IoFlag", "0");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Ccy1", "01");// 01人民币
			String szAmt = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxAmt");
			// 银联金额格式CNY100.00
			if (!StringTool.isNullOrEmpty(szAmt) && szAmt.length() >= 3) {
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Amt1", szAmt.substring(3));
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Amt2", szAmt.substring(3));
			} else {
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Amt1", "");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Amt2", "");
			}
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].AccptInstBrc", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].AuthNo", "");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntTpId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].MctMcc");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Flag2", "7");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntNo",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Mid");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyerInf[0].IDTp",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].IdType");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyerInf[0].IDNo",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].IdNo");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].BizTp",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].BusiSysType");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].RskInf[0].devId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].TranTermNo");
			//EPOper.copy(tpID, tpID, "fmt_CUP_SVR_OUT[0].MsgHeader[0].Trxtyp",
			//		"fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].TermSysType");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].TermSysType", "06");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].TranSysType", "14"); //TODO
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 发送核心系统报文 退款
	 */
	public static int HostS805016ByCup(String tpID) throws Exception {
		try {
			/*
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801053_Req[0].Flag1", "4");
			EPOper.copy(tpID, tpID, "T_NCP_BOOK_HIST[0].PAYEE_ACCT_NO",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].AcctNo1");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Ccy1", "01");
			*/
			//退货 上送贷方账号
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Flag1", "7");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].AcctNo1", "0000000000000000000000");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Flag2", "4");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].AcctNo2");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Ccy1", "01");// 01人民币
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Ccy2", "01");// 01人民币
			String szAmt = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxAmt");
			// 银联金额格式CNY100.00
			if (!StringTool.isNullOrEmpty(szAmt) && szAmt.length() >= 3) {
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Amt1", szAmt.substring(3));
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Amt2", szAmt.substring(3));
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Amount", szAmt.substring(3));
			} else {
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Amt1", "");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Amt2", "");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Amount", "");
			}
			EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_NO", "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Srvstan");
			EPOper.copy(tpID, tpID, "T_NCP_BOOK_HIST[0].HOST_SEQ",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].AssSeqNo");
			EPOper.copy(tpID, tpID, "T_NCP_BOOK_HIST[0].HOST_DATE",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].AccTranDate");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntTpId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].MctMcc");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].BizTp",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].BusiSysType");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].RskInf[0].devId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].TranTermNo");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].TermSysType", "06");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].TranSysType", "17"); //TODO
			//交易日期时间
			String szPlatDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
			String TermTime = PubTool.getTime();
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].TranDateTime", szPlatDate.substring(4, 6)+szPlatDate.substring(6, 8)+TermTime);
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 发送核心系统报文 贷记付款
	 */
	public static int HostS801003ByCup(String tpID) throws Exception {
		try {
			/*
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Flag1", "4");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].AcctNo1");
			*/
			//贷记付款需上送贷方账号
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Flag1", "7");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].AcctNo1", "0000000000000000000000");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Flag2", "4");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].AcctNo2");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].AcctNo2", 
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].PyeeInf[0].PyeeAcctId"); //返回银联收款方账户
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].AcctNo2",
					"T_NCP_BOOK[0].PAYEE_ACCT_NO");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Ccy1", "01");// 01人民币
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Ccy2", "01");// 01人民币

			String szAmt = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxAmt");
			// 银联金额格式CNY100.00
			if (!StringTool.isNullOrEmpty(szAmt) && szAmt.length() >= 3) {
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Amt1", szAmt.substring(3));
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Amt2", szAmt.substring(3));
			} else {
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Amt1", "");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Amt2", "");
			}
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].SettlmtDt",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].ClearDate");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntTpId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].MctMcc");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].BizTp",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].BusiSysType");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].RskInf[0].devId",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].TranTermNo");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].TermSysType", "06");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].TranSysType", "14"); //TODO
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].FeePayAcctName1", "manual");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].Type", "1");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyeeInf[0].PyeeNm",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].FeePayAcctName4"); //户名
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyeeInf[0].IDNo",
					"fmt_CUP_SVR_IN[0].HOST_CLI_S801003_Req[0].FeePayAcctName3");//证件号
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月21日
	 * 
	 * @version 1.0 发送贷记系统报文 查询类交易
	 */
	public static int msgBody030517() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030517_Req[0].CARDNO",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].RcverAcctId"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030517_Req[0].TranDateTime",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].SndDt"));
			// EPOper.put(tpID,
			// "fmt_CUP_SVR_IN[0].CREDIT_CLI_030517_Req[0].PIN", ""));
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月21日
	 * 
	 * @version 1.0 发送贷记系统报文 消费类交易
	 */
	public static int msgBody030105() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].TranCode", "030105");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].CardNo",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyerInf[0].PyerAcctId"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].TrsAmt",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxAmt"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].DevType", "C1");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].CrdTranType", "P02");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].ChkTrkFlg", "0");
			// EPOper.put(tpID,
			// "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].Track2", "");
			// EPOper.put(tpID,
			// "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].Track3", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].Ccy", "01");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].Passwd", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].AcctName",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].PyerInf[0].PyerNm"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].ProfitBrc", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].TranDateTime",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].SndDt"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].F55", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].CardSeqNo", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].Brc2", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].MctMcc",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntTpId"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].Mid",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntNo"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].AccptNameAddr",
					EPOper.get(tpID, "fmt_CUP_SVR_IN.Req_Body[0].MrchntInf[0].MrchntPltfrmNm"));
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].GoodsNo", "");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].POSCondCode", "");
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月21日
	 * 
	 * @version 1.0 发送贷记系统报文 消费冲正交易
	 */
	public static int msgBody212006() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].TranCode", "212006");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].CardNo",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].CardNo"));// 原交易账号
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].TrsAmt",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].TrsAmt"));// 原交易金额
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].OrgDateTime",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].TranDateTime"));// 原交易时间
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].OrigDevStan",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_030105_Req[0].TermSeq"));// 原交易流水号
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].SEQ_NO",
					EPOper.get(tpID, "T_NCP_BOOK[0].SEQ_NO"));// 原平台流水号
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].CREDIT_CLI_212006_Req[0].PLAT_DATE",
					EPOper.get(tpID, "T_NCP_BOOK[0].PLAT_DATE"));// 原交易平台日期
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月30日
	 * 
	 * @version 1.0 发送借记记系统报文 消费冲正交易
	 */
	public static int msgBody860002() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_860002_Req[0].TranCode", "860002");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_860002_Req[0].SerSeqNo1",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_860001_Req[0].TermSeq"));// 原平台流水号
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_860002_Req[0].TranDate1",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_860001_Req[0].CallDate"));// 原交易平台日期
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_860002_Req[0].Flag", "0");// 暂时写0
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月30日
	 * 
	 * @version 1.0 发送借记记系统报文 消费冲正交易
	 */
	public static int msgBody610001(String szTpid) throws Exception {
		try {
			
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月21日
	 * 
	 * @version 1.0 获取借记卡或贷记卡系统返回的响应信息和响应码
	 * 
	 */
	public static String[] gettMsgRet() throws Exception {
		String fmt = "";
		String[] value = new String[2];
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			TrcLog.log("epdata.log", EPOper.epToJSON(DtaInfo.getInstance().getTpId(), "utf-8"));
			// 目的DTA的交易码
			String svcName = (String) EPOper.get(tpID, "INIT[0].TxnCd");
			String card_type = (String) EPOper.get(tpID, "INIT[0]._CARD_TYPE");
			// 核心应答码转换为银联应答码和应答信息
			BusiPub.chanMsg(svcName);
			// 处理响应码
			if ("0".equals(card_type)) {
				fmt = "fmt_CUP_SVR_OUT[0].HOST_CLI_" + svcName + "_Rsp[0].";
			} else if ("5".equals(card_type)) {
				fmt = "fmt_CUP_SVR_OUT[0].CREDIT_CLI_" + svcName + "_Rsp[0].";
			}
			// SysPub.appLog("DEBUG", "svcName:%s,fmt:%s", svcName, fmt);
			value[0] = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd");
			value[1] = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnDesc");
			// SysPub.appLog("DEBUG", "value[0]:%s,value[1]:%s", value[0],
			// value[1]);
			if (!SysPubDef.CUP_SUC_RET.equals(value[0])) {
				EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "2");
			} else {
				EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "1");
			}
			if (SysPubDef.CUP_TIME_RET.equals(value[0])) {
				value[0] = SysPubDef.CUP_TIME_RET;
				value[1] = SysPubDef.CUP_TIME_RET;
				EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "3");
			}
			EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE", value[0]);
			EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG", value[1]);
			EPOper.put(tpID, "T_NCP_BOOK[0].HOST_DATE", EPOper.get(tpID, fmt + "TranDate"));
			EPOper.put(tpID, "T_NCP_BOOK[0].HOST_SEQ", EPOper.get(tpID, fmt + "SerSeqNo"));
		} catch (Exception e) {
			throw e;
		}
		return value;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月17日
	 * 
	 * @version 1.0 发送核心系统报文头赋值
	 */
	public static void headHost(String svcName) throws Exception {

		headHost("fmt_CUP_SVR_IN", svcName);
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月17日
	 * 
	 * @version 1.0 发送核心系统报文头赋值
	 */
	public static void headHost(String _ObjName, String _svcName) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String fmt = _ObjName + "[0].HOST_CLI_" + _svcName + "_Req[0].";
		SysPub.appLog("DEBUG", "fmt=[%s]", fmt);
		String TermTime = PubTool.getTime();
		// 内部服务码= "S" + 外部服务码
		EPOper.put(tpID, fmt + "TranCode", _svcName.substring(1));
		String szPlatDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
		SysPub.appLog("DEBUG", "szPlatDate=[%s]", szPlatDate);
		String szFmtDate = szPlatDate.substring(0, 4) + "-" + szPlatDate.substring(4, 6) + "-"
				+ szPlatDate.substring(6, 8);
		SysPub.appLog("DEBUG", "szFmtDate=[%s]", szFmtDate);
		EPOper.put(tpID, fmt + "TermDate", szFmtDate );// 前置交易日期
		EPOper.put(tpID, fmt + "TermTime", TermTime);// 前置交易时间
		EPOper.copy(tpID, tpID, "INIT[0].BrchNo", fmt + "Brc");// 机构号
		EPOper.copy(tpID, tpID, "INIT[0].BrchNo", fmt + "AplyBrc");// 机构号
		EPOper.copy(tpID, tpID, "INIT[0].TlrNo", fmt + "Teller");// 柜员号
		EPOper.copy(tpID, tpID, "INIT[0].SeqNo", fmt + "TermSeq");// 平台流水号
		EPOper.put(tpID, fmt + "Desc2", "");
		//EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_NO", fmt + "FrntNo");
		EPOper.put(tpID, fmt + "FrntNo", "UNCPS001");
		EPOper.put(tpID, fmt + "AuthFlag", "0");
		EPOper.put(tpID, fmt + "CallDate", szFmtDate );// 平台日期
		EPOper.put(tpID, fmt + "TranType", "15");// TODO 需要定义
		EPOper.put(tpID, fmt + "ChannelId", "NC");// TODO 需要定义
		EPOper.put(tpID, fmt + "FeeFlag", "");
		EPOper.put(tpID, fmt + "FeeCode1", "");
		EPOper.put(tpID, fmt + "FeeCcy1", "");
		EPOper.put(tpID, fmt + "FeeAmt1", "");
		EPOper.put(tpID, fmt + "FeePayAcctNo1", "");
		EPOper.put(tpID, fmt + "FeePaySrc1", "");
		EPOper.put(tpID, fmt + "FeePayTime1", "1");
		EPOper.put(tpID, fmt + "FeeCode2", "");
		String Trxtyp = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].Trxtyp"); //交易码
		EPOper.put(tpID, fmt + "Code", Trxtyp );
		EPOper.copy(tpID, tpID, "T_TX[0].TX_NAME", fmt + "Desc2"); //交易描述
		//EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctIssrId", fmt + "FwdInsBrc"); //设备发送行机构码
		//EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId", fmt + "AcqInsBrc");//设备受理行机构码
		if(!"S215072".equals(_svcName)&&!"S818888".equals(_svcName)&&!"S801010".equals(_svcName)
			&&!"S801008".equals(_svcName)){
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].SettlmtDt", fmt + "ClearDate");//清算日期	
		}		
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月17日
	 * 
	 * @version 1.0 发送贷记卡系统报文头赋值
	 */
	public static void headCred(String svcName) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String fmt = "fmt_CUP_SVR_IN[0].CREDIT_CLI_" + svcName + "_Req[0].";
		EPOper.put(tpID, fmt + "TermId", "001");// 终端号
		int TermSeq = PubTool.sys_get_seq2();
		EPOper.put(tpID, fmt + "TermSeq", String.valueOf(TermSeq));// 终端流水号
		EPOper.put(tpID, fmt + "TranName", svcName);// 交易名称
		EPOper.put(tpID, fmt + "Brc", "0000001");// 机构码
		EPOper.put(tpID, fmt + "BrcName0", "00001");// 交易机构名称
		EPOper.put(tpID, fmt + "Teller", "00002");// 交易柜员
		EPOper.put(tpID, fmt + "TellerName", "15");// 柜员名称
	}

	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年8月27日
	 * 
	 * @version 1.0 发送短信平台请求报文头赋值
	 */
	public static void headSms(String svcName) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		//int TermSeq = PubTool.sys_get_seq2();
		String TermTime = PubTool.getTime();
		String fmt = "SndMsgIn[0].SMS_CLI_" + svcName + "_Req[0].Head.";
		
		EPOper.put(tpID, fmt + "userid", "");// 用户ID
		EPOper.copy(tpID, tpID, "INIT[0].BrchNo", fmt + "brc");// 交易机构
		EPOper.copy(tpID, tpID, "INIT[0].TlrNo", fmt + "teller");// 交易柜员
		EPOper.put(tpID, fmt + "channelid", "");// 渠道ID
		EPOper.copy(tpID, tpID, "INIT[0].SeqNo", fmt + "channelseq");// 渠道流水
		EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_DATE", fmt + "channeldate");//渠道日期
		EPOper.put(tpID, fmt + "channeltime", TermTime);// 渠道时间
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月22日
	 * 
	 * @version 1.0 发送银联系统报文头赋值 IN:输入数据对象 OUT:输出数据对象 银联公共报文赋值
	 */
	public static void putCupPubMsg(String tpID) throws Exception {
		try {
			/* 报文头 */
			// 报文版本
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].MsgVer", "fmt_CUP_SVR_OUT[0].MsgHeader[0].MsgVer");
			// 报文发起日期时间
			EPOper.copy(tpID, tpID, "INIT[0].TRAN_DATETM", "fmt_CUP_SVR_OUT[0].MsgHeader[0].SndDt");
			// 交易类型
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].Trxtyp", "fmt_CUP_SVR_OUT[0].MsgHeader[0].Trxtyp");
			// 发起方所属机构标识
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].MsgHeader[0].IssrId", SysPubDef.BRANKNO);
			// 报文方向
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].MsgHeader[0].Drctn", SysPubDef.DRCTN);
			// 签名证书序列号
			EPOper.put(tpID, "fmt_CUP_SVR_OUT.MsgHeader[0].SignSN",BusiPub.getUnionUserID());
			// 加密证书序列号
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].EncSN", "fmt_CUP_SVR_OUT[0].MsgHeader[0].EncSN");
			// 敏感信息对称加密密钥
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN.MsgHeader[0].EncKey", "fmt_CUP_SVR_OUT.MsgHeader[0].EncKey");
			// 摘要算法类型
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].MDAlgo", "fmt_CUP_SVR_OUT[0].MsgHeader[0].MDAlgo");
			// 签名和密钥加密算法类型（注）
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].SignEncAlgo",
					"fmt_CUP_SVR_OUT[0].MsgHeader[0].SignEncAlgo");
			// 对称加密算法类型
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].EncAlgo",
					"fmt_CUP_SVR_OUT[0].MsgHeader[0].EncAlgo");
			/* 业务种类 */
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizTp", "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizTp");
			/* 交易信息 */
			// 清算日期--银联文档规定不用返回？？TODO
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].SettlmtDt",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].TrxInf[0].SettlmtDt");
			// 交易流水号
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].TrxInf[0].TrxId");
			// 交易金额
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].TrxInf[0].TrxAmt");
			/* 系统响应信息 */
			// 系统返回码
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd", SysPubDef.CUP_ERR_RET);
			// 系统返回说明
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnDesc", SysPubDef.CUP_ERR_MSG);
			// 系统返回时间
			EPOper.copy(tpID, tpID, "INIT[0].TRAN_DATETM",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnTm");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联公共报文赋值方法处理异常");
			throw e;
		}
	}

	/**
	 * 接收方信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupRcverInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RcverInf[0].RcverAcctId");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RcverInf[0].RcverAcctIssrId");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RcverInf[0].MobNo");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联接收方信息赋值异常");
			throw e;
		}
	}

	/**
	 * 发起方信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupSderInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderIssrId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].SderInf[0].SderIssrId");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctIssrId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].SderInf[0].SderAcctIssrId");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctInf",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].SderInf[0].SderAcctInf");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联发起方信息赋值异常");
			throw e;
		}
	}

	/**
	 * 风险监控信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupRskInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceMode",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].deviceMode");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceLanguage",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].deviceLanguage");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].sourceIP",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].sourceIP");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].MAC",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].MAC");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].devId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].devId");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].extensiveDeviceLocation",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].extensiveDeviceLocation");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceNumber",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].deviceNumber");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceSIMNumber",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].deviceSIMNumber");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].accountIDHash",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].accountIDHash");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].riskScore",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].riskScore");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].riskReasonCode",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].riskReasonCode");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].mchntUsrRgstrTm",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].mchntUsrRgstrTm");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].mchntUsrRgstrEmail",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].mchntUsrRgstrEmail");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].rcvProvince",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].rcvProvince");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].rcvCity",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].rcvCity");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].goodsClass",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].RskInf[0].goodsClass");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联风险监控信息赋值异常");
			throw e;
		}
	}

	/**
	 * 业务响应信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupBizInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].RPFlg",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].RPFlg");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].TrxAmt");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriOrdrId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].OriOrdrId");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联业务响应信息赋值异常");
			throw e;
		}
	}

	/**
	 * 渠道方信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupChannelIssrInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].ChannelIssrInf[0].SgnNo");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联渠道方信息赋值异常");
			throw e;
		}
	}

	/**
	 * 付款方/接收方信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupPyerInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].PyerInf[0].PyerAcctId");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联付款方/接收方信息赋值异常");
			throw e;
		}
	}

	/**
	 * 订单信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupOrdrInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OrdrInf[0].OrdrId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].OrdrInf[0].OrdrId");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联订单信息赋值异常");
			throw e;
		}
	}

	/**
	 * 收款方/接收方信息标记赋值
	 * 
	 * @param tpID
	 * @throws Exception
	 */
	public static void putCupPyeeInfMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctId",
					"fmt_CUP_SVR_OUT[0].Rsp_Body[0].PyeeInf[0].PyeeAcctId");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联订单信息赋值异常");
			throw e;
		}
	}

	/**
	 * 
	 * 管理端报文头赋值
	 * @param tpID
	 * @throws Exception
	 */
	public static void putMngHeadMsg(String tpID) throws Exception {
		try {
			EPOper.copy(tpID, tpID, "MngChkIn[0].MsgHead[0].Brc","MngChkOut[0].MsgHead[0].Brc");
			EPOper.copy(tpID, tpID, "MngChkIn[0].MsgHead[0].Teller","MngChkOut[0].MsgHead[0].Teller");
			EPOper.copy(tpID, tpID, "MngChkIn[0].MsgHead[0].TranTime","MngChkOut[0].MsgHead[0].TranTime");
			EPOper.copy(tpID, tpID, "MngChkIn[0].MsgHead[0].TranDate","MngChkOut[0].MsgHead[0].TranDate");
			EPOper.copy(tpID, tpID, "MngChkIn[0].MsgHead[0].TranSeqNo","MngChkOut[0].MsgHead[0].TranSeqNo");
			EPOper.put(tpID,"MngChkOut[0].MsgHead[0].RspCode",SysPubDef.ERR_RET);
			EPOper.put(tpID,"MngChkOut[0].MsgHead[0].RspMsg",SysPubDef.ERR_MSG);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "管理端报文头赋值");
			throw e;
		}
	}
	
	/**
	 * 无卡支付管理端协议请求报文长度处理
	 */
	public static byte[] setMngPFMTInLength(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID,"MNG_SVR_IN[0].__GDTA_ITEMDATA_LENGTH[0]");
		EPOper.put(tpID,"MNG_SVR_IN[0].__GDTA_ITEMDATA_LENGTH[0]", length+54);
		byte[] bytes = String.valueOf(length+54).getBytes();
		return bytes;
		
	}
	
	/**
	 * 无卡支付管理端协议响应报文长度处理
	 */
	public static byte[] setMngPFMTOutLength(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID,"MNG_SVR_OUT[0].__GDTA_ITEMDATA_LENGTH[0]");
		EPOper.put(tpID,"MNG_SVR_OUT[0].__GDTA_ITEMDATA_LENGTH[0]", length+54);
		byte[] bytes = String.valueOf(length+54).getBytes();
		return bytes;
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String szPlatDate="20170820";
		SysPub.testLog("INFO", "szFmtDate=[%s]", szPlatDate.substring(0, 4));
		String szFmtDate = szPlatDate.substring(0, 4) + "-" + szPlatDate.substring(4, 6) + "-"
				+ szPlatDate.substring(6, 8);
		SysPub.testLog("INFO", "szFmtDate=[%s]", szFmtDate);
	}
	
	/**
	 * 检查Sms610001发送短信报文必输字段
	 */
	public static void chkSms610001Fmt(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String mobile = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Body.mobile");//手机号
		if(mobile==null||mobile.isEmpty()){
			throw new BaseException("P10000", "必输字段[手机号]不能为空");
		}
		String msg = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Body.msg");//短信内容		
		if(msg==null||msg.isEmpty()){
			throw new BaseException("P10000", "必输字段[短信内容]不能为空");
		}
		String userid = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.userid");//用户id	
		if(userid==null||userid.isEmpty()){
			throw new BaseException("P10000", "必输字段[用户id]不能为空");
		}
		String brc = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.brc");//交易机构
		if(brc==null||brc.isEmpty()){
			throw new BaseException("P10000", "必输字段[交易机构]不能为空");
		}
		String teller = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.teller");//交易柜员		
		if(teller==null||teller.isEmpty()){
			throw new BaseException("P10000", "必输字段[交易柜员]不能为空");
		}
		String channelid = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.channelid");//渠道id		
		if(channelid==null||channelid.isEmpty()){
			throw new BaseException("P10000", "必输字段[渠道id]不能为空");
		}
		String channelseq = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.channelseq");//渠道流水		
		if(channelseq==null||channelseq.isEmpty()){
			throw new BaseException("P10000", "必输字段[渠道流水]不能为空");
		}
		String channeldate = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.channeldate");//渠道日期	
		if(channeldate==null||channeldate.isEmpty()){
			throw new BaseException("P10000", "必输字段[渠道日期]不能为空");
		}
		String channeltime = (String) EPOper.get(tpID,"SMS_CLI_S610001_IN.Head.channeltime");//渠道时间		
		if(channeltime==null||channeltime.isEmpty()){
			throw new BaseException("P10000", "必输字段[渠道时间]不能为空");
		}
		
	}
	
	/**短信客户端
	 * 获取错误码和错误信息
	 */
	public static void getSmsErrMsg(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		//获取内部服务码
		String svcName = dtaInfo.getSvcName();
		String repCode = (String) EPOper.get(tpID,"SMS_CLI_"+svcName.substring(2)+"_OUT.Head.respcode");
		String repMsg = (String) EPOper.get(tpID,"SMS_CLI_"+svcName.substring(2)+"_OUT.Head.respmsg");
		EPOper.put(DtaInfo.getInstance().getTpId(),"__GDTA_FORMAT[0].__ERR_RET[0]", repCode);
		EPOper.put(DtaInfo.getInstance().getTpId(),"__GDTA_FORMAT[0].__ERR_MSG[0]", repMsg);
	}
	
	/*
	 * 
	 */
	public static boolean chkSmsRetCode(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		//获取内部服务码
		String svcName = dtaInfo.getSvcName();
		String repFmt = "SMS_CLI_"+svcName+"_OUT.Head.respcode";
		String repcode = (String) EPOper.get(tpID,repFmt);
		if(!"000000".equals(repcode)){
			return true;	
		}else{
			return false;
		}
	}
	
	/**
	 * 短信客户端协议报文长度处理
	 */
	public static byte[] setSMSPFMTLength(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID,"SMS_CLI_IN_OUT[0].__GDTA_ITEMDATA_LENGTH");
		EPOper.get(tpID,"SMS_CLI_IN_OUT[0].__GDTA_ITEMDATA_LENGTH", length+8);
		EPOper.put(tpID,"SMS_CLI_IN_OUT[0].FMT_TYPE", "RQ");
		byte[] bytes = String.valueOf(length+8).getBytes();
		return bytes;
	}

}
