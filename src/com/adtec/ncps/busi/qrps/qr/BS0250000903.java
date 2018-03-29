package com.adtec.ncps.busi.qrps.qr;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.qrps.QrBusiPub;
import com.adtec.ncps.busi.qrps.bean.QrBook;
import com.adtec.ncps.busi.qrps.dao.QrBookDao;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

/**
 * @ClassName: BS0250000903
 * @Description: 接收c2b交易通知
 * @author Q
 * @date 2018年1月2日下午2:16:52
 *
 */
public class BS0250000903 {

    public static int deal() throws Exception {
	SysPub.appLog("INFO", "开始BS0250000903业务处理");

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
	String qrNo = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].qrNo");
	ret = QrBusiPub.queryQrpsBook(platDate, qrNo, "0210000903");
	if (ret > 0) {
	    // 对原交易进行处理
	    origBookDeal();
	    SysPub.appLog("INFO", "原交易c2b申请交易处理完毕");
	} else
	    SysPub.appLog("INFO", "原交易c2b申请交易没有找到");

	// 组织数据登记到book表
	ret = insQrBook();
	if (ret != 1) {
	    SysPub.appLog("ERROR", "插入数据库表失败");
	} else {
	    SysPub.appLog("INFO", "插入数据库成功");
	}

	return 0;
    }

    public static int origBookDeal() throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();

	int ret = 0;
	String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
	String origReqType = (String) EPOper.get(tpID, "OBJ_QRUP_ALL[0].origReqType");
	SysPub.appLog("INFO", "原交易类型[%s]", origReqType);

	// 原交易要素判断 TODO

	// 修改原申请c2b码交易状态
	String stat = null;
	if ("0310000903".equals(origReqType))// 消费交易0310000903
	    stat = "400";
	else if ("0320000903".equals(origReqType))// 消费冲正交易0320000903
	    stat = "401";
	else if ("0330000903".equals(origReqType))// 消费撤销交易0330000903
	    stat = "402";
	else if ("0340000903".equals(origReqType))// 退货交易0340000903
	    stat = "403";
	int seqNo = (Integer) EPOper.get(tpID, "T_QRP_BOOK[0].SEQ_NO");
	ret = QrBusiPub.uptQrpsBookStat(platDate, seqNo, stat);
	if (ret != 1) {
	    SysPub.appLog("ERROR", "修改原交易状态失败");
	}
	return 0;
    }

    public static int insQrBook() throws Exception {
	SysPub.appLog("INFO", "开始BS0250000903登记簿数据");
	int iResult = 0;
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();

	QrBook qrBook = new QrBook();

	try {// 先产生流水号
	    BusiPub.getPlatSeq();
	    int seqNo = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
	    String platDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
	    // 非对象类型赋值
	    QrBusiPub.qrBookData(tpID, qrBook, platDate, seqNo);

	    qrBook.setResp_code("00");
	    qrBook.setResp_msg("接收c2b交易通知成功");

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

}
