package com.adtec.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * <p>TcpServer</p>
 * <p>TCP服务端模拟器实现类</p>
 * <p>Copyright: Copyright (c) 2013</p> 
 * <p>Company: 北京先进数通信息技术股份公司</p> 
 * @author  jiangling
 * @version 1.0 2014年4月2日 jiangling
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public class TcpServer {
    private static Socket sock;
    
    public String src = "";
    //监听端口
    public static int port = 5680;
    //超时时间
    public static int timeOut = 10000;
    //协议报文长度,不包括8字节的报文长度
    public static int LEN = 50;
    public static void main(String[] args) throws Exception {
    	TcpServer tcpServer = new TcpServer();
    	try{
    		while( true )
    		{
    			tcpServer.startServer();
    		}
    	}
    	catch( Exception e )
    	{
    		e.printStackTrace();
    	}
    }
 
    /* (non-Javadoc)
     * @see com.adtec.starring.simulate.SimComm#send(java.lang.String, byte[], int)
     */
    public void send(String svcname, byte[] sndBuf, int len) {
        try {
            //sock.send(sndBuf, len, 0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.simulate.SimComm#receive(java.lang.String, byte[], int)
     */
    public String receive() throws Exception {
        byte[] buf = new byte[6];
        int len = 0;
        int rlen = 0;
        String svc = ""; 
        try {
            len = 6;
            InputStream in = sock.getInputStream();
            //读取报文长度
            rlen = in.read(buf, 0, len);
            //读取报文内容=协议报文+报文体
            int dateLen = Integer.parseInt(new String(buf));
            System.out.println("buf:["+new String(buf)+"]");
            buf = new byte[dateLen];
            rlen = in.read(buf, 0, dateLen);
            //svc = new String(buf);
            //System.out.println("svcMsg:["+svc+"]");
            //解析服务码
            //String svcName = (svc.substring(0, 8));
            //System.out.println("svcName:[" + svcName + "]");
            //读取返回报文
            String msgBody = "000386HTSW{\"#RCODE\":\"000\",\"#RMSG\":\"操作成功\",\"#DATA_RES\":{\"#JFH\":\"708710\",\"#RCLZ\":\"2017070400001560\",\"#OCLZ\":\"2016041512850084\",\"#CLM\":\"资阳五月阳光房地产开发有限公司\",\"#ADDR\":\"希望未来城3-1-7-7\",\"#TEL\":\"\",\"#TNUM\":\"177\",\"#TQFM\":\"558.65\",\"#TWYJ\":\"21.35\",\"#QFCNT\":\"1\",\"#QFD0\":{\"#YF\":\"2017-04\",\"#YFQS\":\"0\",\"#YFZS\":\"144\",\"#YFSL\":\"144\",\"#YFJE\":\"454.80\",\"#YFWYJ\":\"21.20\"}}} ";//readMsgBody.RtMsg(svcName);
            
            sock.getOutputStream().write(msgBody.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return svc;
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.simulate.SimComm#close()
     */
    
    
public void startServer() throws Exception{
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			sock = server.accept();
			sock.setSoTimeout(timeOut);
			receive();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != sock){
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != server){
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

