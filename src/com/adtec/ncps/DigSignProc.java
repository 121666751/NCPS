package com.adtec.ncps;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 数字签名处理类 * *
 *******************************************************/
public class DigSignProc {

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月27日
	 * 
	 * @version 从root节点后面截取数字签名
	 */
	public static void xmlFormatIn() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		String szXmlSrc = new String(srcXml, "UTF-8");
		int index = szXmlSrc.indexOf("</root>");
		// </root>后面字符串位置
		index = index + 7;

		int ixmlStrLen = szXmlSrc.length();
		if (ixmlStrLen < index) {
			BusiPub.setCupMsg("PS500022", "请求报文格式有误","2");
			SysPub.appLog("ERROR", "请求报文格式有误");
			//不能直接抛这个系统错误 TODO 
		}
		// 截取数字签名前面内容
		String szXmlStr = szXmlSrc.substring(0, index);
		String szDigSign = szXmlSrc.substring(index);

		// 验证签名
		String szKeyName = ResPool.configMap.get("UNION_yl_keyName");//验签证书
		String szUserID = szXmlSrc.substring(szXmlSrc.indexOf("<SignSN>")+("<SignSN>").length(), szXmlSrc.indexOf("</SignSN>"));//用户标识
		String szData = new String(Base64.encodeBase64(szXmlStr.getBytes("UTF-8")),"UTF-8");//签名数据（Base64编码）
		String szSign = szDigSign.substring(3, szDigSign.length() - 1);//签名后结果（Base64编码）
		int iRet = UnionSign.chkDateSign(szKeyName,szUserID,szData,szSign);
		if (1 != iRet) {
			BusiPub.setCupMsg("PS500025", "请求报文签名未通过验证","2");
			SysPub.appLog("ERROR", "验签失败");
			//不能直接抛这个系统错误 TODO 
		}else{
			SysPub.appLog("DEBUG", "验签成功");
		}

	    SysPub.appLog("DEBUG", "in---szDigSign=%s, szXmlSrc=%s", szDigSign,szXmlStr);
	    SysPub.appLog("DEBUG", "in---szData=%s, szSign=%s", szDigSign,szXmlStr);
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		EPOper.delete(tpID, "fmt_CUP_SVR_IN[0].DigSign");
		EPOper.put(tpID, "fmt_CUP_SVR_IN[0].DigSign", szDigSign);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", szXmlStr);

	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月27日
	 * 
	 * @version root节点后面增加数字签名
	 */
	public static void xmlFormatOut() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		String szxmlStr = new String(srcXml, "UTF-8");
		// 查找>开头的位置 加签要去掉XML声明
		int iLen = szxmlStr.indexOf('>') + 1;
		String szKeyName = ResPool.configMap.get("UNION_gxnx_keyName");//加签证书
		String szUserID = szxmlStr.substring(szxmlStr.indexOf("<SignSN>")+("<SignSN>").length(), szxmlStr.indexOf("</SignSN>"));
		String szData = new String(Base64.encodeBase64(szxmlStr.substring(iLen).getBytes("UTF-8")),"UTF-8");//签名数据（Base64编码）
		String sign = UnionSign.dateSign(szKeyName,szUserID,szData);
		if (!StringTool.isNullOrEmpty(sign)) {
			//SysPub.appLog("ERROR", "签名成功");
		}else{
			SysPub.appLog("ERROR", "签名失败");
		}	
		String szDigSign = "{S:" + sign + "}";
		// root节点后面增加数据签名	
		SysPub.appLog("DEBUG", "root节点后面增加数据签名");
		szxmlStr = szxmlStr + szDigSign;
		SysPub.appLog("DEBUG", szxmlStr);
	    SysPub.appLog("DEBUG", "out--szxmlStr=%s,szDigSign=%s", szxmlStr,szDigSign);
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", szxmlStr);

		iLen = (Integer) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		int iLength = szxmlStr.getBytes().length;
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", iLength);

		SysPub.appLog("DEBUG", "root节点后面增加数字签名成功");
	}
}
