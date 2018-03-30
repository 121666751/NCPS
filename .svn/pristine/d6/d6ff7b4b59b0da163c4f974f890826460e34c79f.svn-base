package com.union.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器组基类
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 * 
 * @param <T> 服务器实体类
 */
public class BaseServerGroup<T extends BaseServer> implements ServerGroupI<T> {
	
	private List<T> servers;  // 组中所有的服务器
	
	
	public BaseServerGroup() {
		servers = new ArrayList<T>();
	}
	
	
	public List<T> getServers() {
		return servers;
	}
	
	
	public boolean addServer(T server) {
		return servers.add(server);
	}
	
	
	public boolean removeServer(T server) {
		return servers.remove(server);
	}

}
