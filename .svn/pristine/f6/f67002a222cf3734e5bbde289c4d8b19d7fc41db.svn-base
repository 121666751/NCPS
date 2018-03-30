package com.union.loadbalance;

import org.apache.log4j.Logger;

import com.union.interfaces.UnionServer;

/**
 * 服务器检测线程
 * 
 * @author wu
 * @date 2016-04-27
 * @version 1.0
 */
public class UnionCheckRunnable implements Runnable {
	
	private UnionServer server;
	private UnionServerChecker checker;
	
	private static Logger logger = Logger.getLogger(UnionCheckRunnable.class);
	
	public UnionCheckRunnable(UnionServer server, UnionServerChecker checker) {
		this.server = server;
		this.checker = checker;
	}

	public void run() {
		if(!checker.check(server)) {  // 服务器故障,继续检测
			logger.warn("[Check] Server[" + server.getIp() + ":" + server.getPort() + "] is still fault!");
			server.setCheckFlag(false);
			UnionLoadBalancer.checkServer(server, server.getBelongGroup().getIntervalTime());
		} else {  // 服务器恢复正常,停止检测
			server.setCheckFlag(false);
			UnionLoadBalancer.removeOneFaultServer(server);
			if(logger.isDebugEnabled()) {
				logger.warn("[Check] Server[" + server.getIp() + ":" + server.getPort() + "] is recovery.");
			}
		}
		
	}

}
