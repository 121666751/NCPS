package com.adtec.ncps;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.qry.SQRYPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.respool.StarringSeq;
import com.adtec.starring.struct.dta.DtaInfo;
import it.sauronsoftware.ftp4j.FTPClient; 
import com.adtec.ncps.ftp.FTPToolkit; 

/**
 * 柜面渠道公共方法
 * 
 * @author GuoFan
 *
 */
public class TermPubBean {

	/**
	 * 组织柜面协议报文
	 */
	public static void setTermPFMT() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
		EPOper.put(tpID, "TERM_SVR_IN_OUT[0].FMT_TYPE[0]", "RS");// 报文类型 RS输出报文
																	// RQ输入报文
		EPOper.copy(tpID, tpID, "TERM_" + svcName + "_OUT[0].FileFlag[0]", "TERM_SVR_IN_OUT[0].FILE_FLAG[0]"); // 文件标识

	}

	/**
	 * 柜面公共响应报文
	 * 
	 * @param element
	 *            返回对象名称
	 * @param txCode
	 *            交易码
	 * @param brc
	 *            机构号
	 * @param teller
	 *            柜员号
	 */
	public static void termPublicResponseFmt(String element, String txCode, String brc, String teller) {
		// DtaInfo dtaInfo = DtaInfo.getInstance();
		// 获取内部服务码
		// String svcName = dtaInfo.getSvcName();
		// 组织柜面公共响应报文
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		EPOper.put(tpID, element + "[0].RspCode", "000000"); // 返回码
		EPOper.put(tpID, element + "[0].RspMsg", "交易成功"); // 返回信息
		// DataBaseTool.copyValue(
		// svcName+"_IN.TranDate",element+"[0].TranDate"); // 账务日期
		EPOper.put(tpID, element + "[0].SerSeqNo", ""); // 核心流水号
		EPOper.put(tpID, element + "[0].FileFlag", 2); // 文件标识
		EPOper.put(tpID, element + "[0].FileName", txCode + brc + teller); // 文件名
		EPOper.put(tpID, element + "[0].InqFormid", txCode); // 文件格式类型
		EPOper.put(tpID, element + "[0].Rmak1", ""); // 备注1
		EPOper.put(tpID, element + "[0].Rmak2", ""); // 备注2
		EPOper.put(tpID, element + "[0].PlatSeqNo", PubTool.sys_get_seq());// 前置流水号
	}

	/**
	 * 柜面协议报文长度处理
	 */
	public static byte[] setTermPFMTLength() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID, "TERM_SVR_IN_OUT[0].__GDTA_ITEMDATA_LENGTH[0]");
		EPOper.put(tpID, "TERM_SVR_IN_OUT.FMT_LENGTH", length + 58);
		byte[] bytes = String.valueOf(length + 58).getBytes();
		return bytes;

	}

	/**
	 * 获取渠道号并赋值给公共数据对象
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static void getChnNo() throws UnsupportedEncodingException {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();

		EPOper.put(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].BkData[0].ChnlNo", "12");
	}

	/**
	 * 柜面渠道 获取错误码和错误信息
	 */
	public static void getTermErrMsg() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		// 获取内部服务码
		String svcName = dtaInfo.getSvcName();
		String errRet = "";
		String errMsg = "";
		try {
			String errRetObj = (String) EPOper.get(DtaInfo.getInstance().getTpId(), "__PLAT_FLOW[0].__ERR_CODE[0]");
			String errMsgObj = (String) EPOper.get(DtaInfo.getInstance().getTpId(), "__PLAT_FLOW[0].__ERR_MSG[0]");
			String errRetObj1 = (String) EPOper.get(DtaInfo.getInstance().getTpId(), "__GDTA_FORMAT[0].__ERR_RET[0]");
			String errMsgObj1 = (String) EPOper.get(DtaInfo.getInstance().getTpId(), "__GDTA_FORMAT[0].__ERR_MSG[0]");
			if (errRetObj != null && !errRetObj.isEmpty() && errMsgObj != null && !errMsgObj.isEmpty()) {
				errRet = (String) errRetObj;
				errMsg = (String) errMsgObj;
			} else {
				errRet = (String) errRetObj1;
				errMsg = (String) errMsgObj1;

			}

		} catch (Exception e) {

			throw new BaseException("P10000", e.getMessage());
		} finally {
			EPOper.put(DtaInfo.getInstance().getTpId(), "TERM_" + svcName + "_OUT[0].RspCode", errRet);
			EPOper.put(DtaInfo.getInstance().getTpId(), "TERM_" + svcName + "_OUT[0].RspMsg", errMsg);
			EPOper.put(DtaInfo.getInstance().getTpId(), "__MONS_TRAN[0].__RETURN_CODE[0]", errRet);
			EPOper.put(DtaInfo.getInstance().getTpId(), "__MONS_TRAN[0].__RETURN_DESC[0]", errMsg);
		}
	}

	/**
	 * 柜面渠道相应码转换
	 * 
	 */
	public static void putTermRspCode(String code, String msg) {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 获取内部服务码
		String svcName = dtaInfo.getSvcName();
//		String svrName6 = svcName.substring(6, 12);
//		String NewsvrName = "NCP_QRY_" + svrName6 + "";
//		EPOper.put(tpID, "TERM_" + NewsvrName + "_OUT[0].RspMsg", msg);
//		EPOper.put(tpID, "TERM_" + NewsvrName + "_OUT[0].RspCode", code);
		EPOper.put(tpID,"ISO_8583[0].iso_8583_012", code);
		EPOper.put(tpID,"ISO_8583[0].iso_8583_013", msg);
		
		EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG", msg);
		EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE", code);
	}

	/**
	 * 给监控数据对象赋值
	 */
	public static void setMonsTranData() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		int seq = StarringSeq.getCustomSeq("1");// 取序号发生器生成的值
		String nodeName = dtaInfo.getNodeName(); // 取机器节点号
		int nodeNo = Integer.parseInt(nodeName); // 转化为整型的节点号
		seq = nodeNo * 10000000 + seq;// 根据节点号、序号发生器产生的值生成最终的流水号
		// String svcName = dtaInfo.getSvcName();//获取服务名
		// String channelId =
		// (String)DataBaseTool.get("TERM_"+svcName+"_IN[0].ChannelId[0]");//获取渠道号
		// String busSeqNo = channelId +
		// DateTool.getNowDate("yyyyMMdd")+String.valueOf(seq);//生成全局流水号
		String busSeqNo = PubTool.getDate8() + String.valueOf(seq);// 生成全局流水号
		dtaInfo.getDrqInfo().setBusSeqNo(busSeqNo);// 给全局流水号赋值
		// DataBaseTool.copyValue(
		// "TERM_"+svcName+"_IN[0].Brc[0]","__MONS_TRAN[0].__BRCH_NO[0]");//给机构号赋值
		// DataBaseTool.copyValue("TERM_"+svcName+"_IN[0].Teller[0]","__MONS_TRAN[0].__TELL_NO[0]");//给柜员号赋值
	}

	/*
	 * 返回柜面日期
	 */
	public static void returnTermDate() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();// 获取内部服务码
		String tranDate = (String) EPOper.get(tpID, "TERM_" + svcName + "_IN[0].TermDate");// 取出交易日期
		if (tranDate != null && !tranDate.isEmpty()) {
			EPOper.put(tpID, "TERM_" + svcName + "_OUT[0].TermDate", tranDate);// 返回柜面上送日期
		} else {
			String Date = PubTool.getDate10();
			EPOper.put(tpID, "TERM_" + svcName + "_OUT.TermDate", Date);// 返回系统日期
		}
	}

	/*
	 * 柜面交易前处理
	 */
	public static void ecapTermFormat(String msg) {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();// 获取内部服务码
		String svrName6 = svcName.substring(6, 12);
		String NewsvrName = "NCP_QRY_" + svrName6 + "";
		int iPlatSeq = PubTool.sys_get_seq();
		EPOper.put(szTpID, "TERM_" + NewsvrName + "_OUT[0].RspCode", "99999");
		EPOper.put(szTpID, "TERM_" + NewsvrName + "_OUT[0].RspMsg", msg);
		EPOper.put(szTpID, "TERM_" + NewsvrName + "_OUT[0].TermDate", PubTool.getDate10());
		EPOper.put(szTpID, "TERM_" + NewsvrName + "_OUT[0].TermSeq", iPlatSeq);
		EPOper.put(szTpID, "TERM_" + NewsvrName + "_OUT[0].TermTime", PubTool.getTime());
	}

	public static void Sendfile() throws UnsupportedEncodingException {

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		String fileName = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_025");
		String filePath = SysDef.WORK_DIR + ResPool.configMap.get("FilePath");

		String szLocalName = filePath + fileName;
		TrcLog.log("te.log", "szLocalName==" + szLocalName, new Object[0]);
		File sourpath = new File(szLocalName);
		if (sourpath.exists()) {

			//FTPToolkit ftp = new FtpToolkit();

			String szHost = ResPool.configMap.get("MNG_SVR_ipAddress");

			int port = Integer.valueOf(ResPool.configMap.get("MNG_SVR_port"));

			String szUser = ResPool.configMap.get("MNG_SVR_userName");

			String szPwd = ResPool.configMap.get("MNG_SVR_userPassword");

			boolean bPass = true;
			TrcLog.log("ftp.log", "host" + szHost+"port"+port+"szUser"+szUser+"szPwd"+szPwd+"bPass"+bPass, new Object[0]);
			
			FTPClient client = FTPToolkit.makeFtpConnection(szHost, port, szUser, szPwd); 
			//ftp.makeFtpConnection(szHost, port, szUser, szPwd, bPass);
			
			//String szRemoteName = "/home/snqt/tmp/" + fileName;

			//ftp.upload(szLocalName, szRemoteName);
            FTPToolkit.upload(client, szLocalName, "/home/snqt/tmp"); 
           // FTPToolkit.download(client, "/home/snqt/tmp/splash.bmp", "D:\\"); 
            FTPToolkit.closeConnection(client); 

			//ftp.closeConnection();
		}

	}

}