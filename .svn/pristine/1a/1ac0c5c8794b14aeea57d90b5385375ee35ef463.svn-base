package com.adtec.ncps;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.union.sdk.SDKConstants;
import com.union.sdk.SDKUtil;
import com.union.sdk.SignService;

public class QrDigSign {

    /**
     * @Description: 验证签名
     * @author Q
     * @throws Exception
     * @date 2017年12月15日上午11:05:27
     */
    public static void check() throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();
	String checkFlag;
	byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
	String szXmlSrc = new String(srcXml, "UTF-8");
	SysPub.appLog("INFO", "请求报文:%s", szXmlSrc);

	// 将请求转换为map
	Map<String, String> reqDataMap = SDKUtil.convertResultStringToMap(szXmlSrc);

	// 验签
	if (SignService.validate(reqDataMap, SDKConstants.UTF_8_ENCODING)) {
	    checkFlag = "0";
	    SysPub.appLog("INFO", "验证签名成功");
	    String respCode = reqDataMap.get("respCode");
	    if (("00").equals(respCode)) {

	    } else {
		// 其他应答码为失败请排查原因
		// TODO
	    }
	} else {
	    checkFlag = "1";
	    SysPub.appLog("ERROR", "验证签名失败");
	    // TODO 检查验证签名失败的原因
	}
	szXmlSrc = szXmlSrc + "&signCheckFlag=" + checkFlag + "&";
	EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
	EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");
	EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", szXmlSrc);
	EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", szXmlSrc.getBytes().length);

    }

    /**
     * @Description: 增加签名
     * @author Q
     * @throws Exception
     * @date 2017年12月15日上午11:06:05
     */
    public static void sign() throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();

	String signatureTag = "signature=";

	byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
	String szxmlStr = new String(srcXml, "UTF-8");
	SysPub.appLog("INFO", "发送报文要加签的字符串：%s", szxmlStr);
	if (StringUtils.isEmpty(szxmlStr)) {
	    SysPub.appLog("INFO", "应答报文体为空");
	    return;
	}
	// 解析前去掉最后的&
	String szxmlStr2 = szxmlStr.substring(0, szxmlStr.length() - 1);
	// 解码前去掉空的value
	// 去掉没有value的key
	szxmlStr2 = delEmptyKeys(szxmlStr2);
	SysPub.appLog("INFO", "去掉空value后的字符串：%s", szxmlStr2);

	// 在加签前再解码一次
	String deSzxmlStr2 = URLDecoder.decode(szxmlStr2, SDKConstants.UTF_8_ENCODING);
	SysPub.appLog("INFO", "重新转码后的字符串：%s", deSzxmlStr2);

	Map<String, String> signMap = SignService.sign(SDKUtil.coverResultString2Map(deSzxmlStr2),
		SDKConstants.UTF_8_ENCODING);
	String signature = signMap.get(SDKConstants.param_signature);
	String newStr = null;
	if (StringUtils.isBlank(signature)) {
	    newStr = szxmlStr2;
	    SysPub.appLog("ERROR", "签名失败");
	} else {
	    // 替换掉原来的signature=xxx
	    int begin = szxmlStr2.indexOf(signatureTag) + signatureTag.length();
	    int end = szxmlStr2.indexOf("&", begin);
	    SysPub.appLog("INFO", "begin：%d, end:%d", begin, end);

	    // 对签名串url转码
	    SysPub.appLog("INFO", "转码前签名:%s", signature);
	    signature = URLEncoder.encode(signature, SDKConstants.UTF_8_ENCODING);
	    SysPub.appLog("INFO", "转码后签名:%s", signature);

	    newStr = szxmlStr2.substring(0, begin) + signature + szxmlStr2.substring(end);
	}

	EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
	EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", newStr);

	int iLength = newStr.getBytes().length;
	EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", iLength);

	SysPub.appLog("INFO", "生成数字签名成功");
    }

    public static String delEmptyKeys(String origData) {
	if (StringUtils.isEmpty(origData)) {
	    return origData;
	}
	String subData;
	int begin;
	int end;
	end = origData.indexOf("=&");
	if (end > 0) {
	    subData = origData.substring(0, end);
	    begin = subData.lastIndexOf("&");
	    if (-1 == begin) {// 第一个key的value就是空的
		origData = origData.substring(end + 2);
	    } else {
		origData = subData.substring(0, subData.lastIndexOf("&") + 1) + origData.substring(end + 2);
	    }
	    // 嵌套调用
	    origData = delEmptyKeys(origData);
	}

	if (origData.endsWith("=")) {
	    // 去掉最后一个
	    origData = origData.substring(0, origData.lastIndexOf("&"));
	}

	return origData;
    }

    public static void main(String[] args) {
	String data = "version=1.0.0&signature=0&certId=68759529225&reqType=0180000903&issCode=90880019&txnNo=201801043533336173310604970104122353&txnAmt=300000&currencyCode=156&payerInfo=e2FjY05vPTYyMTYyNjEwMDAwMDAwMDI0ODUmYWNjdENsYXNzPTEmY2FyZEF0dHI9MDEmbW9iaWxlPTEzNTI1Njc3ODA5Jm5hbWU95a6L5bCPfQ==&payeeInfo=e2lkPTc3NzI5MDA1ODEzNTg4MCZtZXJDYXRDb2RlPTU4MTEmbmFtZT3llYbmiLflkI3np7AmdGVybUlkPTQ5MDAwMDAyfQ==&encryptCertId=";
	String newData = delEmptyKeys(data);
	System.out.println(data);
	System.out.println(newData);
    }
}
