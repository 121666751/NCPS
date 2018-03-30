package com.adtec.tcp;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.simulate.SimComm;
import com.adtec.starring.socket.NSockClt;
import com.sun.net.httpserver.HttpExchange;

/**

 */
public class TcpClient implements SimComm {
	NSockClt sock;
	private HttpExchange ww;

	public TcpClient() {
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adtec.starring.simulate.SimComm#init()
	 */
	public void init() {
		try {
			sock = new NSockClt();

			sock.setBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseException(SysPubDef.CUP_ERR_RET, e, e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adtec.starring.simulate.SimComm#connect(java.lang.String, int)
	 */
	public void connect(String url, int timeout) {
		String[] addr = url.split(":");
		String IP = addr[0];
		int port = Integer.parseInt(addr[1]);

		try {
			sock.connect(IP, port, timeout * 1000);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseException(SysErr.E_COMM_CONNECT, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adtec.starring.simulate.SimComm#send(java.lang.String, byte[],
	 * int)
	 */
	public void send(String svcname, byte[] sndBuf, int len) {
		try {
			sock.send(sndBuf, len, 0);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseException(SysErr.E_COMM_SOCK_SEND, e, len);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adtec.starring.simulate.SimComm#receive(java.lang.String,
	 * byte[], int)
	 */
	public String receive(byte[] rcvBuf, int timeout) {
		byte[] buf = new byte[8];
		int len = 0;
		int rlen = 0;
		String svc;

		try {
			len = 8;
			rlen = sock.read(buf, len, timeout * 1000);

			int dataLen = Integer.parseInt(new String(buf))+1;

			// buf = new byte[15];
			// len = 15;
			// rlen = sock.read(buf, len, timeout*1000);
			// svc = new String(buf).trim();

			buf = new byte[dataLen];
			len = dataLen;
			rlen = sock.read(buf, dataLen, timeout * 1000);

			byte[] tmpBuf = new byte[4];

			System.arraycopy(buf, 56, tmpBuf, 0, 4);
			String slen = new String(tmpBuf);

			int rlens = Integer.parseInt(slen.trim());
			System.out.println(rlens);
			byte[] tmpBuf1 = new byte[rlens - 5];
			printHexString("111111", buf);
			System.arraycopy(buf, 65, tmpBuf1, 0, rlens - 5);

			// ***********************解析8583报文测试--start***********************//
			Map back = ISO8583ToolKit.analyze8583(tmpBuf1);

			System.out.println("完成解析8583报文==" + back.toString() + "==");

			System.out.println("Th:[" + Thread.currentThread().getId() + "] Recv:[" + new String(buf) + "]");
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseException(SysErr.E_COMM_SOCK_READ, e, len);
		}

		return new String(buf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adtec.starring.simulate.SimComm#close()
	 */
	public void close() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adtec.starring.simulate.SimComm#term()
	 */
	public void term() {
	}

	//
	public static void main(String[] args) {

		try {

			String srcStr = "";
			String requestFormat[][][] = {
					{ { "name", "AllLen" }, { "length", "8" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "Fixlen" }, { "length", "56" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "Len" }, { "length", "4" }, { "parameterClass", "java.lang.String" } },
					{ { "name", "SvcName" }, { "length", "5" }, { "parameterClass", "java.lang.String" } }

			};

			// ***********************组装8583报文测试--start*****8001******************//
			TreeMap filedMap = new TreeMap();// 报文域
			filedMap.put("FIELD002", "50001");// 交易码
			filedMap.put("FIELD003", "50001");// 交易码
			filedMap.put("FIELD016", "8001");// 交易日期
			
			filedMap.put("FIELD025", "UP05519200486592020102018010411002700000000000010000137");// 账号
			//filedMap.put("FIELD025", args[0]);
			//filedMap.put("FIELD033", "aa索隆bb");// 注意这个域是变长域!
			//filedMap.put("FIELD036", "1456");// 注意这个域是变长域!
			
			// ***********************组装8583报文测试--start*****8004******************//
//			TreeMap filedMap = new TreeMap();// 报文域
//			filedMap.put("FIELD002", "00110");// 交易码
//			filedMap.put("FIELD003", "00110");// 交易码
//			filedMap.put("FIELD007", "000443");// 交易码
//			filedMap.put("FIELD016", "8007");// 交易日期
//			filedMap.put("FIELD019", "035001");// 账号
//			filedMap.put("FIELD046", "20171206");// 账号
//			filedMap.put("FIELD027", "");//交易名称
//			filedMap.put("FIELD041", "1");// 账号
//			filedMap.put("FIELD042", "20");// 账号
//			filedMap.put("FIELD068", "0");// 差错类型
//			filedMap.put("FIELD069", "N");// 差错确认状态
//			filedMap.put("FIELD070", "N");// N-对账差错手工调账查询
//			filedMap.put("FIELD071", "1");// 1—文件
			//filedMap.put("FIELD033", "aa索隆bb");// 注意这个域是变长域!
			//filedMap.put("FIELD036", "1456");// 注意这个域是变长域!
			
			
//			TreeMap filedMap = new TreeMap();// 报文域
//			filedMap.put("FIELD002", "00110");// 交易码
//			filedMap.put("FIELD003", "00110");// 交易码
//			filedMap.put("FIELD007", "000443");// 交易码
//			filedMap.put("FIELD016", "8007");// 交易日期
//			filedMap.put("FIELD019", "035001");// 账号
//			filedMap.put("FIELD046", "20171206");// 账号
//			filedMap.put("FIELD027", "");//交易名称
//			filedMap.put("FIELD041", "1");// 账号
//			filedMap.put("FIELD042", "20");// 账号
//			filedMap.put("FIELD068", "0");// 差错类型
//			filedMap.put("FIELD069", "N");// 差错确认状态
//			filedMap.put("FIELD070", "N");// N-对账差错手工调账查询
//			filedMap.put("FIELD071", "1");// 1—文件
			
//			TreeMap filedMap = new TreeMap();// 报文域
//			filedMap.put("FIELD002", "00110");// 交易码
//			filedMap.put("FIELD003", "00110");// 交易码
//			filedMap.put("FIELD007", "000443");// 交易码
//			filedMap.put("FIELD016", "8009");// 交易日期
//			filedMap.put("FIELD019", "035001");// 账号
//			filedMap.put("FIELD046", "20171206");// 账号
//			filedMap.put("FIELD025", "RD1027YYYYMMDD99");//交易名称

			
			// ***********************组装8583报文测试--start*****8005******************//
						//TreeMap filedMap = new TreeMap();// 报文域
//						filedMap.put("FIELD002", "70201");// 交易码
//						filedMap.put("FIELD003", "70201");// 交易码
//						filedMap.put("FIELD005", "20171219");// 交易码
//						filedMap.put("FIELD007", "000443");// 交易码
//						filedMap.put("FIELD010", "0696");// 交易码
//						filedMap.put("FIELD016", "8004");// 交易日期
//						filedMap.put("FIELD019", "00603500170201|");// 账号
						//filedMap.put("FIELD046", "20171202");// 账号
						//filedMap.put("FIELD033", "aa索隆bb");// 注意这个域是变长域!
						//filedMap.put("FIELD036", "1456");// 注意这个域是变长域!

			byte[] send = ISO8583ToolKit.make8583(filedMap);
			if (send == null) {
				System.out.println("完成组装8583报文==is null");
				return;
			}
			System.out.println("完成组装8583报文==" + new String(send, "GBK") + "==");
			// ***********************组装8583报文测试--end***********************//

			SnccbTermHeads sn = new SnccbTermHeads();
			int len = send.length;
			len = len + 60 + 5 + 8;
			sn.AllLen = String.valueOf(len);
			sn.Fixlen = "1234";
			sn.Len = String.valueOf(send.length + 5);
			sn.SvcName = "01001";

			String head = FixLenPackKit.FixedLenPack(requestFormat, sn, ' ', '0', "gb2312");

			byte[] SendBuf = new byte[len + 1];

			System.arraycopy(head.getBytes(), 0, SendBuf, 0, 65 + 8);
			// TrcLog.log("te.log", "--" , new Object[0]);
			System.out.println("----len" + len);
			printHexString("send", send);
			System.arraycopy(send, 0, SendBuf, 65 + 8, send.length);
			// SendBuf[len+1]=;

			TcpClient tcpClient = new TcpClient();
			tcpClient.connect("160.160.9.20:12024", 20000);
			//tcpClient.connect("160.161.12.120:12024", 20000);
			tcpClient.send("610000", SendBuf, SendBuf.length);
			tcpClient.receive(new byte[200], 5);

			tcpClient.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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
