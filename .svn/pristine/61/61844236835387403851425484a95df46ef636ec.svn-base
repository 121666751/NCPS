package com.union.interfaces;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;




/**
 * 服务器组
 * 
 * @author wu
 * @date 2016-04-13
 * @version 1.0
 */
public class UnionServerGroup extends BaseServerGroup<UnionServer> {
	
	private String serverType;  // 服务器类型, 固定为: ESSC、TKMS、KMS、IKMS
	private String sysID;  // 系统ID
	private String appID;  // 应用ID
	private int headLen;  // 消息头长度域
	private int timeout;  // 超时时间
	private int intervalTime;  // 检测的间隔时间
	private String connType;  // 连接类型: "0"为短连接, "1"为长连接
	private int maxConnCount;  // 最大连接数
	
	public UnionServerGroup() {
		super();
	}
	
	public UnionServerGroup(String serverType, String sysID, String appID, int headLen, int timeout, int intervalTime, String connType, int maxConnCount) {
		super();
		this.serverType = serverType;
		this.sysID = sysID;
		this.appID = appID;
		this.headLen = headLen;
		this.timeout = timeout;
		this.intervalTime = intervalTime;
		this.connType = connType;
		this.maxConnCount = maxConnCount;
	}
	
	private AtomicInteger indexAtomic = new AtomicInteger(0);
	
	public int getRrIndex() {
		return indexAtomic.get();
	}
	
	public void addRrIndex() {
		indexAtomic.set(indexAtomic.incrementAndGet() % getServers().size());
	}
	

	
	@Override
	public boolean addServer(UnionServer server) {
		if(super.addServer(server)) {
			server.setBelongGroup(this);
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean removeServer(UnionServer server) {
		if(super.removeServer(server)) {
			server.setBelongGroup(null);
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean hasAliveServer() {
		List<UnionServer> servers = super.getServers();
		for (UnionServer server : servers) {
			if (server.getAliveState()) {
				return true;
			}
		}
		return false;
	}
	

	public String getServerType() {
		return serverType;
	}
	

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	

	public String getSysID() {
		return sysID;
	}
	

	public void setSysID(String sysID) {
		this.sysID = sysID;
	}
	

	public String getAppID() {
		return appID;
	}
	

	public void setAppID(String appID) {
		this.appID = appID;
	}
	

	public int getHeadLen() {
		return headLen;
	}
	

	public void setHeadLen(int headLen) {
		this.headLen = headLen;
	}
	

	public int getTimeout() {
		return timeout;
	}
	

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	

	public int getIntervalTime() {
		return intervalTime;
	}
	

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public String getConnType() {
		return connType;
	}
	

	public void setConnType(String connType) {
		this.connType = connType;
	}
	

	public int getMaxConnCount() {
		return maxConnCount;
	}
	

	public void setMaxConnCount(int maxConnCount) {
		this.maxConnCount = maxConnCount;
	}
	
}
