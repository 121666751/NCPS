package com.adtec.ncps.busi.chnl.utils;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoTools {

	private String desKey = "DHCCSoft";
	private String CharacterSet = "GBK";
	private final byte[] DESIV = { 18, 52, 86, 120, -112, -85, -51, -17 };

	private AlgorithmParameterSpec iv = null;
	private Key key = null;
	private static CryptoTools instance;

	public static CryptoTools getInstance() throws Exception {
		if (instance == null) {
			instance = new CryptoTools();
		}
		return instance;
	}

	private CryptoTools() throws Exception {
		DESKeySpec keySpec = new DESKeySpec(this.desKey.getBytes(CharacterSet));
		this.iv = new IvParameterSpec(this.DESIV);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		this.key = keyFactory.generateSecret(keySpec);
	}

	public byte[] encode(byte[] data) throws Exception {
		return encode(data, this.key);
	}

	public byte[] decode(byte[] data) throws Exception {
		return decode(data, this.key);
	}

	private byte[] encode(byte[] data, Key desKey) throws Exception {
		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		enCipher.init(1, desKey, this.iv);
		byte[] pasByte = enCipher.doFinal(data);
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(pasByte).getBytes(CharacterSet);
	}

	private byte[] decode(byte[] data, Key desKey) throws Exception {
		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		deCipher.init(2, desKey, this.iv);
		BASE64Decoder base64Decoder = new BASE64Decoder();

		byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(new ByteArrayInputStream(data)));
		return pasByte;
	}
}