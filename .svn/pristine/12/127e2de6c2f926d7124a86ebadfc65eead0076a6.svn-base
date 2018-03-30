package com.adtec.ncps.busi.qrps.qr;

import java.net.URLEncoder;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.qrps.QrBusiPub;
import com.adtec.ncps.busi.qrps.bean.QrBook;
import com.adtec.ncps.busi.qrps.dao.QrBookDao;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.union.sdk.SDKConstants;

/**
 * @ClassName: BS0210000903
 * @Description: C2B码申请
 * @author Q
 * @date 2017年12月16日下午9:57:52
 */
public class BS0210000903 {
    
    private static QrBook pubBook = new QrBook();

    public static int chk() throws Exception {
	SysPub.appLog("INFO", "C2B码申请开始");
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String szTpID = dtaInfo.getTpId();

	try {
	    // 初始化返回报文
	    initResFmt();
	} catch (Exception e) {
	    EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
	    SysPub.appLog("ERROR", "BS0210000903chk失败");
	    e.printStackTrace();
	    throw e;
	}
	EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "0");
	SysPub.appLog("INFO", "C2B码申请chk完成");
	return 0;
    }

    public static void initResFmt() {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String szTpID = dtaInfo.getTpId();

//	EPOper.put(szTpID, "OBJ_QRUP_ALL[0].version", SDKConstants.VERSION_1_0_0);
//	EPOper.put(szTpID, "OBJ_QRUP_ALL[0].signature", "0");
//	EPOper.copy(szTpID, szTpID, "OBJ_QRUP_ALL[0].certId", "OBJ_QRUP_ALL[0].certId");
//	EPOper.copy(szTpID, szTpID, "OBJ_QRUP_ALL[0].reqType", "OBJ_QRUP_ALL[0].reqType");
//	EPOper.copy(szTpID, szTpID, "OBJ_QRUP_ALL[0].issCode", "OBJ_QRUP_ALL[0].issCode");
//	EPOper.put(szTpID, "OBJ_QRUP_ALL[0].qrValidTime", 100);
//	EPOper.put(szTpID, "OBJ_QRUP_ALL[0].qrNo", "0");
//	EPOper.put(szTpID, "OBJ_QRUP_ALL[0].respCode", "99");
//	EPOper.put(szTpID, "OBJ_QRUP_ALL[0].respMsg", "初始化错误");
    }

    
    
    public static int transDeal()
    {
    	try{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		SysPub.appLog("INFO", "执行 0210000903-transDeal 方法开始");

		String svrReq = "OBJ_EBANK_SVR_QR0210_REQ";
		String cltReq = "OBJ_QRUP_ALL";
		String svrRes = "OBJ_EBANK_SVR_QR0210_RES";
		String cltRes = "OBJ_QRUP_ALL";


		SysPub.appLog("INFO", "svrReq= " + svrReq);
		EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);
		
		SysPub.appLog("INFO", "复制svrReq成功");
	
		
		//插入登记簿
		int ret = deal(svrReq);
		if( ret!=0){
			EPOper.copy(tpID, tpID, svrReq+"[0].tx_code", svrRes+"[0].tx_code");
			EPOper.put(tpID, svrRes+"[0].hostErrorMessage", "记录重复，插入登记簿失败");
			EPOper.put(tpID, svrRes+"[0].hostReturnCode", "9999");
			EPOper.put(tpID, svrRes+"[0].msgReturnFrom", "银联二维码");
			
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
            return -1;
			
		}
		//发送银联
		ret = sendUP(svrReq, cltReq, cltRes);
		if( ret != 0 ){
			EPOper.copy(tpID, tpID, svrReq+"[0].tx_code", svrRes+"[0].tx_code");
			EPOper.put(tpID, svrRes+"[0].hostErrorMessage", "发送银联交易请求失败");
			EPOper.put(tpID, svrRes+"[0].hostReturnCode", "9999");
			EPOper.put(tpID, svrRes+"[0].msgReturnFrom", "银联二维码");
			
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			String retCode = (String) EPOper.get(tpID, svrRes+"[0].hostReturnCode");
			String retMsg = (String) EPOper.get(tpID, svrRes+"[0].hostErrorMessage");
			QrBusiPub.uptBookErrRet(retCode, retMsg);
			return -1;
		}
		
		//银联返回后处理
		ret=recvUP(svrRes);
		if( ret != 0 ){
			EPOper.copy(tpID, tpID, svrReq+"[0].tx_code", svrRes+"[0].tx_code");
			EPOper.put(tpID, svrRes+"[0].hostErrorMessage", "接收银联交易响应失败");
			EPOper.put(tpID, svrRes+"[0].hostReturnCode", "9999");
			EPOper.put(tpID, svrRes+"[0].msgReturnFrom", "银联二维码");
			
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			String retCode = (String) EPOper.get(tpID, svrRes+"[0].hostReturnCode");
			String retMsg = (String) EPOper.get(tpID, svrRes+"[0].hostErrorMessage");
			QrBusiPub.uptBookErrRet(retCode, retMsg);
			return -1;
		}
		//返回前端赋值
		EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res[0].respMsg", svrRes+"[0].hostErrorMessage");
	
		EPOper.copy(tpID, tpID, svrReq+"[0].tx_code", svrRes+"[0].tx_code");
		
		String  respCode= (String) EPOper.get(tpID, "OBJ_ALA_abstarct_RES[0].res[0].respCode");
		
		if("00".equals(respCode)){
			EPOper.put(tpID, svrRes+"[0].hostReturnCode", "0000");
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res[0].qrNo", svrRes+"[0].cd[0].qrNo");
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res[0].qrValidTime", svrRes+"[0].cd[0].qrValidTime");
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res[0].issCode", svrRes+"[0].cd[0].issCode");
		}else
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res[0].respCode", svrRes+"[0].hostReturnCode");
		
		EPOper.put(tpID, svrRes+"[0].msgReturnFrom", "银联二维码");
		
		EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
		
		String retCode = (String) EPOper.get(tpID, svrRes+"[0].hostReturnCode");
		String retMsg = (String) EPOper.get(tpID, svrRes+"[0].hostErrorMessage");
		QrBusiPub.uptBookErrRet(retCode, retMsg);
		
    	}catch (Exception e){
    		return -1;
    	}
    	return 0;
    }


    public static int deal(String svrReq) throws Exception {
	SysPub.appLog("INFO", "开始BS0210000903业务处理");
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();


	// 组织数据登记到book表
	int ret = insQrBook(svrReq);
	if (ret != 1) {
	    EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "-1");
	    SysPub.appLog("ERROR", "插入数据库表失败");
	} else {
	    EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "0");
	    SysPub.appLog("INFO", "插入数据库成功");
	}

	return 0;
    }

    public static int insQrBook(String svrReq) throws Exception {
	SysPub.appLog("INFO", "开始BS0210000903登记簿数据");
	int iResult = 0;
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();

	QrBook qrBook = new QrBook();

	try {// 先产生流水号
	    BusiPub.getPlatSeq();
	    int iseq_no = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
	    qrBook.setSeq_no(iseq_no);
	    String qr_code = String.valueOf(iseq_no);
	    qrBook.setQr_code(qr_code);
	    qrBook.setOrder_no(qr_code);
	    // 保存平台流水号和平台日期，更新流水时用
	    qrBook.setPlat_date((String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE"));
	    qrBook.setVersion(SDKConstants.VERSION_1_0_0);
	    qrBook.setCert_id((String) EPOper.get(tpID, svrReq + "[0].cd[0].certId"));
	    qrBook.setReq_type((String) EPOper.get(tpID, svrReq + "[0].cd[0].reqType"));
	    qrBook.setIss_code((String) EPOper.get(tpID, svrReq + "[0].cd[0].issCode"));
	    qrBook.setQr_type((String) EPOper.get(tpID, svrReq + "[0].cd[0].qrType"));

	    // 付款方信息 payerInfo
	    // 卡号与账户类型必填，姓名、证件号、cvn2、有效期均可不填；若上送了部分上述可选要素，银联会将这些要素填入 8583
	    // 消费报文中送给发卡行系统验证PAYERINFO
	    qrBook.setPayer_info_acc_no((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].accNo"));
	    qrBook.setPayer_info_acct_class((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].acctClass"));
	    qrBook.setPayer_info_card_attr((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].cardAttr"));
	    qrBook.setPayer_info_certif_id((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].certifId"));
	    qrBook.setPayer_info_certif_tp((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].certifTp"));
	    qrBook.setPayer_info_cvn2((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].cvn2"));
	    qrBook.setPayer_info_expired((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].expired"));
	    qrBook.setPayer_info_iss_code((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].issCode"));
	    qrBook.setPayer_info_mobile((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].mobile"));
	    qrBook.setPayer_info_name((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].name"));
	    qrBook.setPayer_info_payer_bank_info((String) EPOper.get(tpID, svrReq + "[0].cd[0].payerInfo[0].payerBankInfo"));

	    String qrValidTime = (String) EPOper.get(tpID, svrReq + "[0].cd[0].qrValidTime");
	    qrBook.setQr_valid_time(Long.valueOf(qrValidTime));

	    // riskinfo
	    qrBook.setRisk_info((String) QrBusiPub.parsEle2Base64Info("riskInfo", true, svrReq));
	    
	    
	    // addnCond
	    qrBook.setAddn_cond_currency((String) EPOper.get(tpID, svrReq + "[0].cd[0].addnCond[0].currency"));

	    String pinFree = (String) EPOper.get(tpID, svrReq + "[0].cd[0].addnCond[0].pinFree");
	    qrBook.setAddn_cond_maxamont(Double.valueOf(pinFree));
	    String maxAmont = (String) EPOper.get(tpID, svrReq + "[0].cd[0].addnCond[0].maxAmont");
	    qrBook.setAddn_cond_pinfree(Double.valueOf(maxAmont));

	    qrBook.setAddn_opurl((String) EPOper.get(tpID, svrReq + "[0].cd[0].addnOpUrl"));
	    qrBook.setEncrypt_cert_id((String) EPOper.get(tpID, svrReq + "[0].cd[0].encryptCertId"));
	    qrBook.setBack_url((String) EPOper.get(tpID, svrReq + "[0].cd[0].backUrl"));
	    qrBook.setReq_reserved((String) EPOper.get(tpID, svrReq + "[0].cd[0].reqReserved"));

	    qrBook.setStat("000");
	    
	    pubBook = qrBook;

	    iResult = QrBookDao.insert(qrBook);
	    if (iResult <= 0) {
		SysPub.appLog("ERROR", "插入t_qrp_book表失败");
	    }
	} catch (Exception e) {
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
     * @return
     * @throws Exception
     * @date 2017年12月17日下午5:04:44
     */
    public static int sendUP(String svrReq, String cltReq, String cltRes) throws Exception {
	SysPub.appLog("INFO", "发往银联开始");
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();

	try {
	    String version = null;
	    String signature = null;
	    String certId = null;
	    String reqType = null;
	    String issCode = null;
	    String qrType = null;
	    String payerInfo = null;
	    String qrValidTime = null;
	    String riskInfo = null;
	    String qrNo = null;
	    String addnCond = null;
	    String addnOpUrl = (String) EPOper.get(tpID, svrReq+"[0].cd[0].addnOpUrl");
	    String encryptCertId = null;
	    String backUrl = (String) EPOper.get(tpID, svrReq+"[0].cd[0].backUrl");
	    String reqReserved = null;

	    /* 报文头赋值 */
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].version", SDKConstants.VERSION_1_0_0);
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].signature", "0");
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].certId", "68759529225");


	    // 付款方信息 payerInfo  118.122.185.158 12028
	    // 卡号与账户类型必填，姓名、证件号、cvn2、有效期均可不填；若上送了部分上述可选要素，银联会将这些要素填入 8583
	    // 消费报文中送给发卡行系统验证
	    payerInfo = QrBusiPub.parsEle2Base64Info("payerInfo", true, svrReq);
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].payerInfo", payerInfo);

	    // EPOper.put(tpID, "OBJ_QRUP_ALL[0].qrValidTime", );
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].issCode", "OBJ_QRUP_ALL[0].issCode");
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].reqType", "OBJ_QRUP_ALL[0].reqType");
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].qrType", "OBJ_QRUP_ALL[0].qrType");
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].qrValidTime", "OBJ_QRUP_ALL[0].qrValidTime");
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].addnOpUrl", "OBJ_QRUP_ALL[0].addnOpUrl");
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].backUrl", "OBJ_QRUP_ALL[0].backUrl");
	    EPOper.copy(tpID, tpID, svrReq+"[0].cd[0].reqReserved", "OBJ_QRUP_ALL[0].reqReserved");


	    riskInfo = QrBusiPub.parsEle2Base64Info("riskInfo", true, svrReq);
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].riskInfo", riskInfo);

	    addnCond = QrBusiPub.parsEle2Base64Info("addnCond", true, svrReq);
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].addnCond", addnCond);

	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].addnOpUrl",
		    URLEncoder.encode(addnOpUrl, SDKConstants.UTF_8_ENCODING));
	    // EPOper.put(tpID, "OBJ_QRUP_ALL[0].encryptCertId",
	    // encryptCertId);
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].backUrl",
		    URLEncoder.encode(backUrl, SDKConstants.UTF_8_ENCODING));
	    // EPOper.put(tpID, "OBJ_QRUP_ALL[0].reqReserved",
	    // reqReserved);

		EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
		SysPub.appLog("INFO", "复制OBJ_ALA_abstarct_REQ[0].req成功");
	    // 调度银联CUP0303服务
	    SysPub.appLog("INFO", "调用0210000903服务开始");
	    DtaTool.call("QRUP_CLI", "BS0210");
	    
	    
	    
	} catch (Exception e) {
	    EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "-1");
	    SysPub.appLog("ERROR", "调用银联服务0210000903失败：%s", e.getMessage());
	    return -1;
	    //throw e;
	}
	EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "0");

	return 0;
    }

    /**
     * @Description: 银联返回处理
     * @author Q
     * @return
     * @throws Exception
     * @date 2017年12月17日下午5:07:06
     */
    public static int recvUP(String svrRes) throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String szTpID = dtaInfo.getTpId();
	int iResult = 0;
	
	QrBook qrBook = pubBook;
	
	try {
		
		EPOper.copy(szTpID, szTpID, "OBJ_ALA_abstarct_RES[0].res", "OBJ_QRUP_ALL");
	    String respCode = (String) EPOper.get(szTpID, "OBJ_ALA_abstarct_RES[0].res[0].respCode");
	    String respMsg = (String) EPOper.get(szTpID, "OBJ_ALA_abstarct_RES[0].res[0].respMsg");
	    String qrNo = (String) EPOper.get(szTpID, "OBJ_ALA_abstarct_RES[0].res[0].qrNo");
	    String signChkFlag = (String) EPOper.get(szTpID, "OBJ_ALA_abstarct_RES[0].res[0].signCheckFlag");
	    SysPub.appLog("INFO", "付款码[%s]银联返回错误码：[%s]错误信息[%s]验签状态[%s]", qrNo, respCode, respMsg, signChkFlag);

	    // 更新数据库
	    qrBook.setResp_code(respCode);
	    qrBook.setResp_msg(respMsg);
	    qrBook.setQr_code(qrNo);
	    qrBook.setSign_chk_flag(signChkFlag);

	    iResult = QrBookDao.update(qrBook);
	    if (iResult <= 0) {
		EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
		SysPub.appLog("ERROR", "更新t_qrp_book表失败");
	    }
	} catch (Exception e) {
	    SysPub.appLog("ERROR", "银联返回信息处理异常：%s", e.getMessage());
	    EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
	    e.printStackTrace();
	    return -1;
	    //throw e;
	}
	EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "0");

	return 0;
    }

}
