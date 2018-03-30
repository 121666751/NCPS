package com.adtec.ncps.busi.ncp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.adtec.ncps.busi.ncp.qry.SQRYPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.ftp.FtpToolkit;

/********************************************************
 * *
 * 
 * * 文件处理 * 
 *******************************************************/
public class FileTool {

	/*
	 * 把文件内容写入指定路劲的文件，在文件尾添加
	 * @param text 文本内容
	 * @param filePath 文件路径
	 * @param fileName 文件名
	 * @param unicode 编码集 如： GBK UTF-8
	 * @throws Exception
	 */
	public static void writeFileontheend(String text,String filePath,String fileName,String unicode) throws Exception{
		FileOutputStream fos = null;  
	      try {
	      	//判断文件路劲格式是否正确
	          if(filePath != null && !filePath.endsWith("\\")&& !filePath.endsWith("/")) {
	          	filePath += "/";
	          	
	          	// 若文件夹不存在则生成
	          	File path = new File(filePath);
	          	if(!path.exists()) {
	          		// 判断创建文件夹是否成功
	          		boolean creatFlag = path.mkdirs();
	          		
	          		if(creatFlag == false) {
	          			SysPub.appLog("ERROR", "创建文件夹["+filePath+"]失败！");
	          			throw new BaseException("P10311", "创建文件夹["+filePath+"]失败！");
	          		}
	          	}
	          }
	          //创建文件
	  		File file = new File(filePath+fileName);
	  		fos = new FileOutputStream(file,true);
	          //把内容写入文件
	          fos.write(text.getBytes(unicode)); 
	      }catch (FileNotFoundException e) {
	          e.printStackTrace(BaseLog.getExpOut());
	          throw e;
	      } catch (IOException e) {
	          e.printStackTrace(BaseLog.getExpOut());
	          throw e;
	      } finally {
	          if (fos != null) {
	              try {
	                  fos.flush();
	              } catch (Exception e) {
	                  e.printStackTrace(BaseLog.getExpOut());
	              }
	              try {
	                  fos.close();
	              } catch (Exception e) {
	                  e.printStackTrace(BaseLog.getExpOut());
	              }
	          }
	      }
	}
	
	/**
	 * 复制文件
	 * @param sourfilePath 源文件全路径
	 * @param destfilePath 目的文件全路径
	 * @param sourUnicode 源文件编码集 如： GBK UTF-8 GB2312
	 * @param destUnicode 目的文件编码集 如： GBK UTF-8 GB2312
	 * @throws Exception
	 */
	public static void copyFile(String sourfilePath,String destfilePath,String sourUnicode,String destUnicode)throws Exception{
		  InputStreamReader isr = null;  
		  FileInputStream fis = null;  
		  FileOutputStream fos = null;  
	      try {
	          	
	          	File sourpath = new File(sourfilePath);
	          	if(!sourpath.exists()){
	          		SysPub.appLog("ERROR", "文件["+sourfilePath+"]不存在！");
          			throw new BaseException("P10311", "文件["+sourfilePath+"]不存在！");
	          	}
	          	fis = new FileInputStream(sourpath);
	          	isr = new InputStreamReader(fis,sourUnicode);
	          	// 若文件夹不存在则生成
	          	File destpath = new File(destfilePath);
	          	if(!destpath.getParentFile().exists()) {
	          		// 判断创建文件夹是否成功
	          		boolean creatFlag = destpath.getParentFile().mkdirs();
	          		
	          		if(creatFlag == false) {
	          			SysPub.appLog("ERROR", "创建文件夹["+destfilePath+"]失败！");
	          			throw new BaseException("P10311", "创建文件夹["+destfilePath+"]失败！");
	          		}
	          	}
	            //创建文件
	          	if (!destpath.exists()) {
					if (!destpath.createNewFile()) {
						SysPub.appLog("ERROR", "创建[%s]文件失败", destfilePath);
						throw new BaseException("P10311", "创建文件["+destfilePath+"]失败！");
					}
				}
	          	fos = new FileOutputStream(destpath);
	          	StringBuffer sb = new StringBuffer();
	          	char[] buffer = new char[1024];	            
	          	int len = -1;
				
				while((len=isr.read(buffer,0,1024))!=-1){
					sb.append(buffer,0,len);
				}
				fos.write(new String(sb).getBytes(destUnicode));
	      }catch (FileNotFoundException e) {
	          e.printStackTrace(BaseLog.getExpOut());
	          throw e;
	      } catch (IOException e) {
	          e.printStackTrace(BaseLog.getExpOut());
	          throw e;
	      } finally {
	    	  if (isr != null) {
	              try {
	            	  isr.close();
	              } catch (Exception e) {
	                  e.printStackTrace(BaseLog.getExpOut());
	              }
	          }
	    	  if (fis != null) {
	              try {
	            	  fis.close();
	              } catch (Exception e) {
	                  e.printStackTrace(BaseLog.getExpOut());
	              }
	          }
	          if (fos != null) {
	              try {
	                  fos.flush();
	              } catch (Exception e) {
	                  e.printStackTrace(BaseLog.getExpOut());
	              }
	              try {
	                  fos.close();
	              } catch (Exception e) {
	                  e.printStackTrace(BaseLog.getExpOut());
	              }
	          }
	      }
	}
	

}
