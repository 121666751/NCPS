package com.union.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.union.connect.CustomServer;
import com.union.connect.UnionSocket;
import com.union.connect.UnionPoolFactory;
import com.union.connect.UnionPoolGroup;
import com.union.error.ConfigMistakeException;
import com.union.interfaces.UnionServer;
import com.union.interfaces.UnionServerGroup;
import com.union.utils.UnionStr;

/**
 * 运行环境
 * 
 * @author wu
 * @date 2016-04-14
 * @version 1.0
 */
public class Environment {
	
	private final static Environment instance = new Environment();
	
	// 主服务器组
	private final static Map<String, UnionServerGroup> masterServGroups;
	
	// 备用服务器 
	private final static Map<String, UnionServerGroup> backupServGroups;
	
	//添加特殊服务指定系统
	private final static Map<String, CustomServer> cusserverGroups ;
	
	private final static Map<String, CustomServer> cusserverCode ;
	
	static {
		masterServGroups = new HashMap<String, UnionServerGroup>();
		backupServGroups = new HashMap<String, UnionServerGroup>();		//by linxl
		cusserverGroups = new HashMap<String, CustomServer>();
		cusserverCode = new HashMap<String, CustomServer>();
	}
	
	private final static String DEFAULT_CONFIG_FILE = "/serverList.conf";
		
	private static final Logger logger = Logger.getLogger(Environment.class);
	
	private Environment() {};
	
	/*
	 * 是否启用负载均衡标志，通过配置的服务器项数等于2或者等于4自动判断。
	 * false：基础版本；true：负载均衡版本
	 */
	private static boolean loadbalanceEnable = false;	
	
	public static Environment getInstance() {
		return instance;
	}
	
	/**
	 * 加载默认服务器配置文件
	 * @return 
	 */

	
	//2017.2.6    for by jzg
	public boolean loadServConfFile() {
		
		//当前类加载
		/*
		 * jmeter压测出错修改 2017.07.07 linxl
		 * InputStream is =  Environment.class.getClass().getResourceAsStream(DEFAULT_CONFIG_FILE);  
		 */
		InputStream is =  Environment.class.getResourceAsStream(DEFAULT_CONFIG_FILE);
		
		if(is != null){
			if(loadServConfFile(is)){
				return true;
			}else{
				logger.error("加载配置文件出错");
				return false;
			}
		}
		return false;
	}
	
	
	/**
	 * 加载服务器配置文件
	 * 
	 * @param filePath  配置文件的路径
	 */
	public synchronized boolean loadServConfFile(InputStream is) {
		// for dubug 2016.11.28
//		filePath = "/data/spdbjmeter/apache-jmeter-2.13/bin/serverList.conf";
		if(masterServGroups.size() != 0 || backupServGroups.size() != 0) {	//by linxl
			return true;
		}
		// 解析配置文件
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {
			fr = new InputStreamReader(is);
			br = new BufferedReader(fr);
			String lineStr = br.readLine();
			int lineNum = 1;
			String[] bufArray = null;
			String type = null;
			String partFlag = null;	//用于判断在配置文件的第几部分
			int customNum = 0;
 			while(lineStr != null) {
				lineStr = lineStr.replaceAll(" ", "");
				
//				去掉tab键 \t
				lineStr = lineStr.replaceAll("\t", "");
				boolean flag = true;
				if(lineStr.indexOf("[") == 0) {
					
//					排除配置文件错误
					String[] checkArrayBegin = lineStr.split("\\[", -1);			//-1是为了把最后的空字符也保留
					String[] checkArrayEnd = lineStr.split("]", -1);	
					if(partFlag == null) {						//配置文件第一部分
						if(checkArrayBegin.length-1 != ConfigParams.SERVER_GROUP_ITEM_COUNT
								|| checkArrayEnd.length-1 !=  ConfigParams.SERVER_GROUP_ITEM_COUNT
						) {	
							return false;
						}
					} else if(!"CUSTOM".equals(partFlag)) {	//配置文件第二部分
						//第一台服务器配置，设置是否为负载均衡版本标志
						if(ConfigParams.server_counts == 0) {
							//配置项数为4项时，启用负载均衡；为2项时，启用基础版本；否则配置文件出错
							if(checkArrayBegin.length-1 == ConfigParams.SERVER_ITEM_COUNT 
								 &&  checkArrayEnd.length-1 ==  ConfigParams.SERVER_ITEM_COUNT
								) {
								loadbalanceEnable = true;
							} else if(checkArrayBegin.length-1 == ConfigParams.SERVER_ITEM_COUNT_BASE
								&& checkArrayEnd.length-1 ==  ConfigParams.SERVER_ITEM_COUNT_BASE
							) {
								loadbalanceEnable = false;
							} else {
								return false;
							}
						} else {
							if(loadbalanceEnable) {
								//负载均衡版本
								if(checkArrayBegin.length-1 != ConfigParams.SERVER_ITEM_COUNT
										|| checkArrayEnd.length-1 !=  ConfigParams.SERVER_ITEM_COUNT
								) {
									return false;
								}
							} else {
								//基础版本，控制一个类型的服务器组中，只能配置一台服务器
								if(masterServGroups.get(partFlag).getServers().size() >= 1) {
									return false;
								}
								if(checkArrayBegin.length-1 != ConfigParams.SERVER_ITEM_COUNT_BASE
										|| checkArrayEnd.length-1 != ConfigParams.SERVER_ITEM_COUNT_BASE
								) {	
									return false;
								}
							}
						}
						
					} else if("CUSTOM".equals(partFlag)){   //配置文件第三部分
						if(checkArrayBegin.length-1 != ConfigParams.CUSSERVER_ITEM_COUNT
								|| checkArrayEnd.length-1 !=  ConfigParams.CUSSERVER_ITEM_COUNT
						) {	
							return false;
						}
					}
					
					lineStr = lineStr.replaceAll("\\[", "");
					bufArray = lineStr.split("]");
					if(lineStr.charAt(0) > '0' && lineStr.charAt(0) <= '9' ) {  // ip地址等信息
						if(loadbalanceEnable && ConfigParams.SERVER_SWITCH_OFF.equals(bufArray[3])) {		//服务器设为down时，不加入服务器组
							continue;
						}
						if(bufArray == null || !addServer(type, bufArray)) {  // 添加服务器
							flag = false;
						}else{
							ConfigParams.server_counts++;
						}
					} else if(bufArray.length > 3){  // 服务器类型
						if(bufArray == null || bufArray.length < ConfigParams.SERVER_GROUP_ITEM_COUNT || !addServerGroup(bufArray)) {  // 添加服务器类型(组)
							flag = false;
						}
						type = bufArray[0];
						partFlag = null;
					}else {	//指定服务等信息
						if((bufArray == null || bufArray.length < ConfigParams.CUSTOM_SERVER  || !addCusServer(bufArray))){
							flag = false;
						}
						customNum ++;
						if(customNum > 100) {		//最多100条
							flag = false;
						}
						type = bufArray[0];
					}
				} else if(lineStr.indexOf("{") == 0) {  // 服务器标识
					if(lineStr.indexOf("}") < 0) {  // 配置错误
						flag = false;
					} else {
						type = lineStr.substring(1, lineStr.indexOf("}"));
						partFlag = type;
					}
				}
				
				if(!flag) {
					ConfigMistakeException cme = new ConfigMistakeException("Config file mistake at line: " + lineNum);
					logger.error("加载默认配置文件失败."+cme);
					return false;
				}else{
					 lineStr = br.readLine();
					 lineNum++;
				}
			}
 			
 			//当服务器总台数为零，提示配置文件错误
 			if(ConfigParams.server_counts == 0) {
 				logger.error("配置文件错误：服务器总台数为0.");
 				throw new ExceptionInInitializerError("The number of server is 0");
 			}
 			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Fail to read config file!", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Fail to read config file!", e);
		} finally {
			try {
				br.close();
				br = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
		
	}
	
	
	/**
	 * 重新加载服务器配置文件
	 * 
	 * @param filePath  配置文件的路径
	 * 
	 * @throws Exception 
	 */
	public void reloadServConfile(InputStream filePath) throws Exception {
		// 待补充，释放旧资源
		
		loadServConfFile(filePath);
	}
	
	
	/**
	 * 添加服务器组
	 * 
	 * @param confItems 配置信息
	 * 
	 * @return boolean true:添加成功;false:添加失败
	 */
	private boolean addServerGroup(String[] confItems) {
		String ust = getUnionSystemType(confItems[0]);
		if(ust == null) {
			return false;
		}
		
		try {
			// 主服务器组
			UnionServerGroup musg = new UnionServerGroup();
			
//			如果已经有这个服务器组，则不再加入
			Iterator<String> masterKeySet = masterServGroups.keySet().iterator();
			Iterator<String> backupKeySet = backupServGroups.keySet().iterator();
			while(masterKeySet.hasNext() || backupKeySet.hasNext()) {
				if(ust.equals(masterKeySet.next()) || ust.equals(backupKeySet.next())) {
					return true;
				}
			}
			
			musg.setServerType(confItems[0]);
			musg.setSysID(confItems[1]);
			musg.setAppID(confItems[2]);
			musg.setHeadLen(Integer.parseInt(confItems[3]));
			musg.setTimeout(Integer.parseInt(confItems[4]));
			
//			musg.setIntervalTime(Integer.parseInt(confItems[5]));
//			2017-4-21 linxl 解决重新检测时间小于20秒的问题
			int intervalTime = Integer.parseInt(confItems[5]);
			if(intervalTime < 20) {
				musg.setIntervalTime(20);
			} else {
				musg.setIntervalTime(intervalTime);
			}
			
			musg.setConnType(confItems[6]);
			musg.setMaxConnCount(Integer.parseInt(confItems[7]));
			masterServGroups.put(ust, musg);
			

			// 备用服务器组
			UnionServerGroup busg = new UnionServerGroup();
			busg.setServerType(confItems[0]);
			busg.setSysID(confItems[1]);
			busg.setAppID(confItems[2]);
			busg.setHeadLen(Integer.parseInt(confItems[3]));
			busg.setTimeout(Integer.parseInt(confItems[4]));
			
//			busg.setIntervalTime(Integer.parseInt(confItems[5]));
			int intervalTime2 = Integer.parseInt(confItems[5]);
			if(intervalTime2 < 20) {
				busg.setIntervalTime(20);
			} else {
				busg.setIntervalTime(intervalTime2);
			}
			
			busg.setConnType(confItems[6]);
			busg.setMaxConnCount(Integer.parseInt(confItems[7]));
			backupServGroups.put(ust, busg);
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Fail to addServerGroup!", e);
			return false;
		}
	}
	
	
	
	
	/**
	 * 添加服务器
	 * 
	 * @param server 服务器
	 * 
	 * @return boolean true:添加成功;false添加失败
	 */
	private boolean addServer(String type, String[] confItems) {
		String ust = getUnionSystemType(type);
		if(ust == null) {
			return false;
		}
		
		UnionServerGroup serverGroup = null;
		try {
			UnionServer server = null;
			//判断API版本
			if(loadbalanceEnable) {
				//负载均衡版本
				server = new UnionServer(confItems[0], Integer.parseInt(confItems[1]), confItems[2], confItems[3]);
			} else {
				//基础版本
				server = new UnionServer(confItems[0], Integer.parseInt(confItems[1]),"master", "up");
			}
			
			if(ConfigParams.MASTER_SERVER_TYPE.equalsIgnoreCase(server.getType())) {  //  添加至主服务器组
				serverGroup = masterServGroups.get(ust);
				// 连接方式为长连接,初始化连接池
				if(serverGroup != null){
						if(ConfigParams.CONNECT_TYPE_LONG.equalsIgnoreCase(serverGroup.getConnType()) ) {
							 GenericObjectPool<UnionSocket> pool = UnionPoolFactory.createDefaultPool(server, serverGroup.getTimeout(), serverGroup.getMaxConnCount());
							 UnionPoolGroup.addPool(server, pool);
						}
						return serverGroup.addServer(server);
				}

			} else if(ConfigParams.BACKUP_SERVER_TYPE.equalsIgnoreCase(server.getType())) {  //添加至备用服务器组
				serverGroup = backupServGroups.get(ust);
				// 连接方式为长连接,初始化连接池
				if(serverGroup != null){
					if(ConfigParams.CONNECT_TYPE_LONG.equalsIgnoreCase(serverGroup.getConnType())) {
						GenericObjectPool<UnionSocket> pool = UnionPoolFactory.createDefaultPool(server, serverGroup.getTimeout(), serverGroup.getMaxConnCount());
						 UnionPoolGroup.addPool(server, pool);
					}
					return serverGroup.addServer(server);
				} 
			} else {
				return false;
			}
			
			return false;
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Fail to addServer!", e);
			return false;
		}
	}
	
	
	/**
	 * 添加指定服务
	 * 
	 * @param confItems 配置信息
	 * 
	 * @return boolean true:添加成功;false:添加失败
	 */
	private boolean addCusServer(String[] confItems) {
		try {
			
			CustomServer server = new CustomServer();
			//判断服务器组首字母，服务类型
			String CusServerType= UnionStr.ServerType(confItems[1].substring(0,1));
			//判断是否符合，服务器类型
			String ServerType = getUnionSystemType(CusServerType);
			if(ServerType == null) {
				return false;
			}else{
				server.setServergroup(UnionStr.ServerType(confItems[1]));
				cusserverGroups.put(ServerType, server);
			}
			
			// 指定服务
		
			if(!UnionStr.CodeType(confItems[0])){
				logger.error("配置文件服务码错误"+confItems[0]);
            	return false;
			}else{
				server.setServercode(confItems[0]);
				server.setServergroup(ServerType);
				cusserverCode.put(confItems[0], server);
			}
		
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Fail to addCusServer!", e);
			return false;
		}
	}
	
	/**
	 * 获取系统类型
	 * 
	 * @param serverType 服务器类型(字符)
	 * 
	 * @return UnionServerType
	 */
	private String getUnionSystemType(String systemType) {
		if ("ESSC".equalsIgnoreCase(systemType)) {
			return UnionSystemType.ESSC;
		}
		if ("TKMS".equalsIgnoreCase(systemType)) {
			return UnionSystemType.TKMS;
		}
		if ("KMS".equalsIgnoreCase(systemType)) {
			return UnionSystemType.KMS;
		}
		if ("IKMS".equalsIgnoreCase(systemType)) {
			return UnionSystemType.IKMS;
		}
		if ("UAC".equalsIgnoreCase(systemType)) {
			return UnionSystemType.UAC;
		}
		if ("OTPS".equalsIgnoreCase(systemType)) {
			return UnionSystemType.OTPS;
		}
		return null;
	}


	public static Map<String, UnionServerGroup> getMasterservgroups() {
		return masterServGroups;
	}

	public static Map<String, UnionServerGroup> getBackupservgroups() {	
		return backupServGroups;
	}

	public static Map<String, CustomServer> getCusserverGroups() {
		return cusserverGroups;
	}
	
	public static Map<String, CustomServer> getCusserverCode() {
		return cusserverCode;
	}

}
