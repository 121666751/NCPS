package com.adtec.ncps;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;

import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.dta.protocol.IComm;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.ErrLog;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;

public class ApacheHttpServerComm implements IComm {

    private ICommSession iCommSession = null;
    
	public void init(boolean isLong) {
		// TODO Auto-generated method stub
		
	}

	public void term() {
		// TODO Auto-generated method stub
		
	}

	public void connect(String ipAddress, int port, String url, int timeOut) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public boolean check(String ipAddress, int port) {
		// TODO Auto-generated method stub
		return false;
	}

	public void send(String svcName, byte[] sendByte, int len) {
		// TODO Auto-generated method stub
		HttpResponse reponse = (HttpResponse)iCommSession.getApacheResponse();
		HttpRequest request= (HttpRequest)iCommSession.getApacheRequest();
		Header[] head= request.getAllHeaders();
		if (head != null) {
			int iLen = head.length;
			for (int i = 0; i < iLen; i++) {
				if ("MsgTp".equals(head[i].getName())) {
					reponse.addHeader("MsgTp", head[i].getValue());
				} else if ("OriIssrId".equals(head[i].getName())) {
					reponse.addHeader("OriIssrId", head[i].getValue());
				} else if ("SderReserved".equals(head[i].getName())) {
					reponse.addHeader("SderReserved", head[i].getValue());
				} else if ("RcverReserved".equals(head[i].getName())) {
					reponse.addHeader("RcverReserved", head[i].getValue());
				}else if("CupsReserved".equals(head[i].getName())){
					reponse.addHeader("CupsReserved", head[i].getValue());
				}
			}
		}else{
			 new ErrLog().errlog(SysErr.E_NULLPOINT, "请求HTTP头为空!");
		}
		reponse.setEntity(new ByteArrayEntity(sendByte));
	}

	public byte[] receive(String svcName, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

	public void recvFile(String fileName) {
		// TODO Auto-generated method stub
		
	}
	
	public void setICommSession(ICommSession iCommSession){
        this.iCommSession = iCommSession;
        DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		HttpRequest request= (HttpRequest)iCommSession.getApacheRequest();
		Header[] head= request.getAllHeaders();
		if (head != null) {
			int iLen = head.length;
			for (int i = 0; i < iLen; i++) {
				//TrcLog.log("httpSvr.log", "http:[%s]", head[i].getValue());
				if ("MsgTp".equals(head[i].getName())) {
					EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_SVCNAME", head[i].getValue());
					EPOper.put(tpID, "CUP_HTTP_HEAD[0].MsgTp", head[i].getValue());
				}
				else if("OriIssrId".equals(head[i].getName())){
					EPOper.put(tpID, "CUP_HTTP_HEAD[0].OriIssrId", head[i].getValue());
				}else if("RcverReserved".equals(head[i].getName())){
					EPOper.put(tpID, "CUP_HTTP_HEAD[0].RcverReserved", head[i].getValue());
				}else if("CupsReserved".equals(head[i].getName())){
					EPOper.put(tpID, "CUP_HTTP_HEAD[0].CupsReserved", head[i].getValue());
				}else if("SderReserved".equals(head[i].getName())){
					EPOper.put(tpID, "CUP_HTTP_HEAD[0].SderReserved", head[i].getValue());
				}
			}
		}else{
			 new ErrLog().errlog(SysErr.E_NULLPOINT, "请求HTTP头为空!");
		}
    	
    }

    public ICommSession getICommSession() {
        return iCommSession;
    }
}