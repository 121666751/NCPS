package com.adtec.ncps;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.union.sdk.SDKConstants;

public class QrDtaUtil {

    /**
     * @Description: 由于平台解析标记报文在字符串的末尾必须也有一个分隔符，此处增加一个
     * @author Q
     * @throws Exception
     * @date 2017年12月15日上午10:58:27
     */
    public static void addTagAtLast() throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();
	byte srcBytes[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
	String srcStr = new String(srcBytes, "UTF-8");

	srcStr = srcStr + "&";
	SysPub.appLog("INFO", "接收报文增加&分隔符后的报文信息：%s", srcStr);
	// 进行转码
	srcStr = URLDecoder.decode(srcStr, SDKConstants.UTF_8_ENCODING);

	EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
	EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");
	EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", srcStr);
	EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", srcStr.getBytes().length);
    }

    /**
     * @Description: 组织交易报文后，将平台默认组装的最后附带的分隔符去掉
     * @author Q
     * @throws Exception
     * @date 2017年12月15日上午10:59:15
     */
    public static void deleteTagAtLast() throws Exception {
	String signatureTag = "signature=";
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();
	byte srcBytes[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
	String srcStr = new String(srcBytes, "UTF-8");
	SysPub.appLog("INFO", "发送报文，修改前的报文信息：%s", srcStr);
	if (StringUtils.isEmpty(srcStr)) {
	    SysPub.appLog("INFO", "返回报文体为空");
	    return;
	}

	int begin = srcStr.indexOf(signatureTag) + signatureTag.length();
	int end = srcStr.indexOf("&", begin);
	SysPub.appLog("INFO", "begin：%d, end:%d", begin, end);
	if (-1 == end) {
	    return;
	}

	String signStr = srcStr.substring(0, begin) + "2343423424234234234" + srcStr.substring(end);
	SysPub.appLog("INFO", "发送报文，增加签名后的报文信息：%s", signStr);

	String newStr = null;
	if (signStr.endsWith("&")) {
	    newStr = signStr.substring(0, signStr.length() - 1);
	}
	SysPub.appLog("INFO", "发送报文，去掉&符号后的报文信息：%s", newStr);

	EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
	EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", newStr);

	int iLen = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
	SysPub.appLog("INFO", "发送报文，修改前的信息长度：%d", iLen);

	int iLength = newStr.getBytes().length;
	SysPub.appLog("INFO", "发送报文，修改后的信息长度：%d", iLength);

	EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", iLength);
    }

}
