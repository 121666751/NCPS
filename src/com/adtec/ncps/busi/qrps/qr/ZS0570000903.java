package com.adtec.ncps.busi.qrps.qr;

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
import com.union.sdk.SDKConstants;

/**
 * @ClassName: ZS0140000903
 * @Description: 查询付款状态
 * @author Q
 * @date 2018年1月4日上午11:26:41
 *
 */
public class ZS0570000903 {

    public static int deal() throws Exception {
	SysPub.appLog("INFO", "开始ZS0570000903业务处理");
	
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
	
	SysPub.appLog("INFO", "复制svrReq成功[%S]",svrReq);
	
	// 查询付款交易
	String qrCode = (String) EPOper.get(tpID, svrReq+"[0].cd[0].qrCode");
	ret = QrBusiPub.queryQrpsBookByQrCode(qrCode, "0510000903");
	if (ret != 1) {
	    if (0 == ret) {
	    	SysPub.appLog("ERROR", "查询二维码信息为空");
	    } else {
	    	SysPub.appLog("ERROR", "查询二维码信息失败");
	    }
	    return -1;
	}
	// 组织报文发送到银联
	sendUP( tpID,  cltReq,  svrReq);
	
	ret = recvUP( tpID,  qrBook,  platDate,  iseq_no,  cltRes,  svrRes,svrReq);
	if (ret != 0) {
	    return -1;
	}

	insQrBook(qrBook, tpID, platDate, iseq_no);
	if (ret != 1) {
	    SysPub.appLog("ERROR", "插入数据库表失败");
	} else {
	    SysPub.appLog("INFO", "插入数据库成功");
	}

	return 0;
    }

    public static int insQrBook(QrBook qrBook, String tpID, String platDate, int seqNo) throws Exception {
    	SysPub.appLog("INFO", "开始ZS0160000903登记簿数据");
    	int iResult = 0;

    	try {
    	    // 非对象类型赋值
    	    QrBusiPub.qrBookData(tpID, qrBook, platDate, seqNo);

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
    
    private static int recvUP(String tpID, QrBook qrBook, String platDate, int iseq_no, String cltRes, String svrRes, String svrReq) throws Exception {
    	// 非对象类型赋值
	    QrBusiPub.qrBookData(tpID, qrBook, platDate, iseq_no);
	    
    	
    	EPOper.delete(tpID, cltRes);
    	EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
    	
    	EPOper.put(tpID, svrRes+"[0].cd[0].reqType", (String) EPOper.get(tpID, cltRes+"[0].reqType"));
    	EPOper.put(tpID, svrRes+"[0].cd[0].acqCode", (String) EPOper.get(tpID, cltRes+"[0].acqCode"));
    	EPOper.put(tpID, svrRes+"[0].cd[0].respMsg", (String) EPOper.get(tpID, cltRes+"[0].respMsg"));
    	EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
    	
    	String respCode = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].respCode");
    	if("00".equals(respCode)) {
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].respCode","0000");
 	    }else {
 	    	EPOper.put(tpID, svrRes+"[0].cd[0].respCode", (String) EPOper.get(tpID, cltRes+"[0].respCode"));
 	    }
 	    EPOper.copy(tpID, tpID,svrRes, "OBJ_ALA_abstarct_RES[0].res");
 	    
 	    if (!"00".equals(respCode)) {
     	    SysPub.appLog("ERROR", "银联服务0570000903返回失败：%s-%s", respCode,
     		    (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].respMsg"));
     	    return -1;
     	}
		return 0;
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
	    EPOper.put(tpID, "OBJ_QRUP_ALL[0].qrCode", (String) EPOper.get(tpID, svrReq+"[0].cd[0].qrCode"));

	    EPOper.copy(tpID, tpID,cltReq , "OBJ_ALA_abstarct_REQ[0].req" );
	    // 调度银联0140000903服务
	    SysPub.appLog("INFO", "调用银联0570000903服务开始");
	    DtaTool.call("QRUP_CLI", "ZS0570");

	} catch (Exception e) {
	    SysPub.appLog("ERROR", "调用银联服务0570000903失败：%s", e.getMessage());
	    throw e;
	}

	return 0;
    }

}
