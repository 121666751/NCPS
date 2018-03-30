package com.union.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.union.interfaces.UnionServer;
import com.union.interfaces.UnionServerGroup;

/**
 * 记录处理服务器响应时、成功次数、权重
 * 记录各服务器组最大权重服务器、最大公约数、服务器选择序列、权重总和
 * @author linxl
 *
 */
public class UnionServerWeight {

	private static Map<String, Long> sumDealTime;		//记录全部服务器处理时间
	private static Map<String, Integer> succTimes;		//记录全部服务器成功次数
	private static Map<String, Integer> servWeight;		//记录全部服务器权重
	
	private static Map<String, UnionServer> serversMaxWeightServer;			//记录服务器组中拥有最大权重的服务器
	private static Map<String, Integer> serversMaxGCD;		//记录服务器组最大公约数
	private static Map<String, List<UnionServer>> servWeightList;				//服务器组的服务器选择序列
	private static Map<String, Integer> servsSumWeight; 		//服务器组的权重总和
	
	static {
		sumDealTime = new HashMap<String, Long>();
		succTimes = new HashMap<String, Integer>();
		servWeight = new HashMap<String, Integer>();
		serversMaxWeightServer = new HashMap<String, UnionServer>();
		serversMaxGCD = new HashMap<String, Integer>();
		servWeightList = new HashMap<String, List<UnionServer>>();
		servsSumWeight = new HashMap<String, Integer>();
	}
	
	/**
	 * 重新设置服务器处理时间
	 * @param setTime		需要设置的时间
	 * @param server			需要设置时间的服务器
	 */
	public void setTotalTime(long setTime, UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		sumDealTime.put(servKey, setTime);
	}
	
	/**
	 * 增加服务器处理时间
	 * @param addTime		需要增加的时间差
	 * @param server			需要增加时间的服务器
	 */
	public void addTotalTime(long addTime, UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		if(sumDealTime.get(servKey) != null) {
			long totalTime = sumDealTime.get(servKey) + addTime;
			sumDealTime.put(servKey, totalTime);
		} else {
			sumDealTime.put(servKey, addTime);
		}
	}
	
	/**
	 * 返回服务器总处理时间
	 * @param server
	 * @return 服务器总处理时间
	 */
	public long getTotalTime(UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		if(sumDealTime.get(servKey) == null) {
			return 0;		//不存在
		}
		return sumDealTime.get(servKey);
	}
	
	/**
	 * 重新设置服务器成功次数
	 * @param setTimes		设定的次数
	 * @param server			需要设定次数的服务器
	 */
	public void setSuccTime(int setTimes, UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		succTimes.put(servKey, setTimes);
	}
	
	/**
	 * 服务器成功次数增加1
	 * @param server		需要增加次数的服务器
	 */
	public void addSuccTime(UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		if(succTimes.get(servKey) != null) {
			int tempTimes = succTimes.get(servKey) + 1;
			succTimes.put(servKey, tempTimes);
		} else {
			succTimes.put(servKey, 1);
		}
	}
	
	/**
	 * 返回服务器的成功次数
	 * @param server
	 * @return 该服务器成功次数
	 */
	public int getSuccTimes(UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		if(succTimes.get(servKey) == null) {
			return 0;		//不存在
		}
		return succTimes.get(servKey);
	}
	
	/**
	 * 设置服务器权重
	 * @param weight	需要设定的权重
	 * @param server		需要设定权重的服务器
	 */
	public void setServWeight(int weight, UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		servWeight.put(servKey, weight);
	}
	
	/**
	 * 返回服务器权重
	 * @param server
	 * @return 服务器权重
	 */
	public int getServWeight(UnionServer server) {
		String servKey = server.getBelongGroup().getServerType() + server.getIp() + server.getPort();
		if(servWeight.get(servKey) == null) {
			return 0;		//不存在
		}
		return servWeight.get(servKey);
	}
	
	/**
	 * 设置服务器组拥有最大权重的服务器
	 * @param server
	 * @param serverGroup
	 */
	public void setServsMaxWeightServ(UnionServer server, UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		serversMaxWeightServer.put(servsKey, server);
	}
	
	/**
	 * 返回服务器组拥有最大权重的服务器
	 * @param serverGroup
	 * @return
	 */
	public UnionServer getServsMaxWeightServ(UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		return serversMaxWeightServer.get(servsKey);
	}
	
	/**
	 * 设置服务器组最大公约数
	 * @param maxGCD
	 * @param serverGroup
	 */
	public void setServsMaxGCD(int maxGCD, UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		serversMaxGCD.put(servsKey, maxGCD);
	}
	
	/**
	 * 返回服务器组最大公约数
	 * @param serverGroup
	 * @return
	 */
	public int getServsMaxGCD(UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		if(serversMaxGCD.get(servsKey) == null) {
			return 0;		//不存在
		}
		return serversMaxGCD.get(servsKey);
	}
	
	/**
	 * 设置服务器组的服务器选择序列
	 * @param servWeights
	 * @param serverGroup
	 */
	public synchronized void setServerGroupWeightList(List<UnionServer> servWeights, UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		servWeightList.put(servsKey, servWeights);
	}
	
	/**
	 * 返回服务器组的服务器选择序列
	 * @param serverGroup
	 * @return
	 */
	public synchronized List<UnionServer> getServerGroupWeightList(UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		return servWeightList.get(servsKey);
	}
	
	/**
	 * 移除该服务器组的选择序列中使用了的服务器，移除第一个
	 * @param serverGroup
	 */
	public synchronized void remUsedServ(UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		List<UnionServer> servList = servWeightList.get(servsKey);
		if (servList != null && !servList.isEmpty()) {
			servList.remove(0);
		}
		servWeightList.put(servsKey, servList);
	}
	
	/**
	 * 设置/记录 服务器组的权重总和
	 * @param sumW
	 * @param serverGroup
	 */
	public void setServsSumWeight(int sumW, UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		servsSumWeight.put(servsKey, sumW);
	}
	
	/**
	 * 返回服务器组的权重总和
	 * @param serverGroup
	 * @return int 服务器组的权重总和
	 */
	public int getServsSumWeight(UnionServerGroup serverGroup) {
		String servsKey = serverGroup.getServerType() + serverGroup.getServers().get(0).getType();
		if(servsSumWeight.get(servsKey) == null) {
			return 0;		//不存在
		}
		return servsSumWeight.get(servsKey);
	}
	
}
