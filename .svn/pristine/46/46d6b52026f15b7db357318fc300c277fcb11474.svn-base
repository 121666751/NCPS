package com.union.connect;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.union.config.ConfigParams;
import com.union.interfaces.UnionServer;

/**
 * 连接池工厂
 * 
 * @author wu
 * @date 2016-04-27
 * @version 1.0
 */
public class UnionPoolFactory {
	
	/**
	 * 创建一个默认配置连接池
	 * 
	 * @return
	 */
	public static GenericObjectPool<UnionSocket> createDefaultPool(UnionServer server, int timeout, int maxConnCount) {
		UnionSocketFactory factory = new UnionSocketFactory(server);
		
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		// 资源耗尽时阻塞
		config.setBlockWhenExhausted(true);
		// 获取资源时阻塞的最大时间
		config.setMaxWaitMillis(timeout);
		// 最大连接数
		config.setMaxTotal(maxConnCount);
//		// 公平锁(先到先得)
//		config.setFairness(true);
		// 队列方式(先进先出)
		config.setLifo(false);
		// 最小空闲连接数
		config.setMinIdle(0);
		//逐出连接的最小空闲时间,10分钟
		config.setMinEvictableIdleTimeMillis(10*60*1000);
		//运行逐出线程,逐出扫描的时间间隔,1分钟
		config.setTimeBetweenEvictionRunsMillis(ConfigParams.POOL_CHECK_INTERVAL);
		// 设置每次回收空闲连接数量
		config.setNumTestsPerEvictionRun(4);
		
		// 创建资源时,检测有效性
		config.setTestOnCreate(true);
		// 获取资源时,检测有效性(无效则从连接池中移除,并尝试继续获取)
		config.setTestOnBorrow(true);
		
		return createPool(factory, config);
	}
	
	public static GenericObjectPool<UnionSocket> createPool(UnionSocketFactory factory, GenericObjectPoolConfig config) {
		return new GenericObjectPool<UnionSocket>(factory, config);
	}

}
