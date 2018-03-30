package com.adtec.ncps;

import teadapter.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.dta.protocol.IComm;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;

public class TongeasyClientComm implements IComm {

	long id = 0;
	int res = 0;
	int reqDLen = 0;
	String rcvData = new String();
	byte[] bt = new byte[9999999];
	int flag = 0;
	int rcvDLen = 0;

	tongeasy te = null;
	DataHandle dh = null;

	public boolean check(String arg0, int arg1) {
		// TODO Auto-generated method stub

		if (te == null) {
			return false;
		}
		return true;
	}

	public void close() {
		// TODO Auto-generated method stub
		if (id > 0) {
			res = te.TE_tpterm(id);
			if (res < 0)
				TrcLog.log("te.log", "TE_tpcommit Fail res==" + res, new Object[0]);

			TrcLog.log("te.log", "TE_tpterm Sucess res==" + res, new Object[0]);
			te = null;
			dh = null;
			id = 0;
			// te.TEAppTerm();
		}
	}

	public void connect(String ipAddress, int port, String url, int timeOut) {

		// TODO Auto-generated method stub

		try {

			if (te == null) {

				te = new tongeasy();

				te.TEAppInit();
				// flag = te.PKTNEEDANS | te.TENEEDNSFWD;

				UPNODE_INFO uInfo = new UPNODE_INFO();

				id = te.TE_tpinit(0, 0, uInfo);
				TrcLog.log("te.log", "TE_tpbegin  id==[" + id + "]", new Object[0]);
				// res = te.SetDebugLevel(0);

				if (id <= 0) {
					TrcLog.log("te.log", "TE_tpinit Fail id====" + id);
					return;
				}

			}

		} catch (Exception e) {
			TrcLog.log("te.log", "TongEasyInit错误信息：" + e.getMessage());
			throw new BaseException(SysErr.E_COMM_CONNECT, e, ipAddress, port);

		}
	}

	public ICommSession getICommSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(boolean arg0) {

	}

	public byte[] receive(String svcName, int timeout) {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		/** begin*****判断是否需要并发送文件 */
		String type = (String) EPOper.get(tpID, "HOST_CLT_OUT[0].TYPE");
		TrcLog.log("te.log", "receive type=" + type);

		if ("1".equals(type)) {
			String fileName = (String) EPOper.get(tpID, "HOST_CLT_OUT[0].fileName");
			TrcLog.log("te.log", "receive fileName=" + fileName);
			sendFile(fileName);
		}
		/** end*****判断是否需要并发送文件 */

		flag = te.PKTNEEDANS | te.TENEEDNSFWD;

		res = te.TE_tpbegin(flag, 60, id);

		if (res == -1) {

			// res = te.TE_tpterm(id);

			TrcLog.log("te.log", "TE_tpbegin Fail res==[" + res + "]", new Object[0]);
			throw new BaseException(SysErr.E_COMM_SOCK_READ, 0);
			// return null;
		}

		dh = new DataHandle();

		try {

			res = dh.SetSendData(bt);
			// TrcLog.log("te.log", "res=====" + res, new Object[0]);

			res = te.TE_SetBranchMsg(flag, 60, id);
			if (res == -1)
				TrcLog.log("te.log", "TE_SetBranchMsg Fail res====" + res);
			long txbegtime = PubTool.gettimems();
			res = te.TE_tpcall("_balmain", reqDLen, dh, id); /* 发起事务分支请求 */
			if (res < 0) { /** 检查返回结果，如失败回滚一个事务 **/

				TrcLog.log("te.log", "TE_tpcall Fail res==" + res, new Object[0]);
				res = te.TE_tpabort(id);
				if (res < 0) {

					TrcLog.log("te.log", "TE_tpabort Fail res==" + res, new Object[0]);
					throw new BaseException(SysErr.E_COMM_SOCK_READ, 0);
				}

				// return null;
			}
			long txendtime = PubTool.gettimems();
			TrcLog.log("yl.log", "TE_tcpcall [" + (txendtime - txbegtime) + "]", new Object[0]);
			rcvDLen = dh.GetDataLen();

			TrcLog.log("te.log", "rcvDLen rcvDlen==" + rcvDLen, new Object[0]);

			byte[] bt1 = dh.GetRecvData(); /** 获取接收到的数据 **/

			res = te.TE_tpcommit(id); /** 正常结束一个事务 ***/
			if (res < 0)
				TrcLog.log("te.log", "TE_tpcommit Fail res==" + res, new Object[0]);
			TrcLog.log("te.log", "TE_tpcommit res==[" + res + "]", new Object[0]);

			/** begin *****判断是否有接收文件，并接收 */
			if (bt1.length > 57 && (char) bt1[20] == '9') {
				//String recFileName = "/netbank/file/50001EBNK";
				
				String  teller= (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_003");
				String chnlNo =  (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_010");
				String recFileName = "/netbank/"+teller+chnlNo;
				recvFile(recFileName);
			}
			/** end *****判断是否有接收文件，并接收 */
			return bt1;

		} catch (Exception e) {
			TrcLog.log("te.log", "TongEasyr接收错误：" + e.getMessage());
			throw new BaseException(SysErr.E_COMM_SOCK_READ, e, 0);
		}

	}

	/**
	 * 接收附件，并保存为文件
	 * 
	 * @param recFileName
	 *            接收文件零时文件名（可能含路径）
	 */
	public void recvFile(String recFileName) {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		TrcLog.log("te.log", "recvFile recFileName:[" + recFileName + "]");
		int timeout = 30;
		String ftpServiceName = "BALFTP";
		
		String filePathName = SysDef.WORK_DIR + ResPool.configMap.get("FilePath") + recFileName;
		recFileName = filePathName.substring(filePathName.lastIndexOf("/")+1);

		TrcLog.log("te.log", "调用RecvFile开始！");
		// test start
		TrcLog.log("te.log", "事务" + id + "RecvFile开始");
		// test end
		byte[] header = GetTongEasyHeader('1', recFileName, 0, 0, 0, 0, '0');
		if (header == null) {
			TrcLog.log("te.log", "Fail RecvFile获得文件头部错误");
			return;
		}

		// 组合后字节数组 文件头+文件内容
		byte[] sendData = new byte[header.length];

		System.arraycopy(header, 0, sendData, 0, header.length);

		/****** 组包结束 开始调用 tongeasy函数进行发包 ********/
		// 事务标志，用于设定事务属性，取值为下面定义的各宏的一个或多个的组合
		// PKTIS_XA:XA 事务
		// P_ACKKEENACKAND：确认需要确认应答
		// P_MST_NEEDACK:需要确认
		int flag = 0;
		// 函数返回值
		int res = 0;
		// 发送的数据的长度
		int dataLen = 0;
		// 返回的数据
		byte[] resultData = null;
		// DataHandle对象
		DataHandle dataHandle = new DataHandle();
		try {
			// edit by Liucb start 201009008 标志位如果为P_MST_NEEDACK会导致后台不释放资源
			// flag=TongEasy.P_MST_NEEDACK;
			flag = te.PKTNEEDANS | te.TENEEDNSFWD;
			// edit by Liucb end 201009008
			// 开始一个事务
			int TID = te.TE_tpbegin(flag, timeout, id);
			if (TID >= 0) {
				TrcLog.log("te.log", "TE_tpbegin success " + TID);
			} else {
				TrcLog.log("te.log", "RecvFile--TE_tpbegin Fail rtn:" + TID);
				return;
			}

			// 事务循环
			// 设置发送数据
			dataHandle.SetSendData(sendData);
			// 设置发送数据的长度
			dataLen = sendData.length;

			// 发送或者接收一笔事务
			te.TE_SetBranchMsg(flag, timeout, id);
			res = te.TE_tpcall(ftpServiceName, dataLen, dataHandle, id);
			if (res >= 0) {
				TrcLog.log("te.log", "TE_tpcall Sucess rtn:" + res);
			} else {
				TrcLog.log("te.log", "RecvFile--TE_tpcall Fail rtn:" + res);
				res = te.TE_tpabort(id);
				if (res < 0)
					TrcLog.log("te.log", "RecvFile--TE_tpabort Fail rtn:" + res);
				return;
			}
			// 获取tongeasy服务器返回的数据
			resultData = dataHandle.GetRecvData();

			// 提交事务结果
			res = te.TE_tpcommit(id);
			if (res != 0) {
				TrcLog.log("te.log", "RecvFile--TE_tpcommit失败！res=" + res);
				return;
			} else
				TrcLog.log("te.log", "TE_tpcommit成功！");
		} catch (Exception ex) {
			TrcLog.log("te.log", "RecvFile 错误信息：" + ex.getMessage());
			return;
		} finally {
			dataHandle = null;
		}
		/*************** bigin___保存为文件 ***************/
		// 取文件附件不成功
		if (resultData == null) {
			// 关闭与tongeasy服务器的连接
			TrcLog.log("te.log", "取文件附件不成功=>" + filePathName);
			te.TE_tpterm(id);
			// TongEasy.TEAppTerm();
			id = 0;
			return;
		}
		// 将文件名保存到HOST_CLT_IN[0].recFileName中
		// EPOper.put(tpID, "HOST_CLT_IN[0].recFileName", recFileName);
		byte[] tempData = new byte[resultData.length - 152];
		System.arraycopy(resultData, 152, tempData, 0, tempData.length);
		
		String seqNo =  (String) EPOper.get(tpID, "ISO_8583[0].pub[0].PlatSeqNo");
		filePathName = filePathName+seqNo;
		// 将交易文件附件保存成临时文件
		if (!this.SaveFiletxt(filePathName, tempData)) {
			TrcLog.log("te.log", "保存交易附件文件失败");
			// 关闭与tongeasy服务器的连接
			te.TE_tpterm(id);
			// TongEasy.TEAppTerm();
			id = 0;
			return;
		}
		TrcLog.log("te.log", "保存交易附件文件成功");
		// added by wangquan 20110303 start
		// 日志记录附件长度
		TrcLog.log("te.log", recFileName + "附件长度为" + tempData.length);
		/*************** end___保存为文件 ***************/

		// test start
		TrcLog.log("te.log", "事务" + id + "RecvFile结束");
		// test end
		TrcLog.log("te.log", "调用RecvFile结束！");
		return;
	}

	/**
	 * 将二进制保存为文件
	 * 
	 * @param FileName
	 *            文件名（全路径）
	 * @param pcDataBuf
	 *            二进制文件内容
	 * @return
	 */
	private boolean SaveFiletxt(String filePath, byte[] pcDataBuf) {
		FileOutputStream fos = null;

		boolean isSuccess = true;
		TrcLog.log("te.log", " 函数： SaveFiletxt保存文件内容");

		TrcLog.log("te.log", "要保存的文件路径：" + filePath);
		// 如果文件路径为空,则错误,返回
		if (filePath == null)
			return false;
		File file = new File(filePath);

		// 文件不存在创建新文件
		if (!file.exists()) {
			// 创建文件失败，返回false
			try {
				if (!file.createNewFile())
					return false;
			} catch (IOException e) {
				TrcLog.log("te.log", e.getMessage() + "函数：SaveFiletxt");
				e.printStackTrace();
				return false;
			}
		}

		try {

			fos = new FileOutputStream(filePath, false);
			if (fos == null)
				return false;
			fos.write(pcDataBuf);
			fos.flush();
			isSuccess = true;
		} catch (Exception e) {
			isSuccess = false;
			TrcLog.log("te.log", e.getMessage() + " 函数： SaveFiletxt");
		} finally {
			try {
				if (fos != null) {
					fos.close();
					fos = null;
					file = null;
				}
			} catch (Exception e1) {
				TrcLog.log("te.log", e1.getMessage() + " 函数： SaveFiletxt");
			}
		}
		return isSuccess;
	}

	public void send(String svcName, byte[] sendByte, int len) {
		// TODO Auto-generated method stub
		// offset = dh.TEDataPutBlock(bt, 0, sendByte, 0, 0);
		bt = sendByte;
		reqDLen = len;
	}

	/**
	 * 发送附件
	 * 
	 * @param filename
	 *            发送的附件名
	 * @return
	 */
	public void sendFile(String fileName) {
		int timeout = 30;
		String ftpServiceName = "BALFTP";

		TrcLog.log("te.log", "TE_tpbegin sendFile 调用SendFile开始");
		// test start
		TrcLog.log("te.log", "TE_tpbegin sendFile 事务" + id + "SendFile开始");
		// test end
		// ForXmlServiceImpl xmlServiceImpl= new ForXmlServiceImpl();

		if (fileName == null) {
			TrcLog.log("te.log", "TE_tpabort Fail 发送文件附件不成功，filename=null");
			return;
		}
		TrcLog.log("te.log",
				"TE_tpbegin sendFile te模式上传文件名称" + SysDef.WORK_DIR + ResPool.configMap.get("FilePath") + fileName);
		String filePathName = SysDef.WORK_DIR + ResPool.configMap.get("FilePath") + fileName;
		fileName = filePathName.substring(filePathName.lastIndexOf("/")+1);
		File file = new File(filePathName);
		// 察看文件是否存在
		if (!file.exists()) {
			TrcLog.log("te.log", "TE_tpabort Fail 发送文件附件不存在，fileName=" + fileName);
			return;
		}
		// 文件长度
		int filesize = (int) file.length();
		// 文件修改时间
		int modtime = (int) (file.lastModified() / 1000);

		byte[] header = GetTongEasyHeader('2', fileName, filesize, modtime, 0, filesize, '1');
		if (header == null) {
			TrcLog.log("te.log", "TE_tpabort Fail SendFile获得文件头部错误");
			return;
		}
		// 获得文件文件数据
		byte[] data = GetFiletxt(filePathName);

		if (data == null) {
			TrcLog.log("te.log", "TE_tpabort Fail 发送文件附件数据为空，fileName=" + fileName);
			return;
		}

		// 组合后字节数组 文件头+文件内容
		byte[] sendData = new byte[header.length + data.length];

		System.arraycopy(header, 0, sendData, 0, header.length);
		System.arraycopy(data, 0, sendData, header.length, data.length);

		/****** 组包结束 开始调用 tongeasy函数进行发包 ********/
		// 事务标志，用于设定事务属性，取值为下面定义的各宏的一个或多个的组合
		// PKTIS_XA:XA 事务
		// P_ACKKEENACKAND：确认需要确认应答
		// P_MST_NEEDACK:需要确认
		int flag = 0;
		// 函数返回值
		int res = 0;
		// 发送的数据长度
		int dataLen = 0;
		// DataHandle对象
		DataHandle dataHandle = new DataHandle();

		// 事务处理
		try {
			// edit by Liucb start 201009008 标志位如果为P_MST_NEEDACK会导致后台不释放资源
			// flag=TongEasy.P_MST_NEEDACK;
			flag = te.PKTNEEDANS | te.TENEEDNSFWD;
			// edit by Liucb end 201009008
			int TID = te.TE_tpbegin(flag, timeout, id);
			if (TID >= 0) {
				TrcLog.log("te.log", "TE_tpabort Sucess rtn:" + TID);
			} else {
				TrcLog.log("te.log", "TE_tpabort Fail rtn:" + TID);
				return;
			}

			// 设置发送数据
			dataHandle.SetSendData(sendData);
			dataLen = sendData.length;

			te.TE_SetBranchMsg(flag, timeout, id);
			res = te.TE_tpcall(ftpServiceName, dataLen, dataHandle, id);
			if (res >= 0) {
				TrcLog.log("te.log", "TE_tpabort Sucess rtn:" + res);
			} else {
				TrcLog.log("te.log", "TE_tpabort Fail rtn:" + res);
				res = te.TE_tpabort(id);
				if (res < 0)
					TrcLog.log("te.log", "TE_tpabort Fail rtn:" + res);
				return;
			}

			res = te.TE_tpcommit(id);
			if (res != 0) {
				TrcLog.log("te.log", "TE_tpabort--TE_tpcommit失败！res=" + res);
				return;
			} else
				TrcLog.log("te.log", "TE_tpcommit成功！");
		} catch (Exception e) {
			TrcLog.log("te.log", "SendFile 错误信息：" + e.getMessage());
			return;
		} finally {
			dataHandle = null;
		}
		// test start
		TrcLog.log("te.log", "事务" + id + "SendFile结束");
		// test end
		TrcLog.log("te.log", "调用SendFile结束！");
		return;

	}

	/**
	 * 获取文件数据
	 * 
	 * @param filePath
	 *            文件绝对路径
	 * @return
	 */
	private byte[] GetFiletxt(String filePath) {

		byte[] pcDataBuf = null;

		FileInputStream fileInputStream = null;

		TrcLog.log("te.log", " 执行函数： GetFiletxt开始获取文件内容");

		TrcLog.log("te.log", "文件路径：" + filePath);
		// 如果文件路径为空,则错误,返回
		if (filePath == null) {
			TrcLog.log("te.log", "GetFiletxt终止，FileName=" + filePath);
			return null;
		}
		try {
			fileInputStream = new FileInputStream(filePath);
			if (fileInputStream != null) {
				// 取得文件长度
				int fileLen = fileInputStream.available();
				// 建立字节缓冲
				pcDataBuf = new byte[fileLen];
				// 将文件内容读入字节缓冲区
				fileInputStream.read(pcDataBuf);
			}
		} catch (Exception e) {
			TrcLog.log("te.log", e.getMessage() + " 函数： GetFiletxt");
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
					fileInputStream = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// 关闭文件流
		return pcDataBuf;
	}

	public void setICommSession(ICommSession arg0) {
		// TODO Auto-generated method stub

	}

	public void term() {
		// TODO Auto-generated method stub

		// res = te.TE_tpterm(id);
		// te.TEAppTerm();
		TrcLog.log("te.log", "TE_tpterm id==[" + id + "]" + "res" + res, new Object[0]);
		close();
		te.TEAppTerm();

	}

	/**
	 * @param mesgtype
	 *            请求种类:0-查询,1-下载,2-上传 1位
	 * @param name
	 *            文件名 129位
	 * @param filesize
	 *            文件大小 4位
	 * @param modtime
	 *            修改时间 4位
	 * @param offset
	 *            文件偏移量 4位
	 * @param datasize
	 *            需要的数据长度 4位
	 * @param flag
	 *            写文件标志,0-普通,1-覆盖 1位
	 * @return 文件头字节数组 功能：生成长度为147位的文件头
	 */
	static byte[] GetTongEasyHeader(char mesgtype, String name, int filesize, int modtime, int offset, int datasize,
			char flag) {
		byte[] header = new byte[152];
		byte[] tmp = null;
		// 将mesgtype信息加入头字节数组 起始位置0 长度 1
		header[0] = (byte) mesgtype;

		// 将name信息加入头字节数组 起始位置1 长度 129
		if (name != null) {// 文件名存在
			tmp = name.getBytes();
			System.arraycopy(tmp, 0, header, 1, tmp.length);
			for (int j = 0; j < tmp.length; j++) {
				if (j % 10 == 0) {
					System.out.println();
				}
				System.out.print("  " + j + ":" + header[j] + "  " + (char) header[j] + "  ");
			}
		} else {// 文件名不存在
			return null;
		}
		// 将filessize信息加入头字节数组 起始位置130 长度 4
		tmp = toByteArray(filesize);
		if (tmp != null) {
			System.arraycopy(tmp, 0, header, 132, tmp.length);
		}
		// 将modtime信息加入头字节数组 起始位置134 长度 4
		tmp = toByteArray(modtime);
		if (tmp != null) {
			System.arraycopy(tmp, 0, header, 136, tmp.length);

		}
		// 将offset信息加入头字节数组 起始位置138 长度 4
		tmp = toByteArray(offset);
		if (tmp != null) {
			System.arraycopy(tmp, 0, header, 140, tmp.length);
		}
		// 将datasize信息加入头字节数组 起始位置142 长度 4
		tmp = toByteArray(datasize);
		if (tmp != null) {
			System.arraycopy(tmp, 0, header, 144, tmp.length);
		}
		// 将flag信息加入头字节数组 起始位置146 长度 1
		header[148] = (byte) flag;

		return header;
	}

	/**
	 * @功能： 整型数字转化为四字节的字节数组
	 */
	static byte[] toByteArray(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}
}
