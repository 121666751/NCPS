package com.union.interfaces;

/**
 * 服务器接口
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 */
public interface ServerI {
	
	/**
	 * 获取ip地址
	 * 
	 * @return String ip地址
	 */
	public String getIp();
	
	
	/**
	 * 获取端口号
	 * 
	 * @return int 端口号
	 */
	public int getPort();

}
