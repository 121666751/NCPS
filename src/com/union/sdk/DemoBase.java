package com.union.sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.union.sdk.AcpService;
import com.union.sdk.CertUtil;
import com.union.sdk.SDKConfig;
import com.union.sdk.SDKConstants;
import com.union.sdk.SDKUtil;
import com.union.sdk.SecureUtil;
/**
 * 用途：demo中用的的方法<br>
 * 日期： 2017-03<br>
 * 声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 */
public class DemoBase {

	//默认配置的是UTF-8
	public static String encoding = "UTF-8";
	
	//二维码报文版本号
	public static String version = "1.0.0";
	
	// 商户发送交易时间 格式:YYYYMMDDhhmmss
	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	public static String getCurrentDate() {
		return new SimpleDateFormat("yyyyMMdd000000").format(new Date());
	}
	
	// AN8..40 商户订单号，不能含"-"或"_"
	public static String getOrderId() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}
	
   /**
	 * 组装请求，返回报文字符串用于显示
	 * @param data
	 * @return
	 */
    public static String genHtmlResult(Map<String, String> data){

    	TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			String key = en.getKey(); 
			String value =  en.getValue();
			if("couponInfo".equals(key)){
				try {
					String decodedCouponInfo = AcpService.base64Decode(value, DemoBase.encoding);
					sf.append("<b>couponInfo解base64后的值="+ decodedCouponInfo +"</br></b>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if("dnList".equals(key)){
				try {
					String decodedCouponInfo = AcpService.base64Decode(value, DemoBase.encoding);
					sf.append("<b>dnList解base64后的值="+ decodedCouponInfo +"</br></b>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if("respCode".equals(key)){
				sf.append("<b>"+key + SDKConstants.EQUAL + value+"</br></b>");
			}else
				sf.append(key + SDKConstants.EQUAL + value+"</br>");
		}
		return sf.toString();
    }

 	/**
	 * 组装收款方信息
	 * @param encoding 编码方式
	 * @return 用{}连接并base64后的收款方信息
	 */
	public static String getPayeeInfo(Map<String, String> payeeInfoMap,String encoding) {
		return formInfoBase64(payeeInfoMap,encoding);
    }
	
	/**
	 * 组装收款方信息(接入机构配置了敏感信息加密)
	 * @param encoding 编码方式
	 * @return 用{}连接并base64后的收款方信息
	 */
	public static String getPayeeInfoWithEncrpyt(Map<String, String> payeeInfoMap,String encoding) {
		return formInfoBase64WithEncrpyt(payeeInfoMap,encoding);
    }
	
	/**
     * 组装付款方信息
     * @param encoding 编码方式
     * @return 用{}连接并base64后的付款方信息
     */
	public static String getPayerInfo(Map<String, String> payarInfoMap, String encoding) {
		return formInfoBase64(payarInfoMap,encoding);
    }
	
	/**
     * 组装付款方信息(接入机构配置了敏感信息加密)
     * @param encoding 编码方式
     * @return 用{}连接并base64后的付款方信息
     */
	public static String getPayerInfoWithEncrpyt(Map<String, String> payarInfoMap, String encoding) {
		return formInfoBase64WithEncrpyt(payarInfoMap,encoding);
    }

	
	/**
     * 组装附加处理条件
     * @param encoding 编码方式
     * @return 用{}连接并base64后的附加处理条件
     */
	public static String getAddnCond(Map<String, String> addnCondMap,String encoding) {
		return formInfoBase64(addnCondMap,encoding);
    }
	
	/**
	 * 用{}连接并base64
	 * @param map
	 * @param encoding
	 * @return
	 */
	public static String formInfoBase64(Map<String, String> map,String encoding){
		StringBuffer sf = new StringBuffer();
        String info = sf.append(SDKConstants.LEFT_BRACE).append(SDKUtil.coverMap2String(map)).append(SDKConstants.RIGHT_BRACE).toString();
        try {
        	info = new String(AcpService.base64Encode(info, encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return info;
	}
	
	/**
	 * 用{}连接并base64(接入机构配置了敏感信息加密)
	 * @param map
	 * @param encoding
	 * @return
	 */
	public static String formInfoBase64WithEncrpyt(Map<String, String> map,String encoding){
		StringBuffer sf = new StringBuffer();
        String info = sf.append(SDKConstants.LEFT_BRACE).append(SDKUtil.coverMap2String(map)).append(SDKConstants.RIGHT_BRACE).toString();
        info = SecureUtil.encryptData(info, encoding, CertUtil.getEncryptCertPublicKey());
        return info;
	}
	
	/**
	 * 解析返回报文的payerInfo域，敏感信息不加密时使用：<br>
	 * @param payerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String, String> parsePayerInfo(String payerInfo, String encoding){
		try {
			byte[] b = SecureUtil.base64Decode(payerInfo.getBytes(encoding));
			payerInfo = new String(b,encoding);
			return SDKUtil.convertResultStringToMap(payerInfo);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 解析返回报文的payerInfo域，敏感信息不加密时使用：<br>
	 * @param payerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String, String> parsePayeeInfo(String payeeInfo, String encoding){
		return parsePayerInfo(payeeInfo, encoding);
	}
	
	/**
	 * 解析返回报文的payerInfo域，敏感信息加密时使用：<br>
	 * @param payerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String, String> parsePayerInfoEnc(String payerInfo, String encoding){
		payerInfo = AcpService.decryptData(payerInfo, encoding);
		Map<String, String> payerInfoMap;
		payerInfoMap = SDKUtil.convertResultStringToMap(payerInfo);
		return payerInfoMap;
	}
	
	/**
	 * 解析返回报文中的payeeInfo域，敏感信息加密时使用：<br>
	 * @param payeeInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String, String> parsePayeeInfoEnc(String payeeInfo, String encoding){
		return parsePayerInfoEnc(payeeInfo, encoding);
	}
	
	/**
	 * 解析返回报文中的payerInfo域，敏感信息加密时使用，多证书方式。<br>
	 * @param payerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String, String> parsePayerInfoEnc(String payerInfo, String certPath, 
			String certPwd, String encoding){
		payerInfo = AcpService.decryptData(payerInfo, certPath, certPwd, encoding);
		Map<String, String> payerInfoMap;
		payerInfoMap = SDKUtil.convertResultStringToMap(payerInfo);
		return payerInfoMap;
	}
	
	/**
	 * 解析返回报文中的payeeInfo域，敏感信息加密时使用，多证书方式。<br>
	 * @param payeeInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String, String> parsePayeeInfoEnc(String payeeInfo, String certPath, 
			String certPwd, String encoding){
		return parsePayerInfoEnc(payeeInfo, certPath, certPwd, encoding);
	}
	/**
	 * base64 字符串解base64并用16进制表示
	 * @param base64Str
	 * @return
	 */
	public static String base64Str2HexStr(String base64Str) {  
		byte[] byteA = null;
		try {
			byteA = SecureUtil.base64Decode(base64Str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes2HexString(byteA);
	}
	
	/**
	 * 将byte数组转换为16进制字符串表示
	 * @param b
	 * @return
	 */
	public static String bytes2HexString(byte[] b) {  
        StringBuffer result = new StringBuffer();  
        String hex;  
        for (int i = 0; i < b.length; i++) {  
            hex = Integer.toHexString(b[i] & 0xFF);  
            if (hex.length() == 1) {  
                hex = '0' + hex;  
            }
            result.append(hex.toUpperCase());  
        }  
        return result.toString();  
    }
	

	public static void main(String[] args) {
		SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
		String s1 = "JifvAvoeOFdNVSmlXJ2pF4bCGzb2EPrEBPp+RAeHXTi8XdsJO8ppUQSuH8srZyWnbvAJgS30LeVK5ixows2p3QkjQQMIO+ofIV+tyHaeQYw2bhdOkV49plxYyHXDv0fHkZrxIqtsPopRY/xnYPwxiOzGsubpVTCsWRzqybgAWPx7GCouwh4K6p6hJw+T2WvGMPqMyqTSs1tUdF7U1puC6s6hVInQKBST1Vu2qOXzzSQ7hFxXUI7d+3gMjLhrnAJ6PlQ2QkgpqH9Ra1SwYCejlMhNSlZ7OQVplaithcC27tBOpmDSAefpy6z7vAuuSlFqTIZGwQ9cHI9WbePzZ5UUSQ==";
		System.out.println(parsePayeeInfoEnc(s1, "utf-8"));
		//s1 = "e2lkPTc3NzI5MDA1ODEzNTg4MCZtZXJDYXRDb2RlPTU4MTEmbmFtZT3llYbmiLflkI3np7AmdGVybUlkPTQ5MDAwMDAyfQ==";
		//System.out.println(parsePayeeInfo(s1, "utf-8"));
	}
}