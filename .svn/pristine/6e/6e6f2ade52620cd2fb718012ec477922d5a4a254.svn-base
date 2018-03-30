package com.adtec.ncps.busi.chnl;

import java.security.MessageDigest;

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

	private static String toHex(byte[] bytes) {

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
	private static String snccbbMD5(String content) {
		
		byte[] MD51 = MD5(content);
		
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
	private static boolean MD5Check(String ResMD5, String content) {
		
		String MD5Res=snccbbMD5(content);
		
		if (MD5Res.equalsIgnoreCase(ResMD5)) {
			return true;
		} else {
			return false;
		}

	}

	public static void main(String[] args) {
		// byte[] PIN_KEY=
		// {0x01,0x23,0x45,0x67,(byte)0x89,0x01,0x23,0x45,0x67,(byte)0x89,0x01,0x23,0x45,0x67,(byte)0x89,0x01};

		String content = "null";
		String MD52 = "C96549382C6138EDC18EA36942D87E66";
		byte[] MD51 = MD5(content);
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

		byte[] InStr2 = ThreeDESUtils.encryptMode(MD512, ThreeDESUtils.Algorithm3DES);

		sTmp = toHex(InStr1);
		System.out.println(sTmp);
		sTmp = toHex(InStr2);
		System.out.println(sTmp);
		byte[] InStr = new byte[16];
		System.arraycopy(InStr1, 0, InStr, 0, 8);
		System.arraycopy(InStr2, 0, InStr, 8, 8);

		String MD5Res = toHex(InStr);
		System.out.println(MD52);
		System.out.println(MD5Res);
	}
}
