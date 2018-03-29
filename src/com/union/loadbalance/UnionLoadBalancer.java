package com.union.loadbalance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.union.config.ConfigParams;
import com.union.config.Environment;
import com.union.interfaces.UnionServer;
import com.union.interfaces.UnionServerGroup;
import com.union.message.UnionMessage;
import com.union.utils.UnionStr;

/**
 * 负载均衡
 * 
 * @author wu
 * @date 2016-04-18
 * @version 1.0
 */
public class UnionLoadBalancer {
	
	private static UnionServerAssigner assigner = new UnionServerAssigner();  // 分配器
	private static UnionServerChecker checker = new UnionServerChecker();  // 检测器
	private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private final static Logger logger = Logger.getLogger(UnionLoadBalancer.class);
	
	private static ReentrantLock checkLock = new ReentrantLock();
	
	private static String unionSystemType = null; 		//服务器组类型
	private static String unionType = null;       //服务码类型
	
	/**
	 * 分配一个可用的服务器
	 * 
	 * @return UnionServer 服务器(没有可用服务器则返回null)
	 */
	public static UnionServer assignOneServer(UnionMessage um) {
		Iterator<String> iter = Environment.getMasterservgroups().keySet().iterator();
		
		UnionServer server = null;
		
		//指定服务器组分配，没有在配置文件中的{CUSTOM}配置的服务返回null
		server = UnionCustomServer.assignCusServer(um);
		
		if(server != null){
			return server;
		}

		
		//服务默认选择器
		unionType = um.getHeadField("serviceCode").substring(0, 1);
		
		//根据服务码的首字母来判断服务器
		for(;iter.hasNext();) {
				unionSystemType = iter.next();
				//截取第一个服务器标识的首字母
				String type = unionSystemType.substring(0,1);
				//默认发往首字母一样的服务器组
				if(unionType.equals(type)){
					 server = assignOneServer(unionSystemType);
				}
				//如果是A或U开头的服务发往UAC
				if ("A".equalsIgnoreCase(unionType) || "U".equalsIgnoreCase(unionType)) {
					 server = assignOneServer("UAC");
				}
				if(server != null){
					return server;
				}
		}
		//没有找到对应的服务器类型
		String errStr = "serverList.conf can't find the ServerType:" + UnionStr.ServerType(unionType);
		logger.error(errStr);
		throw new ExceptionInInitializerError(errStr);
	}
	
	
	/**
	 * 根据系统类型分配一个可用的服务器
	 * 
	 * @param unionSystemType 系统类型
	 * 
	 * @return UnionServer 服务器(没有可用服务器则返回null)
	 */
	public static UnionServer assignOneServer(String unionSystemType) {
		UnionServerGroup masterGroup = Environment.getMasterservgroups().get(unionSystemType);
		UnionServerGroup backupGroup = Environment.getBackupservgroups().get(unionSystemType);
		
		//该服务器组不存在或服务器组中没有服务器
		if((masterGroup == null || masterGroup.getServers().size() == 0) 
			 && (backupGroup == null || backupGroup.getServers().size() == 0)) {
			logger.error("None server in ServerType[" + unionSystemType + "]!");
			return null;
		}
		
		// 仅有一个服务器
		if (masterGroup.getServers().size() == 1
				&& (backupGroup == null || backupGroup.getServers().size() == 0)) {
			return masterGroup.getServers().get(0);
		}
		
		//加权轮询版
		// 主备切换
		UnionServer server = assigner.assignOneServer(masterGroup);  // 主服务器
		if(server != null ) {
			return server;
		}
		
		logger.warn("[LB] loadBalancer switch to backupGroup");
		server = assigner.assignOneServer(backupGroup);  // 备用服务器
		
		if(server == null) {
			logger.warn("All servers are fault!");
			// 强制恢复
			server = coerceOneServToNormal();
			if (server == null) {
				try {
					server = masterGroup.getServers().get(0);					
				} catch(IndexOutOfBoundsException e) {
					logger.error("unionSystemType: " + unionSystemType
							+ "\nmasterGroup: " + masterGroup
							+ "\nallServerGroup: " + Environment.getMasterservgroups()
							); 
					throw e;
				}
			}
			return server;
		}else{
			return server;
		}
	}
	
	
	/**
	 * 启动服务器检测线程
	 * 
	 * @param server 待检测的服务器
	 */
	public static void checkServer(UnionServer server, long delay) {
		try {
			checkLock.lock();
			if(server.getCheckFlag()) {
				return;
			}
			server.setCheckFlag(true);
		} finally {
			checkLock.unlock();
		}
		
		logger.warn("[LB] Check Server[" + server.getIp() + ":" + server.getPort() + "].");
		UnionCheckRunnable runn = new UnionCheckRunnable(server, checker);
		executor.schedule(runn, delay, TimeUnit.SECONDS);
	}
	
	// 强制恢复机制
	private static List<UnionServer> faultServers = new ArrayList<UnionServer>();  // 故障服务器
	private final static ReentrantLock fsLock = new ReentrantLock();  // 故障服务器读写锁
	
	
	public static void addOneFaultServer(UnionServer server) {
		// logger.warn("[LB] add one FaultServer");
		try {
			fsLock.lock();
			if(server.getAliveState() || server.getSwitchState().equals(ConfigParams.SERVER_SWITCH_RECOVERING)) {
				server.setAliveState(false);
				server.setSwitchState(ConfigParams.SERVER_SWITCH_OFF); 
				server.setDownTime(System.currentTimeMillis()); //记录停用时的时间戳
				
				//判断失败组中是否已经存在该服务器，是则替换，否则直接加入
				if(faultServers.indexOf(server) == -1) {
					faultServers.add(server);
				} else {
					faultServers.remove(server);
					faultServers.add(server);
				}
				
				logger.warn("[LB] Server[" + server.getIp() + ":" + server.getPort() + "] is fault!");				
			  }
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			fsLock.unlock();
		}
	}
	
	
	public static void removeOneFaultServer(UnionServer server) {
		// logger.warn("[LB] remove one FaultServer");
		try {
			fsLock.lock();
			if(!server.getAliveState()) {
				faultServers.remove(server);
				server.setAliveState(true);
				server.setSwitchState(ConfigParams.SERVER_SWITCH_ON);  //将服务器置为可用
				logger.warn("[LB] server[" + server.getIp() + ":"
						+ server.getPort() + "] resumed normal!");
			}
		} finally {
			fsLock.unlock();
		}
	}
	
	private static UnionServer coerceServer;  // 最近被强制恢复的服务器
	
	/**
	 * 强制将第一个发生故障的服务器恢复正常
	 */
	public static UnionServer coerceOneServToNormal() {
		try {
			fsLock.lock();
			if(faultServers.size() > 0) {
				coerceServer = faultServers.get(0);
				coerceServer.setAliveState(true);
				coerceServer.setSwitchState(ConfigParams.SERVER_SWITCH_ON);
				logger.warn("[LB] Coerce server[" + coerceServer.getIp() + ":"
						+ coerceServer.getPort() + "] to normal!");
				faultServers.remove(0);
			}
		} finally {
			fsLock.unlock();
		}
		
		return coerceServer;
	}

}
