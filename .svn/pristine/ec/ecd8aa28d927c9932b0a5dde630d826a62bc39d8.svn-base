package com.union.loadbalance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.union.interfaces.ServerCheckerI;
import com.union.interfaces.UnionServer;

/**
 * 服务器健康检测
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 */
public class UnionServerChecker implements ServerCheckerI<UnionServer> {
	
	private Socket socket;
	
	public boolean check(UnionServer server) {
		socket = new Socket();
		try {
			SocketAddress sa = new InetSocketAddress(server.getIp(), server.getPort());
			socket.connect(sa, server.getBelongGroup().getTimeout());
			return socket.isConnected();
		} catch(Exception e) {
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}
