package com.union.connect;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.pool2.ObjectPool;
import org.apache.log4j.Logger;

import com.union.config.ConfigParams;
import com.union.config.MessageType;
import com.union.error.UnionError;
import com.union.interfaces.UnionServer;
import com.union.interfaces.UnionServerGroup;
import com.union.loadbalance.UnionLoadBalancer;
import com.union.message.UnionMessage;
import com.union.message.UnionResponse;
import com.union.utils.UnionServerWeight;
import com.union.utils.UnionStr;

public class UnionComm {
	
	private final static String DEFAULT_CHARSET = "GBK";  // 默认的字符编码为GBK
	private final static String DEFAULT_MESSAGE_TYPE = "TLV";  // 默认的报文格式为XML
	
	private static String charset = DEFAULT_CHARSET;  // 字符编码
	private static String msgType = DEFAULT_MESSAGE_TYPE;  // 报文格式
	
	private static Logger logger = Logger.getLogger(UnionComm.class);
	
	
	/**
	 * 发送以及接收报文
	 * 
	 * @param head
	 * @param body
	 * @param maxSendCounts  服务器个数
	 * 
	 * @return TUnionTransInfo
	 */
	public static UnionMessage sendAndRecvMsg(UnionMessage um, int maxSendCounts) {				
		byte[] responseBytes = null;
		if(maxSendCounts == 1){ //只有一台，次数加1
			maxSendCounts++;
		}
		while(maxSendCounts > 0) {  // 重连次数与服务器总数一致
			UnionServer server = UnionLoadBalancer.assignOneServer(um);
			compMessage(um, server.getBelongGroup());
			byte[] requestBytes = um.toByteArray();
			if(requestBytes.length > ConfigParams.MESSAGE_MAX_LENGTH) {  // 报文过大
				return UnionResponse.newFailInstance(UnionError.MESSAGE_TOO_BIG);
			}
			for (int i = 0; i < ConfigParams.MAX_CONNECT_COUNTS; i++) { // 单台重试
				responseBytes = sendAndRecvMsg(server, requestBytes);
				if(responseBytes != null){
					break;
				}
			}
			maxSendCounts--; //重连次数减1
			if(responseBytes == null || responseBytes.length == 0){  // 通讯失败,将服务器置为不可用				
				logger.warn("communication is failed!!The server ip is "+ server.getIp()+ " and port is " + server.getPort());				
				UnionLoadBalancer.addOneFaultServer(server);
			}else{ // 通讯成功
				if(server.getSwitchState().equals(ConfigParams.SERVER_SWITCH_RECOVERING)){					
					UnionLoadBalancer.removeOneFaultServer(server); //若服务器处于恢复中状态，则将其置为可用
				}
				return analyzeRecvMsg(responseBytes); //返回报文
			}
		}
		return UnionResponse.newFailInstance(UnionError.FAIL_CONNECT_SERVER);
	}
	
	
	private static byte[] sendAndRecvMsg(UnionServer server, byte[] message) {
		String connType = server.getBelongGroup().getConnType();
		UnionSocket us = null;
		UnionServerWeight usw = new UnionServerWeight();		//		by linxl
		if (ConfigParams.CONNECT_TYPE_SHORT.equalsIgnoreCase(connType)) { // 短连接
			try {
				for (int i = 0; i < ConfigParams.MAX_CONNECT_COUNTS; i++) { // 重连机制
					us = new UnionSocket();
					if (us.connect(server)) { // 连接成功
						long startTime = UnionStr.getCurrentTimeMS();				//获得交易开始时间
						byte[] response = us.sendAndRecv(message, charset);
						if(response.length != 0) {				//发送成功，记录成功的交易次数和时间
							long endTime = UnionStr.getCurrentTimeMS();			//获得交易结束时间
							usw.addTotalTime(endTime - startTime, server);			//该服务器增加总处理时间
							usw.addSuccTime(server);			//服务器成功次数加1
						}
						return response;
					}
				}

				logger.warn("Server[" + server.getIp() + ":" + server.getPort()
						+ "] is fault!");
				return null;
			} finally {
				us.close();
			}
		} else {  // 长连接
			byte[] responseByte = null;
			// 浦发要求服务器接收的任务数要相等，负载策略已改成轮循，故无需实时更新服务器处理的任务数
//			// 任务开始,任务数加一
//			server.addTaskCounts();
			// 从池中获取连接,执行任务
			ObjectPool<UnionSocket> pool = UnionPoolGroup.getPool(server);
			try {
				us = pool.borrowObject();//从池中获取一个对象
				
				long startTime = UnionStr.getCurrentTimeMS();			//获得交易开始时间 by linxl
				responseByte = us.sendAndRecv(message, charset);
				if(responseByte != null) {
					long endTime = UnionStr.getCurrentTimeMS();		//获得交易结束时间
					usw.addTotalTime(endTime - startTime, server);		//该服务器增加总处理时间
					usw.addSuccTime(server);			//服务器成功次数加1
				}
			} catch (Exception e) {  // 获取有效的连接失败(池中无空闲资源或服务器故障)
				logger.warn("Fail to get resource from pool!The server ip is "+ server.getIp()+ " and port is " + server.getPort(), e);
			} finally {
				// 浦发要求服务器接收的任务数要相等，负载策略已改成轮循，故无需实时更新服务器处理的任务数
//				// 任务结束,任务数减一
//				server.reduceTaskCounts();
				if(us != null) {
					if(responseByte == null){ //通讯失败						
						try {
							pool.invalidateObject(us);  //池中销毁socket
							pool.clear();  //清理空闲连接
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("release pool is fault!The server ip is "+ server.getIp()+ " and port is " + server.getPort(), e);
						}
					}else{ //通讯成功
						try {
							pool.returnObject(us); //归还socket回池中
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("return pool is fault!The server ip is "+ server.getIp()+ " and port is " + server.getPort(), e);
						}
					}
				}
			}
			return responseByte;
		}
	}
	
	
	/**
	 * 解析返回的报文数据
	 * 
	 * @param message 报文数据
	 * 
	 * @return TUnionTransInfo
	 */
	private static UnionMessage analyzeRecvMsg(byte[] data) {
		if("-".charAt(0) == data[0]) {  // 返回的是错误码
			String errorCode = new String(data);
			return UnionResponse.newFailInstance(UnionError.getInstanceByCode(errorCode));
		}
		
//		// 记录返回报文
//		String messageStr;
//		try {
//			messageStr = new String(data, charset);
//		} catch (UnsupportedEncodingException e) {
//			if(logger.isDebugEnabled()) {
//				logger.debug("不支持" + charset + "字符编码", e);
//			}
//			return UnionMessage.newFailInstance(UnionError.UNSUPPORT_CHARACTER_ENCODING);
//		}
		
		// 此处待后续修改(根据MessageType自动获取相应的处理类,而非现在固定写死)
		if(MessageType.TLV.equalsIgnoreCase(msgType)) {  // TLV报文格式
			try {
				return UnionResponse.parse(data);
			} catch (Exception e) {
				if(logger.isDebugEnabled()) {
					logger.debug("解析TLV报文失败", e);
				}
				return UnionResponse.newFailInstance(UnionError.FAIL_ANALYSE_MESSAGE);
			}
		// mod by 2016.11.20 暂移除XML格式报文
//		} else if(MessageType.XML.equalsIgnoreCase(msgType)) {  // XML报文格式
//			return UnionMessage.parseXmlStr(messageStr);
		} else {  // 不支持的报文格式
			return UnionResponse.newFailInstance(UnionError.UNSUPPORT_MESSAGE_TYPE);
		}
	}
	
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 补全待发送的报文信息
	 * 
	 * @param um 待发送的报文
	 * 
	 */
	private static void compMessage(UnionMessage um, UnionServerGroup sug) {
		um.putHeadField("sysID", sug.getSysID());
		um.putHeadField("appID", sug.getAppID());
		um.putHeadField("clientIPAddr", getIpAddress());
		String transTime = sdf.format(Calendar.getInstance().getTime());
		um.putHeadField("transTime", transTime);
		um.putHeadField("transFlag", "1");
		String userInfo = transTime + Thread.currentThread().getId();
		um.putHeadField("userInfo", userInfo);
	}
	
	/**
	 * 获取本机IP地址
	 * 
	 * @return String ip地址
	 */
	public static String getIpAddress() {
		String hostIp = "";
		if(!UnionStr.isEmpty(clientIp) ){
			return clientIp;
		}
		try {
			InetAddress netAddress = InetAddress.getLocalHost();
			hostIp = netAddress.getHostAddress();
			clientIp = hostIp;
		}  catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostIp;

	}
	

	public static String getCharset() {
		return charset;
	}

	public static void setCharset(String charset) {
		UnionComm.charset = charset;
	}

	public static String getMsgType() {
		return msgType;
	}

	public static void setMessageType(String msgType) {
		UnionComm.msgType = msgType;
	}
		
	private static String clientIp;
	
	public static void setClientIp(String clientIp) {
		UnionComm.clientIp = clientIp;
	}

	
}
