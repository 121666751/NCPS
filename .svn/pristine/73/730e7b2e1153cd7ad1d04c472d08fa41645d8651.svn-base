package com.adtec.ncps;

import com.adtec.ncps.busi.ncp.SysPub;
import com.union.message.UnionMessage;
import com.union.api.UnionEssc;

public class UnionSign {

	private static UnionEssc api = new UnionEssc();
	
	/**
	 * 数字签名
	 * @return 签名数据
	 * @param keyName 			密钥名称，收单机构签名密钥
	 * @param userID    		用户标识，当密钥为SM2时，可用，可选,不送userlID为 默认1234567812345678
	 * @param data   			 签名数据
	 * @throws Exception 
	 */
	public static String dateSign(String szKeyName,String szUserID,String szData) throws Exception{
		UnionMessage transInfo = api.servEA30(szKeyName, szUserID,szData);
		if ("000000".equalsIgnoreCase(transInfo.getHeadField("responseCode"))){
			return transInfo.getBodyField("sign");
		}
		else{
			SysPub.appLog("ERROR", "数字签名失败,错误码[%s]，错误码说明[%s]",transInfo.getHeadField("responseCode"),transInfo.getHeadField("responseRemark"));
			//SysPub.appLog("ERROR", "错误日志[%s]",transInfo.getLog());
		}
		return "";
	}
	/**
	 * 
	 * @param keyName 	密钥名称，银联验签密钥
	 * @param userID 	用户标识，当密钥为SM2时，可用，可选,不送userlID为 默认1234567812345678
	 * @param data		签名数据
	 * @param sign		签名结果，Base64的签名结果
	 * @return 返回验签结果，1-验签成功，其他为失败
	 * @throws Exception
	 */
	public static int chkDateSign(String szKeyName,String szUserID,String szData, String szSign) throws Exception{
		UnionMessage transInfo = api.servEA31(szKeyName, szUserID,szData,szSign);
		
		if ("000000".equalsIgnoreCase(transInfo.getHeadField("responseCode"))){
			return 1;
		}
		else{
			SysPub.appLog("ERROR", "数字验签失败,错误码[%s]，错误码说明[%s]",transInfo.getHeadField("responseCode"),transInfo.getHeadField("responseRemark"));
			SysPub.appLog("ERROR", "数据[%s]，签名串[%s]",szData,szSign);
			//SysPub.appLog("ERROR", "错误日志[%s]",transInfo.getLog());
			return -1;
		}
	}
	/**
	 * 敏感信息加密
	 * @param szKeyName				密钥名称，银联加密密钥
	 * @param szSensInfo    		敏感信息，需要加密的数据
	 * @return 返回加密的敏感信息和密钥
	 * @throws Exception 
	 */
	public static String[] infoEncry(String szKeyName,String szSensInfo) throws Exception{
		String []arrEcnInfo = new String[2];
		UnionMessage transInfo  = api.servEA32(szKeyName,szSensInfo);
		if ("000000".equalsIgnoreCase(transInfo.getHeadField("responseCode"))){
			arrEcnInfo[0] = transInfo.getBodyField("encKey");
			arrEcnInfo[1] = transInfo.getBodyField("encInfo");
			return arrEcnInfo;
		}
		else{
			SysPub.appLog("ERROR", "敏感信息加密失败,错误码[%s]，错误码说明[%s]",transInfo.getHeadField("responseCode"),transInfo.getHeadField("responseRemark"));
			//SysPub.appLog("ERROR", "错误日志[%s]",transInfo.getLog());
			return null;
		}
	}
	
	/**
	 * 敏感信息解密
	 * @param szKeyName		密钥名称，发卡机构加密密钥，机构入网时在银联系统录入机构的加密证书
	 * @param encKey		公钥加密的保护密钥，银联加密密钥，公钥加密的保护密钥,BASE64格式
	 * @param szencInfo		保护密钥加密的敏感信息,BASE64格式
	 * @return				 明文的敏感信息
	 * @throws Exception
	 */
	public static String infoDecry(String szKeyName,String szEncKey,String szEncInfo) throws Exception{
		String szSensInfo = "";
		UnionMessage transInfo = api.servEA33(szKeyName,szEncKey,szEncInfo);
		if ("000000".equalsIgnoreCase(transInfo.getHeadField("responseCode"))){
			szSensInfo = transInfo. getBodyField("sensInfo");
			return szSensInfo;
		}
		else{
			SysPub.appLog("ERROR", "敏感信息解密失败,错误码[%s]，错误码说明[%s]",transInfo.getHeadField("responseCode"),transInfo.getHeadField("responseRemark"));
			return null;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
