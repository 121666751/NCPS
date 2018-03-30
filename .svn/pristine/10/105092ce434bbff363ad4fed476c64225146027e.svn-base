package com.union.loadbalance;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.union.interfaces.ServerAssignerI;
import com.union.interfaces.UnionServer;
import com.union.interfaces.UnionServerGroup;

/**
 * 服务器分配
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 */
public class UnionServerAssigner implements ServerAssignerI<UnionServerGroup, UnionServer> {
	private final static Logger logger = Logger.getLogger(UnionServerAssigner.class);

	public UnionServer assignOneServer(UnionServerGroup serverGroup) {		//加权轮询分配
		try {
			Class<?> clazz = Class.forName("com.union.loadbalance.WeightRoundRobin");
			Method method = clazz.getMethod("assignOneServer", UnionServerGroup.class);
			return (UnionServer) method.invoke(null, serverGroup);
		} catch (Exception e) {
			final String errmsg = "can not create WeightRoundRobin instance or found the method named assignOneServer.";
			logger.error(errmsg, e);
			throw new ExceptionInInitializerError(errmsg + e.getMessage());
		}
	}

}
