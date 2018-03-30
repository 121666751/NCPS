package com.union.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class UnionStr {

	public static int msgHeadLen = 2;
	
	/**
	 * 通过指定路径加载配置文件
	 * @param  confFilePath [配置文件路径]
	 * @return     
	 */
	public static Properties loadPropertiesByPath(String confFilePath) {
		Properties propspath =new Properties();
		try {
			propspath.load(new FileInputStream(confFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return propspath;
	}
	
	
	/**
	 * 鍒ゆ柇瀛楃涓叉槸鍚︿负绌�
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || "".equalsIgnoreCase(str.trim())){
			return true;
		}
		return false;
	}

	public static String getCurrentTimeBuf() {
		SimpleDateFormat currentFullSystemDateTime = new SimpleDateFormat(
				"yyyyMMddHHmmss");

		String timeBuf = currentFullSystemDateTime.format(Calendar
				.getInstance().getTime());

		return timeBuf;
	}

	public static long getThreadid() {
		Thread current = Thread.currentThread();
		return current.getId();
	}

	public static long getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 璁剧疆鏃ユ湡鏍煎紡
		return Long.parseLong(df.format(new Date()));// new Date()涓鸿幏鍙栧綋鍓嶇郴缁熸椂闂�
	}
	
	public static long getCurrentTimeMS() {	//用毫秒显示时间方便相减
		return new Date().getTime();
	}

	public static String getEndTime(int intervalTime) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, intervalTime * 1000);// intervalTime绉掑悗閲嶈瘯
		String endTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(cal
				.getTime());
		return endTime;

	}

	public static String bcdhex_to_aschex(byte[] bcdhex) {
		byte[] aschex = { 0, 0 };
		String res = "";
		String tmp = "";
		for (int i = 0; i < bcdhex.length; i++) {
			aschex[1] = hexLowToAsc(bcdhex[i]);
			aschex[0] = hexHighToAsc(bcdhex[i]);
			tmp = new String(aschex);
			res += tmp;
		}
		return res;
	}

	public static byte hexLowToAsc(byte xxc) {
		xxc &= 0x0f;
		if (xxc < 0x0a)
			xxc += '0';
		else
			xxc += 0x37;
		return (byte) xxc;
	}

	public static byte hexHighToAsc(int xxc) {
		xxc &= 0xf0;
		xxc = xxc >> 4;
		if (xxc < 0x0a)
			xxc += '0';
		else
			xxc += 0x37;
		return (byte) xxc;
	}

	public static byte[] aschex_to_bcdhex(String aschex) {
		byte[] aschexByte = aschex.getBytes();
		int j = 0;
		if (aschexByte.length % 2 == 0) {
			j = aschexByte.length / 2;
			byte[] resTmp = new byte[j];
			for (int i = 0; i < j; i++) {
				resTmp[i] = ascToHex(aschexByte[2 * i], aschexByte[2 * i + 1]);
			}
			return resTmp;

		} else {
			j = aschexByte.length / 2 + 1;
			byte[] resTmp = new byte[j];
			for (int i = 0; i < j - 1; i++) {
				resTmp[i] = ascToHex((byte) aschexByte[2 * i],
						(byte) aschexByte[2 * i + 1]);
			}
			resTmp[j - 1] = ascToHex((byte) aschexByte[2 * (j - 1)], (byte) 0);
			return resTmp;
		}
	}

	public static byte ascToHex(byte ch1, byte ch2) {
		byte ch;
		if (ch1 >= 'A')
			ch = (byte) ((ch1 - 0x37) << 4);
		else
			ch = (byte) ((ch1 - '0') << 4);
		if (ch2 >= 'A')
			ch |= (byte) (ch2 - 0x37);
		else
			ch |= (byte) (ch2 - '0');
		return ch;
	}

	/*
	 * 瀵瑰瓧绗︿覆Str1涓巗tr2浣滃紓鎴栬繍绠�
	 */
	public static String UnionXOR(String str1, String str2) {
		String tmpStr = "";
		char ch1;
		char ch2;
		int temp = 0;

		if (str1.length() > str2.length())
			temp = str2.length();
		else
			temp = str1.length();

		for (int i = 0; i < temp; i++) {
			ch1 = str1.charAt(i);
			if (ch1 >= 'A' && ch1 <= 'F')
				ch1 = (char) (ch1 - 0x40 + 0x09);
			else if (ch1 >= 'a' && ch1 <= 'f')
				ch1 = (char) (ch1 - 0x60 + 0x09);
			else
				ch1 -= 0x30;

			ch2 = str2.charAt(i);
			if (ch2 >= 'A' && ch2 <= 'F')
				ch2 = (char) (ch2 - 0x40 + 0x09);
			else if (ch2 >= 'a' && ch2 <= 'f')
				ch2 = (char) (ch2 - 0x60 + 0x09);
			else
				ch2 -= 0x30;

			ch1 = (char) (ch1 ^ ch2);

			if (ch1 > 0x09)
				ch1 = (char) (0x40 + (ch1 - 0x09));
			else
				ch1 += 0x30;
			tmpStr += ch1;
		}
		return tmpStr;
	}
/* 
 * modify by zhouxw 20151110
	public static String getClientIpAddr() {
		String hostIp = "";
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1
			|| System.getProperty("os.name").toLowerCase().indexOf("hp") > -1
			|| System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
			try {
				hostIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return hostIp;
		}
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (!ni.getName().equals("eth0")) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						hostIp = ia.getHostAddress();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return hostIp;
	}
 */
	public static String getClientIpAddr() {
		String hostIp = "";
		
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				
				Enumeration<?> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					if (ia != null && !ia.isSiteLocalAddress() &&!ia.isLoopbackAddress() && ia instanceof Inet4Address)
					{
						hostIp = ia.getHostAddress();
						return hostIp;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return hostIp;
	}
// modify 20151110 end

	/**
	 * 
	 * 鍔熻兘:璁＄畻hash
	 * 
	 * DateTime 2014-12-9 涓婂崍11:09:17
	 * 
	 * @param inStr
	 * @param hashFlag
	 *            hash绠楁硶鏍囪瘑
	 * @return
	 */
	public static String SHA1(String inStr, String hashFlag) {
		if (inStr == null || "".equals(inStr)) {
			return "";
		}
		MessageDigest md = null;
		String outStr = null;
		try {
			md = MessageDigest.getInstance(hashFlag); // 閫夋嫨SHA-1锛屼篃鍙互閫夋嫨MD5
			byte[] digest = md.digest(inStr.getBytes()); // 杩斿洖鐨勬槸byet[]锛岃杞寲涓篠tring瀛樺偍姣旇緝鏂逛究
			outStr = bcdhex_to_aschex(digest);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		return outStr.toUpperCase();
	}

	/**
	 * 根据首字母来选择服务器类型
	 * @param Type
	 * @return
	 */
	public static String ServerType(String Type){
		String newType = null;
		if(Type.equals("E")){
			newType = "ESSC";
		}
		if(Type.equals("K")){
			newType = "KMS";
		}
		if(Type.equals("T")){
			newType = "TKMS";
		}
		if(Type.equals("U")){
			newType = "UAC";
		}
		if(Type.equals("I")){
			newType = "IKMS";
		}
		
		return newType;
		
	}
	

	
	/**
	 * 判断服务码
	 * 服务码中只能是大写字母和数字组成
	 * @param code
	 * @return
	 */
	public static boolean CodeType(String code){
		boolean flag = false;
		int len = code.length();
		if(len == 4){
			for(int i = 0; i < len; i++) {
				char  item =  code.charAt(i);
				if((item >= '0' && item <= '9') || (item >= 'A' && item <= 'Z')){
					flag = true;
				}else{
					return false;
				}
			}	
		}else{
			return false;
		}
		return flag;
	}
	
	
}
