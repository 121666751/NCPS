package com.union.loadbalance;

import java.util.Iterator;

import com.union.config.Environment;
import com.union.connect.CustomServer;
import com.union.interfaces.UnionServer;
import com.union.message.UnionMessage;

public class UnionCustomServer {
	
	
	private static String unionCusType = null;       //特定服务器类型
	private static String unionCuscode = null;      //特定服务码
	
	
	public static UnionServer assignCusServer(UnionMessage um){
	//指定服务器
	Iterator<String> cusiter = Environment.getCusserverGroups().keySet().iterator();
	
	Iterator<String> cuscodeiter = Environment.getCusserverCode().keySet().iterator();
	
	UnionServer server = null;
	
	
	if (cusiter.hasNext()){
		unionCusType = cusiter.next();
	}
	if (cuscodeiter.hasNext()){
		unionCuscode = cuscodeiter.next();
	}
	
	//指定服务器，如果没有指定则默认发往配置服务器
	if (unionCusType != null || unionCuscode != null){
		
		//再次加载指定服务器进行发送
		Iterator<String> cuscodeiter2 = Environment.getCusserverCode().keySet().iterator();
		 
		CustomServer cusServer = new CustomServer();
		//循环发送服务
			for(;cuscodeiter2.hasNext();){
				unionCuscode = cuscodeiter2.next();
				cusServer = Environment.getCusserverCode().get(unionCuscode);
				unionCusType = cusServer.getServergroup();
	            if(unionCuscode.equals(um.getHeadField("serviceCode"))){
	            	server = UnionLoadBalancer.assignOneServer(unionCusType);
			        if(server != null){
					     return server;
			        }
	            }else{
	            	continue;
	            }	
			}
		}
	 return server;
	
	}

}
