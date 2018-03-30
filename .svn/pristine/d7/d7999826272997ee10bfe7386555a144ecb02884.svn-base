package com.adtec.ncps.busi.chnl.utils;

import java.security.MessageDigest;

import com.adtec.starring.log.TrcLog;
import com.cup.security.des.DesUtils;
import com.cup.security.security.Constants;

public class MD5Utils {

	private static byte[] MD5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(s.getBytes("utf-8"));
			return bytes; // toHex(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toHex(byte[] bytes) {

		final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}
	/*
	 * 遂宁银行网银渠道MD5生产
	 */
	public static String snccbbMD5(String content) {
		
		byte[] MD51 = MD5(content);
		
		System.out.println("--------MD5-New---------"+toHex(MD51));
		byte[] MD511 = new byte[8];
		byte[] MD512 = new byte[8];
		
		System.arraycopy(MD51, 0, MD511, 0, 8);
		System.arraycopy(MD51, 8, MD512, 0, 8);
		
		byte[] InStr1 = ThreeDESUtils.encryptMode(MD511, ThreeDESUtils.Algorithm3DES);

		byte[] InStr2 = ThreeDESUtils.encryptMode(MD512, ThreeDESUtils.Algorithm3DES);

		byte[] InStr = new byte[16];
		
		System.arraycopy(InStr1, 0, InStr, 0, 8);
		System.arraycopy(InStr2, 0, InStr, 8, 8);

		String MD5Res = toHex(InStr);
		
		return MD5Res;
	}
/*
 *   遂宁银行网银渠道MD5教研算法
 */
	public static boolean MD5Check(String ResMD5, String content) {
		
		String MD5Res=snccbbMD5(content);
		
		if (MD5Res.equalsIgnoreCase(ResMD5)) {
			TrcLog.log("json.log", "MD5Res["+MD5Res+"]MD5Tag["+ResMD5+"]不一致");
			return true;
		} else {
			TrcLog.log("json.log", "MD5Res["+MD5Res+"]MD5Tag["+ResMD5+"]不一致");
			
			return false;
		}

	}

	public static void main(String[] args) {
		// byte[] PIN_KEY=
		// {0x01,0x23,0x45,0x67,(byte)0x89,0x01,0x23,0x45,0x67,(byte)0x89,0x01,0x23,0x45,0x67,(byte)0x89,0x01};
		String t3 = "{\"tx_code\":\"gw0005\",\"hostReturnCode\":\"0000\",\"hostErrorMessage\":\"交易成功\",\"uuid\":\"16f998c1-690f-45a8-894c-18a3cd45ea27\",\"cd\":{\"accountNo\":\"6210900300001895980\",\"accountName\":\"赵志茂\",\"openNode\":\"20108\",\"openNodeName\":\"遂宁银行新阳街新城门支行\",\"openDate\":\"20060817\",\"accountState\":\"0\",\"currencyType\":\"01\",\"balance\":\"99.28\",\"balanceAvailable\":\"99.28\",\"intRate\":\"00385000\",\"customerNo\":\"10027556\",\"accountType\":\"1\",\"certType\":\"1\",\"attr\":\"3\",\"certNo\":\"510212196706120850\",\"lockstat\":\"0\"},\"msgReturnFrom\":\"核心系统\",\"mac\":\"CA97A3052E3FE1B118292448604D8A3E\"}";

		net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(t3);
		String sztmp = json.getString("cd");
		System.out.println("------MAC-old------------"+json.getString("mac"));
		String content = "null";
		String MD52 = "C96549382C6138EDC18EA36942D87E66";
		byte[] MD51 = MD5(sztmp);
		byte[] MD511 = new byte[8];
		byte[] MD512 = new byte[8];
		System.arraycopy(MD51, 0, MD511, 0, 8);
		System.arraycopy(MD51, 8, MD512, 0, 8);
		String sTmp = toHex(MD51);
		System.out.println(sTmp);
		sTmp = toHex(MD511);
		System.out.println(sTmp);
		sTmp = toHex(MD512);
		System.out.println(sTmp);
		byte[] InStr1 = ThreeDESUtils.encryptMode(MD511, ThreeDESUtils.Algorithm3DES);
		byte[] tt = ThreeDESUtils.decryptMode(InStr1,ThreeDESUtils.Algorithm3DES);
		sTmp = toHex(tt);
		System.out.println(sTmp);
		byte[] InStr2 = ThreeDESUtils.encryptMode(MD512, ThreeDESUtils.Algorithm3DES);
		byte[] tt2 = ThreeDESUtils.decryptMode(InStr2,ThreeDESUtils.Algorithm3DES);
		sTmp = toHex(tt2);

		System.out.println(sTmp);
		byte[] InStr = new byte[16];
		System.arraycopy(InStr1, 0, InStr, 0, 8);
		System.arraycopy(InStr2, 0, InStr, 8, 8);

		String MD5Res = toHex(InStr);
		System.out.println(MD52);
		System.out.println(MD5Res);
	}
}
