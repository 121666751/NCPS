package com.union.error;

public enum UnionError {
	
	WRONG_PARAM_TYPE("-1001", "参数类型错误"),
	WRONG_PARAM_VALUE("-1002", "参数值错误"),
	WRONG_PARAM_LENGTH("-1003", "参数长度错误"),
	
	FAIL_FORMAT_TLV("-2001", "格式化TLV报文失败"),
	FAIL_FORMAT_XML("-2002", "格式化XML报文失败"),
	FAIL_ANALYSE_MESSAGE("-2003", "解析报文失败"),
	
	UNSUPPORT_CHARACTER_ENCODING("-3001", "不支持的字符编码"),
	UNSUPPORT_MESSAGE_TYPE("-3002", "不支持的报文格式"),
	UNSUPPORT_DIGEST_ALGORITH("-3003", "不支持的消息算法"),
	
	MESSAGE_TOO_BIG("-4001", "发送的报文太大"),
	
	FAIL_CONNECT_SERVER("-5001", "连接服务器失败"),
	FAIL_SEND_MESSAGE("-5002", "发送数据失败"),
	FAIL_RECEIVE_MESSAGE("-5003", "接收数据失败"),
	
	NONE_USERABLE_SERVER("-6001", "无可用服务器"),
	
	UNKNOWN_WRONG("-9001", "未知的错误");
	
	
	/* - - - - - - - - - - 分割线  - - - - - - - - - -*/
	
	private String code;  // 错误码
	private String message;  // 错误信息
	
	private UnionError(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * 获取错误码
	 * 
	 * @return String 错误码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return String 错误信息
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 根据错误码获取对象实例
	 * 
	 * @param code 错误码
	 * 
	 * @return UnionError 对象实例
	 */
	public static UnionError getInstanceByCode(String code) {
		UnionError[] errors = UnionError.values();
		for(UnionError error : errors) {
			if(error.getCode().equals(code)) {
				return error;
			}
		}
		return UNKNOWN_WRONG;
	}

}
