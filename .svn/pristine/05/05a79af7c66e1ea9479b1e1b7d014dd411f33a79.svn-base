package com.adtec.ncps.busi.ncp;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpClientUtils {

	/**
	 * 初始化日志引擎
	 */


	/** Sftp */
	ChannelSftp sftp = null;
	/** 主机 */
	private String host = "";
	/** 端口 */
	private int port = 0;
	/** 用户名 */
	private String username = "";
	/** 密码 */
	private String password = "";

	public SftpClientUtils() {
		// TODO Auto-generated constructor stub
	}

	public ChannelSftp getSftp() {
		return sftp;
	}

	public void setSftp(ChannelSftp sftp) {
		this.sftp = sftp;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 构造函数
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * 
	 */
	public void SftpClientUtil(String host, int port, String username, String password) {

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * 连接sftp服务器
	 * 
	 * @throws Exception
	 */
	public void connect() throws Exception {

		JSch jsch = new JSch();
		Session sshSession = jsch.getSession(this.username, this.host, this.port);
		System.out.println(this.username+this.host+this.port);
		TrcLog.log("sftp.log", "方法：connect==>	username：[%s]",this.username);
		TrcLog.log("sftp.log", "方法：connect==>	host：[%s]",this.host);
		TrcLog.log("sftp.log", "方法：connect==>	port：[%s]",this.port);
		TrcLog.log("sftp.log", "方法：connect==>	password：[%s]",this.password);
		TrcLog.log("sftp.log", "Session created.");

		sshSession.setPassword(password);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(sshConfig);
		sshSession.connect(20000);
		TrcLog.log("sftp.log", " Session connected.");

		TrcLog.log("sftp.log", " Opening Channel.");
		Channel channel = sshSession.openChannel("sftp");
		channel.connect();
		this.sftp = (ChannelSftp) channel;
		TrcLog.log("sftp.log", " Connected to " + this.host + ".");
	}

	/**
	 * Disconnect with server
	 * 
	 * @throws Exception
	 */
	public void disconnect() throws Exception {
		if (this.sftp != null) {
			if (this.sftp.isConnected()) {
				this.sftp.disconnect();
			} else if (this.sftp.isClosed()) {
				TrcLog.log("sftp.log", " sftp is closed already");
			}
		}
	}

	/**
	 * 上传单个文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件
	 * 
	 * @throws Exception
	 */
	public void upload(String directory, String uploadFile) throws Exception {
		this.sftp.cd(directory);
		File file = new File(uploadFile);
		this.sftp.put(new FileInputStream(file), file.getName());
	}

	/**
	 * 上传目录下全部文件
	 * 
	 * @param directory
	 *            上传的目录
	 * 
	 * @throws Exception
	 */
	public void uploadByDirectory(String directory) throws Exception {

		String uploadFile = "";
		List<String> uploadFileList = this.listFiles(directory);
		Iterator<String> it = uploadFileList.iterator();

		while (it.hasNext()) {
			uploadFile = it.next().toString();
			this.upload(directory, uploadFile);
		}
	}

	/**
	 * 下载单个文件
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveDirectory
	 *            存在本地的路径
	 * 
	 * @throws Exception
	 */
	public void download(String directory, String downloadFile, String saveDirectory) throws Exception {
		String saveFile = saveDirectory + "//" + downloadFile;

		this.sftp.cd(directory);
		File file = new File(saveFile);
		this.sftp.get(downloadFile, new FileOutputStream(file));
	}

	/**
	 * 下载目录下全部文件
	 * 
	 * @param directory
	 *            下载目录
	 * 
	 * @param saveDirectory
	 *            存在本地的路径
	 * 
	 * @throws Exception
	 */
	public void downloadByDirectory(String directory, String saveDirectory) throws Exception {
		String downloadFile = "";
		List<String> downloadFileList = this.listFiles(directory);
		Iterator<String> it = downloadFileList.iterator();

		while (it.hasNext()) {
			downloadFile = it.next().toString();
			if (downloadFile.toString().indexOf(".") < 0) {
				continue;
			}
			this.download(directory, downloadFile, saveDirectory);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 * 
	 * @throws Exception
	 */
	public void delete(String directory, String deleteFile) throws Exception {
		this.sftp.cd(directory);
		this.sftp.rm(deleteFile);
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * 
	 * @return list 文件名列表
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> listFiles(String directory) throws Exception {

		Vector fileList;
		List<String> fileNameList = new ArrayList<String>();

		fileList = this.sftp.ls(directory);
		Iterator it = fileList.iterator();

		while (it.hasNext()) {
			String fileName = ((LsEntry) it.next()).getFilename();
			if (".".equals(fileName) || "..".equals(fileName)) {
				continue;
			}
			fileNameList.add(fileName);

		}

		return fileNameList;
	}

	/**
	 * 更改文件名
	 * 
	 * @param directory
	 *            文件所在目录
	 * @param oldFileNm
	 *            原文件名
	 * @param newFileNm
	 *            新文件名
	 * 
	 * @throws Exception
	 */
	public void rename(String directory, String oldFileNm, String newFileNm) throws Exception {
		this.sftp.cd(directory);
		this.sftp.rename(oldFileNm, newFileNm);
	}

	public void cd(String directory) throws Exception {
		this.sftp.cd(directory);
	}

	public InputStream get(String directory) throws Exception {
		InputStream streatm = this.sftp.get(directory);
		return streatm;
	}

	public static void main(String[] args) {  
		String host = "160.161.12.120";// 主机地址
		String port = "22";// 主机端口
		String username = "sftpncps";// 服务器用户名
		String password = "sftpncps1121";// 服务器密码
		String planPath = "test";// 文件所在服务器路径
		

		String fileName = "KJ_CUST_KBYJ";// KJ_CUST_KBYJ20140326.txt
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String currentDate = formatter.format(new Date());
		String downloadFile = "testForGw3004_3";

		
		SftpClientUtils sftp = new SftpClientUtils();
		sftp.host="160.161.12.120";
		sftp.port=22;
		sftp.username="sftpncps";
		sftp.password="sftpncps1121";
		
		try {
			sftp.connect();
			String filename = "";
			// String[] strs=planUrl.split("/");
			String filePath = "/home/sftp/test";
			// 列出目录下的文件
			List<String> listFiles = sftp.listFiles(filePath);
			boolean isExists = listFiles.contains(downloadFile);
			if (isExists) {
				sftp.cd(filePath);
				sftp.download(filePath, downloadFile, "D:\\");

			}
			sftp.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}

	}
	/**
	 * 于网银通过sftp上传下载文件
	 * @param sftpDir sftp绝对路径
	 * @param fileName 文件名
	 * @param flag 上传下载标志	1：下载  2：上传
	 * @return
	 * @throws Exception
	 */
	public static boolean netbankFile(String sftpDir ,String fileName ,String flag) throws Exception{
				TrcLog.log("sftp.log", "netbankFile begin");
				String host = ResPool.configMap.get("NETBANK_host").trim();// 主机地址
				int port = Integer.parseInt(ResPool.configMap.get("NETBANK_port").trim());// 主机端口
				String username = ResPool.configMap.get("NETBANK_username").trim();// 服务器用户名
				String password = ResPool.configMap.get("NETBANK_password").trim();// 服务器密码
				String localPath = SysDef.WORK_DIR+ResPool.configMap.get("FilePath")+"/netbank/";// 本机存放路径
				
				TrcLog.log("sftp.log", "方法：netbankFile==>host：[%s]",host);
				TrcLog.log("sftp.log", "方法：netbankFile==>port：[%s]",port);
				TrcLog.log("sftp.log", "方法：netbankFile==>username：[%s]",username);
				TrcLog.log("sftp.log", "方法：netbankFile==>password：[%s]",password);
				TrcLog.log("sftp.log", "方法：netbankFile==>localPath：[%s]",localPath);
				SftpClientUtils sftp = new SftpClientUtils();
//				sftp.SftpClientUtil(host,port,username,password);
				sftp.setHost(host);
				sftp.setPassword(password);
				sftp.setPort(port);
				sftp.setUsername(username);
				
				try {
					sftp.connect();
					TrcLog.log("sftp.log", "netbankFile 连接已建立");
					if("1".equals(flag)){//下载//
						TrcLog.log("sftp.log", "netbankFile 下载[%s]开始",sftpDir+fileName);
						// 列出目录下的文件
						List<String> listFiles = sftp.listFiles(sftpDir);
						boolean isExists = listFiles.contains(fileName);
						if (isExists) {
							sftp.download(sftpDir, fileName, localPath );
							TrcLog.log("sftp.log", "netbankFile 下载[%s]完成",sftpDir+fileName);
							return true;
						}
					}else if("2".equals(flag)) {//上传
						TrcLog.log("sftp.log", "netbankFile 上传[%s]完成",localPath+fileName);
						File file = new File(localPath+fileName);
						if(file.exists()){
							sftp.upload(sftpDir, fileName);
							TrcLog.log("sftp.log", "netbankFile 上传到[%s]完成",sftpDir+ fileName);
							return true;
						}
					}
					
					sftp.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					TrcLog.log("sftp.log", "netbankFile end");
				}
		return false;
	}
}