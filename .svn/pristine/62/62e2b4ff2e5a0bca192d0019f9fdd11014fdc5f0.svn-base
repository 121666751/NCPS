package com.adtec.tcp;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import teadapter.DataHandle;
import teadapter.UPNODE_INFO;
import teadapter.tongeasy;

public class TongClient {

	private static byte BAL_COMM_PASSWD[]={'e','w','d','h','c','c','n'};
	private static byte BAL_COMM_PASSWDd[]={'d','h','c','c','n','e','w'};
	/**
	 * @param IntegrateData
	 *            .net传过来的数据
	 * @param FileFlag
	 *            1 有附加文件传送 0 没有附加文件传送
	 * @param FileName
	 *            文件名
	 * @return 经过TongEasy处理后的数据 功能：同TongEasy数据交互
	 */
	public static byte[] DoTradeByTongEasy(byte[] IntegrateData, String FileFlag,
			String FileName) {
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,
//				"调用DoTradeByTongEasy开始");
		/***********************************************************************
		 * 开始处理逻辑
		 **********************************************************************/
		/** added by wangquan 20110910 start */
		//DeleteBackupData(FileName + ".BackupData");
		/** added by wangquan 20110910 end */

		byte[] fileData = null;
		// tongeasy返回数据
		byte[] resultData = null;
		// 若有文件附件，returnData是resultData+fileData;
		byte[] returnData = null;
		// 建立连接ID号
		long id = 0;

		// 临时返回的数据包
		byte[] tmpData = null;
		// 临时文件名
		String tmpFileName = null;
		String tmpStr = "";
		int len = 0;
		// 定义tongesay处理类对象
		TongEasyFileTran teFileTran = TongEasyFileTran.GetInstance();

		try {
			// 初始化TongEasy连接
			id = teFileTran.TongEasyInit();
			if (id <= 0) {
				// 初始化失败
//				CLogWrite.GetInstance().WriteLogfile(Level.Error,
//						"TongEasyInit失败。");
				return null;
			}
			// 判断是否有文件数据 "FileFlag=1:有文件 FileFlag=0:无文件
			// 有附件的情况下先发送附件
			if ("1".equals(FileFlag)) {
				// 利用balftp文件上传文件
				if (!teFileTran.SendFile(FileName, id)) {
//					CLogWrite.GetInstance().WriteLogfile(Level.Error,
//							"发送文件附件不成功=>" + FileName);
					// 关闭与tongeasy服务器的连接
					teFileTran.TongEasyClose(id);
					// 上传文件不成功，返回
					return null;
				}
			}
			// 发送交易数据包,得到返回结果
			resultData = teFileTran.SendData(IntegrateData, id);
			// 写日志，测试数据是否返回
			if (resultData != null) {
				String msg = new String(resultData);
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug,
//						"TongEasy balmain 返回信息" + msg);
			} else {
				// 异常情况，关闭TongEasy
//				CLogWrite.GetInstance().WriteLogfile(Level.Error,
//						"balmain 没有从TongEasy得到返回信息");
				// 关闭与tongeasy服务器的连接
				teFileTran.TongEasyClose(id);
				return null;
			}
			// 判断是否有文件附件传回，判断依据是取固定位置的数据进行比较
			// 同时对于只有 四位错误码 的 错误 返回形式 也直接返回报文数据
			if (resultData.length > 57 && (char) resultData[20] == '9') {
				// 用户获取接收到的文件
//				fileData = teFileTran.RecvFile(FileName, id);
//
//				// 取文件附件不成功
//				if (fileData == null) {
//					// 关闭与tongeasy服务器的连接
////					CLogWrite.GetInstance().WriteLogfile(Level.Error,
////							"取文件附件不成功=>" + FileName);
//					teFileTran.TongEasyClose(id);
//					return null;
//				}
//				// 将交易文件附件保存成临时文件
//				tmpFileName = FileName + "dhcc";
//				if (!this.SaveFiletxt(tmpFileName, fileData)) {
//					CLogWrite.GetInstance().WriteLogfile(Level.Error,
//							"保存交易附件文件失败");
//					// 关闭与tongeasy服务器的连接
//					teFileTran.TongEasyClose(id);
//					return null;
//				}
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "保存交易附件文件成功");
//				// added by wangquan 20110303 start
//				// 日志记录附件长度
//				CLogWrite.GetInstance().WriteLogfile(Level.Error,
//						FileName + "附件长度为" + fileData.length);
//				// added by wangquan 20110303 end
//
//				// 返回临时数据包，数据包+10字节
//				returnData = new byte[resultData.length + 10];
//
//				len = String.valueOf(fileData.length).length();
//				for (int i = 0; i < 10 - len; i++) {
//					tmpStr = tmpStr + "0";
//				}
//				tmpStr = tmpStr.trim() + String.valueOf(fileData.length);
//				tmpData = tmpStr.getBytes();
//				System.arraycopy(resultData, 0, returnData, 0,
//						resultData.length);
//				System.arraycopy(tmpData, 0, returnData, resultData.length, 10);
			} else {
				returnData = resultData;
			}
		} catch (Exception ex) {
			//CLogWrite.GetInstance().WriteLogfile(Level.Error,
				//	"DoTradeByTongEasy错误信息：" + ex.getMessage());
			return null;
		} finally {
			// 结束事务
			// 关闭与tongeasy服务器的连接
			teFileTran.TongEasyClose(id);
		}
		//CLogWrite.GetInstance().WriteLogfile(Level.Debug,
			//	"调用DoTradeByTongEasy结束");
		// added by wangquan 20110303 start
		// 保存返回的报文数据到临时文件
//		if (!this.SetBackupData(FileName + ".BackupData", returnData)) {
//			// 保存副本报文失败
//			CLogWrite.GetInstance().WriteLogfile(Level.Error,
//					"报文副本[" + FileName + ".BackupData]保存失败！");
//		}
		// added by wangquan 201103030 end
		return returnData;
	}

	public static void main(String[] args) throws Exception {
		
	
		try {

			String srcStr = "";
			String requestFormat[][][] = {
					{ { "name", "TranType" }, { "length", "5" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "Svc" }, { "length", "15" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "Fixlen" }, { "length", "36" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "Len" }, { "length", "4" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "SvcName" }, { "length", "5" }, { "parameterClass", "java.lang.String" } }

			};

			// ***********************组装8583报文测试--start*****8001******************//
			TreeMap filedMap = new TreeMap();// 报文域
			filedMap.put("FIELD002", "50001");// 交易码
			filedMap.put("FIELD003", "50001");// 交易码
			filedMap.put("FIELD005", "20180104");// 交易码
			filedMap.put("FIELD007", "900023");// 交易日期
			//filedMap.put("FIELD016", "6234");// 交易日期
			filedMap.put("FIELD016", "7360");
			filedMap.put("FIELD010", "NCPS");// 交易日期
			//filedMap.put("FIELD026", "银联待清算户");// 交易日期
//			filedMap.put("FIELD030", "6231788880000230340");// 交易日期
//			filedMap.put("FIELD031", "9300100100630");// 交易日期
//			filedMap.put("FIELD040", "0000000000000001");// 交易日期
//			filedMap.put("FIELD044", "20180104");// 交易日期
//			filedMap.put("FIELD052", "000010000138");// 交易日期
//			filedMap.put("FIELD065", "20180104");// 交易日期
//			filedMap.put("FIELD066", "1");// 交易日期
//			filedMap.put("FIELD067", "2");// 交易日期
//			filedMap.put("FIELD071", "0");// 交易日期
//			filedMap.put("FIELD073", "00000N00");// 交易日期
//			filedMap.put("FIELD127", "N");// 交易日期
			

			byte[] send = ISO8583ToolKit.make8583(filedMap);
			if (send == null) {
				System.out.println("完成组装8583报文==is null");
				return;
			}
			//System.out.println("完成组装8583报文==" + new String(send, "GBK") + "==");
			// ***********************组装8583报文测试--end***********************//

			SnccbTermHead sn = new SnccbTermHead();
			int len = send.length;
			len = len  +60+ 5;
			sn.Svc="6234";
			sn.TranType = " ";
			sn.Fixlen = "4              8                   0";
			sn.Len = String.valueOf(send.length + 5);
			sn.SvcName = "01001";

			String head = FixLenPackKit.FixedLenPack(requestFormat, sn, ' ', '0', "GB2312");

			byte[] SendBuf = new byte[len + 1];

			System.arraycopy(head.getBytes(), 0, SendBuf, 0, 65 );
			// TrcLog.log("te.log", "--" , new Object[0]);
			//System.out.println("----len" + len);
			//printHexString("send", send);
			
			byte[] buf1 = new byte[send.length];
			
			
			//TrcLog.log("Tongeasy.log", "["+srcXml.toString()+"]" , new Object[0]);
			buf1=snccbEncFmt(send,send.length,BAL_COMM_PASSWD, 1);
			
			System.arraycopy(buf1, 0, SendBuf, 65 , send.length);
			//printHexString("req host:", SendBuf);
			
			byte[] tt = DoTradeByTongEasy(SendBuf,"0","filename");
			//System.out.println(tt.length);
			byte[] buf2=new byte[tt.length-60];
			System.arraycopy(tt, 60, buf2, 0 , tt.length-60);
			byte[] buf3=new byte[buf2.length];
			buf3 = snccbEncFmt(buf2,buf2.length,BAL_COMM_PASSWDd, 0);
			byte[] buf4 = new byte[buf3.length-5];
			System.arraycopy(buf3, 5, buf4, 0 , buf3.length-5);
			Map back=ISO8583ToolKit.analyze8583(buf4);
			
			Iterator itor = back.keySet().iterator();  
			  while(itor.hasNext())  
			  {  
			   String key = (String)itor.next();  
			   String value = (String) back.get(key);  
			   System.out.println("解析后报文域  {"+key+"} Value:"+value);
			 }  

		    // System.out.println(back.toString());
			//printHexString("ret host:", buf4);
	}
		finally{
			
		}
		
	}
	
	private static byte[] snccbEncFmt(byte[] buf, int len, byte[] passwd, int flag)
	{
	
		 int klen = passwd.length;
		 
		     for( int i=0;i< len;i++)
		     {
		    	 if( flag==1 )
		    	     buf[i]=(byte) (buf[i]+passwd[i%klen]);
		    	 else
		    		 buf[i]=(byte) (buf[i]-passwd[i%klen]); 
		     }
		     
			return buf;
	}
		/**
		 * 将指定byte数组以16进制的形式打印到控制台
		 * 
		 * @param hint
		 *            String
		 * @param b
		 *            byte[]
		 * @return void
		 */
		public static void printHexString(String hint, byte[] b) {
			System.out.print(hint);
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				if (i % 16 == 0)
					System.out.println("\n");
				System.out.print(hex.toUpperCase() + " ");
			}
			System.out.println("");
		}

}

