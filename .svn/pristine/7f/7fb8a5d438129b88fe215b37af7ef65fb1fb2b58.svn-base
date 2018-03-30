package com.adtec.ncps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.dta.protocol.IComm;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.dta.protocol.http.HttpConnectionManager;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.struct.dta.DtaInfo;

/**
 * <p>ApacheHttpCientComm</p>
 * <p>apache的httpclinet</p>
 * <p>Copyright: Copyright (c) 2013</p> 
 * <p>Company: 北京先进数通信息技术股份公司</p> 
 * @author  cuizhw
 * @version 1.0 2015年8月20日 cuizhw
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public class ApacheHttpClientComm implements IComm {
    private int                 timeout          = 5000;                                             // 超时时间
    private HttpClient    httpClient       = null;
    private HttpPost            post             = null;
    
    private boolean             isLong;                                                              // 是否是长连接
    private String              strUrl           = null;   
      

        
    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#init(java.lang.String, int,
     * int, boolean)
     */
    public void init(boolean isLong) {
        this.isLong = isLong;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#term()
     */
    public void term() {
       
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#connect(java.lang.String, int)
     */
    public void connect(String ipAddress, int port, String url,int timeOut) {
        // TODO Auto-generated method stub

        this.timeout = timeOut;
        strUrl = String.format("http://%s:%d/%s", ipAddress, port, url);
        if(isLong){
            
        }
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        post = new HttpPost(strUrl);
        httpClient = HttpConnectionManager.getHttpClient();  
       

        post = new HttpPost(strUrl);
        
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#close()
     */
    public void close() {
        if(null!=post)
            post.abort();
        if(null!=httpClient){
            
            //httpClient.getConnectionManager().shutdown();
        }
       
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#check(java.lang.String, int)
     */
    public boolean check(String ipAddress, int port) {
        return false;
    }

    /**
     * 发送http信息方法(请注意:调用此方法之前必须调用connect连接方法)
     * @param svcName 服务名

     * @param sendByte 发送数据

     * @para len 要发送数据的长度
     */
    public void send(String svcName, byte[] sendByte, int len) {
    	DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		post.setHeader("Content-Type","application/xml;charset=utf-8");
    	post.setHeader("MsgTp",(String)EPOper.get(tpID, "CUP_HTTP_HEAD[0].MsgTp"));
    	post.setHeader("OriIssrId",(String)EPOper.get(tpID, "CUP_HTTP_HEAD[0].OriIssrId"));
    	post.setHeader("RcverReserved",(String)EPOper.get(tpID, "CUP_HTTP_HEAD[0].RcverReserved"));
    	post.setHeader("CupsReserved",(String)EPOper.get(tpID, "CUP_HTTP_HEAD[0].CupsReserved"));
    	post.setHeader("SderReserved",(String)EPOper.get(tpID, "CUP_HTTP_HEAD[0].SderReserved"));
    	post.setEntity(new ByteArrayEntity(sendByte));
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#receive(java.lang.String, int,
     * int)
     */
    public byte[] receive(String svcName, int timeout) {
        HttpResponse response;
        byte[] responseData = null;
        Long time = System.currentTimeMillis();
        
        try {
            post.setHeader("Connection", "close");  
            response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new BaseException(SysErr.E_COMM_RECV_ERR, strUrl, statusCode,
                        "not 200");
            }
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
                responseData = EntityUtils.toByteArray(entity);
            }
            /*
            long tempTime = System.currentTimeMillis() - time ;// 此次交易处理时间
            if(tempTime>1000)
                System.out.println("time:"+tempTime);*/
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            /*
            long tempTime = System.currentTimeMillis() - time ;// 此次交易处理时间
            if(tempTime>1000)
                System.out.println("time1:"+tempTime);*/
            throw new BaseException(SysErr.E_COMM_RECV_ERR, e,strUrl,"",e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            /*
            long tempTime = System.currentTimeMillis() - time ;// 此次交易处理时间
            if(tempTime>1000)
                System.out.println("time2:"+tempTime);*/
            throw new BaseException(SysErr.E_COMM_RECV_ERR, e, strUrl,"",e.getMessage());
        }finally{
        
            if(post!=null)
                post.releaseConnection();
        }
        return responseData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#sendFile(java.lang.String)
     */
    public void sendFile(String fileName) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#recvFile(java.lang.String)
     */
    public void recvFile(String fileName) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.protocol.IComm#getICommSession()
     */
    public ICommSession getICommSession() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setICommSession(ICommSession session) {
    	// TODO Auto-generated method stub

    }

}
