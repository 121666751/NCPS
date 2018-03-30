package com.adtec.ncps.busi.chnl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.adtec.ncps.DtaTool;

import com.adtec.ncps.busi.chnl.bean.NetbankConf;
import com.adtec.ncps.busi.chnl.bean.RetMsg;
import com.adtec.ncps.busi.chnl.utils.Digest;
import com.adtec.ncps.busi.chnl.utils.EhcacheUtil;
import com.adtec.ncps.busi.chnl.utils.MD5Utils;
import com.adtec.ncps.busi.chnl.utils.SoftEncrypt;
import com.adtec.ncps.busi.ncp.AmountUtils;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ParmPool;
import com.adtec.starring.respool.PoolOperate;
import com.adtec.starring.respool.SystemParameter;
import com.adtec.starring.struct.admin.DtaParm;
import com.adtec.starring.struct.admin.LogParam;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class PubDeal {

	private static final char[] HEXDIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	/**
	 * toString(控制长度的将byte[]的转换为相应的十六进制String表示)
	 */
	public static String toString(byte[] ba, int offset, int length) {
		char[] buf = new char[length * 2];
		int j = 0;
		int k;
		for (int i = offset; i < offset + length; i++) {
			k = ba[i];
			buf[j++] = HEXDIGITS[(k >>> 4) & 0x0F];
			buf[j++] = HEXDIGITS[k & 0x0F];
		}
		return new String(buf);
	}

	/**
	 * 功能:将byte[]的转换为相应的十六进制字符串
	 * 
	 * @param ba
	 *            字节数组
	 * @return 十六进制字符串
	 */
	public static String toString(byte[] ba) {
		return toString(ba, 0, ba.length);
	}

	public static byte[] fromHex(String hex) throws UnsupportedEncodingException {
		if (hex == null || "".equals(hex)) {
			return null;
		}
		byte[] ba = hex.getBytes("GBK");

		String tmp = toString(ba);

		return tmp.getBytes("GBK");

	}

	/**
	 * 功能:将十六进制字符串转换为字节数组
	 * 
	 * @param hex
	 *            十六进制字符串
	 * @return 字节数组
	 */
	public static byte[] fromString(String hex) {
		if (hex == null || "".equals(hex)) {
			return null;
		}
		int len = hex.length();

		byte[] buf = new byte[(len + 1) / 2];
		int i = 0;
		int j = 0;
		if ((len % 2) == 1) {
			buf[j++] = (byte) fromDigit(hex.charAt(i++));
		}
		while (i < len) {
			buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) | fromDigit(hex.charAt(i++)));
		}
		return buf;
	}

	/**
	 * fromDigit(将十六进制的char转换为十进制的int值)
	 */
	public static int fromDigit(char ch) {
		if (ch >= '0' && ch <= '9') {
			return ch - '0';
		}
		if (ch >= 'A' && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if (ch >= 'a' && ch <= 'f') {
			return ch - 'a' + 10;
		}
		throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
	}

	/*
	 * 
	 */
	public static boolean chkEbillRetCode() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 获取内部服务码
		String svcName = dtaInfo.getSvcName();
		String repFmt = "OBJ_EBILL_CLT_" + svcName + "_ALL_RES[0].header[0].retCode";
		String repcode = (String) EPOper.get(tpID, repFmt);
		if (!"000000".equals(repcode)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 返回的标记报文格式化
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static void httpSvrNoURLGetSvcName() {

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");

		String szName = new String(srcXml);
		;

		JSONObject jsonObject = new JSONObject();

		Map map1 = jsonObject.parseObject(szName, Map.class);

		String szSvcName = (String) map1.get("tx_code");

		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_SVCNAME[0]", szSvcName.toUpperCase());

	}

	/**
	 * cd域计算mac
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static void setJsonCDMac() throws JsonParseException, JsonMappingException, IOException {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte[] cdJson = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		int len = (int) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");
		//TrcLog.log("json.log", MD5Utils.toHex(cdJson));

		String jsonStr = new String(cdJson, "GBK");
		TrcLog.log("json.log", jsonStr);
		String tmp = jsonStr + "}";
		tmp = tmp.replaceAll("\r|\n| |\t", "");
		TrcLog.log("json.log", jsonStr);
		JSONObject jsonObj = JSON.parseObject(tmp);
		TrcLog.log("json.log", tmp);
		Map map = new HashMap();

		for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			if ("mac".equals(entry.getKey())) {
				JSONObject tb = (JSONObject) jsonObj.get("cd");
				String szCD = "";
				if (tb != null && !tb.isEmpty()) {
					szCD = jsonObj.get("cd").toString();
				} else {
					szCD = "{}";
					map.put("cd", "{}");
				}
				String str = new String(szCD.getBytes("GBK"), "GBK");
				String szMac = MD5Utils.snccbbMD5(str);
				map.put(entry.getKey(), szMac);
			} else
				map.put(entry.getKey(), entry.getValue());
		}
		String json = JSON.toJSONString(map, true);
		//json = json.replace("\"null\"", "null");

		// json = json.replaceAll("\r|\n| ", "");
		System.out.println(json);
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");

		int tmpLen = json.length();
		byte[] newJson = new byte[tmpLen];
		newJson = json.getBytes("GBK");
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", newJson);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", newJson.length);

	}

	/**
	 * cd域计算mac
	 * 
	 * @throws Exception
	 */
	public static void checkJsonCDMac() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte[] cdJson = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		int len = (int) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");
		TrcLog.log("json.log", MD5Utils.toHex(cdJson));

		String jsonStr = new String(cdJson, "GBK");
		TrcLog.log("json.log", jsonStr);
		// String tmp = jsonStr + "}";

		JSONObject jsonObj = JSON.parseObject(jsonStr);
		TrcLog.log("json.log", jsonStr);

		String resMac = jsonObj.get("mac").toString();

		JSONObject jsonTp = (JSONObject) jsonObj.get("cd");
		String szCD = "";
		if (jsonTp == null)
			szCD = "{}";
		else
			szCD = jsonObj.get("cd").toString();

		// szCD = "{"+getStrings(jsonStr,1)+"}";
		szCD = szCD.replaceAll("\r|\n| |\t", "");
		TrcLog.log("json.log", szCD);
		if (MD5Utils.MD5Check(resMac, szCD) == false) {
			TrcLog.log("json.log", "MD5Utils.MD5Check false!");

			// throw new Exception("MAC校验失败!");

		}

	}
	public static void setuuid()
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = (String) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_SVCNAME");
		String req = "OBJ_EBANK_SVR_"+svcName +"_REQ[0].uuid";
		String res = "OBJ_EBANK_SVR_"+svcName +"_RES[0].uuid";
		//保持uuid原值返回
		EPOper.copy(tpID, tpID, req, res);
	}

	/**
	 * 无卡支付管理端协议请求报文长度处理
	 */
	public static byte[] setHTPFMTOutLength(int len) {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int length = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		// EPOper.put(tpID, "TRM_SVR_IN[0].AllLEN[0]", length + 60);
		byte[] bytes = String.valueOf(length + len).getBytes();
		return bytes;

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

		String tmp = new String(srcXml, "GB2312");

		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "\",\"", "\",\"#");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "}\"", "}\"#");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		tmp = StringUtils.replace(tmp, "{\"", "{\"#");

		len = tmp.length();
		byte[] buf1 = new byte[len];
		buf1 = tmp.getBytes("GB2312");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", buf1);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", buf1.length);

	}

	private static String DoDataRes(String jsonStr) {
		JSONObject jsonObj = JSON.parseObject(jsonStr);

		Map map = new HashMap();

		for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			if (!"DATA_RES".equals(entry.getKey())) {
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
				String szQuery = "QFD";
				// System.out.println(szQuery);
				int flag = entry.getKey().indexOf(szQuery);
				if (flag == -1 && !"QFDCNT".equals(entry.getKey())) {
					mapDataRes.put(entry.getKey(), entry.getValue());

					TrcLog.log("htsw.log", "[%s]%s%s", szQuery, entry.getKey(), entry.getValue());
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

    /**
     * 服务码信息初始化到cache中
     */
	public static void jsonHTFMTFormatIn() throws UnsupportedEncodingException {

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		// byte srcXml[] = (byte[])
		// EPOper.get(tpID,"__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		int len = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		String tmp = new String(srcXml, "GB2312");

		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "\",\"#", "\",\"");
		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);

		tmp = StringUtils.replace(tmp, "}\"#", "}\"");

		tmp = StringUtils.replace(tmp, "{\"#", "{\"");

		tmp = StringUtils.replace(tmp, ",\"#", ",\"");

		TrcLog.log("htsw.log", "[" + tmp + "]", new Object[0]);
		String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
		if ("GW1023".equalsIgnoreCase(svcName1)) {

			String dd = DoDataRes(tmp);
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
     * 服务码信息初始化到cache中
     */
	public static void NetBankMapInit() throws Exception {
		// DtaInfo dtaInfo = DtaInfo.getInstance();
		// String tpID = dtaInfo.getTpId();

		Object[] value = {};
		String szSql = "select * from t_netbank_conf order by code,type,type_seq";

		Class clazz = Class.forName("com.adtec.ncps.busi.chnl.bean.NetbankConf");
		List<?> listNbc = (List<?>) DataBaseUtils.queryList(szSql, value, clazz);
		int iRet = listNbc.size();
		// System.out.println(listNbc.toString());
		String svcName = "";
		List<NetbankConf> ntc = new ArrayList<NetbankConf>();

		EhcacheUtil ecache = EhcacheUtil.getInstance();

		for (int i = 0; i < listNbc.size(); i++) {
			NetbankConf nb = (NetbankConf) listNbc.get(i);
			if (i == 0) {
				svcName = nb.getCODE();
				ntc.add(nb);
			} else {

				if (!svcName.equalsIgnoreCase(nb.getCODE())) {

					List<NetbankConf> ntcs = new ArrayList<NetbankConf>();

					ntcs.addAll(ntc);

					ecache.put("MapCache", svcName, ntcs);

					TrcLog.log("json.log", "SVCNAME:---" + svcName + "[i]=" + i +"arraysize:"+ ntc.size());
					ntc.clear();
					ntc.add(nb);
					svcName = nb.getCODE();
				} else
					ntc.add(nb);

				if (i == listNbc.size() - 1) {
					svcName = nb.getCODE();
					ecache.put("MapCache", svcName, ntc);
					TrcLog.log("json.log", "SVCNAME:[" + svcName + "] [i]=" + i);
				}
			}

		}

		// 响应码缓存
		NetBankRetInit();

	}
    /**
     * 响应码信息初始化到cache中
     */
	public static void NetBankRetInit() throws Exception {
		// DtaInfo dtaInfo = DtaInfo.getInstance();
		// String tpID = dtaInfo.getTpId();

		Object[] value = {};
		String szSql = "select * from t_ret_msg ";

		Class clazz = Class.forName("com.adtec.ncps.busi.chnl.bean.RetMsg");
		List<?> listNbc = (List<?>) DataBaseUtils.queryList(szSql, value, clazz);
		
		EhcacheUtil ecache = EhcacheUtil.getInstance();

		for (int i = 0; i < listNbc.size(); i++) {
			RetMsg rt = (RetMsg) listNbc.get(i);

			TrcLog.log("json.log", "Name[%s]Value[%s]", rt.getRetName().trim(), rt.getRetMsg().trim());

			ecache.put("RetCache", rt.getRetName().trim(), rt.getRetMsg().trim());

		}

	}

	public static String getStrings1(String str, int n) {
		List<String> result = new ArrayList<String>();
		Pattern p = Pattern.compile("\"tx_code\":\"(.+?)\"");
		Matcher m = p.matcher(str);
		m.find();

		return m.group(1);
	}
    /**
     * 金额转换，金额乘100 
     * @param szAmt 金额
     */
	public static String getStrings(String str, int n) {
		List<String> result = new ArrayList<String>();
		Pattern p = Pattern.compile("\"cd\":\\{(.+?)\\}");
		Matcher m = p.matcher(str);
		m.find();

		return m.group(1);
	}
   
    /**
     * 金额转换，金额乘100 
     * @param szAmt 金额
     */
	public static byte[]ChgY2F(String szAmt) throws UnsupportedEncodingException
	{
		if(szAmt == null||"".equals(szAmt))
			return null;
		String szAmt2=AmountUtils.changeY2F(szAmt);
		
		return szAmt2==null?null:szAmt2.getBytes("GBK");
		
	}
	
    /**
     * 金额转换，金额除100 
     * @param szAmt 金额
     */
	public static byte[]ChgF2Y(String szAmt) throws UnsupportedEncodingException
	{
		if(szAmt == null||"".equals(szAmt))
			return null;
		String szAmt2=AmountUtils.changeY2F(szAmt);
		
		return szAmt2==null?null:szAmt2.getBytes("GBK");
		
		
	}
	
    /**
     * 数据处理 
     * @param elName 数据元素名
     * @param 数据值
     */
	public static void Numbers(String elName, String szNum)  
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		
		if(szNum==null || "".equals(szNum)){
			szNum = "0";
		}
		
		EPOper.put(tpID, elName, szNum);
			
	}

	
	
	
    /**
     * 统一内部错误处理 
     * @param szCode 错误码
     * @param szName 错误名称
     */
	public static void retErrDeal(String szCode, String szName)  
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
        
		String objName1 = "OBJ_EBANK_SVR_"+svcName.toUpperCase()+"_RES[0].hostReturnCode";
		String objName2 = "OBJ_EBANK_SVR_"+svcName.toUpperCase()+"_RES[0].hostErrorMessage";
		String objName3 = "OBJ_EBANK_SVR_"+svcName.toUpperCase()+"_RES[0].tx_code";
		
		EPOper.put(tpID, objName1, szCode);
		EPOper.put(tpID, objName2, szName);
		EPOper.put(tpID, objName3, svcName.toLowerCase());
			
	}

/*
 * 设置服务日志级别，实现host_cli心跳交易不记录日志	
 */
	public void setDtaTranLogParm(int delay){
        if(delay<SystemParameter.DBGTYP_NO && delay>SystemParameter.DBGTYP_DELAY){
            throw new BaseException(SysErr.E_MESSAGE,"日志级别设置错误");
        }
        //SystemParameter.DBGTYP_DELAY;
        DtaInfo dtaInfo = DtaInfo.getInstance();
        LogParam logParam = dtaInfo.getAdapterLogParam();
        //如果日志配置结构类不存在，则重新取值
        if( null==logParam && !StringTool.isNullOrEmpty(dtaInfo.getDtaName()) ){
            ParmPool parmPool = null;
            //如果没有业务版本号，则取当前业务池
            if(null==dtaInfo.getParVersion()){
                parmPool = PoolOperate.getCurrVerParmPool();
            }else{
                parmPool = PoolOperate.getParmPoolByVersion(dtaInfo.getParVersion());
            }
            DtaParm dtaParm = parmPool.getEsadmin().getDtaParm(dtaInfo.getDtaName());
            logParam = dtaParm.getDtaLog();
        }
        //如果取不到dta的日志配置信息，则退出
        if(  null==logParam )
            return ;
        //克隆新的日志配置结构类
        LogParam newLogParam = (LogParam) logParam.objectClone();
        //将日志级别设置成调试
        //newLogParam.setDbgTyp( SystemParameter.DBGTYP_DELAY );
        newLogParam.setDbgTyp( delay );
        //重新设置日志配置信息
        dtaInfo.setAdapterLogParam(newLogParam);
        
    }
	/*
	 *   实现ala-chnl映射到host_cli时关联子格式组织需要顶层对象拷贝
	 *   输入参数   szName(8583子格式名次)
	 *   返回值        
	 */
	public static void ISO8583Copy(String szName)
	{
		 DtaInfo dtaInfo = DtaInfo.getInstance();
		 String tpID = dtaInfo.getTpId();

		 String szObjNme = szName.toUpperCase() +"0";
		// EPOper.copy(tpID,"ISO_8583[0].ISO_8583_1160[0].iso_8583_1164",tpID,"ISO_8583_1160[0].iso_8583_1164");
		 //EPOper.copy(tpID,"ISO_8583[0].ISO_8583_1160[0].iso_8583_1165",tpID,"ISO_8583_1160[0].iso_8583_1165");
		 
		 EPOper.copy(tpID,tpID,"ISO_8583."+szObjNme,szName.toUpperCase());
		 EPOper.put(tpID, "ISO_8583[0]."+szName, "iso");
		// String name  = (String) EPOper.get(tpID, szName.toUpperCase()+"[0].iso_8583_1164");
		 //String name2  = (String) EPOper.get(tpID, szName.toUpperCase()+"[0].iso_8583_1165");
		 //byte[] szName1 = EPOper.epToBuf(tpID);
		 //String sztmp = new String(szName1);
		 //System.out.println(name+"1"+name2);
	}
	
	private static final byte[] DESEDE_KEY = { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, 0x01, 0x23, 0x45, 0x67, (byte) 0x89,
			0x01, 0x23, 0x45, 0x67, (byte) 0x89, 0x01 };

	public static void main(String[] args) throws Exception {
		// NetBankMapInit();
		String jsonStr = "{\r\n        \"tx_code\":\"gw3002\",\r\n\r\n  \"cd\":{\r\n             \"batchNo\":\"201712291000000635\"          },\r\n   \r\n     \"mac\":\"B0AB192186F988222D285D0441F55010\"\r\n}\r\n";
		String szstr = "{ \"tx_code\":\"gw5503\", \"cd\":{\"INVENTORY_AMOUNT\":\"0\",\"SIGN_TELLER\":\"000929\",\"SIGN_MECHANISM\":\"00001\"},\"mac\":\"CEFDEB8FB590DCDE2115913AD4975B23\"}";
		String szStr1 = "{\"accountNo\":\"6210331886000017260\",\"currencyType\":\"\",\"currIden\":\"\"}";

		String szStr2 = "{\"cd\":{\"funCode\":\"13982950360\",\"smsContent\":\"李群飞于2017-08-30 06:37:25向您账户7260发起19500.00元汇款。本短信不作为入账凭证，请查询您的银行账户确认是否入账。\",	\"channel\":\"\"}}";
		String szStr3 = "{\"cd\":null,\"mac\":\"123\"}";
		String szStr4 = "{\n					\"funCode\":\"13982950360\",\n					\"smsContent\":\"李群飞于2017-08-30 06:37:25向您账户7260发起19500.00元汇款。本短信不作为入账凭证，请查询您的银行账户确认是否入账。\",\n					\"channel\":\"\"\n				}";
		byte[] tt1 = szStr2.getBytes("GB2312");
		String t1 = "{\"tx_code\":\"gw1004\",\"sequenceNo\":\"12541236\",\"uuid\":\"f61a2366-3c2b-4346-b620-1638883083be\",\"encoding\":\"UTF-8\",\"cd\":{\"payAccount\":\"6231786680001754484\",\"payAccountName\":\"李群飞\",\"recAccount\":\"6210331886000017260\",\"recAccountName\":\"李群飞\",\"rcvAcctSvcr\":\"402663800013\",\"rcvAcctSvcrName\":\"\",\"openNode\":\"70202\",\"payAmount\":\"19500.00\",\"chargeFee\":\"0\",\"remark\":\"转账\",\"flag\":\"XE00\",\"password\":\"0F81F2AE66ACD80A\",\"operlevel\":\"\",\"transflag\":\"S\"},\"mac\":\"04ACF3C2A0D57A1625D874CBA09A811F\"}";
		System.out.println("无序遍历结果：");
		// String tt = getStrings1(szstr, 1);
		// System.out.println(tt);
		String t3 = "{\"tx_code\":\"gw0005\",\"hostReturnCode\":\"0000\",\"hostErrorMessage\":\"交易成功\",\"uuid\":\"16f998c1-690f-45a8-894c-18a3cd45ea27\",\"cd\":{\"accountNo\":\"6210900300001895980\",\"accountName\":\"赵志茂\",\"openNode\":\"20108\",\"openNodeName\":\"遂宁银行新阳街新城门支行\",\"openDate\":\"20060817\",\"accountState\":\"0\",\"currencyType\":\"01\",\"balance\":\"99.28\",\"balanceAvailable\":\"99.28\",\"intRate\":\"00385000\",\"customerNo\":\"10027556\",\"accountType\":\"1\",\"certType\":\"1\",\"attr\":\"3\",\"certNo\":\"510212196706120850\",\"lockstat\":\"0\"},\"msgReturnFrom\":\"核心系统\",\"mac\":\"CA97A3052E3FE1B118292448604D8A3E\"}";
		String t2 = "{\"tx_code\":\"gw6006\",\"sequenceNo\":\"12541232\",\"uuid\":\"3df9f91b-82eb-4295-a1f1-c03c3263599e\",\"encoding\":\"UTF-8\",\"cd\":{\"custNo\":\"P10027556\"},\"mac\":\"7F09B4077C7DA7DE7E1E486850458469\"}";
		String t4 = "{\"tx_code\":\"gw6002\",\"hostReturnCode\":\"0000\",\"hostErrorMessage\":\"查询成功\",\"uuid\":\"d04dfc09-2286-4339-9f95-32848165478f\",\"cd\":{\"totalRevenue\":\"0.00\",\"financialAccountNo\":\"00000000000000014351\",\"custNo\":\"P10027556\",\"custType\":\"1\",\"custLevel\":\"1\",\"riskLevel\":\"1\",\"custManager\":\"900015\",\"rows\":[{\"productName\":\"遂宁银行“金荷花·聚鑫”2017年第29期理财产品\",\"amt\":\"50000.00\",\"estimateAmt\":\"0.00\",\"estimateDay\":\"20180129\",\"openId\":\"00101\",\"productId\":\"00000000000000000133\",\"productOwnDate\":\"3\",\"productCode\":\"JHHII2017-029\",\"InvestLimitDate\":\"153\",\"YqYearRate\":\"4.00%\",\"StartDate\":\"20170829\",\"EndDate\":\"20180129\",\"buyAccount\":\"00000000000000014351\",\"State\":\"持仓\"}]},\"mac\":\"1C3615D45E025D4B992BE4BE10F2FB03\"}";
		String t5 = "{\"tx_code\":\"gw8203\",\"sequenceNo\":\"12541225\",\"uuid\":\"6aab1deb-aa77-40dd-91de-473ad74ccf31\",\"encoding\":\"UTF-8\",\"cd\":{\"pageNum\":\"1\",\"pageLines\":\"10\",\"termType\":\"2\",\"accountNo\":\"\",\"customerNo\":\"10027556\",\"startNum\":\"1\"},\"mac\":\"BA7D756518145FEEF95190C8BBB4CEFB\"}";
		String t6 = "{\"tx_code\":\"gw0006\",\"sequenceNo\":\"12541221\",\"uuid\":\"01362fa8-8a54-4b4e-9e4b-0ff2527cfdd6\",\"encoding\":\"UTF-8\",\"cd\":{\"custNo\":\"P10027556\",\"virtualAcctNo\":\"\"},\"mac\":\"C04F386CA77794031F3EA62F3999A769\"}";
		String t7 = "{\"tx_code\":\"gw7101\",\"sequenceNo\":\"21901\",\"uuid\":\"791a2008-505f-47c0-a005-fc1e23868018\",\"encoding\":\"UTF-8\",\"cd\":null,\"mac\":\"C96549382C6138EDC18EA36942D87E66\"}";
		String t8 = "{\"tx_code\":\"gw7101\",\"hostReturnCode\":\"0000\",\"hostErrorMessage\":\"交易成功\",\"uuid\":\"28c21427-08bd-4d25-8aab-ac5415fb491d\",\"cd\":{\"rows\":[{\"productBatch\":\"XFC_2017_027\",\"productName\":\"幸福存2017年第027期『安享』二年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"2.730000\",\"maxOpenAmt\":\"50000000.00\",\"minOpenAmt\":\"10000.00\",\"increaseAmt\":\"1000.00\",\"productType\":\"B\",\"productCode\":\"2X2\",\"totalQuota\":\"50000000.00\",\"leftQuota\":\"34450000.00\",\"duration\":\"2\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_027\",\"productName\":\"幸福存2017年第027期『安享』三年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"3.575000\",\"maxOpenAmt\":\"100000000.00\",\"minOpenAmt\":\"10000.00\",\"increaseAmt\":\"1000.00\",\"productType\":\"B\",\"productCode\":\"2X3\",\"totalQuota\":\"100000000.00\",\"leftQuota\":\"52493000.00\",\"duration\":\"3\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_027\",\"productName\":\"幸福存2017年第027期『安享』五年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"4.600000\",\"maxOpenAmt\":\"100000000.00\",\"minOpenAmt\":\"30000.00\",\"increaseAmt\":\"1000.00\",\"productType\":\"B\",\"productCode\":\"2X5\",\"totalQuota\":\"100000000.00\",\"leftQuota\":\"32785000.00\",\"duration\":\"5\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_027\",\"productName\":\"幸福存2017年第027期『安享』一年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"1.950000\",\"maxOpenAmt\":\"100000000.00\",\"minOpenAmt\":\"10000.00\",\"increaseAmt\":\"1000.00\",\"productType\":\"B\",\"productCode\":\"2X1\",\"totalQuota\":\"100000000.00\",\"leftQuota\":\"61910000.00\",\"duration\":\"1\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_028\",\"productName\":\"幸福存2017年第028期【月月薪】\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"4.600000\",\"maxOpenAmt\":\"50000000.00\",\"minOpenAmt\":\"30000.00\",\"increaseAmt\":\"1000.00\",\"productType\":\"D\",\"productCode\":\"2XB\",\"rateThFlag\":\"0\",\"totalQuota\":\"50000000.00\",\"leftQuota\":\"49810000.00\",\"duration\":\"5\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_008_VIP\",\"productName\":\"幸福存2017年第008期【月月薪】VIP\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"4.600000\",\"maxOpenAmt\":\"50000000.00\",\"minOpenAmt\":\"50000.00\",\"increaseAmt\":\"1000.00\",\"productType\":\"D\",\"productCode\":\"2XB\",\"rateThFlag\":\"1\",\"totalQuota\":\"50000000.00\",\"leftQuota\":\"3476000.00\",\"duration\":\"5\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_029\",\"productName\":\"幸福存2017年第029期【零整宝】一年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"1.950000\",\"maxOpenAmt\":\"14000000.00\",\"minOpenAmt\":\"500.00\",\"increaseAmt\":\"100.00\",\"productType\":\"C\",\"productCode\":\"2X6\",\"totalQuota\":\"14000000.00\",\"leftQuota\":\"11158400.00\",\"duration\":\"1\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_029\",\"productName\":\"幸福存2017年第029期【零整宝】三年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"3.575000\",\"maxOpenAmt\":\"36000000.00\",\"minOpenAmt\":\"500.00\",\"increaseAmt\":\"100.00\",\"productType\":\"C\",\"productCode\":\"2X7\",\"totalQuota\":\"36000000.00\",\"leftQuota\":\"29394000.00\",\"duration\":\"3\",\"durUnit\":\"Y\"},{\"productBatch\":\"XFC_2017_029\",\"productName\":\"幸福存2017年第029期【零整宝】五年\",\"saleBeginDate\":\"20170801\",\"saleStopDate\":\"20170831\",\"rate\":\"4.600000\",\"maxOpenAmt\":\"300000000.00\",\"minOpenAmt\":\"500.00\",\"increaseAmt\":\"100.00\",\"productType\":\"C\",\"productCode\":\"2X8\",\"totalQuota\":\"300000000.00\",\"leftQuota\":\"270480000.00\",\"duration\":\"5\",\"durUnit\":\"Y\"}]},\"mac\":\"18552FFB46AED8BD5D808EC66F8DB12A\"}";

		net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(t3);
		String sztmp = json.getString("cd");
		System.out.println("------MAC-old------------" + json.getString("mac"));

		String szMac = MD5Utils.snccbbMD5(sztmp);

		System.out.println("------MAC-new------------" + szMac);

		String bodyStr_md5 = Digest.getStrMD5(sztmp);
		System.out.println("-----MD5-old--------------" + bodyStr_md5);

		String key = "01234567890123456789012345678901";
		String md1 = bodyStr_md5.substring(0, 16);
		String md2 = bodyStr_md5.substring(16, 32);
		System.out.println("------MAC-old------------" + md1 + "\n" + md2);
		String encryptedBodyStr = SoftEncrypt.DES_3(bodyStr_md5.substring(0, 16), key, 0)
				+ SoftEncrypt.DES_3(bodyStr_md5.substring(16, 32), key, 0);
		System.out.println("------MAC-old------------" + encryptedBodyStr);
		//

	}

}
