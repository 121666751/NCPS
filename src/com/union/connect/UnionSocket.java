package com.union.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.union.interfaces.UnionServer;

/**
 * Socket连接资源
 * 
 * @author wu
 * @date 2016-04-19
 * @version 1.0
 */
public class UnionSocket {
	
	private Socket socket;  // socket连接
	private OutputStream outputStream;  // socket输出流
	private InputStream inputStream;  // socket输入流
	private final static Logger logger = Logger.getLogger(UnionSocket.class);
	
	public UnionSocket() {
		socket = new Socket();
	}
	
	public boolean connect(UnionServer server) {
		return connect(server.getIp(), server.getPort(), server.getBelongGroup().getTimeout());
	}
	
	
	/**
	 * 建立连接
	 * 
	 * @param ip  ip地址
	 * @param port  端口号
	 * @param timeout  超时时间(ms)
	 * @return boolean  true: 成功; false: 失败
	 */
	public boolean connect(String ip, int port, int timeout) {
		InetSocketAddress addr = null;
		try {
			addr = new InetSocketAddress(ip, port);
		} catch(Exception e) {  // ip地址或端口号不合法
			logger.error("The ip[" + ip + "] or port[" + port + "] is worng!", e);
			return false;
		}
		if(connect(addr, timeout)){
			return true;
		}else{
			logger.warn("Fail to connect server[" + ip + ":" + port+ "]");
			return false;
		}
		
	}
	
	
	private boolean connect(InetSocketAddress addr, int timeout) {
		try {
			socket.connect(addr, timeout);
			socket.setTcpNoDelay(true);  // 启用Nagle算法
			socket.setKeepAlive(true);  // 开启保持活动状态的套接字
			socket.setSoTimeout(timeout);  // 读取数据的最大阻塞时间
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	/**
	 * 获取连接状态
	 * 
	 * @return boolean true: 连通状态; false: 断开状态
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}
	
	
	/**
	 * 关闭连接
	 */
	public void close() {
		if (socket != null) { // 多次关闭时需要判断上一次是否为空
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socket = null;
				outputStream = null;
				inputStream = null;
			}
		}
	}
	
	
	/**
	 * 发送数据至服务器
	 * 
	 * @param data 待发送数据
	 * 
	 * @return boolean true: 发送成功; false: 发送失败
	 */
	private boolean send(byte[] data, String encoding) {
		// 发送数据
		try {
			outputStream = socket.getOutputStream();
			data[0] = (byte) ((data.length - 2) >>> 8);
			data[1] = (byte) ((data.length - 2) & 0xff);
			outputStream.write(data);
			outputStream.flush();
		} catch (IOException e) {
			try {
				logger.error("Fail to send message! message is :" + new String(data ,encoding), e);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			close();
			return false;
		}
		return true;
	}
	
	/**
	 * 接收服务器返回的数据
	 * 
	 * @return byte[] 服务器返回的数据
	 */
	private byte[] recv() {
		byte[] data = new byte[0];		//2017.06.05  socket超时连带空指针错误解决
		// 接收数据
		try {
			inputStream = socket.getInputStream();
			// 2字节报文长度(16进制)
			int high = inputStream.read();
			int low = inputStream.read();
			int dataLen = 0;  // 报文长度
			if(high != -1 && low != -1) {
				dataLen = (high << 8) | low;
			}
			data = new byte[dataLen];
			int rcvdLen = 0;
			while(rcvdLen < dataLen) {
				rcvdLen += inputStream.read(data, rcvdLen, dataLen - rcvdLen);
			}
		} catch (IOException e) {
			logger.warn("Fail to recv message! May Network or Server anomaly!", e);
			close();
		}
		return data;
	}
	

	
	/**
	 * 发送及接收数据
	 * 
	 * @param data  发送到服务器的数据
	 * 
	 * @return byte[]  服务器返回的数据
	 * 
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] sendAndRecv(byte[] data, String encoding) {
		if(send(data, encoding)) {  // 数据发送成功
			// 记录发送的报文
			if(logger.isDebugEnabled()) {
				try {
					logger.debug("Send Message: " + new String(data, encoding));
				} catch (UnsupportedEncodingException e) {
					logger.debug(data, e);
				}
			}
			// 接收服务器返回数据
			byte[] recvData = recv();
			
			// 记录接收的报文
			if(logger.isDebugEnabled()) {
				try {
					logger.debug("Recv Message: " + new String(recvData, encoding));
				} catch (UnsupportedEncodingException e) {
					logger.debug(recvData, e);
				}
			}
			
			return recvData; 
		} else {  // 数据发送失败
			return null;
		}
	}

}
