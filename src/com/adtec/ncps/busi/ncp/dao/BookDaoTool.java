package com.adtec.ncps.busi.ncp.dao;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.bean.Book;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/**************************************************
 * 
 * @author dingjunbo 流水表处理类
 *
 **************************************************/
public class BookDaoTool {

	/**
	 * 更新流水表，返回插入记录数 表对象也赋值，更新的时候，直接从表对象取值更新
	 * 
	 * @return int
	 */
	public static int instBook() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		int iResult = 0;
		String tpID = dtaInfo.getTpId();
		try {
			// 先产生流水号
			BusiPub.getPlatSeq();

			// 判断是否登记流水
			String INS_JRNL = (String) EPOper.get(tpID, "T_TX[0].INS_JRNL");
			if ("N".equals(INS_JRNL))
				return 0;
			Book book = new Book();
			int iseq_no = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
			book.setSeq_no(iseq_no);
			// 保存平台流水号和平台日期，更新流水时用
			EPOper.copy(tpID, tpID, "INIT[0].SeqNo", "T_NCP_BOOK[0].SEQ_NO");

			book.setPlat_date((String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE"));
			EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_DATE", "T_NCP_BOOK[0].PLAT_DATE");

			String szSvcNa = dtaInfo.getSvcName();
			book.setTx_code(szSvcNa);
			EPOper.put(tpID, "T_NCP_BOOK[0].TX_CODE", szSvcNa);

			book.setTx_name((String) EPOper.get(tpID, "T_TX[0].TX_NAME"));
			EPOper.copy(tpID, tpID, "T_TX[0].TX_NAME", "T_NCP_BOOK[0].TX_NAME");

			// 业务类型（第三方业务编号）
			String szentr_no = (String) EPOper.get(tpID, "T_ENTR[0].ENTR_NO");
			book.setEntr_no(szentr_no);
			EPOper.put(tpID, "T_NCP_BOOK[0].ENTR_NO", szentr_no);

			// 交易渠道
			String szChnl_no = (String) EPOper.get(tpID, "T_CHANNEL[0].CHN_NO");
			book.setChnl_no(szChnl_no);
			EPOper.put(tpID, "T_NCP_BOOK[0].CHNL_NO", szChnl_no);

			String szTx_type = (String) EPOper.get(tpID, "T_TX[0].TX_TYPE");
			book.setTx_type(szTx_type);
			EPOper.put(tpID, "T_NCP_BOOK[0].TX_TYPE", szTx_type);

			// 交易信息
			if( "NC".equals(szChnl_no))
			{	
				String szOth_seq = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxId");
				book.setOth_seq(szOth_seq);
				EPOper.put(tpID, "T_NCP_BOOK[0].OTH_SEQ", szOth_seq);
	
				String szTx_seq = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxId");
				book.setTx_seq(szTx_seq);
				EPOper.put(tpID, "T_NCP_BOOK[0].TX_SEQ", szTx_seq);
	
				String szOth_date = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxDtTm");
				book.setOth_date(szOth_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].OTH_DATE", szOth_date);
	
				String szTx_date = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");
				
				book.setTx_date(szTx_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].TX_DATE", szTx_date);
	
				String szClear_date = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].SettlmtDt");
				book.setClear_date(szClear_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].CLEAR_DATE", szClear_date);
	
				String szChk_act_no = BusiPub.crtChkActNo(szSvcNa,szentr_no,szClear_date);;
				book.setChk_act_no(szChk_act_no);
				EPOper.put(tpID, "T_NCP_BOOK[0].CHK_ACT_NO", szChk_act_no);
				String szAcct_input = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].AcctInTp");
				book.setAcct_input(szAcct_input);
				EPOper.put(tpID, "T_NCP_BOOK[0].ACCT_INPUT", szAcct_input);
	
				String szTerm_type = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxTrmTp");
				book.setTerm_type(szTerm_type);
				EPOper.put(tpID, "T_NCP_BOOK[0].TERM_TYPE", szTerm_type);
	
				String szTerm_no = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxTrmNo");
				book.setTerm_no(szTerm_no);
				EPOper.put(tpID, "T_NCP_BOOK[0].TERM_NO", szTerm_no);
	
				String szRp_flag = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].RPFlg");
				book.setRp_flag(szRp_flag);
				EPOper.put(tpID, "T_NCP_BOOK[0].RP_FLAG", szRp_flag);
	
				String szTrxAmt = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt");
				// 金额格式：CNY100.00
				if (!StringTool.isNullOrEmpty(szTrxAmt) && szTrxAmt.length() >= 3) {
					book.setTx_amt(Double.valueOf(szTrxAmt.substring(3)));
					EPOper.put(tpID, "T_NCP_BOOK[0].TX_AMT", Double.valueOf(szTrxAmt.substring(3)));
				} else {
					book.setTx_amt(0.00);
					EPOper.put(tpID, "T_NCP_BOOK[0].TX_AMT", 0.00);
				}
				// 付款方信息
				String szPay_brch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctIssrId");
				if (StringTool.isNullOrEmpty(szPay_brch))
					szPay_brch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId");
				book.setPay_brch(szPay_brch);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_BRCH", szPay_brch);
	
				String szPyerAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctId");
				if (StringTool.isNullOrEmpty(szPyerAcctId))
					szPyerAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");
				book.setPay_acct_no(szPyerAcctId);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_ACCT_NO", szPyerAcctId);
	
				String szPyerAcctTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctTp");
				if (StringTool.isNullOrEmpty(szPyerAcctTp))
					szPyerAcctTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctTp");
				book.setPay_acct_type(szPyerAcctTp);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_ACCT_TYPE", szPyerAcctTp);
	
				String szPyerNm = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerNm");
				if (StringTool.isNullOrEmpty(szPyerNm))
					szPyerNm = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm");
				book.setPay_acct_name(szPyerNm);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_ACCT_NAME", szPyerNm);
	
				String szIDTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].IDTp");
				if (StringTool.isNullOrEmpty(szIDTp))
					szIDTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");
				book.setPay_cert_type(szIDTp);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_CERT_TYPE", szIDTp);
	
				String szIDNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].IDNo");
				if (StringTool.isNullOrEmpty(szIDNo))
					szIDNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");
				book.setPay_cert_no(szIDNo);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_CERT_NO", szIDNo);
	
				String szMobNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].MobNo");
				if (StringTool.isNullOrEmpty(szMobNo))
					szMobNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo");
				book.setPay_phn(szMobNo);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAY_PHN", szMobNo);
	
				String szSgnNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].SgnNo");
				if (StringTool.isNullOrEmpty(szSgnNo)) {
					szSgnNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].SgnNo");
				}
				if (StringTool.isNullOrEmpty(szSgnNo)) {
					szSgnNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo");
				}
				book.setSign_no(szSgnNo);
				EPOper.put(tpID, "T_NCP_BOOK[0].SIGN_NO", szSgnNo);
				// 收款方信息
				String szPayee_brch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctIssrId");
				book.setPayee_brch(szPayee_brch);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_BRCH", szPayee_brch);
	
				String szPayee_acct_no = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctId");
				book.setPayee_acct_no(szPayee_acct_no);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_NO", szPayee_acct_no);
	
				String szPayee_acct_type = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctTp");
				book.setPayee_acct_type(szPayee_acct_type);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_TYPE", szPayee_acct_type);
	
				String szPayee_acct_name = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeNm");
				book.setPayee_acct_name(szPayee_acct_name);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_NAME", szPayee_acct_name);
	
				String szPayee_cert_type = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].IDTp");
				book.setPayee_cert_type(szPayee_cert_type);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_CERT_TYPE", szPayee_cert_type);
	
				String szPayee_cert_no = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].IDNo");
				book.setPayee_cert_no(szPayee_cert_no);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_CERT_NO", szPayee_cert_no);
	
				String szPayee_area = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAreaNo");
				book.setPayee_area(szPayee_area);
				EPOper.put(tpID, "T_NCP_BOOK[0].PAYEE_AREA", szPayee_area);
	
				// 发起方信息
				String szSndBrch = "";
				String szSndAcctBrch = "";
				if ("SACCT0021001".equals(szSvcNa) || "SACCT0021002".equals(szSvcNa) || "SACCT0021003".equals(szSvcNa)
						|| "SQRY00023001".equals(szSvcNa)) {
					//SysPub.appLog("DEBUG", "szSvcNa[%s]", szSvcNa);
					// 协议支付，直接支付，借记转账，入账通知 发起机构为收款方机构标识
					szSndBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeIssrId");
					szSndAcctBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctIssrId");
				} else if ("SACCT0022001".equals(szSvcNa) || "SACCT0021101".equals(szSvcNa)) {
					//SysPub.appLog("DEBUG", "szSvcNa[%s]", szSvcNa);
					// 贷记付款，退货 发起机构为付款方机构标识
					szSndBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyeeIssrId");
					szSndAcctBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyerAcctIssrId");
				} else {
					//SysPub.appLog("DEBUG", "szSvcNa[%s]", szSvcNa);
					// 其他为发起机构标识
					szSndBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderIssrId");
					szSndAcctBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctIssrId");
				}
				//SysPub.appLog("DEBUG", "szSndBrch=[%s]szSndAcctBrch=[%s]", szSndBrch, szSndAcctBrch);
				book.setSnd_brch_no(szSndBrch);
				EPOper.put(tpID, "T_NCP_BOOK[0].SND_BRCH_NO", szSndBrch);
				book.setSnd_acct_brch(szSndAcctBrch);
				EPOper.put(tpID, "T_NCP_BOOK[0].SND_ACCT_BRCH", szSndAcctBrch);
	
				// 交易机构
				String szBrchNo = (String) EPOper.get(tpID, "INIT[0].BrchNo");
				EPOper.copy(tpID, tpID, "INIT[0].BrchNo", "T_NCP_BOOK[0].BRCH_NO");
				book.setBrch_no(szBrchNo);
				// 交易柜员
				String szteller_no = (String) EPOper.get(tpID, "INIT[0].TlrNo");
				EPOper.copy(tpID, tpID, "INIT[0].TlrNo", "T_NCP_BOOK[0].TELLER_NO");
				book.setTeller_no(szteller_no);
				// 产品信息
				String szProductTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ProductInf[0].ProductTp");
				if (StringTool.isNullOrEmpty(szProductTp))
					szProductTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].ProductTp");
				book.setProduct_type(szProductTp);
				EPOper.put(tpID, "T_NCP_BOOK[0].PRODUCT_TYPE", szProductTp);
				// 订单信息
				String szOrder_no = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OrdrInf[0].OrdrId");
				book.setOrder_no(szOrder_no);
				EPOper.put(tpID, "T_NCP_BOOK[0].ORDER_NO", szOrder_no);
				// 原交易信息
				String szOri_oth_seq = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriTrxId");
				book.setOri_oth_seq(szOri_oth_seq);
				EPOper.put(tpID, "T_NCP_BOOK[0].ORI_OTH_SEQ", szOri_oth_seq);
	
				String OriTrxAmt = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriTrxAmt");
				if (!StringTool.isNullOrEmpty(OriTrxAmt) && OriTrxAmt.length() >= 3) {
					book.setOri_tx_amt(Double.valueOf(OriTrxAmt.substring(3)));
					EPOper.put(tpID, "T_NCP_BOOK[0].ORI_TX_AMT", Double.valueOf(OriTrxAmt.substring(3)));
				}
	
				String szOri_order_no = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriOrdrId");
				book.setOri_order_no(szOri_order_no);
				EPOper.put(tpID, "T_NCP_BOOK[0].ORI_ORDER_NO", szOri_order_no);
	
				String szOri_tx_date = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriTrxDtTm");
				book.setOri_tx_date(szOri_tx_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].ORI_TX_DATE", szOri_tx_date);
				// 业务响应信息
				String szAcct_lvl = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizInf[0].AcctLvl");
				book.setAcct_lvl(szAcct_lvl);
				EPOper.put(tpID, "T_NCP_BOOK[0].ACCT_LVL", szAcct_lvl);
	
				String szChk_stat = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizInf[0].ChkStat");
				book.setChk_stat(szChk_stat);
				EPOper.put(tpID, "T_NCP_BOOK[0].CHK_STAT", szChk_stat);
	
				book.setStat("0");
				EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "0");
	
				String szBusi_type = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].BizTp");
				book.setBusi_type(szBusi_type);
				EPOper.put(tpID, "T_NCP_BOOK[0].BUSI_TYPE", szBusi_type);
	
				//book.setChk_act_no(""); 之前已赋值，不置空
				//EPOper.put(tpID, "T_NCP_BOOK[0].CHK_ACT_NO", "");
	
				String szSnd_time = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].SndDt");
				book.setSnd_time(szSnd_time);
				EPOper.put(tpID, "T_NCP_BOOK[0].SND_TIME", szSnd_time);
	
				String szSnd_brch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].CupHttpHead[0].OriIssrId");
				book.setSnd_brch(szSnd_brch);
				EPOper.put(tpID, "T_NCP_BOOK[0].SND_BRCH", szSnd_brch);
			}
			else if( "12".equals(szChnl_no)) // 柜面渠道
			{
				// 交易时间
				String szTx_date = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");		
				book.setTx_date(szTx_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].TX_DATE", szTx_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].SND_TIME", szTx_date);
				EPOper.put(tpID, "T_NCP_BOOK[0].OTH_DATE", szTx_date);
				
				// 交易机构
				String szBrchNo = (String) EPOper.get(tpID, "INIT[0].BrchNo");
				EPOper.copy(tpID, tpID, "INIT[0].BrchNo", "T_NCP_BOOK[0].BRCH_NO");
				book.setBrch_no(szBrchNo);
				// 交易柜员
				String szteller_no = (String) EPOper.get(tpID, "INIT[0].TlrNo");
				EPOper.copy(tpID, tpID, "INIT[0].TlrNo", "T_NCP_BOOK[0].TELLER_NO");
				book.setTeller_no(szteller_no);
				
				EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "0");
			}
			// 插入流水
			iResult = BookDao.insert(book);
			if (iResult <= 0) {
				SysPub.appLog("ERROR", "插入t_ncp_book表失败");
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "插入t_ncp_book表失败");
			e.printStackTrace();
			throw e;
		}
		return iResult;
	}

	/**
	 * 根据平台流水号和平台日期查询原流水信息，更新的内容需要放入表对象:t_ncp_book
	 * 
	 * @return int
	 * @throws Exception
	 */
	public static int uptBook() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		int iResult = 0;
		String tpID = dtaInfo.getTpId();
		Book book = new Book();
		try {
			String INS_JRNL = (String) EPOper.get(tpID, "T_TX[0].INS_JRNL");
			if ("N".equals(INS_JRNL))
				return 0;
			String szPlat_date = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PLAT_DATE");
			Integer iSeq_no = (Integer) EPOper.get(tpID, "T_NCP_BOOK[0].SEQ_NO");
			if (szPlat_date == null || iSeq_no == null)
				return 0;
			book.setSeq_no(iSeq_no);
			book.setPlat_date(szPlat_date);
			String szSvcNa = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TX_CODE");
			book.setTx_code(szSvcNa);

			String szTxName = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TX_NAME");
			book.setTx_name(szTxName);

			// 业务类型（第三方业务编号）
			String szentr_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ENTR_NO");
			book.setEntr_no(szentr_no);

			// 交易渠道
			String szChnl_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].CHNL_NO");
			book.setChnl_no(szChnl_no);

			String szTx_type = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TX_TYPE");
			book.setTx_type(szTx_type);

			// 交易信息
			String szOth_seq = (String) EPOper.get(tpID, "T_NCP_BOOK[0].OTH_SEQ");
			book.setOth_seq(szOth_seq);

			String szTx_seq = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TX_SEQ");
			book.setTx_seq(szTx_seq);

			String szOth_date = (String) EPOper.get(tpID, "T_NCP_BOOK[0].OTH_DATE");
			book.setOth_date(szOth_date);

			String szTx_date = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TX_DATE");
			book.setTx_date(szTx_date);

			String szClear_date = (String) EPOper.get(tpID, "T_NCP_BOOK[0].CLEAR_DATE");
			book.setClear_date(szClear_date);

			String szAcct_input = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ACCT_INPUT");
			book.setAcct_input(szAcct_input);

			String szTerm_type = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TERM_TYPE");
			book.setTerm_type(szTerm_type);

			String szTerm_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TERM_NO");
			book.setTerm_no(szTerm_no);

			String szRp_flag = (String) EPOper.get(tpID, "T_NCP_BOOK[0].RP_FLAG");
			book.setRp_flag(szRp_flag);

			Double szTrxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].TX_AMT");
			if (szTrxAmt != null)
				book.setTx_amt(szTrxAmt);

			String szPay_brch = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_BRCH");
			book.setPay_brch(szPay_brch);

			String szPyerAcctId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_NO");
			book.setPay_acct_no(szPyerAcctId);

			String szPyerAcctTp = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_TYPE");
			book.setPay_acct_type(szPyerAcctTp);

			String szPyerNm = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_NAME");
			book.setPay_acct_name(szPyerNm);

			String szIDTp = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_CERT_TYPE");
			book.setPay_cert_type(szIDTp);

			String szIDNo = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_CERT_NO");
			book.setPay_cert_no(szIDNo);

			String szMobNo = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_PHN");
			book.setPay_phn(szMobNo);

			String szSgnNo = (String) EPOper.get(tpID, "T_NCP_BOOK[0].SIGN_NO");
			book.setSign_no(szSgnNo);

			String szPyeeIssrId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].SND_BRCH_NO");
			book.setSnd_brch_no(szPyeeIssrId);

			String szSnd_brch = (String) EPOper.get(tpID, "T_NCP_BOOK[0].SND_BRCH");
			book.setSnd_brch(szSnd_brch);

			String szPayee_brch = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_BRCH");
			book.setPayee_brch(szPayee_brch);

			String szPayee_acct_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_NO");
			book.setPayee_acct_no(szPayee_acct_no);

			String szPayee_acct_type = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_TYPE");
			book.setPayee_acct_type(szPayee_acct_type);

			String szPayee_acct_name = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_ACCT_NAME");
			book.setPayee_acct_name(szPayee_acct_name);

			String szPayee_cert_type = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_CERT_TYPE");
			book.setPayee_acct_type(szPayee_cert_type);

			String szPayee_cert_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_CERT_NO");
			book.setPayee_cert_no(szPayee_cert_no);

			String szPayee_area = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAYEE_AREA");
			book.setPayee_area(szPayee_area);

			// 发起方信息
			String szSnd_acct_brch = (String) EPOper.get(tpID, "T_NCP_BOOK[0].SND_ACCT_BRCH");
			book.setSnd_acct_brch(szSnd_acct_brch);

			String szProductTp = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PRODUCT_TYPE");
			book.setProduct_type(szProductTp);

			// 订单信息
			String szOrder_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ORDER_NO");
			book.setOrder_no(szOrder_no);

			String szOri_oth_seq = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ORI_OTH_SEQ");
			book.setOri_oth_seq(szOri_oth_seq);

			Double OriTrxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].ORI_TX_AMT");
			if (OriTrxAmt != null)
				book.setOri_tx_amt(OriTrxAmt);

			String szOri_order_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ORI_ORDER_NO");
			book.setOri_order_no(szOri_order_no);

			String szOri_tx_date = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ORI_TX_DATE");
			book.setOri_tx_date(szOri_tx_date);

			// 业务响应信息
			String szAcct_lvl = (String) EPOper.get(tpID, "T_NCP_BOOK[0].ACCT_LVL");
			book.setAcct_lvl(szAcct_lvl);

			String szChk_stat = (String) EPOper.get(tpID, "T_NCP_BOOK[0].CHK_STAT");
			book.setChk_stat(szChk_stat);

			String szBusi_type = (String) EPOper.get(tpID, "T_NCP_BOOK[0].BUSI_TYPE");
			book.setBusi_type(szBusi_type);

			// 柜员
			String szTeller_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].TELLER_NO");
			book.setTeller_no(szTeller_no);
			// 机构
			String szBrch_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].BRCH_NO");
			book.setBrch_no(szBrch_no);

			String szChk_act_no = (String) EPOper.get(tpID, "T_NCP_BOOK[0].CHK_ACT_NO");
			book.setChk_act_no(szChk_act_no);

			String szSnd_time = (String) EPOper.get(tpID, "T_NCP_BOOK[0].SND_TIME");
			book.setSnd_time(szSnd_time);

			book.setStat((String) EPOper.get(tpID, "T_NCP_BOOK[0].STAT"));
			book.setRet_code((String) EPOper.get(tpID, "T_NCP_BOOK[0].RET_CODE"));
			book.setRet_msg((String) EPOper.get(tpID, "T_NCP_BOOK[0].RET_MSG"));
			book.setHost_msg((String) EPOper.get(tpID, "T_NCP_BOOK[0].HOST_MSG"));
			// 返回时间取机器时间 TODO 请注意 这个地方会对返回报文的返回时间赋值
			String szRetTime = PubTool.getDate("yyyy-MM-dd'T'HH:mm:ss");
			book.setRet_time(szRetTime);
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnTm", szRetTime);

			String szBegTmMs = (String) EPOper.get(tpID, "INIT[0].TRAN_MS");
			String szEndTmMs = PubTool.getDate("HH:mm:ss.SSS");
			long lIntSec = PubTool.subDateMs(szEndTmMs, szBegTmMs, "HH:mm:ss.SSS");
			SysPub.appLog("DEBUG", "[%s][%s]", szBegTmMs, szEndTmMs);
			// 交易耗时
			book.setTime_sec((int) lIntSec);
			SysPub.appLog("INFO", "[%s]交易耗时[%s]", szSvcNa, lIntSec);

			book.setOpen_brch((String) EPOper.get(tpID, "T_NCP_BOOK[0].OPEN_BRCH"));
			book.setHost_date((String) EPOper.get(tpID, "T_NCP_BOOK[0].HOST_DATE"));
			book.setHost_seq((String) EPOper.get(tpID, "T_NCP_BOOK[0].HOST_SEQ"));
			Double dRefund_amt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].REFUND_AMT");
			if (dRefund_amt != null)
				book.setRefund_amt(Double.valueOf(dRefund_amt));
			book.setChk_flag((String) EPOper.get(tpID, "T_NCP_BOOK[0].CHK_FLAG"));
			book.setChk_msg((String) EPOper.get(tpID, "T_NCP_BOOK[0].CHK_MSG"));
			Double dAmt1 = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].AMT1");
			if (dAmt1 != null)
				book.setAmt1(Double.valueOf(dAmt1));
			Double dAmt2 = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].AMT2");
			if (dAmt1 != null)
				book.setAmt2(Double.valueOf(dAmt2));
			iResult = BookDao.update(book);
			if (iResult <= 0) {
				SysPub.appLog("ERROR", "更新t_ncp_book表失败");
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "更新t_ncp_book表失败");
			e.printStackTrace();
			throw e;
		}
		return iResult;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String szSvcNa = "SACCT0021001";
		if ("SACCT0021001".equals(szSvcNa) || "SACCT0021002".equals(szSvcNa) || "SACCT0021003".equals(szSvcNa)
				|| "SQRY00023001".equals(szSvcNa)) {
			SysPub.testLog("DEBUG", "szSvcNa[%s]", szSvcNa);
		}
		if ("SACCT0022001".equals(szSvcNa) || "SACCT0021101".equals(szSvcNa)) {
			SysPub.testLog("DEBUG", "szSvcNa[%s]", szSvcNa);
		} else {
			SysPub.testLog("DEBUG", "szSvcNa[%s]", szSvcNa);
		}
	}

}
