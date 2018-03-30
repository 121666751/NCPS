package com.adtec.ncps.busi.chnl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {
	private static MessageDigest MD5 = null;
	private static MessageDigest SHA = null;
	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
			SHA = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
		}
	}
	
	/**
	 * 取得文件的MD5值
	 * @param filePath 文件路径
	 * @return
	 * @throws IOException
	 */
	public static String getFileMD5(String filePath) throws IOException{
		return getFileMD5(new File(filePath));
	}

	/**
	 * 取得文件的MD5值
	 * @param file 文件
	 * @return
	 * @throws IOException
	 */
	public static String getFileMD5(File file) throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			FileChannel fc = in.getChannel();
			MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					file.length());
			MD5.update(buf);
		} finally {
			if (in != null)
				in.close();
		}
		return MD5Utils.toHex(MD5.digest());
	}
	
	/**
	 * 取得字符串的MD5值
	 * @param data
	 * @return
	 */
	public static String getStrMD5(String data){
		MD5.update(data.getBytes());
		return StringUtil.Bytes2HexString(MD5.digest());
	}
	
	public static String getFileSHA(String filePath) throws IOException{
		return getFileSHA(new File(filePath));
	}
	
	public static String getFileSHA(File file) throws IOException{
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			FileChannel fc = in.getChannel();
			MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					file.length());
			SHA.update(buf);
		} finally {
			if (in != null)
				in.close();
		}
		return MD5Utils.toHex(SHA.digest());
		
	}
	
	public static String getStrSHA(String data){
		SHA.update(data.getBytes());
		return MD5Utils.toHex(SHA.digest());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File big = new File("D:\\apache-ant-1.9.2-bin.zip");
		
		try {
			System.out.println(getFileMD5(big));
			System.out.println(getFileSHA(big));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(getStrMD5("123"));
		System.out.println(getStrSHA("123"));

	}

}
