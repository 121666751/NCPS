package com.union.interfaces;

/**
 * 服务器健康检测接口
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 * 
 * @param <T> 服务器实体类
 */
public interface ServerCheckerI<T extends ServerI> {
	
	/**
	 * 检测服务器状态
	 * 
	 * @param server 待检测的服务器
	 * 
	 * @return boolean true:正常; false:故障
	 */
	public boolean check(T server);
	
}
