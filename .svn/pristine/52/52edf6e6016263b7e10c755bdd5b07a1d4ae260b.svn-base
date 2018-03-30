package com.union.connect;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.union.config.ConfigParams;
import com.union.config.Environment;

public class UnionStart {
	public static final String systemEnv = "UNION_API_CONFIG_FILE";
	
	private  int maxSendTimes;  // 重连次数
	
	private static String configFilePath;  // 配置文件路径，为空表示默认路径
	
	private final static Logger logger = Logger.getLogger(UnionStart.class);
	
	public UnionStart() {
		//指定配置文件
		try {
		if(configFilePath == null || "".equals(configFilePath)) {  // 默认配置文件
			if(!Environment.getInstance().loadServConfFile()){
			   throw new Exception("loadServConfFile try error!\n");
			}
		} 
			maxSendTimes = ConfigParams.server_counts;  //重连次数为服务器台数
			setMaxSendTimes(maxSendTimes);
		}catch (Exception e) {
			logger.error("初始化配置文件出错"+ e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过配置文件的绝对路径或者环境变量访问配置文件 <br />
	 * <blockquote>
	 * 		绝对路径：E:/mybranchWorkSpace/UnionAPI3.0.0/src/serverList.conf <br />
	 * 		环境变量：UNION_API_CONFIG_FILE=E:\mybranchWorkSpace\UnionAPI3.0.0\src
	 * </blockquote>
	 * 
	 * @param absolutePath 	配置文件的绝对路径或环境变量
	 */
	public UnionStart(String configfile) {
		final String path = System.getenv(configfile);
		if (path != null) {
			configfile = path + "/serverList.conf";
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(configfile);
			if(!Environment.getInstance().loadServConfFile(is)) {
				logger.error("加载配置文件出错" + configfile);
				throw new ExceptionInInitializerError("配置文件存在错误" + configfile);
			}
			maxSendTimes = ConfigParams.server_counts;
			setMaxSendTimes(maxSendTimes);
		} catch (Exception e) {
			String errmsg = "read configuration file failed. file absolutePath:" + configfile;
			logger.error(errmsg, e);
			throw new ExceptionInInitializerError(errmsg);
		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 设置配置文件路径
	 * 
	 * @param absolutePath 配置文件的绝对路径
	 */
	public static void setConfigFile(String absolutePath) {
		configFilePath = absolutePath;
	}


	public int getMaxSendTimes() {
		return maxSendTimes;
	}


	public  void setMaxSendTimes(int maxSendTimes) {
		this.maxSendTimes = maxSendTimes;
	}
	
	
}
