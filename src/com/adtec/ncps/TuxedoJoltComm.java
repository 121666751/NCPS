package com.adtec.ncps;

import com.adtec.starring.dta.protocol.IComm;
import com.adtec.starring.dta.protocol.ICommSession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import com.adtec.starring.dta.protocol.IComm;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;

import bea.jolt.JoltRemoteService;
import bea.jolt.JoltSession;
import bea.jolt.JoltSessionAttributes;

/**
 * jorl调用tuxedo服务通讯
 * @author GuoFan
 *
 */
public class TuxedoJoltComm implements IComm{
	public JoltSession session;
	public JoltRemoteService passFml;
    public JoltSession getSession() {
		return session;
	}

	public void setSession(JoltSession session) {
		this.session = session;
	}

	public JoltRemoteService getPassFml() {
		return passFml;
	}

	public void setPassFml(JoltRemoteService passFml) {
		this.passFml = passFml;
	}
	public boolean check(String ipAddress, int port) {
		// TODO 自动生成的方法存根
		if (session == null) {
            return false;
        }
		return true;
	}

	public void close() {
		// TODO 自动生成的方法存根
		if (session != null) {
			passFml.clear();
			session.endSession();
			TrcLog.log("Tuxedo.log", "关闭连接", new Object[0]);
			if(session !=null){
				session = null;
				TrcLog.log("Tuxedo.log", "清空Joltsession对象", new Object[0]);
			}
		}	
        
	}

	@SuppressWarnings("static-access")
	public void connect(String ipAddress, int port,String url,int timeOut) {
		// TODO 自动生成的方法存根
		String userName = ResPool.configMap.get(DtaInfo.getInstance().getDtaName() + "_tuxedo_userName");
		String userRole = ResPool.configMap.get(DtaInfo.getInstance().getDtaName() + "_tuxedo_userRole");
		String userPassword = ResPool.configMap.get(DtaInfo.getInstance().getDtaName() + "_tuxedo_userPassword");
	    String appPassword = ResPool.configMap.get(DtaInfo.getInstance().getDtaName() + "_tuxedo_appPassword");
       
	    JoltSessionAttributes sattr = new JoltSessionAttributes();
		sattr.setString("APPADDRESS", "//"+ ipAddress + ":" + port);
		TrcLog.log("Tuxedo.log", "建立目标链接地址=" + ipAddress + ":" + port, new Object[0]);
		sattr.setInt("IDLETIMEOUT", timeOut);
		sattr.setInt("SENDTIMEOUT", timeOut);
		sattr.setInt("RECVTIMEOUT", timeOut);
		TrcLog.log("Tuxedo.log", "设置超时时间=" + sattr.getIntDef("IDLETIMEOUT", 0) + ",数据发送超时时间=" + sattr.getIntDef("SENDTIMEOUT", 0) + ",数据接收超时时间=" + sattr.getIntDef("RECVTIMEOUT", 0), new Object[0]);
		session = new JoltSession(sattr, userName, userRole, userPassword, appPassword);
		TrcLog.log("Tuxedo.log", "建立初始化连接", new Object[0]);
	}

	public ICommSession getICommSession() {
		// TODO 自动生成的方法存根
		return null;
	}

	public void init(boolean arg0) {
		// TODO 自动生成的方法存根
		
	}

	public byte[] receive(String svcName, int timeout) {
		
		try
	    {
	      TrcLog.log("Tuxedo.log", "[开始返回信息]", new Object[0]);
	      String outputString = this.passFml.getStringDef("STRING", null);
	      byte[] output = outputString.getBytes("8859_1");
	      TrcLog.log("Tuxedo.log", "[返回信息为]" + new String(output, "GBk"), new Object[0]);
	      //this.passFml.clear();
	      //this.session.endSession();
	      TrcLog.log("Tuxedo.log", "[结束]", new Object[0]);
	      return output;
	    }
	    catch (Exception e1) {
	      e1.printStackTrace();
	    }finally {
	    	this.passFml.clear();
		    this.session.endSession();
	    }
		return null;
	}

	public void recvFile(String arg0) {
		// TODO 自动生成的方法存根
		
	}

	public void send(String svcName, byte[] sendByte, int len){
		try
	    {
	      TrcLog.log("Tuxedo.log", "[开始发送信息]", new Object[0]);
	      this.passFml = new JoltRemoteService(svcName, this.session);
	      this.passFml.setString("STRING", new String(sendByte, "8859_1"));
	      TrcLog.log("Tuxedo.log", "[发送信息为]" + new String(sendByte, "GBK"), new Object[0]);
	      this.passFml.call(null);
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      this.passFml.clear();   
	      this.session.endSession();
	    }	    
	  }

	public void sendFile(String arg0) {
		// TODO 自动生成的方法存根
		
	}

	public void setICommSession(ICommSession arg0) {
		// TODO 自动生成的方法存根
		
	}

	public void term() {
		// TODO 自动生成的方法存根
		
	}
	

	public static final String toGBK(String msg) {
		try {
			byte[] b = msg.getBytes("8859_1");
			String convert = new String(b, "GBK");
			return convert;
		} catch (Exception localException) {
		}
		return new String("Convert failed!");
	}

	public static final String toISO8859(String msg) {
		try {
			byte[] b = msg.getBytes("GB2312");
			String convert = new String(b, "8859_1");
			return convert;
		} catch (Exception e) {
		}
		return new String("Convert failed!");
	}
	
	/**
	 * 调用TUXEDO的FTP服务下载文件
	 * @param fileName 文件名称   
	 * @param localPath 本地文件存放路径 
	 * @throws Exception
	 */
	public static void downLoadFile(String fileName,String localPath) throws Exception {
		 TrcLog.log("Tuxedo.log", "fileName=" + fileName + ",localPath=" + localPath, new Object[0]);
		String userName = ResPool.configMap.get("HOST_CLI_tuxedo_userName");
		String userRole = ResPool.configMap.get("HOST_CLI_tuxedo_userRole");
		String userPassword = ResPool.configMap.get("HOST_CLI_tuxedo_userPassword");
	    String appPassword = ResPool.configMap.get("HOST_CLI_tuxedo_appPassword");
		String ipAddress = ResPool.configMap.get("HOST_CLI_tuxedo_ipAddress");
		String IpPort = ResPool.configMap.get("HOST_CLI_tuxedo_port");
		if(!localPath.startsWith("/")){
			localPath =localPath + "/";
        }
		if(!fileName.startsWith("fil/")){
			localPath = localPath + fileName;
        	fileName="fil/"+fileName;
        }else{
        	localPath = localPath + fileName.substring(4);
        }				
		JoltSession session = null;
		JoltRemoteService joltService = null;
		try {
			File file = new File(localPath);
			if(!file.exists()){
				file.createNewFile();
			}
			TrcLog.log("Tuxedo.log", "文件存放本地路径=" + localPath, new Object[0]);
			// 设置链接属性
			JoltSessionAttributes attr = new JoltSessionAttributes();
		    attr.setString("APPADDRESS", "//" + ipAddress +":"+ IpPort);
		    TrcLog.log("Tuxedo.log", "设置目标链接地址=" + ipAddress + ":" + IpPort, new Object[0]);
		    attr.setInt("IDLETIMEOUT", 300);
		    TrcLog.log("Tuxedo.log", "设置超时时间=" + attr.getIntDef("IDLETIMEOUT", 0), new Object[0]);
			// 建立初始化连接
			session = new JoltSession(attr, userName, userRole, userPassword, appPassword);
			TrcLog.log("Tuxedo.log", "建立初始化连接", new Object[0]);
			// 需要的总控
			joltService = new JoltRemoteService("ftpsrv", session);
			TrcLog.log("Tuxedo.log", "【开始发送信息】", new Object[0]);

			// 设置发送消息
			joltService.setString("FTPFLAG", "2");
			joltService.setInt("FTPBLOCKID", 1);
			joltService.setInt("FTPBLOCKSIZE", 4096);
			TrcLog.log("Tuxedo.log", "设置要下载的文件名=" + fileName, new Object[0]);
			joltService.setString("FTPFILENAME", fileName);

			// 调用服务
			joltService.call(null);
			
			// 获取文件信息
			int iFileSize = joltService.getIntDef("FTPFILESIZE", 0);
			int iFileBlockSize = joltService.getIntDef("FTPBLOCKSIZE", 0);
			try {
				int blkNum = (iFileSize - 1) / iFileBlockSize + 1;
				byte[] buffer = new byte[iFileBlockSize];
				BufferedOutputStream out = null;
				try {
					 TrcLog.log("Tuxedo.log", "【开始下载文件】", new Object[0]);
					// 读取并写入第一个块
					out = new BufferedOutputStream(new FileOutputStream(file));
					buffer = joltService.getBytesItemDef("FTPFILEDATA", 0, null);
					out.write(buffer);
					
					// 读取并写入后面的块
					for (int i = 2; i <= blkNum; i++) {

						joltService.setInt("FTPBLOCKID", i);
						joltService.setInt("FTPBLOCKSIZE", 4096);

						joltService.call(null);
						
						buffer = joltService.getBytesItemDef("FTPFILEDATA", 0, null);
						out.write(buffer);
					}
					TrcLog.log("Tuxedo.log", "【文件下载完毕】", new Object[0]);
					out.flush();
				} catch (Exception e) {
					throw e;
				} finally {
					if (out != null) {
						out.close();
					}
				}

				String outputString = new String(buffer);
				TrcLog.log("Tuxedo.log", "文件内容为:" + outputString, new Object[0]);
			} catch (Exception e) {
				e.getStackTrace();
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (joltService != null) {
				joltService.clear();
			}
			if (session != null) {
				session.endSession();
			}
		}
	}
	
	/**
	 * 调用TUXEDO的FTP服务上传文件
	 * @param fileName 文件名称       
	 * @param localPath 本地文件存放路径     
	 * @return 文件
	 * @throws Exception
	 */
	public static void upLoadFile(String fileName,String localPath) throws Exception {
		TrcLog.log("Tuxedo.log", "fileName=" + fileName + ",localPath=" + localPath, new Object[0]);
		String userName = ResPool.configMap.get("HOST_CLI_tuxedo_userName");
		String userRole = ResPool.configMap.get("HOST_CLI_tuxedo_userRole");
		String userPassword = ResPool.configMap.get("HOST_CLI_tuxedo_userPassword");
	    String appPassword = ResPool.configMap.get("HOST_CLI_tuxedo_appPassword");
		String ipAddress = ResPool.configMap.get("HOST_CLI_tuxedo_ipAddress");
		String IpPort = ResPool.configMap.get("HOST_CLI_tuxedo_port");
		JoltSession session = null;
		JoltRemoteService joltService = null; 
		try {
			
			if(!localPath.startsWith("/")){
				localPath = localPath + "/";
	        }
			// 判断本地文件路径
			localPath = localPath + fileName;
			
			// 设置链接属性
			JoltSessionAttributes attr = new JoltSessionAttributes();
		    attr.setString("APPADDRESS", "//" + ipAddress +":"+ IpPort);
		    TrcLog.log("Tuxedo.log", "设置目标链接地址=" + ipAddress + ":" + IpPort, new Object[0]);
		    attr.setInt("IDLETIMEOUT", 300);
		    TrcLog.log("Tuxedo.log", "设置超时时间=" + attr.getIntDef("IDLETIMEOUT", 0), new Object[0]);

			// 建立初始化连接
			session = new JoltSession(attr, userName, userRole, userPassword, appPassword);
			 TrcLog.log("Tuxedo.log", "建立初始化连接", new Object[0]);
			// 需要的总控
			joltService = new JoltRemoteService("ftpsrv", session);
			 TrcLog.log("Tuxedo.log", "【开始发送信息】", new Object[0]);

			// 设置发送消息
			joltService.setString("FTPFLAG", "1");
			joltService.setInt("FTPBLOCKID", 1);
			joltService.setInt("FTPBLOCKSIZE",4096);
			TrcLog.log("Tuxedo.log", "设置要下载的文件名=" + fileName, new Object[0]);
			joltService.setString("FTPFILENAME",fileName);
			TrcLog.log("Tuxedo.log", "【开始上传文件】", new Object[0]);
			BufferedInputStream buf = null;
			try {
				buf = new BufferedInputStream(new FileInputStream(localPath));
				TrcLog.log("Tuxedo.log", "文件存放本地路劲=" + localPath, new Object[0]);
				byte[] bytes = new byte[1024];
				int len = -1;
				StringBuffer sbBuffer = new StringBuffer();
				while((len=buf.read(bytes))!=-1){
					sbBuffer.append(new String(bytes,0,len));
				}
				
				joltService.setBytes("FTPFILEDATA", new String(sbBuffer).getBytes(),new String(sbBuffer).length());
				// 调用服务
				joltService.call(null);
				TrcLog.log("Tuxedo.log", "文件内容为：" + new String(sbBuffer), new Object[0]);
				buf.close();
				TrcLog.log("Tuxedo.log", "【文件上传完毕】", new Object[0]);
			} catch (Exception e) {
				throw e;
			} finally {
				if (buf != null) {
					buf.close();
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (joltService != null) {
				joltService.clear();
			}
			if (session != null) {
				session.endSession();
			}
		}
	}
}
