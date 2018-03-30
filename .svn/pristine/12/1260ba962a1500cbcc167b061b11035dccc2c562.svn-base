package com.adtec.ncps.busi.chnl.utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * SecretUtils {3DES加密解密的工具类 }
 * 
 * 
 * 
 * @author dengzt
 * 
 * @date 2013-08-1
 * 
 */

public class ThreeDESUtils {

	// 定义加密算法，有DES、DESede(即3DES)、Blowfish

	public static final String Algorithm3DES = "DESede";// 0

	public static final String AlgorithmDES = "DES";// 1

	// 约定密钥

	private static final byte[] FIX_KEY = { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, 0x01, 0x23, 0x45, 0x67, (byte) 0x89,
			0x01, 0x23, 0x45, 0x67, (byte) 0x89, 0x01 };

	/**
	 * 
	 * 使用约定密钥，加密方法 3des
	 * 
	 * @param src
	 * 
	 * @param Algorithm
	 *            算法
	 * 
	 * @return
	 * 
	 */

	public static byte[] encryptMode(byte[] src, String Algorithm) {

		try {

			SecretKey deskey = new SecretKeySpec(

					Algorithm.equals(Algorithm3DES) ? build3DesKey(FIX_KEY) : build3DesKey(FIX_KEY), Algorithm); // 生成密钥

			Cipher c1 = Cipher.getInstance(Algorithm);// 实例化负责加密/解密的Cipher工具类

			c1.init(Cipher.ENCRYPT_MODE, deskey); // 初始化为加密模式

			return c1.doFinal(src);

		} catch (java.security.NoSuchAlgorithmException e1) {

			e1.printStackTrace();

		} catch (javax.crypto.NoSuchPaddingException e2) {

			e2.printStackTrace();

		} catch (java.lang.Exception e3) {

			e3.printStackTrace();

		}

		return null;

	}

	/**
	 * 
	 * 使用指定密钥的 加密
	 * 
	 * @param src
	 * 
	 * @param key
	 *            密钥
	 * 
	 * @param Algorithm
	 *            算法
	 * 
	 * @return
	 * 
	 */

	public static byte[] encryptMode(byte[] src, byte[] key, String Algorithm) {

		try {

			SecretKey deskey = new SecretKeySpec(

					Algorithm.equals(Algorithm3DES) ? build3DesKey(key) : build3DesKey(key), Algorithm); // 生成密钥

			Cipher c1 = Cipher.getInstance(Algorithm);// 实例化负责加密/解密的Cipher工具类

			c1.init(Cipher.ENCRYPT_MODE, deskey); // 初始化为加密模式

			return c1.doFinal(src);

		} catch (java.security.NoSuchAlgorithmException e1) {

			e1.printStackTrace();

		} catch (javax.crypto.NoSuchPaddingException e2) {

			e2.printStackTrace();

		} catch (java.lang.Exception e3) {

			e3.printStackTrace();

		}

		return null;

	}

	/**
	 * 
	 * 解密函数
	 * 
	 * 
	 * 
	 * @param src
	 * 
	 *            密文的字节数组
	 * 
	 * @return
	 * 
	 */

	public static byte[] decryptMode(byte[] src, String Algorithm) {

		try {

			SecretKey deskey = new SecretKeySpec(

					build3DesKey(FIX_KEY), Algorithm);

			Cipher c1 = Cipher.getInstance(Algorithm);

			c1.init(Cipher.DECRYPT_MODE, deskey); // 初始化为解密模式

			return c1.doFinal(src);

		} catch (java.security.NoSuchAlgorithmException e1) {

			e1.printStackTrace();

		} catch (javax.crypto.NoSuchPaddingException e2) {

			e2.printStackTrace();

		} catch (java.lang.Exception e3) {

			e3.printStackTrace();

		}

		return null;

	}

	/**
	 * 
	 * 去掉java加密后会在后面自动填充的8位
	 * 
	 * @param src
	 * 
	 * @return
	 * 
	 */

	public static byte[] withoutAutofill(byte[] src) {

		byte[] newbyte = new byte[src.length - 8];

		for (int i = 0; i < newbyte.length; i++) {

			newbyte[i] = src[i];

		}

		return newbyte;

	}

	/*
	 * 
	 * 根据字符串生成密钥字节数组
	 * 
	 * 
	 * 
	 * @param keyStr 密钥字符串
	 * 
	 * 
	 * 
	 * @return
	 * 
	 * 
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 */

	public static byte[] build3DesKey(byte[] temp)

			throws UnsupportedEncodingException {

		byte[] key = new byte[24]; // 声明一个24位的字节数组，默认里面都是0

		System.arraycopy(temp, 0, key, 0, temp.length);

		// 补充的8字节就是16字节密钥的前8位

		for (int i = 0; i < 8; i++) {

			key[16 + i] = temp[i];

		}

		return key;

	}

	/*
	 * 
	 * 实现字节数组向十六进制的转换方法一
	 * 
	 */

	public static String byte2HexStr(byte[] b) {

		String hs = "";

		String stmp = "";

		for (int n = 0; n < b.length; n++) {

			stmp = (Integer.toHexString(b[n] & 0XFF));

			if (stmp.length() == 1)

				hs = hs + "0" + stmp + " ";

			else

				hs = hs + stmp + " ";

		}

		return hs.toUpperCase();

	}

	/**
	 * 
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * 
	 * @return byte[]
	 * 
	 */

	public static byte[] hexStringToBytes(String hexString) {

		if (hexString == null || hexString.equals("")) {

			return null;

		}

		hexString = hexString.toUpperCase();

		int length = hexString.length() / 2;

		char[] hexChars = hexString.toCharArray();

		byte[] d = new byte[length];

		for (int i = 0; i < length; i++) {

			int pos = i * 2;

			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}

		return d;

	}

	/**
	 * 
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * 
	 * @return byte
	 * 
	 */

	private static byte charToByte(char c) {

		return (byte) "0123456789ABCDEF".indexOf(c);

	}

	/**
	 * 
	 * @param args
	 * 
	 */

	public static void main(String[] args) {

		String msg = new String(new byte[] { 0x4f, 0x4b, 0x4f, 0x4b, 0x4f, 0x4b, 0x4f, 0x4b });

		System.out.println("【加密前】：" + msg);

		// 加密

		byte[] secretArr = ThreeDESUtils.encryptMode(msg.getBytes(), Algorithm3DES);

		System.out.println("【加密后】：" + new String(withoutAutofill(secretArr)));

		// 解密

		byte[] myMsgArr = ThreeDESUtils.decryptMode(secretArr, Algorithm3DES);

		System.out.println("【解密后】：" + new String(myMsgArr));

	}

}
