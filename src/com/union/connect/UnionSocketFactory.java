package com.union.connect;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;

import com.union.interfaces.UnionServer;
import com.union.loadbalance.UnionLoadBalancer;

/**
 * 资源工厂
 * 
 * @author wu
 * @date 2016-04-27
 * @version 1.0
 */
public class UnionSocketFactory extends BasePooledObjectFactory<UnionSocket> {
	
	private UnionServer server;
	
	private final static Logger logger = Logger.getLogger(UnionSocketFactory.class);
	
	public UnionSocketFactory(UnionServer server) {
		this.server = server;
	}

	/**
	 * 创建对象(建立连接)
	 */
	@Override
	public UnionSocket create() throws Exception {
		
		UnionSocket us = null;
		
		int maxConnCounts = 2;  // 最大尝试连接的次数
		while(maxConnCounts > 0) {
			// for debug
			long beginTime = System.currentTimeMillis();
			us = new UnionSocket();
			if(us.connect(server)) {
				// for debug
				long endTime = System.currentTimeMillis();
				if((endTime - beginTime) > 80) {
					logger.warn("CreateConnect: " + (endTime - beginTime) + "ms");
				}
				return us;  // 连接成功,返回
			}
			maxConnCounts--;
		}
		
		logger.warn("Server[" + server.getIp() + ":" + server.getPort() + "] is fault!");
		// 连接失败,将服务器标识为故障状态
		UnionLoadBalancer.addOneFaultServer(server);
//		// 服务器故障,启动检测
//		UnionLoadBalancer.checkServer(server, 0);
		
		return us;
	}
	
	
	/**
	 * 销毁对象(关闭连接)
	 */
	@Override
    public void destroyObject(PooledObject<UnionSocket> po)
        throws Exception  {
		po.getObject().close();
		super.destroyObject(po);
    }
	
	
	/**
	 * 检测对象是否有效(检查连接状态)
	 */
	@Override
    public boolean validateObject(PooledObject<UnionSocket> po) {
		try {
			return po.getObject().isConnected();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
        
    }
	
	
	@Override
	public PooledObject<UnionSocket> wrap(UnionSocket obj) {
		return new DefaultPooledObject<UnionSocket>(obj);
	}
	

}
