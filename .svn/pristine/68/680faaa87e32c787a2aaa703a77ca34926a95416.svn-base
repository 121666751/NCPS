package com.union.interfaces;

import java.util.List;

/**
 * 服务器组接口
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 * 
 * @param <T> 服务器实体类
 */
public interface ServerGroupI<T extends ServerI> {
	
	/**
	 * 获取组中所有的服务器
	 * 
	 * @return List<T> 服务器列表
	 */
	public List<T> getServers();
	
	
	/**
	 * 添加一个服务器至组中
	 * 
	 * @param server 服务器
	 *  
	 * @return boolean true:添加成功; false:添加失败
	 */
	public boolean addServer(T server);
	
	
	/**
	 * 从组中删除一个服务器
	 * 
	 * @param server 服务器
	 * 
	 * @return boolean true:删除成功; false:删除失败
	 */
	public boolean removeServer(T server);
	
}
