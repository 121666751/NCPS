package com.adtec.ncps;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.adtec.ncps.busi.ncp.SysPub;

/**
 * 交易业务错误
 * @author dengp_w
 * @date 2018年3月28日 下午2:07:32
 */
public class BusinessException extends RuntimeException {
	
	/**
	 * 错误码
	 */
	private String errorCode ;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	public BusinessException(String errorCode ,String errorMsg) throws Exception {
		this("["+errorCode+"]"+errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		
//		记录详细错误堆栈信息
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.printStackTrace(pw);
		SysPub.appLog("ERROR", "异常信息：[\n%s\n]",sw);
		sw.close();
		pw.close();
	}
	public BusinessException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BusinessException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	
}
