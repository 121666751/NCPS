package com.adtec.ncps.busi.chnl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.chnl.utils.CryptoTools;
import com.adtec.ncps.busi.chnl.utils.MD5Utils;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;

/**
 * 理财产品逻辑处理类
 * 
 **/
public class FPDeal {

	/**
	 * 理财产品交易处理总方法
	 * 
	 **/
	public static int FP_DEAL_SWITCH() throws Exception {
		int result = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
		if (svcName.equals("gw6001")) {
			SysPub.appLog("INFO", "调用 FPS_141001DEAL方法开始");
			result = FPS_141001DEAL();

		} else if (svcName.equals("gw6002")) {
			SysPub.appLog("INFO", "调用 FPS_141002DEAL方法开始");
			result = FPS_141002DEAL();
		}

		return result;
	}

	/**
	 * 理财产品查询FPS_141001 接收报文D0050 交易日期，D0160 交易代码
	 * 
	 **/

	public static int FPS_141001DEAL() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			SysPub.appLog("INFO", "执行 signDeal 方法开始");
			// String svcName = dtaInfo.getSvcName();

			String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
			SysPub.appLog("INFO", "svcName1=" + svcName1);
			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			SysPub.appLog("INFO", "svcName2=" + svcName);

			// String svcName = (String) EPOper.get(tpID, svrReq +
			// "[0].cd[0].serviceCode");
			String svrReq = "OBJ_EBANK_SVR_" + "GW6001" + "_REQ";
			String cltReq = "OBJ_FPS_141001_CON_REQ";
			String svrRes = "OBJ_EBANK_SVR_" + "GW6002" + "_RES";
			String cltRes = "OBJ_FPS_141001_RES";

			SysPub.appLog("INFO", "svcName=" + svcName);
			SysPub.appLog("INFO", "svrReq= " + svrReq);
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);

			SysPub.appLog("INFO", "复制svrReq成功");
			/**
			 * 客户端请求报文头
			 **/
			// EPOper.put(tpID, cltReq +
			// "[0].OBJ_FINAN_header[0].requestBranchId", "00110"); // 交易机构
			// EPOper.put(tpID, cltReq +
			// "[0].OBJ_FINAN_header[0].requestBranchId", "00110"); // 交易机构
			// EPOper.put(tpID, cltReq +
			// "[0].OBJ_FINAN_header[0].requestBranchId", "00110"); // 交易机构
			// EPOper.put(tpID, cltReq +
			// "[0].OBJ_FINAN_header[0].requestBranchId", "00110"); // 交易机构
			// EPOper.put(tpID, cltReq +
			// "[0].OBJ_FINAN_header[0].requestBranchId", "00110"); // 交易机构
			EPOper.put(tpID, cltReq + "[0].D0031", "50001"); // 机构编码
			EPOper.put(tpID, cltReq + "[0].D0071", "500024"); // 柜员代码
			EPOper.put(tpID, cltReq + "[0].D0039", "8"); // 交易区域代码

			EPOper.put(tpID, cltReq + "[0].D0050", "20171211"); // 交易日期
			EPOper.put(tpID, cltReq + "[0].D0160", "FPS_141001"); // 交易代码

			String txDate = PubTool.getDate8();
			// EPOper.put(tpID, cltReq + "[0].header[0].requestBusiDate",
			// txDate); //交易机构
			String txTimestamp = PubTool.getDate17();

			/* 报文体 */
			// EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].ID", cltReq +
			// "[0].id_no");
			// SysPub.appLog("INFO", "复制cltReq[0].id_no成功" );
			// EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].NAME", cltReq +
			// "[0].name");
			// SysPub.appLog("INFO", "复制cltReq[0].name成功" );

			/* 登记流水 */
			String platDate = PubTool.getDate8();
			// String strTime = PubTool.getTime();
			int platSeq = PubTool.sys_get_seq10();
			// String brchNo = (String) EPOper.get(tpID, svrReq +
			// "[0].cd[0].requestBranchId");
			// String txDate = (String) EPOper.get(tpID, svrReq +
			// "[0].cd[0].requestBusiDate");
			// String txTime = (String) EPOper.get(tpID, svrReq +
			// "[0].cd[0].requestTimestamp");
			// String tellerNo = (String) EPOper.get(tpID, cltReq +
			// "[0].header[0].teller");
			String txCode = (String) EPOper.get(tpID, svrReq + "[0].tx_code");
			// String chNo = (String) EPOper.get(tpID, svrReq +
			// "[0].cd[0].channelNo");
			// String reqNo = (String) EPOper.get(tpID, svrReq +
			// "[0].cd[0].requestSeqNo");
			String termNO = "FP";
			String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");
			SysPub.appLog("INFO", "获取estwSeq成功");
			String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
			SysPub.appLog("INFO", szSql1);

			// Object[] value1 = { platDate, platSeq, brchNo, tellerNo, termNO,
			// chNo, estwSeq, svcName,txDate,txTime,reqNo };
			Object[] value1 = { platDate, platSeq, termNO, estwSeq, svcName, txDate };
			DataBaseUtils.execute(szSql1, value1);
			SysPub.appLog("INFO", "预计流水完成！" + txCode);
			EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
			SysPub.appLog("INFO", "复制OBJ_ALA_abstarct_REQ[0].req成功");
			try {
				DtaTool.call("FP_CLI", "FPS_141001");

			} catch (Exception e) {
				SysPub.appLog("ERROR", "调用金融产品查询失败");
			}

			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
			// SysPub.appLog("INFO", "OBJ_ALA_abstarct_RES[0].res的值是"+cltRes );
			// String szRetCd = (String) EPOper.get(tpID, cltRes +
			// "[0].header[0].retCode"); // 响应代码
			// if (StringTool.isNullOrEmpty(szRetCd)) {
			//
			//
			// EPOper.copy(tpID, tpID,"__PLAT_FLOW.__ERR_CODE", cltRes +
			// "[0].header[0].retCode");
			// EPOper.copy(tpID, tpID, "__PLAT_FLOW.__ERR_MSG",cltRes +
			// "[0].header[0].retMessage");
			// }
			EPOper.put(tpID, svrRes + "[0].tx_code", "GW6001"); // 交易日期
			EPOper.copy(tpID, tpID, cltRes + "[0].D0120", svrRes + "[0].hostReturnCode");

			EPOper.copy(tpID, tpID, cltRes + "[0].D0130", svrRes + "[0].hostErrorMessage");

			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			/*
			 * 更新返回码 String retCode = (String) EPOper.get(tpID, cltRes +
			 * "[0].check_result"); // String retMsg = (String) EPOper.get(tpID,
			 * cltRes + "[0].header[0].retMessage"); String szSqlStr =
			 * "UPDATE t_jrnl  SET ret_code=?  WHERE plat_date = ? and seq_no = ? 	"
			 * ; Object[] value = { retCode, platDate,platSeq };
			 * DataBaseUtils.execute(szSqlStr, value);
			 */

			SysPub.appLog("INFO", "更新业务状态完成");

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 deal_jrnl 方法失败");
			throw e;
		}

	}

	/**
	 * 理财账户查询FPS_141002
	 * 
	 **/
	public static int FPS_141002DEAL() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			SysPub.appLog("INFO", "执行 signDeal 方法开始");

			String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
			SysPub.appLog("INFO", "svcName1=" + svcName1);
			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			SysPub.appLog("INFO", "svcName2=" + svcName);
			String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
			String cltReq = "OBJ_FPS_141002_REQ";
			String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";
			String cltRes = "OBJ_FPS_141002_RES";

			SysPub.appLog("INFO", "svcName=" + svcName);
			SysPub.appLog("INFO", "svrReq= " + svrReq);
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);

			SysPub.appLog("INFO", "复制svrReq成功");
			/**
			 * 客户端请求报文
			 **/
			EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].custNo", cltReq + "[0].D0380");// 客户号
			EPOper.put(tpID, cltReq + "[0].D0160", "FPS_141002"); // 交易代码
			EPOper.put(tpID, cltReq + "[0].D0710", "2"); // 资金账户类型默认为2
			EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].custNo", cltReq + "[0].D0440");// 资金账户号码
			EPOper.put(tpID, cltReq + "[0].D0460", "1"); // 资金账号序号默认为1
			EPOper.put(tpID, cltReq + "[0].D101A", "1"); // 理财账户类型默认为1，自营理财产品

			String txDate = PubTool.getDate8();
			String txTimestamp = PubTool.getDate17();
			/* 登记流水 */
			String platDate = PubTool.getDate8();
			int platSeq = PubTool.sys_get_seq10();
			String txCode = (String) EPOper.get(tpID, svrReq + "[0].tx_code");
			String termNO = "FP";
			String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");
			SysPub.appLog("INFO", "获取estwSeq成功");
			String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
			SysPub.appLog("INFO", szSql1);

			Object[] value1 = { platDate, platSeq, termNO, estwSeq, svcName, txDate };
			DataBaseUtils.execute(szSql1, value1);
			SysPub.appLog("INFO", "预计流水完成！" + txCode);
			EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
			SysPub.appLog("INFO", "复制OBJ_ALA_abstarct_REQ[0].req成功");
			try {
				DtaTool.call("FP_CLI", "FPS_141002");

			} catch (Exception e) {
				SysPub.appLog("ERROR", "调用金融账户查询失败");
			}

			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
			// }
			EPOper.put(tpID, svrRes + "[0].tx_code", "GW6002"); // 交易日期
			EPOper.copy(tpID, tpID, cltRes + "[0].D0120", svrRes + "[0].hostReturnCode");

			EPOper.copy(tpID, tpID, cltRes + "[0].D0130", svrRes + "[0].hostErrorMessage");

			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			// 更新返回码
			String retCode = (String) EPOper.get(tpID, cltRes + "[0].check_result");
			// String retMsg = (String) EPOper.get(tpID, cltRes +
			// "[0].header[0].retMessage");
			String szSqlStr = "UPDATE t_jrnl  SET ret_code=?  WHERE plat_date = ? and seq_no = ? 	";
			Object[] value = { retCode, platDate, platSeq };
			DataBaseUtils.execute(szSqlStr, value);

			SysPub.appLog("INFO", "更新业务状态完成");
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 FPS_141002DEAL 方法失败");
			throw e;
		}

	}

	/**
	 * 获取 整个报文的长度
	 * 
	 * 
	 **/
	public static byte[] getAlllength() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID, "FP_CLI_IN[0].__GDTA_ITEMDATA_LENGTH[0]");
		EPOper.put(tpID, "FP_CLI_IN[0].MsgLengh[0]", length + 64);
		byte[] bytes = String.valueOf(length + 64).getBytes();
		return bytes;
	}

	/**
	 * 处理接收到报文后报文中的名称乱码
	 * 
	 **/

	public static int resDeal() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			String name = ((String) EPOper.get(tpID, "OBJ_IDENTY_GW5506_RES.name")).trim();// 名字
			String photo_file = ((String) EPOper.get(tpID, "OBJ_IDENTY_GW5506_RES.photo_file")).trim();// 照片文件
			String issue_office = ((String) EPOper.get(tpID, "OBJ_IDENTY_GW5506_RES.issue_office")).trim();// 签发机关

			EPOper.put(tpID, "OBJ_IDENTY_GW5506_RES.name", name); // 名字
			EPOper.put(tpID, "OBJ_IDENTY_GW5506_RES.photo_file", photo_file); // 照片文件
			EPOper.put(tpID, "OBJ_IDENTY_GW5506_RES.issue_office", issue_office); // 签发机关

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 resDeal 方法失败");
			throw e;
		}

	}

	/**
	 * xml报文加密
	 * 
	 **/
	public static int reqencrypt() throws Exception {
		CryptoTools cryptotools = CryptoTools.getInstance();
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte[] xmlreq = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		byte[] szbuf = new byte[xmlreq.length - 8];

		System.arraycopy(xmlreq, 8, szbuf, 0, xmlreq.length - 8);
		byte[] xmlreq2 = cryptotools.encode(szbuf);// 加密之后的xml

		String xmlStr = new String(xmlreq2, "UTF8");
		xmlStr = xmlStr.replaceAll("\r|\n|\t", "");
		int len = xmlStr.length();
		String strlen = String.format("%08d", len);
		String szbuf2 = strlen + xmlStr;

		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		// EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", xmlreq2);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", szbuf2.getBytes());
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		// EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", len);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", szbuf2.length());
		return 0;
	}

	/**
	 * 返回协议报文解密
	 * 
	 **/
	public static int resdecode() throws Exception {
		CryptoTools cryptotools = CryptoTools.getInstance();
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		 String svcName = dtaInfo.getSvcName();

		byte[] res = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		byte[] res2 = cryptotools.decode(res);// 解密之后的协议报文
		String resStr = new String(res2, "GBK");
		System.out.println("协议报文:" + resStr);

		int contentlen = Integer.valueOf(resStr.substring(0, 6));// 交易报文长度
		System.out.println("交易报文长度:" + contentlen);
		String isFIle = resStr.substring(27, 28);// 获取是否有文件标志
		System.out.println("附件标志:" + isFIle);
		String xmlhead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";// xml报文头
		String content = "";
		if (isFIle.equals("1")) {
			content = resStr.substring(64, contentlen + 60);// 交易报文内容
		} else {
			content = resStr.substring(64);// 交易报文内容
		}

		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", content.getBytes("GBK").length);
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", content.getBytes("GBK"));

		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", content.getBytes("GBK"));
		System.out.println("交易报文:" + content);
		System.out.println("交易报文长度:" + content.length());
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", content.getBytes("GBK").length);
		
		EPOper.put(tpID, "PUB_ELEMENT.FILE_FLAG", isFIle);

		if (isFIle.equals("1")) {
			String filestr = resStr.substring(content.getBytes("GBK").length + 64 + 26);
			System.out.println("附件字符串:" + filestr);
			Document document = DocumentHelper.parseText(filestr);
			List<Node> nodes = document.selectNodes("/Table/Rows/row");

			String filePath = SysDef.WORK_DIR + ResPool.configMap.get("FilePath") + "/netbank/file/";
			// int iSeqNo = PubTool.sys_get_seq10();//平台流水号
			String filename = (String) EPOper.get(tpID, "OBJ_"+svcName+"_REQ[0].seqNo");// 文件名赋值

			File f = new File(filePath + filename);
			// EPOper.put(tpID, "OBJ_FPS_141001_RES[0].seqNo",
			// iSeqNo+".txt");//文件名赋值
			byte[] b = { (byte) 0xff }; // 文件分隔符
			String separator = new String(b);

			separator = new String(b);
			// System.out.println(MD5Utils.toHex(separator.getBytes()));
			String fieldlits = "";
			FileOutputStream fos = new FileOutputStream(f);
			int cnt = 0;
			int len = 0;
			for (Node node : nodes) {

				System.out.println("分割后的字符串:" + node.asXML());
				byte[] szStr = new byte[1024];

				List<Node> fileds = (DocumentHelper.parseText(node.asXML())).selectNodes("/row/Fields/field");
				int lens = 0;
				int cnts = 0;
				for (int i = 0; i < fileds.size(); i++) {

					String field = fileds.get(i).asXML().replaceAll("<field>|</field>", "");

					int length = field.getBytes("GB2312").length;
					TrcLog.log("json.log", "[" + field + "] [len]=" + lens + "length=[" + length + "]");
					System.arraycopy(field.getBytes("GB2312"), 0, szStr, lens, length);

					System.arraycopy(b, 0, szStr, lens + length, 1);
					// fieldlits = fieldlits+field+separator;
					cnt = cnt + length + 1;
					cnts = cnts + length + 1;
					lens = cnts;
					// len = cnt;
				}

				fieldlits = "\r\n";

				System.arraycopy(fieldlits.getBytes("GB2312"), 0, szStr, lens, 2);
				cnt = cnt + 2;
				TrcLog.log("json.log", "[len]=" + len + "cnt=[" + cnt + "]");
				fos.write(szStr, 0, lens + 2);

				len = cnt;
			}
			System.out.println("附件内容是:" + fieldlits);

			// BufferedWriter bw= new BufferedWriter(write);
			// bw.write(fieldlits);
			// bw.close();
			fos.close();

		}

		return 0;
	}

}
