package com.adtec.ncps.busi.ncp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.respool.StarringSeq;
import com.adtec.starring.struct.dta.DtaInfo;

public class PubTool {

	/**
	 * 获取6位短信验证码
	 * 
	 * @return （6位随机数）
	 */
	public static int getId6() {
		int random = (int) ((Math.random() * 9 + 1) * 100000);
		return random;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return （yyyyMMddhhmmss）
	 */
	public static String getDate() {
		String fmt = "yyyyMMddHHmmss";
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(fmt);
		String s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return （yyyyMMddhhmmssSSS）
	 */
	public static String getDate17() {
		String fmt = "yyyyMMddHHmmssSSS";
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(fmt);
		String s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 获取当前日期
	 * 
	 * @return （yyyyMMdd）
	 */
	public static String getDate8() {
		String fmt = "yyyyMMdd";
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(fmt);
		String s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 获取当前日期
	 * 
	 * @return日期
	 */
	public static String getDate(String fmt) {
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(fmt);
		String s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 获取当前日期
	 * 
	 * @return （yyyyMMdd）
	 */
	public static String getDate10() {
		String fmt = "yyyy-MM-dd";
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(fmt);
		String s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return （hhmmss）
	 */
	public static String getTime() {
		String fmt = "HHmmss";
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(fmt);
		String s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 比较两个时间
	 * 
	 * @return （hhmmss）
	 */
	public static int compare_date(String date1, String date2) {

		DateFormat df = new SimpleDateFormat("HHmmss");
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 比较两个时间
	 * 
	 * @return （hhmmss）
	 */
	public static int compare_date2(String date1, String date2) {

		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 比较两个时间
	 * 
	 * @return （hhmmss）
	 */
	public static int compare_date4(String date1, String date2, String fmt) {

		DateFormat df = new SimpleDateFormat(fmt);
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param date1
	 *            日期
	 * @param date2
	 *            日期
	 * @param fmt
	 *            日期格式
	 * @return 日期的差,单位：秒
	 */
	public static long subDate(String date1, String date2, String fmt) {

		DateFormat df = new SimpleDateFormat(fmt);
		long ls = 0;
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			ls = (dt1.getTime() - dt2.getTime()) / 1000;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return ls;
	}

	/**
	 * 
	 * @param date1
	 *            日期
	 * @param date2
	 *            日期
	 * @param fmt
	 *            日期格式
	 * @return 日期的差,单位：毫秒
	 */
	public static long subDateMs(String date1, String date2, String fmt) {

		DateFormat df = new SimpleDateFormat(fmt);
		long ls = 0;
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			ls = (dt1.getTime() - dt2.getTime());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return ls;
	}
	
	/**
	 * 
	 * @param szDateTm
	 *            日期
	 * @param szfmt
	 *            日期格式
	 * @param iLen
	 *            间隔秒数
	 * @return 计算多少秒之后的时间
	 * @throws Exception
	 */
	public static String calDateAdd(String szDateTm, String szfmt, int iLen) throws Exception {

		if (szDateTm == null || "".equals(szDateTm) || szfmt == null || "".equals(szfmt)) {
			// 参数不合法
			return "";
		}

		SysPub.appLog("DEBUG", "时间：：" + szDateTm);
		SimpleDateFormat sf = new SimpleDateFormat(szfmt);
		Date dt1 = sf.parse(szDateTm);
		long lSec = dt1.getTime();
		long lSec1 = lSec + (long) iLen * 1000;
		Date date = new Date(lSec1);
		String szcalDate = sf.format(date);
		SysPub.appLog("DEBUG", iLen + "秒后时间：" + szcalDate);
		return szcalDate;
	}

	/**
	 * 比较两日期，格式yyyyMMdd
	 * 
	 * @return （hhmmss）
	 */
	public static int compare_date3(String date1, String date2) {

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 产生一个平台流水号 规则：节点号*10000000L+1#序号发生器产生的值
	 * 
	 * @throws Exception
	 */
	public static int sys_get_seq() {
		/* 取序号发生器生成的值 */
		int seq = StarringSeq.getCustomSeq("1");

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String nodeName = dtaInfo.getNodeName(); // 取机器节点号
		int nodeNo = Integer.parseInt(nodeName); // 转化为整型的节点号

		/* 根据节点号、序号发生器产生的值生成最终的平台流水号 */
		seq = nodeNo * 10000000 + seq;

		return seq;
	}

	/**
	 * 产生一个平台流水号8位流水号
	 */
	public static int sys_get_seq2() {
		/* 取序号发生器生成的值 */
		int seq = StarringSeq.getCustomSeq("1");

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String nodeName = dtaInfo.getNodeName(); // 取机器节点号
		int nodeNo = Integer.parseInt(nodeName); // 转化为整型的节点号

		/* 根据节点号、序号发生器产生的值生成最终的平台流水号 */
		seq = nodeNo * 10000000 + seq;
		return seq;
	}
	/**
	 * 产生一个平台流水号8位流水号
	 */
	public static int sys_get_seq61() {
		/* 取序号发生器生成的值 */
		int seq = StarringSeq.getCustomSeq("1");

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String nodeName = dtaInfo.getNodeName(); // 取机器节点号
		int nodeNo = Integer.parseInt(nodeName); // 转化为整型的节点号

		/* 根据节点号、序号发生器产生的值生成最终的平台流水号 */
		seq = nodeNo * 100000 + seq;
		return seq;
	}
	/**
	 * 产生一个平台流水号10位流水号
	 */
	public static int sys_get_seq10() {
		/* 取序号发生器生成的值 */
		int seq = StarringSeq.getCustomSeq("1");

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String nodeName = dtaInfo.getNodeName(); // 取机器节点号
		int nodeNo = Integer.parseInt(nodeName); // 转化为整型的节点号

		/* 根据节点号、序号发生器产生的值生成最终的平台流水号 */
		seq = nodeNo * 1000000000 + seq;
		try {
			SysPub.appLog("DEBUG", "seq：%s,nodeName:%s", seq, nodeName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return seq;
	}

	/*
	 * 字符串补右补0
	 */
	public static String fomatStr(String str, int iLen) {
		if (str == null || "".equals(str))
			return "";
		int iStrLen = str.length();
		if (iStrLen < iLen) {
			for (int i = 0; i < iLen - iStrLen; i++) {
				str += "0";
			}
		}
		return str;
	}

	/*
	 * 字符串补左补0
	 */
	public static String fomatStrLeft(String str, int iLen) {
		if (str == null || "".equals(str))
			return "";
		int iStrLen = str.length();
		if (iStrLen < iLen) {
			for (int i = 0; i < iLen - iStrLen; i++) {
				str = "0" + str;
			}
		}
		return str;
	}

	/**
	 * 产生一个平台流水号6位流水号
	 */
	public static int sys_get_seq6() {
		/* 取序号发生器生成的值 */
		int seq = StarringSeq.getCustomSeq("2");

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String nodeName = dtaInfo.getNodeName(); // 取机器节点号
		int nodeNo = Integer.parseInt(nodeName); // 转化为整型的节点号

		/* 根据节点号、序号发生器产生的值生成最终的平台流水号 */
		seq = nodeNo * 100000 + seq;
		try {
			SysPub.appLog("DEBUG", "seq：%s,nodeName:%s", seq, nodeName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return seq;
	}
	
	public static String getSeqNo(  )
	{
		String strSeqNo = String.valueOf(sys_get_seq10());		
		
		return strSeqNo;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(getDate("yyyy-MM-dd'T'HH:mm:ss"));

		// SimpleDateFormat sf = new SimpleDateFormat( "yyyyMMdd" );
		// Calendar c=Calendar.getInstance();
		// System.out.println( "当前时间："+sf.format(c.getTime()));
		// System.out.println( getDate("yyyy-MM-dd'T'HH:mm:ss"));
		// c.add(Calendar.DAY_OF_MONTH, 1);
		// System.out.println( "日期加1："+sf.format(c.getTime()));

		// SimpleDateFormat sf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss"
		// );
		// Calendar c=Calendar.getInstance();
		// System.out.println( "当前时间："+sf.format(c.getTime()));
		// System.out.println( getDate("yyyy-MM-dd'T'HH:mm:ss"));
		// c.add(Calendar.SECOND, 120);
		// System.out.println( "86400秒后："+sf.format(c.getTime()));

		//String szDate = calDateAdd("2017-08-24T15:10:10", "yyyy-MM-dd'T'HH:mm:ss", -1200);
		//SysPub.testLog("DEBUG", szDate);
		Long lDateSub =subDate("20171220185011","20171215053000","yyyy-MM-dd'T'HH:mm:ss");
		if (1 < lDateSub) {
			System.out.println("1 < " + lDateSub);
		}


	}

	/*
	 * 获取当前时间，转换为毫秒
	 */
	public static long gettimems(){
		Date date = new Date();
		long time = date.getTime();
		return time;
	}
	
	/*
	 * 获取交易开始时间，毫秒
	 */
	public static void gettxbegtime(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		long txbegtime = gettimems();
		
		EPOper.put(tpID, "TRANSLOG_ELEMENT[0].startime[0]", txbegtime);
	}

}
