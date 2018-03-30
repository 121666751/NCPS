package com.union.utils;

import com.union.error.UnionError;
import com.union.message.UnionMessage;
import com.union.message.UnionResponse;

public class UnionVerify {
	
	/**
	 * 判断字符型参数值是否为空
	 * 
	 * @param value 参数值
	 * 
	 * @return null或空字符串返回true,否则返回false
	 */
	public static boolean paramIsEmpty(String paramValue) {
		return paramValue == null || paramValue.equals("");
	}
	
	
	/**
	 * 判断字符型参数是否为数字字符
	 * 
	 * @param paramValue 参数值
	 * 
	 * @return 只包含数字字符返回true,否则返回false
	 */
	public static boolean paramIsNumStr(String paramValue) {
		return paramValue != null && paramValue.matches("\\d+");
	}
	
	
	/**
	 * 参数值错误
	 * 
	 * @param paramName 参数名
	 * 
	 * @return UnionMessage 
	 */
	public static UnionMessage paramValueWrong(String paramName) {
		String errorCode = UnionError.WRONG_PARAM_VALUE.getCode();
		String errorMessage = paramName + UnionError.WRONG_PARAM_VALUE.getMessage();
		return UnionResponse.newFailInstance(errorCode, errorMessage);
	}
	
	
	/**
	 * 字符前面填充"0"
	 * 
	 * @param srcStr 待填充的字符
	 * @param destStrLen 填充后的字符长度
	 * 
	 * @return 填充后的字符
	 */
	public static String frontPaddingZero(String srcStr, int destStrLen) {
		// mod by 2016.11.18
//		String padding = "";
//		for(int i = 0; i < destStrLen - srcStr.length(); i++) {
//			padding += "0";
//		}
//		return padding + srcStr;
		
		StringBuilder sb = new StringBuilder();
		for(int i = srcStr.length() - destStrLen; i < 0; i++) {
			sb.append("0");
		}
		sb.append(srcStr);
		return sb.toString();
	}
	
	
	/**
	 * 数字前面填充"0"
	 * 
	 * @param srcNum 待填充的数字
	 * @param destStrLen 填充后的字符长度
	 * 
	 * @return 填充后的字符
	 */
	public static String frontPaddingZero(int srcNum, int destStrLen) {
		return frontPaddingZero(String.valueOf(srcNum), destStrLen);
	}

}
