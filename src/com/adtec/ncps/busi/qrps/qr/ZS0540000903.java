package com.adtec.ncps.busi.qrps.qr;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.qrps.QrBusiPub;
import com.adtec.ncps.busi.qrps.bean.QrBook;
import com.adtec.ncps.busi.qrps.dao.QrBookDao;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.union.sdk.DemoBase;
import com.union.sdk.SDKConstants;

/**
 * @ClassName: ZS0540000903
 * @Description: 查询收款状态
 * @author Q
 * @date 2018年1月4日上午11:26:41
 *
 */
public class ZS0540000903 {

    public static int deal() throws Exception {
	SysPub.appLog("INFO", "开始ZS0540000903业务处理");
	
	QrBook qrBook = new QrBook();
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();
	
	int ret = 0;

	// 先产生流水号
    BusiPub.getPlatSeq();
    int iseq_no = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
    String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
    
	String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
	svcName = svcName.toUpperCase();
	
	String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
	String cltReq = "OBJ_QRUP_ALL";
	String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";
	String cltRes = "OBJ_QRUP_ALL";
	
	EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);
	
	SysPub.appLog("INFO", "复制svrReq成功");
	
	// 根据订单号查询收款通知
	String orderNo = (String) EPOper.get(tpID, svrReq+"[0].cd[0].orderNo");
	String orderTime = (String) EPOper.get(tpID, svrReq+"[0].cd[0].orderTime");
	String voucherNum = (String) EPOper.get(tpID, svrReq+"[0].cd[0].voucherNum");
	if(!StringUtils.isEmpty(orderNo)&& !StringUtils.isEmpty(orderTime))
	{
		ret = QrBusiPub.queryBookbyOrderno(orderNo,orderTime, "0530000903");
	}else{
		ret = QrBusiPub.queryBookbyvoucherNum(voucherNum, "0530000903");
	}
	SysPub.appLog("INFO", "查询是否是有ret的状态："+ret);
	
	    if (0 == ret) {
		// 没有查到收款通知，则去银联查询订单状态
//		EPOper.put(tpID, "T_QRP_BOOK[0].TXN_NO", txnNo);
    	// 组织报文发送到银联
    	sendUP( tpID,  cltReq,  svrReq);	
    	ret = uptOrigQrBook(svrReq,cltRes);
    	
    	ret = recvUP( tpID,  qrBook,  platDate,  iseq_no,  cltRes,  svrRes);
    	if (ret != 0) {
    	    return -1;
    	}	
    	
    	if (ret != 0) {
    	    SysPub.appLog("ERROR", "修改数据库表失败");
    	    return -1;
    	} else {
    	    SysPub.appLog("INFO", "修改流水表成功");
    	}	
	    	
		SysPub.appLog("INFO", "没有查到原付款交易");
	    } else if(1 == ret) {
	    	//往流水表中插入数据
	    	insertBOOK();
	    	
	    	EPOper.put(tpID, svrRes+"[0].cd[0].acqCode", (String) EPOper.get(tpID, "T_QRP_BOOK[0].CERT_ID"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].certId", (String) EPOper.get(tpID, "T_QRP_BOOK[0].ACQ_CODE"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].comInfo", (String) EPOper.get(tpID, "T_QRP_BOOK[0].COM_INFO"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].currencyCode", (String) EPOper.get(tpID, "T_QRP_BOOK[0].CURRENCY_CODE"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].orderNo", (String) EPOper.get(tpID, "T_QRP_BOOK[0].ORDER_NO"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].orderTime", (String) EPOper.get(tpID, "T_QRP_BOOK[0].ORDER_TIME"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].origRespMsg", (String) EPOper.get(tpID, "T_QRP_BOOK[0].RESP_MSG"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].origRespCode", (String) EPOper.get(tpID, "T_QRP_BOOK[0].RESP_CODE"));
	    	
	    	HashMap<String, String> contentData = new HashMap<String, String>();
			contentData.put("accNo", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ACC_NO"));
			contentData.put("name", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_NAME"));
			contentData.put("payerBankInfo", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_PAYER_BANK_INFO"));
			contentData.put("issCode", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ISS_CODE"));
			contentData.put("acctClass", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ACCT_CLASS").toString());
			contentData.put("certifTp", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CERTIF_TP"));
			contentData.put("cvn2", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CVN2"));
			contentData.put("expired", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_EXPIRED"));
			contentData.put("certifId", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CERTIF_ID"));
			contentData.put("cardAttr", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CARD_ATTR"));
			contentData.put("mobile", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_MOBILE"));
			String payerInfo = DemoBase.getAddnCond(contentData, "UTF-8");
	    	EPOper.put(tpID, svrRes+"[0].cd[0].payerInfo", payerInfo);
	    	
	    	EPOper.put(tpID, svrRes+"[0].cd[0].reqType", "0540000903");
	    	EPOper.put(tpID, svrRes+"[0].cd[0].respMsg", (String) EPOper.get(tpID, "T_QRP_BOOK[0].RESP_MSG"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].respCode", (String) EPOper.get(tpID, "T_QRP_BOOK[0].RESP_CODE"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].settleDate",(String) EPOper.get(tpID, "T_QRP_BOOK[0].SETTLE_DATE"));
	    	EPOper.put(tpID, svrRes+"[0].cd[0].settleKey", (String) EPOper.get(tpID, "T_QRP_BOOK[0].SETTLE_KEY"));
	    	
	    	EPOper.put(tpID, svrRes+"[0].cd[0].issCode", (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ISS_CODE"));
	    	
	    	EPOper.put(tpID, svrRes+"[0].cd[0].txnAmt", (String) EPOper.get(tpID, "T_QRP_BOOK[0].TXN_AMT").toString());
	    	EPOper.put(tpID, svrRes+"[0].cd[0].version", "1.0.0");
	    	EPOper.put(tpID, svrRes+"[0].cd[0].voucherNum", (String) EPOper.get(tpID, "T_QRP_BOOK[0].VOUCHER_NUM"));
	    	
	    	
	    	
	    }
	
	
	
	
	
	
	

	
	

//	// 修改原订单的信息
//	ret = uptOldQrNoInfo();
//	if (ret != 1) {
//	    SysPub.appLog("ERROR", "修改数据库表失败");
//	    return -1;
//	} else {
//	    SysPub.appLog("INFO", "修改原付款数据成功");
//	}

	return 0;
    }
    /**
     * 处理银联返回结果
     * 
     * **/
    private static int recvUP(String tpID, QrBook qrBook, String platDate, int iseq_no, String cltRes, String svrRes) throws Exception {
    	// 非对象类型赋值
	    QrBusiPub.qrBookData(tpID, qrBook, platDate, iseq_no);
	    
    	
    	EPOper.delete(tpID, cltRes);
    	EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
    	
    	String respCode = (String) EPOper.get(tpID, cltRes+"[0].respCode");
    	EPOper.put(tpID, svrRes+"[0].tx_code", "qr0540");
    	
    	if("00".equals(respCode)) {
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].respCode","00");
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].respMsg", (String) EPOper.get(tpID, cltRes+"[0].respMsg"));
 	    	
 	    	
 	    	EPOper.put(tpID, svrRes+"[0].tx_code", "qr0540");
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].acqCode", (String) EPOper.get(tpID,cltRes+"[0].acqCode"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].certId", (String) EPOper.get(tpID, cltRes+"[0].certId"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].comInfo", (String) EPOper.get(tpID, cltRes+"[0].comInfo"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].currencyCode", (String) EPOper.get(tpID, cltRes+"[0].currencyCode"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].orderNo", (String) EPOper.get(tpID, cltRes+"[0].orderNo"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].orderTime", (String) EPOper.get(tpID, cltRes+"[0].orderTime"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].origRespMsg", (String) EPOper.get(tpID, cltRes+"[0].origRespMsg"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].origRespCode", (String) EPOper.get(tpID, cltRes+"[0].origRespCode"));
 	    	
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].payerInfo", (String) EPOper.get(tpID, cltRes+"[0].payerInfo"));
 	    	
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].reqType", "0540000903");
 	    	
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].settleDate",(String) EPOper.get(tpID, cltRes+"[0].settleDate"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].settleKey", (String) EPOper.get(tpID, cltRes+"[0].settleKey"));
 	    	
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].issCode", (String) EPOper.get(tpID, cltRes+"[0].issCode"));
 	    	
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].txnAmt", (String) EPOper.get(tpID, cltRes+"[0].txnAmt"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].version", "1.0.0");
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].voucherNum", (String) EPOper.get(tpID, cltRes+"[0].voucherNum"));
 	    }else {
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].respCode", (String) EPOper.get(tpID, cltRes+"[0].respCode"));
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].respMsg", (String) EPOper.get(tpID, cltRes+"[0].respMsg"));
 	    	EPOper.put(tpID, svrRes+"[0].tx_code", "qr0540");
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].reqType", "0540000903");
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].version", "1.0.0");
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].acqCode", (String) EPOper.get(tpID,cltRes+"[0].acqCode"));
 	    }
 	    EPOper.copy(tpID, tpID,svrRes, "OBJ_ALA_abstarct_RES[0].res");
 	    
 	    if (!"00".equals(respCode)) {
     	    SysPub.appLog("ERROR", "银联服务0540000903返回失败：%s-%s", respCode,
     		    (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].respMsg"));
     	    return -1;
     	}
    	
    	
    	
    	
    	
    	
    	
    	
		return 0;
	}

	public static int uptOldQrNoInfo() throws Exception {

	// 获取数据源
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String szTpID = dtaInfo.getTpId();

	int ret = 0;

	try {
	    String szSql = "update t_qrp_book set stat = ?,voucher_num = ?, settle_key = ?, settle_date = ?, "
		    + "com_info = ?,resp_code = ?, resp_msg = ? " + "where txn_no = ? and req_type = ? ";

	    String reqType = "0120000903";
	    String txnNo = (String) EPOper.get(szTpID, "T_QRP_BOOK[0].TXN_NO");

	    // 付款成功信息
	    String voucherNum = (String) EPOper.get(szTpID, "OBJ_QRUP_ALL[0].voucherNum");
	    String settleKey = (String) EPOper.get(szTpID, "OBJ_QRUP_ALL[0].settleKey");
	    String settleDate = (String) EPOper.get(szTpID, "OBJ_QRUP_ALL[0].settleDate");
	    String comInfo = (String) EPOper.get(szTpID, "OBJ_QRUP_ALL[0].comInfo");

	    String respCode = (String) EPOper.get(szTpID, "OBJ_QRUP_ALL[0].origRespCode");
	    String respMsg = (String) EPOper.get(szTpID, "OBJ_QRUP_ALL[0].origRespMsg");

	    String stat = "000";
	    if ("00".equals(respCode)) {
		stat = "400";
	    } else {
		stat = "401";
	    }

	    Object[] value = { stat, voucherNum, settleKey, settleDate, comInfo, respCode, respMsg, txnNo, reqType };
	    ret = DataBaseUtils.execute(szSql, value);
	    if (ret == 0) {
		SysPub.appLog("ERROR", "更新原纪录失败");
	    }
	} catch (Exception e) {
	    throw e;
	}
	return ret;

    }
	/**
	 * 保存流水数据
	 * 
	 * **/
    public static int uptOrigQrBook(String svrReq,String cltRes ) throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();
	int iResult = 0;
	String szSql;
	Object[] value = null;
	int seqNo = 0;
	int set = 0;
	QrBook qrBook = new QrBook();
	try {
		String orderNo = (String) EPOper.get(tpID, svrReq+"[0].cd[0].orderNo");
		String orderTime = (String) EPOper.get(tpID, svrReq+"[0].cd[0].orderTime");
		String voucherNum = (String) EPOper.get(tpID, svrReq+"[0].cd[0].voucherNum");
		seqNo = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
		String seq = String.valueOf(seqNo);
		
		if(!StringUtils.isEmpty(orderNo)&& !StringUtils.isEmpty(orderTime))
		{
			set = QrBusiPub.queryBookbyOrderno(orderNo,orderTime, "0540000903");
		}else{
			set = QrBusiPub.queryBookbyvoucherNum(voucherNum, "0540000903");
		}
		EPOper.delete(tpID, cltRes);
    	EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
		String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
		String certId = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].certId");
		String reqType = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].reqType");
		String acqCode = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].acqCode");
		
		String payerInfo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].payerInfo");//payerInfo解出来
		if (payerInfo != null && payerInfo.length() > 0) {
			QrBusiPub.parsBase64Info2Ele(payerInfo, "payerInfo", "OBJ_QRUP_ALL");
			
		}
		String accNo = (String) EPOper.get(tpID, "PAYERINFO[0].accNo");
		String name = (String) EPOper.get(tpID, "PAYERINFO[0].name");
		String payerBankInfo = (String) EPOper.get(tpID, "PAYERINFO[0].payerBankInfo");
		String issCode = (String) EPOper.get(tpID, "PAYERINFO[0].issCode");
		String acctClass = (String) EPOper.get(tpID, "PAYERINFO[0].acctClass");
		String certifTp = (String) EPOper.get(tpID, "PAYERINFO[0].certifTp");
		String certifId = (String) EPOper.get(tpID, "PAYERINFO[0].certifId");
		String cvn2 = (String) EPOper.get(tpID, "PAYERINFO[0].cvn2");
		String expired = (String) EPOper.get(tpID, "PAYERINFO[0].expired");
		String cardAttr = (String) EPOper.get(tpID, "PAYERINFO[0].cardAttr");
		String mobile = (String) EPOper.get(tpID, "PAYERINFO[0].mobile");
		
		String invoiceInfo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].invoiceInfo");//发票信息
		if (invoiceInfo != null && invoiceInfo.length() > 0) {
			QrBusiPub.parsBase64Info2Ele(invoiceInfo, "invoiceInfo", "OBJ_QRUP_ALL");
			
		}
		String id = (String) EPOper.get(tpID, "INVOICEINFO[0].id");
		String amount = (String) EPOper.get(tpID, "INVOICEINFO[0].amount");
		
		 // 返回信息
	    String voucherNum1 = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].voucherNum");
	    String orderNo1 = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].orderNo");
	    String orderTime1 = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].orderTime");
	    String txnAmt = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].txnAmt");
	    String currencyCode = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].currencyCode");
	    
	    
	    String settleDate = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].settleDate");
	    String settleKey = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].settleKey");
	    String comInfo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].comInfo");
	    String payerComments = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].payerComments");
	    String encryptCertId = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].encryptCertId");
	    
	    String origRespCode = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].origRespCode");
	    String origRespMsg = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].origRespMsg");
	    
	    String respCode = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].respCode");
	    String respMsg = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].respMsg");
		
		if(0==set){//数据库中没有记录，插入
			
			if("00".equals(respCode)){
				QrBusiPub.qrBookData(tpID, qrBook, platDate, seqNo);
				qrBook.setQr_code(seq);
				qrBook.setSeq_no(seqNo);
				qrBook.setReq_type(reqType);
				qrBook.setAcq_code(acqCode);
				qrBook.setVoucher_num(voucherNum1);
				qrBook.setOrder_no(orderNo1);
				qrBook.setOrder_time(orderTime1);
				qrBook.setTxn_amt(Double.valueOf(txnAmt));
				qrBook.setCurrency_code(currencyCode);
				qrBook.setSettle_date(settleDate);
				qrBook.setSettle_key(settleKey);
				qrBook.setCom_info(comInfo);
				qrBook.setPayer_comments(payerComments);
				qrBook.setEncrypt_cert_id(encryptCertId);
				qrBook.setOrig_resp_code(origRespCode);
				qrBook.setOrig_resp_msg(origRespMsg);
				qrBook.setResp_code(respCode);
				qrBook.setResp_msg(respMsg);
				
				
				qrBook.setPayer_info_acc_no(accNo);
				qrBook.setPayer_info_name(name);
				qrBook.setPayer_info_payer_bank_info(payerBankInfo);
				qrBook.setPayer_info_iss_code(issCode);
				qrBook.setPayer_info_acct_class(acctClass);
				qrBook.setPayer_info_certif_tp(certifTp);
				qrBook.setPayer_info_certif_id(certifId);
				qrBook.setPayer_info_cvn2(cvn2);
				qrBook.setPayer_info_expired(expired);
				qrBook.setPayer_info_card_attr(cardAttr);
				qrBook.setPayer_info_mobile(mobile);
				
				qrBook.setInvoice_info_id(id);
				if(amount !=null){
					qrBook.setInvoice_info_amount(Double.valueOf(amount));
				}
				
				iResult = QrBookDao.insert(qrBook);
				if (iResult <= 0) {
					SysPub.appLog("ERROR", "插入t_qrp_book表失败");
				}
				
				
			}else{
				QrBusiPub.qrBookData(tpID, qrBook, platDate, seqNo);
				qrBook.setQr_code(seq);
				qrBook.setSeq_no(seqNo);
				qrBook.setReq_type(reqType);
				qrBook.setResp_code(respCode);
				qrBook.setResp_msg(respMsg);
				qrBook.setVoucher_num(voucherNum);
				qrBook.setOrder_no(orderNo);
				qrBook.setOrder_time(orderTime);
				
			}
			
		}else{//更新数据库
			if("00".equals(respCode)){
				qrBook.setQr_code(EPOper.get(tpID, "T_QRP_BOOK[0].ACQ_CODE").toString());
				qrBook.setPlat_date(EPOper.get(tpID, "T_QRP_BOOK[0].PLAT_DATE").toString());
				qrBook.setSeq_no(Long.valueOf(EPOper.get(tpID, "T_QRP_BOOK[0].SEQ_NO").toString()));
				qrBook.setReq_type(reqType);
				qrBook.setAcq_code(acqCode);
				qrBook.setVoucher_num(voucherNum1);
				qrBook.setOrder_no(orderNo1);
				qrBook.setOrder_time(orderTime1);
				qrBook.setTxn_amt(Double.valueOf(txnAmt));
				qrBook.setCurrency_code(currencyCode);
				qrBook.setSettle_date(settleDate);
				qrBook.setSettle_key(settleKey);
				qrBook.setCom_info(comInfo);
				qrBook.setPayer_comments(payerComments);
				qrBook.setEncrypt_cert_id(encryptCertId);
				qrBook.setOrig_resp_code(origRespCode);
				qrBook.setOrig_resp_msg(origRespMsg);
				qrBook.setResp_code(respCode);
				qrBook.setResp_msg(respMsg);
				
				
				qrBook.setPayer_info_acc_no(accNo);
				qrBook.setPayer_info_name(name);
				qrBook.setPayer_info_payer_bank_info(payerBankInfo);
				qrBook.setPayer_info_iss_code(issCode);
				qrBook.setPayer_info_acct_class(acctClass);
				qrBook.setPayer_info_certif_tp(certifTp);
				qrBook.setPayer_info_certif_id(certifId);
				qrBook.setPayer_info_cvn2(cvn2);
				qrBook.setPayer_info_expired(expired);
				qrBook.setPayer_info_card_attr(cardAttr);
				qrBook.setPayer_info_mobile(mobile);
				
				qrBook.setInvoice_info_id(id);
				if(amount !=null){
					qrBook.setInvoice_info_amount(Double.valueOf(amount));
				}
				iResult = QrBookDao.update(qrBook);
				if (iResult <= 0) {
					SysPub.appLog("ERROR", "插入t_qrp_book表失败");
				}
				
			}else{
				qrBook.setQr_code(EPOper.get(tpID, "T_QRP_BOOK[0].ACQ_CODE").toString());
				qrBook.setPlat_date(EPOper.get(tpID, "T_QRP_BOOK[0].PLAT_DATE").toString());
				qrBook.setSeq_no(Long.valueOf(EPOper.get(tpID, "T_QRP_BOOK[0].SEQ_NO").toString()));
				qrBook.setReq_type(reqType);
				qrBook.setResp_code(respCode);
				qrBook.setResp_msg(respMsg);
				iResult = QrBookDao.update(qrBook);
				if (iResult <= 0) {
					SysPub.appLog("ERROR", "插入t_qrp_book表失败");
				}
				
			}
			
			
			
		}
		
	} catch (Exception e) {
	    throw e;
	}

	return 0;
    }
    
    //查询到收款通知后往流水表插入收款信息
    public static int insertBOOK()throws Exception{
    	int iResult = 0;
    	DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		QrBook qrBook = new QrBook();
		try {// 先产生流水号
			BusiPub.getPlatSeq();
			int seqNo = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
			String seq = String.valueOf(seqNo);
			String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
			
			QrBusiPub.qrBookData(tpID, qrBook, platDate, seqNo);
			qrBook.setQr_code(seq);
			qrBook.setResp_code("00");
			qrBook.setResp_msg("查询收款信息成功");
			
			String accNo = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ACC_NO");
			if (accNo != null && accNo.length() > 0) {
				qrBook.setPayer_info_acc_no(accNo);
			}
	    	String name = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_NAME");
	    	if (name != null && name.length() > 0) {
				qrBook.setPayer_info_name(name);
			}
	    	String payerBankInfo = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_PAYER_BANK_INFO");
	    	if (payerBankInfo != null && payerBankInfo.length() > 0) {
				qrBook.setPayer_info_payer_bank_info(payerBankInfo);
			}
	    	String issCode = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ISS_CODE");
	    	if (issCode != null && issCode.length() > 0) {
				qrBook.setPayer_info_iss_code(issCode);
			}
	    	String acctClass =  EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_ACCT_CLASS").toString();
	    	if (acctClass != null && acctClass.length() > 0) {
				qrBook.setPayer_info_acct_class(acctClass);
			}
	    	String certifTp = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CERTIF_TP");
	    	if (certifTp != null && certifTp.length() > 0) {
				qrBook.setPayer_info_certif_id(certifTp);
			}
	    	String certifId = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CERTIF_ID");
	    	if (certifId != null && certifId.length() > 0) {
				qrBook.setPayer_info_certif_id(certifId);
			}
	    	String cvn2 = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CVN2");
	    	if (cvn2 != null && cvn2.length() > 0) {
				qrBook.setPayer_info_payer_bank_info(cvn2);
			}
	    	String expired = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_EXPIRED");
	    	if (expired != null && expired.length() > 0) {
				qrBook.setPayer_info_expired(expired);
			}
	    	String cardAttr = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_CARD_ATTR");
	    	if (cardAttr != null && cardAttr.length() > 0) {
				qrBook.setPayer_info_card_attr(cardAttr);
			}
	    	String mobile = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_INFO_MOBILE");
	    	if (mobile != null && mobile.length() > 0) {
				qrBook.setPayer_info_certif_id(mobile);
			}
	    	String id = (String) EPOper.get(tpID, "T_QRP_BOOK[0].INVOICE_INFO_ID");
	    	if (id != null && id.length() > 0) {
				qrBook.setInvoice_info_id(id);
			}
	    	String amount = (String) EPOper.get(tpID, "T_QRP_BOOK[0].INVOICE_INFO_AMOUNT").toString();
	    	
	    	if (amount != null && amount.length() > 0) {
	    		Double amt = Double.valueOf(amount);
				qrBook.setInvoice_info_amount(amt);
			}
	    	String reqtype = (String) EPOper.get(tpID, "T_QRP_BOOK[0].REQ_TYPE");
	    	if (reqtype != null && reqtype.length() > 0) {//收款查询
				qrBook.setReq_type("0540000903");
			}
	    	String orderno = (String) EPOper.get(tpID, "T_QRP_BOOK[0].ORDER_NO");
	    	if (orderno != null && orderno.length() > 0) {
				qrBook.setOrder_no(orderno);
			}
	    	String ordertime = (String) EPOper.get(tpID, "T_QRP_BOOK[0].ORDER_TIME");
	    	if (ordertime != null && ordertime.length() > 0) {
				qrBook.setOrder_time(ordertime);
			}
	    	String txnAmt = (String) EPOper.get(tpID, "T_QRP_BOOK[0].TXN_AMT").toString();
	    	if (txnAmt != null && txnAmt.length() > 0) {
	    		Double tamt = Double.valueOf(txnAmt);
				qrBook.setTxn_amt(tamt);
			}
	    	String currencyCode = (String) EPOper.get(tpID, "T_QRP_BOOK[0].CURRENCY_CODE");
	    	if (currencyCode != null && currencyCode.length() > 0) {
				qrBook.setCurrency_code(currencyCode);
			}
	    	String voucherNum = (String) EPOper.get(tpID, "T_QRP_BOOK[0].VOUCHER_NUM");
	    	if (voucherNum != null && voucherNum.length() > 0) {
				qrBook.setVoucher_num(voucherNum);
			}
	    	String settleKey = (String) EPOper.get(tpID, "T_QRP_BOOK[0].SETTLE_KEY");
	    	if (settleKey != null && settleKey.length() > 0) {
				qrBook.setSettle_key(settleKey);
			}
	    	String settleDate = (String) EPOper.get(tpID, "T_QRP_BOOK[0].SETTLE_DATE");
	    	if (settleDate != null && settleDate.length() > 0) {
				qrBook.setSettle_date(settleDate);
			}
	    	String encryptCertId = (String) EPOper.get(tpID, "T_QRP_BOOK[0].ENCRYPT_CERT_ID");
	    	if (encryptCertId != null && encryptCertId.length() > 0) {
				qrBook.setEncrypt_cert_id(encryptCertId);
			}
	    	String payerComments = (String) EPOper.get(tpID, "T_QRP_BOOK[0].PAYER_COMMENTS");
	    	if (payerComments != null && payerComments.length() > 0) {
				qrBook.setPayer_comments(payerComments);
			}
	    	String comInfo = (String) EPOper.get(tpID, "T_QRP_BOOK[0].COM_INFO");
	    	if (comInfo != null && comInfo.length() > 0) {
				qrBook.setCom_info(comInfo);

				}
	    	iResult = QrBookDao.insert(qrBook);
			
			
		}

		catch (Exception e) {
			SysPub.appLog("ERROR", "插入t_qrp_book表失败");
			e.printStackTrace();
			throw e;
		}

		SysPub.appLog("INFO", "插入数据，返回:%d", iResult);
    	
    	
    	return iResult;
    }

    /**
     * @Description: 调用银联接口 发送银联
     * @author Q
     * @param cltReq 
     * @param svrReq 
     * @return
     * @throws Exception
     * @date 2017年12月17日下午5:04:44
     */
    public static int sendUP(String tpID, String cltReq, String svrReq) throws Exception {
	SysPub.appLog("INFO", "发往银联开始");
	try {
	    /* 报文头赋值 */
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].version", SDKConstants.VERSION_1_0_0);
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].signature", "0");
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].certId", "68759529225");
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].reqType", (String) EPOper.get(tpID, svrReq+"[0].cd[0].reqType"));
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].acqCode", (String) EPOper.get(tpID, svrReq+"[0].cd[0].acqCode"));
	    EPOper.put	(tpID, "OBJ_QRUP_ALL[0].voucherNum", (String) EPOper.get(tpID, svrReq+"[0].cd[0].voucherNum"));
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].orderNo", (String) EPOper.get(tpID, svrReq+"[0].cd[0].orderNo"));
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].orderTime", (String) EPOper.get(tpID, svrReq+"[0].cd[0].orderTime"));
	    EPOper.copy(tpID, tpID,cltReq , "OBJ_ALA_abstarct_REQ[0].req" );
	    // 调度银联0540000903服务
	    SysPub.appLog("INFO", "调用银联0540000903服务开始");
	    DtaTool.call("QRUP_CLI", "ZS0540");

	} catch (Exception e) {
	    SysPub.appLog("ERROR", "调用银联服务0540000903失败：%s", e.getMessage());
	    throw e;
	}

	return 0;
    }

}
