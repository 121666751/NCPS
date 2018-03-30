package com.union.interfaces;


/**
 * 服务器基类
 * 
 * @author wu
 * @date 2016-04-13
 * @version 1.0
 */
public class BaseServer implements ServerI {
	
	private String ip;  // ip地址
	private int port;  // 端口
	
	private volatile boolean aliveState;  // 服务器存活状态
	
	public BaseServer(String ip, int port, boolean aliveState) {
		this.ip = ip;
		this.port = port;
		this.aliveState = aliveState;
	}
	
	@Override
	public int hashCode() {
		return (ip + ":" + port).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UnionServer) {
			UnionServer serv = (UnionServer) obj;
			return ip.equals(serv.getIp()) && port == serv.getPort();
		} else {
			return false;
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean getAliveState() {
		return aliveState;
	}

	public void setAliveState(boolean aliveState) {
		this.aliveState = aliveState;
	}

	@Override
	public String toString() {
		return "BaseServer [ip=" + ip + ", port=" + port + ", aliveState="
				+ aliveState + "]";
	}

}
