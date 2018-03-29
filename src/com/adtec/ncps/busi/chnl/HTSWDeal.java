package com.adtec.ncps.busi.chnl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HTSWDeal {
	public static int htswDEAL() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			SysPub.appLog("INFO", "执行 signDeal 方法开始");
			// String svcName = dtaInfo.getSvcName();

			String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
			svcName1 = svcName1.toUpperCase();
			SysPub.appLog("INFO", "svcName1=" + svcName1);
			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			SysPub.appLog("INFO", "svcName2=" + svcName);
			svcName = svcName.toUpperCase();

			String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
			String cltReq = "OBJ_HTSWITCH_" + svcName + "_REQ";
			String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";
			String cltRes = "OBJ_HTSWITCH_" + svcName + "_RES";

			SysPub.appLog("INFO", "svcName=" + svcName);
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);

			String txDate = PubTool.getDate8();

			String txTimestamp = PubTool.getDate17();

			Object[] value1 = null;
			/* 登记流水 */
			String platDate = PubTool.getDate8();

			int platSeq = PubTool.sys_get_seq10();

			String txCode = (String) EPOper.get(tpID, svrReq + "[0].tx_code");
			String serialNumber = (String) EPOper.get(tpID, svrReq + "[0].cd[0].serialNumber");

			String termNO = "HTSWITCH";
			String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");

			/* 报文体 */
			if ("GW1025".equalsIgnoreCase(svcName)) {
				EPOper.put(tpID, cltReq + "[0].HT_CODE", "H020");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].serialNumber", cltReq + "[0].CLZ");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].OLZ", cltReq + "[0].OLZ");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].Amt", cltReq + "[0].KKA");
				Object[] tmp = { platDate, platSeq, termNO, estwSeq, svcName, txDate };

				String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
				SysPub.appLog("INFO", szSql1);
				DataBaseUtils.execute(szSql1, tmp);
			}
			if ("GW1024".equalsIgnoreCase(svcName)) {
				EPOper.put(tpID, cltReq + "[0].HT_CODE", "H011");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].payCostNo", cltReq + "[0].DATA_REQ[0].JFH");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].serialNumber", cltReq + "[0].DATA_REQ[0].CLZ");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].queryType", cltReq + "[0].DATA_REQ[0].CXFS");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].date", cltReq + "[0].DATA_REQ[0].FYNY");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].channel", cltReq + "[0].DATA_REQ[0].YWQD");

				Object[] tmp = { platDate, platSeq, termNO, estwSeq, svcName, txDate };

				String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
				SysPub.appLog("INFO", szSql1);
				DataBaseUtils.execute(szSql1, tmp);
			}
			if ("GW1023".equalsIgnoreCase(svcName)) {
				EPOper.put(tpID, cltReq + "[0].HT_CODE", "H010");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].payCostNo", cltReq + "[0].DATA_REQ[0].JFH");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].serialNumber", cltReq + "[0].DATA_REQ[0].CLZ");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].channel", cltReq + "[0].DATA_REQ[0].YWQD");

				Object[] tmp = { platDate, platSeq, termNO, estwSeq, svcName, txDate };

				String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
				SysPub.appLog("INFO", szSql1);
				DataBaseUtils.execute(szSql1, tmp);
			}
			if ("GW1021".equalsIgnoreCase(svcName)) {
				EPOper.put(tpID, cltReq + "[0].CBE_CODE", "C010");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].sdNo", cltReq + "[0].DATA_REQ[0].JFH");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].sdFlag", cltReq + "[0].DATA_REQ[0].SDBZ");

				Object[] tmp = { platDate, platSeq, termNO, estwSeq, svcName, txDate };

				String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
				SysPub.appLog("INFO", szSql1);
				DataBaseUtils.execute(szSql1, tmp);
			}

			SysPub.appLog("INFO", "预计流水完成！" + txCode);
			EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
			try {
				DtaTool.call("WTDS_CLT", svcName);

			} catch (Exception e) {
				SysPub.appLog("ERROR", "调用电票服务失败");
			}
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);

			// String szRetCd = (String) EPOper.get(tpID, cltRes +
			// "[0].header[0].retCode"); // 响应代码
			// if (StringTool.isNullOrEmpty(szRetCd)) {
			//
			// EPOper.copy(tpID, tpID, "__PLAT_FLOW.__ERR_CODE", cltRes +
			// "[0].header[0].retCode");
			// EPOper.copy(tpID, tpID, "__PLAT_FLOW.__ERR_MSG", cltRes +
			// "[0].header[0].retMessage");
			// }
			// /* 报文头 */
			// EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes +
			// "[0].tx_code");
			// EPOper.copy(tpID, tpID, cltRes + "[0].check_result", svrRes +
			// "[0].hostReturnCode");
			// EPOper.copy(tpID, tpID, cltRes + "[0].header[0].retMessage",
			// svrRes + "[0].hostErrorMessage");

			/* 报文体 */
			if ("GW1025".equalsIgnoreCase(svcName)) {
				EPOper.copy(tpID, tpID, cltRes + "[0].RCLZ", svrRes + "[0].cd.rSerialNumber");
				EPOper.copy(tpID, tpID, cltRes + "[0].OCLZ", svrRes + "[0].cd.OCLZ");
				EPOper.copy(tpID, tpID, cltRes + "[0].RAMT", svrRes + "[0].cd.RAMT");
			}

			if ("GW1023".equalsIgnoreCase(svcName)) {

				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].JFH", svrRes + "[0].cd.payCostNo");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].RCLZ", svrRes + "[0].cd.rSerialNumber");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].OCLZ", svrRes + "[0].cd.OCLZ");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].CLM", svrRes + "[0].cd.name");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].ADDR", svrRes + "[0].cd.address");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].TEL", svrRes + "[0].cd.mobileNo");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].TNUM", svrRes + "[0].cd.totalNum");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].TQFM", svrRes + "[0].cd.totalAmt");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].TWYJ", svrRes + "[0].cd.TWYJ");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].QFCNT", svrRes + "[0].cd.QfdetailNum");

			}
			if ("GW1024".equalsIgnoreCase(svcName)) {

				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].JFH", svrRes + "[0].cd.payCostNo");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].RCLZ", svrRes + "[0].cd.rSerialNumber");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].OCLZ", svrRes + "[0].cd.OCLZ");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].CLM", svrRes + "[0].cd.name");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].ADDR", svrRes + "[0].cd.address");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].QFCNT", svrRes + "[0].cd.QfdetailNum");

			}

			if ("GW1021".equalsIgnoreCase(svcName)) {

				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].QFI", svrRes + "[0].cd.address");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].CLM", svrRes + "[0].cd.name");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].QFM", svrRes + "[0].cd.sdbalance");
				EPOper.copy(tpID, tpID, cltRes + "[0].DATA_RES[0].CLZ", svrRes + "[0].cd.traceNo");

			}
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			/* 更新返回码 */
			String retCode = (String) EPOper.get(tpID, cltRes + "[0].check_result");
			// String retMsg = (String) EPOper.get(tpID, cltRes +
			// "[0].header[0].retMessage");
			String szSqlStr = "UPDATE t_jrnl  SET ret_code=?  WHERE plat_date = ? and seq_no = ? 	";
			Object[] value = { retCode, platDate, platSeq };
			DataBaseUtils.execute(szSqlStr, value);

			SysPub.appLog("INFO", "更新业务状态完成");

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 deal_jrnl 方法失败");
			throw e;
		}

	}

	/**
	 * 返回的标记报文格式化
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static void jsonHTFMTFormatOut() throws UnsupportedEncodingException {

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		int len = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		// TrcLog.log("Tongeasy.log",
		// "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0] len=="+len+"]" , new
		// Object[0]);

		String tmp = new String(srcXml);

		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "\",\"", "\",\"#");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "}\"", "}\"#");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		tmp = StringUtils.replace(tmp, "{\"", "{\"#");

		len = tmp.length();
		byte[] buf1 = new byte[len];
		buf1 = tmp.getBytes();
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", buf1);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", len);

	}

	public static void jsonHTFMTFormatIn() throws UnsupportedEncodingException {
 
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
	
		// byte srcXml[] = (byte[])
		// EPOper.get(tpID,"__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		int len = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		String tmp = new String(srcXml,"GB2312");

		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "\",\"#", "\",\"");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "}\"#", "}\"");
		
		tmp = StringUtils.replace(tmp, "{\"#", "{\"");

		tmp = StringUtils.replace(tmp, ",\"#", ",\"");
		
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
		if("GW1023".equalsIgnoreCase(svcName1)){
			
			String dd=DoDataRes(tmp);
			tmp = null;
			tmp = dd;
			TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
			TrcLog.log("htsw.log", "[" + dd + "]", new Object[0]);
		}
		len = tmp.length();
		byte[] buf1 = new byte[len];
		buf1 = tmp.getBytes("GB2312");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", buf1);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", tmp.getBytes("GB2312").length);

		// EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", buf2);
		// EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", len-5);

	}

	/**
	 * 无卡支付管理端协议请求报文长度处理
	 */
	public static byte[] setHTPFMTOutLength() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		// EPOper.put(tpID, "TRM_SVR_IN[0].AllLEN[0]", length + 60);
		byte[] bytes = String.valueOf(length + 4).getBytes();
		return bytes;

	}
	
	private static String DoDataRes(String jsonStr){
      JSONObject jsonObj = JSON.parseObject(jsonStr);
		

		Map map = new HashMap();

		for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			if (!"DATA_RES".equals(entry.getKey())){
			     map.put(entry.getKey(), entry.getValue());
			}
		}

		JSONObject objDataRes = (JSONObject) jsonObj.get("DATA_RES");
		String szCnt = objDataRes.getString("QFCNT");
		int cnt = Integer.valueOf(szCnt);
		Map mapDataRes = new HashMap();
		// JSONObject jsonObj1 = JSON.parseObject(tt);
		for (Map.Entry<String, Object> entry : objDataRes.entrySet()) {
			// System.out.println(entry.getKey() + ":" + entry.getValue());
			for (int i = 0; i < cnt; i++) {
				String szQuery = "QFD" ;
			 //	System.out.println(szQuery);
				int flag = entry.getKey().indexOf(szQuery);
				if (flag ==-1 &&!"QFDCNT".equals(entry.getKey())){
					mapDataRes.put(entry.getKey(), entry.getValue());
				  
				   TrcLog.log("htsw.log", "[%s]%s%s",szQuery, entry.getKey(), entry.getValue());
				}
			}

		}
		String szDataRes = JSON.toJSONString(mapDataRes, true);
		System.out.println(szDataRes);
		JSONObject jsonDataRes = JSON.parseObject(szDataRes);
		
		
		String szRoot = JSON.toJSONString(map, true);
		JSONObject jsonRoot = JSON.parseObject(szRoot);
		
		jsonRoot.put("DATA_RES", jsonDataRes);
		
		
		System.out.println(jsonRoot.toJSONString());
		return jsonRoot.toJSONString();
	}

	public static void main(String[] args) {
		String jsonStr = "{\"RCODE\":\"000\",\"RMSG\":\"操作成功\",\"DATA_RES\":{\"JFH\":\"111111\",\"RCLZ\":\"2017122900001245\",\"OCLZ\":\"20170726150023PB123690\",\"CLM\":\"张华\",\"ADDR\":\"东福小区四区10-2-23-2\",\"TEL\":\"13642379659\",\"TNUM\":\"6\",\"TQFM\":\"18.00\",\"TWYJ\":\"0.00\",\"TBAL\":\"0.00\",\"QFCNT\":\"1\",\"QFD0\":{\"YF\":\"2017-12\",\"YFQS\":\"75\",\"YFZS\":\"81\",\"YFSL\":\"6\",\"YFJE\":\"18.00\",\"YFWYJ\":\"0.00\"}}}";
		String szstr = "{ \"tx_code\":\"gw5503\", \"cd\":{\"INVENTORY_AMOUNT\":\"0\",\"SIGN_TELLER\":\"000929\",\"SIGN_MECHANISM\":\"00001\"},\"mac\":\"CEFDEB8FB590DCDE2115913AD4975B23\"}";
		String szStr1 = "{\"cd\":{\r\n                \"accountNo\":\"5002418200011\"\r\n        }}";
		String szStr2 = "{\"cd\":{\r\n                       }}";
		System.out.println("无序遍历结果：");
		JSONObject jsonObj = JSON.parseObject(jsonStr);
		

		Map map = new HashMap();

		for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			if (!"DATA_RES".equals(entry.getKey())){
			     map.put(entry.getKey(), entry.getValue());
			}
		}

		JSONObject tt = (JSONObject) jsonObj.get("DATA_RES");
		String szCnt = tt.getString("QFCNT");
		int cnt = Integer.valueOf(szCnt);
		Map map1 = new HashMap();
		// JSONObject jsonObj1 = JSON.parseObject(tt);
		for (Map.Entry<String, Object> entry : tt.entrySet()) {
			// System.out.println(entry.getKey() + ":" + entry.getValue());
			for (int i = 0; i < cnt; i++) {
				String szQuery = "QFD" + i;
			//	System.out.println(szQuery);
				if (!szQuery.equals(entry.getKey())){
					map1.put(entry.getKey(), entry.getValue());
				//System.out.println(entry.getKey() + ":" + entry.getValue());
				}
			}

		}
		String tt1 = JSON.toJSONString(map1, true);
		System.out.println(tt1);
		JSONObject jsonObj2 = JSON.parseObject(tt1);
		
		
		String tt2 = JSON.toJSONString(map, true);
		JSONObject jsonObj3 = JSON.parseObject(tt2);
		
		jsonObj3.put("DATA_RES", jsonObj2);
		
		
		System.out.println(jsonObj3.toJSONString());
		for (Map.Entry<String, Object> entry : jsonObj3.entrySet()) {
			// System.out.println(entry.getKey() + ":" + entry.getValue());
			// map.put(entry.getKey(), entry.getValue());
		}
		// System.out.println("-------------------");
		// System.out.println("有序遍历结果：");
		// JSONObject jsonMap = JSON.parseObject(jsonStr);
		// for (Entry<String, Object> entry : jsonMap.entrySet()) {
		// System.out.println(entry.getKey() + ":" + entry.getValue());
		// }

		// String szCD = jsonObj.get("cd").toString();

		// String szMac = MD5Utils.snccbbMD5(szCD);
		// System.out.println("-------------------"+szMac);
	}

}
