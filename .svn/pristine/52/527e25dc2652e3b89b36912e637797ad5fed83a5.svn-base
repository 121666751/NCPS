package com.adtec.ncps.busi.qrps.qr;

import java.util.HashMap;

import com.adtec.ncps.busi.ncp.AmountUtils;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.qrps.QrBusiPub;
import com.adtec.ncps.busi.qrps.bean.QrBook;
import com.adtec.ncps.busi.qrps.dao.QrBookDao;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.union.sdk.DemoBase;

/**
 * @ClassName: ZS0530000903
 * @Description: 收款通知
 * @author Q
 * @date 2018年3月19日下午7:42:52
 *
 */
public class ZS0530000903 {

	public static int deal() throws Exception {
		SysPub.appLog("INFO", "开始ZS0530000903业务处理");

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int ret = 0;

		// 查询原c2b码申请交易数据到数据元素T_QRP_BOOK
		String svrReq = "OBJ_QRUP_ALL";

		String svrRes = "OBJ_QRUP_ALL";

		SysPub.appLog("INFO", "svrReq= " + svrReq);
		EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);

		SysPub.appLog("INFO", "复制svrReq成功");
		String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");

		// 组织数据登记到book表
		ret = insQrBook();
		if (ret != 1) {
			SysPub.appLog("ERROR", "插入数据库表失败");
			EPOper.put(tpID, "OBJ_QRUP_ALL[0].respCode", "12");
			EPOper.put(tpID, "OBJ_QRUP_ALL[0].respMsg", "记录重复");
		} else {
			EPOper.put(tpID, "OBJ_QRUP_ALL[0].respCode", "00");
			EPOper.put(tpID, "OBJ_QRUP_ALL[0].respMsg", "插入数据库成功");
			SysPub.appLog("INFO", "插入数据库成功");
		}
		EPOper.put(tpID, "OBJ_QRUP_ALL[0].signature", "0");
		EPOper.put(tpID, "OBJ_QRUP_ALL[0].certId", "68759529225");

		EPOper.copy(tpID, tpID, "OBJ_QRUP_ALL", "OBJ_ALA_abstarct_RES[0].res");

		return 0;

	}

	public static int insQrBook() throws Exception {
		SysPub.appLog("INFO", "开始ZS0530000903登记簿数据");
		int iResult = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		QrBook qrBook = new QrBook();

		try {// 先产生流水号
			BusiPub.getPlatSeq();
			int seqNo = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
			String seq = String.valueOf(seqNo);
			String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
			// 非对象类型赋值
			QrBusiPub.qrBookData(tpID, qrBook, platDate, seqNo);
			qrBook.setQr_code(seq);
			qrBook.setResp_code("00");
			qrBook.setResp_msg("接收收款通知成功");

			// payerInfo付款方信息PAYERINFO
			String payerInfo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].payerInfo");
			SysPub.appLog("INFO", "payerInfo:%s", payerInfo);
			if (payerInfo != null && payerInfo.length() > 0) {
				QrBusiPub.parsBase64Info2Ele(payerInfo, "payerInfo", "OBJ_QRUP_ALL");

			}

			String accNo = (String) EPOper.get(tpID, "PAYERINFO[0].accNo");
			SysPub.appLog("INFO", "accNo:%s", accNo);
			if (accNo != null && accNo.length() > 0) {
				qrBook.setPayer_info_acc_no(accNo);
			}
			String name = (String) EPOper.get(tpID, "PAYERINFO[0].name");
			SysPub.appLog("INFO", "name:%s", name);
			if (name != null && name.length() > 0) {
				qrBook.setPayer_info_name(name);
			}
			String payerBankInfo = (String) EPOper.get(tpID, "PAYERINFO[0].payerBankInfo");
			SysPub.appLog("INFO", "payerBankInfo:%s", payerBankInfo);
			if (payerBankInfo != null && payerBankInfo.length() > 0) {
				qrBook.setPayer_info_payer_bank_info(payerBankInfo);
			}
			String issCode = (String) EPOper.get(tpID, "PAYERINFO[0].issCode");
			SysPub.appLog("INFO", "issCode:%s", issCode);
			if (issCode != null && issCode.length() > 0) {
				qrBook.setPayer_info_iss_code(issCode);
			}
			String acctClass = (String) EPOper.get(tpID, "PAYERINFO[0].acctClass");
			SysPub.appLog("INFO", "acctClass:%s", acctClass);
			if (acctClass != null && acctClass.length() > 0) {
				qrBook.setPayer_info_acct_class(acctClass);
			}
			String certifTp = (String) EPOper.get(tpID, "PAYERINFO[0].certifTp");
			SysPub.appLog("INFO", "certifTp:%s", certifTp);
			if (certifTp != null && certifTp.length() > 0) {
				qrBook.setPayer_info_certif_id(certifTp);
			}
			String certifId = (String) EPOper.get(tpID, "PAYERINFO[0].certifId");
			SysPub.appLog("INFO", "certifId:%s", certifId);
			if (certifId != null && certifId.length() > 0) {
				qrBook.setPayer_info_certif_id(certifId);
			}
			String cvn2 = (String) EPOper.get(tpID, "PAYERINFO[0].cvn2");
			SysPub.appLog("INFO", "cvn2:%s", cvn2);
			if (cvn2 != null && cvn2.length() > 0) {
				qrBook.setPayer_info_payer_bank_info(cvn2);
			}
			String expired = (String) EPOper.get(tpID, "PAYERINFO[0].expired");
			SysPub.appLog("INFO", "expired:%s", expired);
			if (expired != null && expired.length() > 0) {
				qrBook.setPayer_info_expired(expired);
			}
			String cardAttr = (String) EPOper.get(tpID, "PAYERINFO[0].cardAttr");
			SysPub.appLog("INFO", "cardAttr:%s", cardAttr);
			if (cardAttr != null && cardAttr.length() > 0) {
				qrBook.setPayer_info_card_attr(cardAttr);
			}
			String mobile = (String) EPOper.get(tpID, "PAYERINFO[0].mobile");
			SysPub.appLog("INFO", "mobile:%s", mobile);
			if (mobile != null && mobile.length() > 0) {
				qrBook.setPayer_info_certif_id(mobile);
			}
			String invoiceInfo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].invoiceInfo");
			SysPub.appLog("INFO", "invoiceinfo:%s", invoiceInfo);
			if (invoiceInfo != null && invoiceInfo.length() > 0) {
				QrBusiPub.parsBase64Info2Ele(invoiceInfo, "invoiceInfo", "OBJ_QRUP_ALL");
				String id = (String) EPOper.get(tpID, "INVOICEINFO[0].id");
				SysPub.appLog("INFO", "id:%s", id);
				if (id != null && id.length() > 0) {
					qrBook.setInvoice_info_id(id);
				}
				String amount = (String) EPOper.get(tpID, "INVOICEINFO[0].amount");
				Double amt = Double.valueOf(amount);
				SysPub.appLog("INFO", "amt:%s", amount);
				if (amount != null && amount.length() > 0) {
					qrBook.setInvoice_info_amount(amt);
				}
			}
			String reqtype = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].reqType");
			SysPub.appLog("INFO", "reqtype:%s", reqtype);
			if (reqtype != null && reqtype.length() > 0) {
				qrBook.setReq_type(reqtype);
			}
			String orderno = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].orderNo");
			SysPub.appLog("INFO", "orderno:%s", orderno);
			if (orderno != null && orderno.length() > 0) {
				qrBook.setOrder_no(orderno);
			}
			String ordertime = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].orderTime");
			SysPub.appLog("INFO", "ordertime:%s", ordertime);
			if (ordertime != null && ordertime.length() > 0) {
				qrBook.setOrder_time(ordertime);
			}
			String txnAmt = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].txnAmt");
			Double tamt = Double.valueOf(txnAmt);
			SysPub.appLog("INFO", "txnAmt:%s", txnAmt);
			if (txnAmt != null && txnAmt.length() > 0) {
				qrBook.setTxn_amt(tamt);
			}
			String currencyCode = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].currencyCode");
			SysPub.appLog("INFO", "currencyCode:%s", currencyCode);
			if (currencyCode != null && currencyCode.length() > 0) {
				qrBook.setCurrency_code(currencyCode);
			}
			String voucherNum = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].voucherNum");
			SysPub.appLog("INFO", "voucherNum:%s", voucherNum);
			if (voucherNum != null && voucherNum.length() > 0) {
				qrBook.setVoucher_num(voucherNum);
			}
			String settleKey = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].settleKey");
			SysPub.appLog("INFO", "settleKey:%s", settleKey);
			if (settleKey != null && settleKey.length() > 0) {
				qrBook.setSettle_key(settleKey);
			}
			String settleDate = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].settleDate");
			SysPub.appLog("INFO", "settleDate:%s", settleDate);
			if (settleDate != null && settleDate.length() > 0) {
				qrBook.setSettle_date(settleDate);
			}
			String encryptCertId = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].encryptCertId");
			SysPub.appLog("INFO", "encryptCertId:%s", encryptCertId);
			if (encryptCertId != null && encryptCertId.length() > 0) {
				qrBook.setEncrypt_cert_id(encryptCertId);
			}
			String payerComments = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].payerComments");
			SysPub.appLog("INFO", "payerComments:%s", payerComments);
			if (payerComments != null && payerComments.length() > 0) {
				qrBook.setPayer_comments(payerComments);
			}
			String comInfo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].comInfo");
			SysPub.appLog("INFO", "comInfo:%s", comInfo);
			if (comInfo != null && comInfo.length() > 0) {
				qrBook.setCom_info(comInfo);

				iResult = QrBookDao.insert(qrBook);
				if (iResult <= 0) {
					SysPub.appLog("ERROR", "插入t_qrp_book表失败");
				}
			}

		}

		catch (Exception e) {
			SysPub.appLog("ERROR", "插入t_qrp_book表失败");
			e.printStackTrace();
			throw e;
		}

		SysPub.appLog("INFO", "插入数据，返回:%d", iResult);
		return iResult;
	}

	public static void main(String[] args) {

		HashMap<String, String> contentData = new HashMap<String, String>();
		contentData.put("accNo", "1");
		contentData.put("name", "12345456");
		contentData.put("payerBankInfo", "156");
		contentData.put("issCode", "54782");
		contentData.put("acctClass", "1");
		contentData.put("certifTp", "15");
		contentData.put("cvn2", "123");
		contentData.put("expired", "5478");
		contentData.put("certifId", "68759529225");
		contentData.put("cardAttr", "12");
		contentData.put("mobile", "13388168235");
		String payerInfo = DemoBase.getAddnCond(contentData, "UTF-8");
		// String payerInfo="payerInfo="+trans;
		// resTrans=AcpService.base64Decode(trans, "UTF-8");

		String id = "12345678";
		String name = "1200";

		contentData = new HashMap<String, String>();
		contentData.put("id", id);
		contentData.put("amount", name);

		String invoiceInfo = DemoBase.getAddnCond(contentData, "UTF-8");
		System.out.println(payerInfo + "\n");
		System.out.println(invoiceInfo);

	}
}