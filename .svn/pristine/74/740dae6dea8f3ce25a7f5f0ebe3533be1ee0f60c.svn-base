package com.union.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UnionUtil {

	public static int tlvOrXmlflag = 1; // 报文格式标识("1"：xml默认为1,其他:tlv格式)
	
	/**
	 * 判断是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		if (str == null || "".equals(str)) {
			return false;
		}
		boolean flag = true;
		char[] cs = str.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] < '0' || cs[i] > '9') {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 把字符串中的非数字剔除，前面补0，组成12位纯数字帐号
	 * 
	 * @param str
	 * @return
	 */
	public static String toAccNumber(String str) {
		char[] chAcc = str.toCharArray();
		char[] chNum = new char[50];
		char[] chNum12 = new char[12];
		int numCount = 0;
		int isFilteCheck = 0;

		for (int i = 0; i < chAcc.length; i++) {
			if (chAcc[i] >= '0' && chAcc[i] <= '9') {
				chNum[numCount++] = chAcc[i];
			} else if (i < chAcc.length - 1)
				isFilteCheck = 1;
		}
		if ((chAcc[chAcc.length - 1] >= '0' && chAcc[chAcc.length - 1] <= '9')
				|| isFilteCheck == 1) {
			if (numCount >= 13) {
				for (int i = (numCount - 13), j = 0; i < numCount - 1; i++) {
					chNum12[j++] = chNum[i];
				}
			} else {
				for (int i = 0; i < (12 - numCount); i++) {
					chNum12[i] = '0';
				}
				for (int i = (12 - numCount), j = 0; i < 12; i++, j++) {
					chNum12[i] = chNum[j];
				}
			}
		} else {
			if (numCount >= 12) {
				for (int i = (numCount - 12), j = 0; i < numCount; i++) {
					chNum12[j++] = chNum[i];
				}
			} else {
				for (int i = 0; i < (12 - numCount); i++) {
					chNum12[i] = '0';
				}
				for (int i = (12 - numCount), j = 0; i < 12; i++, j++) {
					chNum12[i] = chNum[j];
				}
			}
		}
		return String.valueOf(chNum12);
	}

	/**
	 * 判断是否为时间
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isTime(int time) {
		boolean flag = true;
		int month = (time % 10000) / 100;
		int date = time % 100;
		if (1 > month || month > 12) {
			flag = false;
		}
		if (month == 2 && (date < 1 || date > 28)) {
			flag = false;
		} else if ((month == 4 || month == 6 || month == 9 || month == 12)
				&& (date < 1 || date > 30)) {
			flag = false;
		} else if ((month == 1 || month == 3 || month == 5 || month == 7
				|| month == 8 || month == 11)
				&& (date < 1 || date > 31)) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String nowTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		return df.format(new Date());// 返回系统当前时间
	}

	/**
	 * 对于需要补0的数据填补
	 * 
	 * @param number
	 *            待转换的数字
	 * @param size
	 *            数字多少位
	 * @return
	 */
	public static String numToString(int number, int size) {
		if (size == 4) {
			if (Integer.toString(number).length() == 1) {
				return "000" + number;
			} else if (Integer.toString(number).length() == 2) {
				return "00" + number;
			} else if (Integer.toString(number).length() == 3) {
				return "0" + number;
			} else if (Integer.toString(number).length() == 4) {
				return "" + number;
			} else {
				return "" + number;
			}
		} else if (size == 3) {
			if (Integer.toString(number).length() == 1) {
				return "00" + number;
			} else if (Integer.toString(number).length() == 2) {
				return "0" + number;
			} else if (Integer.toString(number).length() == 3) {
				return "" + number;
			} else {
				return "" + number;
			}
		} else if (size == 2) {
			if (Integer.toString(number).length() == 1) {
				return "0" + number;
			} else if (Integer.toString(number).length() == 2) {
				return "" + number;
			} else {
				return "" + number;
			}
		} else {
			return "" + number;
		}
	}

	/**
	 * 生成0到某数之间的随机正整数
	 * 
	 * @param minNum
	 * @param maxNum
	 * @return
	 */
	public static int randomNum(int maxNum) {
		if (maxNum < 0) {
			return -1;
		}
		Random random = new Random();
		return random.nextInt(maxNum);
	}

	/**
	 * 类似String.indexOf()，不过是针对byte array
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static int indexOf(byte[] source, byte[] target) {
		int sourceCount = source.length;
		int targetCount = target.length;
		byte first = target[0];
		int max = (sourceCount - targetCount);
		for (int i = 0; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first)
					;
			}
			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && source[j] == target[k]; j++, k++)
					;

				if (j == end) {
					/* Found whole string. */
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 截取数组
	 */
	public static byte[] subBytes(byte[] bytes, int index, int end) {
		byte[] b = new byte[end - index];
		System.arraycopy(bytes, index, b, 0, end - index);
		return b;
	}
	
}
