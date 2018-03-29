package com.adtec.ncps;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.dta.protocol.IComm;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.ErrLog;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * <p>HttpServerComm</p>
 * <p>Http通信Server端，用于Http服务端的数据发送和接收</p>
 * <p>Copyright: Copyright (c) 2013</p> 
 * <p>Company: 北京先进数通信息技术股份公司</p> 
 * @author  zhangmm
 * @version 1.0 2014年3月6日 zhangmm
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public class CupHttpServerComm implements IComm {
    private ICommSession iCommSession = null;
    private OutputStream out = null;
    private HttpExchange httpExchange = null;
    private boolean isLong;
    private int timeout;
    
    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#init(java.lang.String, int, int, boolean)
     */
    public void init( boolean isLong) {
    	TrcLog.log("httpSvr.log", "测试");
        this.isLong = isLong;
                
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#term()
     */
    public void term() {
        // TODO Auto-generated method stub
    	TrcLog.log("httpSvr.log", "测试");
        httpExchange.close();
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#connect(java.lang.String, int)
     */
    public void connect(String ipAddress, int port,String url,int timeOut) {
    	TrcLog.log("httpSvr.log", "测试");
        this.timeout = timeOut;
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#close()
     */
    public void close() {
    	TrcLog.log("httpSvr.log", "测试");
        httpExchange.close();
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#check(java.lang.String, int)
     */
    public boolean check(String ipAddress, int port) {
        // TODO Auto-generated method stub
    	TrcLog.log("httpSvr.log", "测试");
        return true;
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#send(java.lang.String, byte[], int)
     */
    public void send(String svcName, byte[] sendByte, int len) {
        // 如果要发送的数据的长度小于发送长度，则返回发送失败

        if (sendByte.length < len || sendByte.length == 0 || len == 0) {
            // TODO:记录日志
            new ErrLog().errlog(SysErr.E_NULLPOINT, "HttpServerComm发送的sendByte为空");
            TrcLog.log("httpSvr.log", "测试");
        }
        if (null == iCommSession) {
            // TODO:记录日志
            new ErrLog().errlog(SysErr.E_NULLPOINT, "HttpServerComm的iCommSession为空");
            TrcLog.log("httpSvr.log", "测试");
        }
        try {
            httpExchange = iCommSession.getHttpExchange();
            if ( isLong ) {
            	httpExchange.getResponseHeaders().set("Connection", "Keep-Alive");
            	httpExchange.getResponseBody();
            	TrcLog.log("httpSvr.log", "测试");
//                httpExchange.getRequestHeaders().set("Connection", "Keep-Alive");
            } else {
            	/*String szMsgTp = httpExchange.getRequestHeaders().get("MsgTp").get(0);
            	String szOriIssrId = httpExchange.getRequestHeaders().get("OriIssrId").get(0);
            	String szSderReserved = httpExchange.getRequestHeaders().get("SderReserved").get(0);
            	String szRcverReserved = httpExchange.getRequestHeaders().get("RcverReserved").get(0);
            	String szCupsReserved = httpExchange.getRequestHeaders().get("CupsReserved").get(0);
            	httpExchange.getResponseHeaders().set("MsgTp", szMsgTp);
            	httpExchange.getResponseHeaders().set("OriIssrId", szOriIssrId);
            	httpExchange.getResponseHeaders().set("SderReserved", szSderReserved);
            	httpExchange.getResponseHeaders().set("RcverReserved", szRcverReserved);
            	httpExchange.getResponseHeaders().set("CupsReserved", szCupsReserved);*/
            	Headers  rspHead = httpExchange.getResponseHeaders();
            	Headers reqHead = httpExchange.getRequestHeaders();
            	rspHead = reqHead;
            	TrcLog.log("httpSvr.log", "9999测试99999999");
//                httpExchange.getRequestHeaders().set("Connection", "close");
            }
            httpExchange.sendResponseHeaders(200, len);
            out = httpExchange.getResponseBody();
            out.write(sendByte);
            out.flush();
            out.close();
        } catch (IOException e) {
        	try {
        		TrcLog.log("httpSvr.log", "测试");
				httpExchange.sendResponseHeaders(500, 0);
				out.write("服务器内部错误!".getBytes()); 
	            out.flush();
	            out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	//modify by cuizhw at 2017/06/05 原有异常未抛出
            throw new BaseException(SysPubDef.CUP_ERR_RET,e,e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#receive(java.lang.String, int, int)
     */
    public byte[] receive(String svcName, int timeout) {
    	TrcLog.log("httpSvr.log", "svcName=[%s]timeout=[%d]",svcName ,timeout);
    	TrcLog.log("httpSvr.log", "receive()---");
    	DtaInfo dtaInfo = DtaInfo.getInstance();

		String tpID = dtaInfo.getTpId();
    	String szMsgTp = httpExchange.getRequestHeaders().get("MsgTp").get(0);
    	EPOper.put(tpID, "__GDTA_FORMAT[0]__GDTA_SVCNAME", szMsgTp);
        return iCommSession.getBytes();
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#sendFile(java.lang.String)
     */
    public void sendFile(String fileName) {
    	TrcLog.log("httpSvr.log", "测试");
    }

    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#recvFile(java.lang.String)
     */
    public void recvFile(String fileName) {
    	TrcLog.log("httpSvr.log", "测试");
    }
    public void setICommSession(ICommSession iCommSession){
    	httpExchange = iCommSession.getHttpExchange();
    	List<String> szMsgTp = httpExchange.getRequestHeaders().get("MsgTp");
    	List<String> szOriIssrId = httpExchange.getRequestHeaders().get("OriIssrId");
    	List<String> szSderReserved = httpExchange.getRequestHeaders().get("SderReserved");
    	List<String> szRcverReserved = httpExchange.getRequestHeaders().get("RcverReserved");
    	List<String> szCupsReserved = httpExchange.getRequestHeaders().get("CupsReserved");
    	DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcNa = szMsgTp.get(0);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_SVCNAME", svcNa);
    	TrcLog.log("httpSvr.log", "svcNa:[%s]", svcNa);
 
        this.iCommSession = iCommSession;
    }
    /* (non-Javadoc)
     * @see com.adtec.starring.dta.protocol.IComm#getICommSession()
     */
    public ICommSession getICommSession() {
    	TrcLog.log("httpSvr.log", "测试");
        return iCommSession;
    }

}

