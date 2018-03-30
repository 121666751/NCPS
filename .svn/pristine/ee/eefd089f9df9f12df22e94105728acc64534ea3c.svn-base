package com.adtec.ncps;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.dta.IDTA;
import com.adtec.starring.dta.ISrcDtaServiceFlow;
import com.adtec.starring.dta.SrcFlowParam;
import com.adtec.starring.dta.plugin.DtaRunProcess;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.log.ErrLog;
import com.adtec.starring.log.TransLog;
import com.adtec.starring.respool.PoolOperate;
import com.adtec.starring.struct.admin.ESAdmin;
import com.adtec.starring.struct.dta.DTA;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.struct.dta.DtaInstInfo;
import com.adtec.starring.struct.dta.DtaParam;
import com.adtec.starring.struct.dta.DtaRunInfo;
import com.adtec.starring.util.StringTool;

public class SrcApacheHttpHandler implements HttpRequestHandler {

    private ISrcDtaServiceFlow srcDtaServiceFlow;
    private String dtaName;//dta名称
    
    private String svcName = null; //服务码
    private boolean dupFlag = false;
    private int type = DtaInstInfo.TEPE_REQRSP_INST;
    private IDTA dtaBean;
    
    public SrcApacheHttpHandler(String dtaName,IDTA dtaBean){
        this.dtaName = dtaName;
        this.dtaBean = dtaBean;
    }
    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
     */
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
    		throws HttpException, IOException {
        // TODO Auto-generated method stub
        DtaInstInfo instInfo = null;
        try {
            DTA dta = (DTA) PoolOperate.getResData(PoolOperate.DTA, dtaName);
            ESAdmin admin = PoolOperate.getParmPool().getEsadmin();
            SrcFlowParam flowParam = new SrcFlowParam();
            flowParam.setdDtaName(dtaName);
            flowParam.setDta(dta);
            flowParam.setESAdmin(admin);
            
            if(dupFlag){
                
            }
            
            byte [] bytes;
            
            //申请实例，按類型取
            //instInfo = DtaRunProcess.getInstInfo(dtaName);
            instInfo = dtaBean.getDtaInstInfo(type);
            
            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
    		if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
    			throw new MethodNotSupportedException(method + " method not supported");
    		}
    
    		
            if(instInfo==null){
                String str = "系统繁忙";
                bytes = str.getBytes();
                response.setStatusCode(HttpStatus.SC_OK);
                //response.setHeader("Content-Length", Long.toString(bytes.length));
                response.setEntity(new ByteArrayEntity(bytes));
                new ErrLog().errlog(SysPubDef.CUP_ERR_RET, str);
                TransLog.getInstance().error(str);
                return;
            }
            
            instInfo.setBeginTime(System.currentTimeMillis());// 开始时间戳
            instInfo.setDtaInstState(DtaInstInfo.BUSY);
            
            flowParam.setInstId(instInfo.getInstId());
            flowParam.setTpId(instInfo.getTpId());
            flowParam.setCpId(instInfo.getCpId());
            //url格式：http://IP:PORT/HttpServer/DTANAME/SVCNAME
//            System.out.println("httpHandler.begin....");

    		String url = request.getRequestLine().getUri();
//            String url = request..getRequestURI().toString();
            String temp [] = url.split("\\/");
            String type = temp[1];    //该请求应该是http 的类型，url中应该符合
            if(temp.length>=3){
                //this.dtaName = temp[2];   //dta名称
            }
            String reqSeq = null;
//            if(temp.length>=4){
//                svcName = temp[3]; //服务码
//                //added by cuizhw at 2017/07/17
//                if(svcName.indexOf('?')!=-1){
//                    String[] temps = svcName.split("[?]");
//                    svcName = temps[0];
//                    String tmp = temps[1];
//                    if(tmp.indexOf('&')==-1){
//                        tmp = tmp +"&";
//                    }
//                    temps = tmp.split("&");
//                    for(String key:temps){
//                        String[] tt = key.split("=");
//                        if(tt.length>1&&"REQ_SEQ".equals(tt[0])){
//                            reqSeq = tt[1];
//                            break;
//                        }
//                    }
//                }
//            }
//            
//            if(StringTool.isNullOrEmpty(svcName)){
//                new ErrLog().errlog(SysErr.E_NULLPOINT,"服务码：svcName");
//                throw new BaseException(SysErr.E_NULLPOINT,"服务码：svcName");
//            }
            
            if(!SysDef.HTTP_TYPE_NAME.equals(type)){
                String str = "发送请求的类型不符合http协议!";
                bytes = str.getBytes();
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                //response.setHeader("Content-Length", Long.toString(bytes.length));
                response.setEntity(new ByteArrayEntity(bytes));
                new ErrLog().errlog(SysErr.E_MESSAGE, str);
                TransLog.getInstance().error(str);
                return;
            }
            
            //交易报文的长度
            String sLen = request.getFirstHeader("Content-Length").getValue();
            //httpExchange.getRequestHeaders().getFirst("Content-Length");
            int len = Integer.parseInt(sLen);
            //方式2
            RequestLine message =  request.getRequestLine();
            String msg = message.getMethod();

            byte[] entityContent = null;
    		if (request instanceof HttpEntityEnclosingRequest) {
    			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
    			entityContent = EntityUtils.toByteArray(entity);
    			System.out.println("Incoming entity content (bytes): " + entityContent.length);
    		}
            System.out.println("---------"+msg);
            
            processTask(entityContent, flowParam, reqSeq, request, response, context);
            
        } catch (Exception e) {
            
            String str = "接收http请求失败!";
            byte[] bytes = str.getBytes();
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            //response.setHeader("Content-Length", Long.toString(bytes.length));
            response.setEntity(new ByteArrayEntity(bytes));
//            httpExchange.sendResponseHeaders(200, bytes.length);
//            httpExchange.getResponseHeaders().set("Content-Length", Long.toString(bytes.length));
            //OutputStream out =null;
//            response.getStatusLine()
//            out = httpExchange.getResponseBody();
//            out.write(bytes);
//            out.flush();
//            out.close();
            e.printStackTrace(BaseLog.getExpOut());  
            new ErrLog().errlog(SysErr.E_MESSAGE, e,"接收http请求失败!");
            TransLog.getInstance().error("接收http请求失败!");
        }finally{
            if(instInfo!=null){
                //DtaRunProcess.setInstInfoEndState(info, dtaName, reqId, state, type);
                dtaBean.releaseDtaInstInfo(type, instInfo);
            }
        }
      
          
    }
    /**
     * 主流程处理
     * @param bytes  报文
     * @param httpExchange  http请求对象
     * @param flowParam
     * @param 请求方流水号
     */
    private void processTask(byte bytes[], SrcFlowParam flowParam, String reqSeq,
    		HttpRequest request, HttpResponse response, HttpContext context){
        ICommSession iCommSession = new ICommSession();
        iCommSession.setApacheContext(context);
        iCommSession.setApacheRequest(request);
        iCommSession.setApacheResponse(response);
        
        // 源处理流程
        boolean result = false;
        DtaRunInfo dtaRunInfo = null;
        Long dtaInstInfoId = null;
        try {
            // // 1.实例初始化：根据dtaName实例化，
            flowParam.setDtaName(dtaName);
            flowParam.setSession(iCommSession);
            if(!StringTool.isNullOrEmpty(flowParam.getDta().getDtaParm().getiPrtcFmt()))
                iCommSession.setBytes(bytes);
        
            srcDtaServiceFlow.dtaSvrInit(flowParam);
            //added by cuizhw at 2017/08/14
            DtaInfo dtaInfo = DtaInfo.getInstance();
            if(!StringTool.isNullOrEmpty(reqSeq)){
                dtaInfo.setReqSeq(reqSeq);
            }
            //该情况是没有协议的，不需要解析协议报文的，设置数据和服务码转换
            String gdtaSvcName = (String)EPOper.get(flowParam.getTpId(),SysDef.GDTA_FORMAT+0+SysDef.SVCNAME);
            if(StringTool.isNullOrEmpty(gdtaSvcName)){
            	EPOper.put(flowParam.getTpId(),SysDef.GDTA_FORMAT+0+SysDef.SVCNAME,svcName);
            }
            EPOper.put(flowParam.getTpId(),SysDef.GDTA_FORMAT+0+SysDef.FORMAT_ITEMDATA,bytes);
            EPOper.put(flowParam.getTpId(),SysDef.GDTA_FORMAT+0+SysDef.FORMAT_LENGTH,bytes.length);
            //复制报文长度和报文值
            EPOper.copy(flowParam.getTpId(), flowParam.getTpId(), SysDef.GDTA_FORMAT
                    + 0 + SysDef.FORMAT_ITEMDATA, SysDef.GDTA_FORMAT + 0
                    + SysDef.ITEMDATA);
            EPOper.copy(flowParam.getTpId(), flowParam.getTpId(), SysDef.GDTA_FORMAT
                    + 0 + SysDef.FORMAT_LENGTH, SysDef.GDTA_FORMAT + 0
                    + SysDef.ITEMDTA_LENGTH);
            DtaParam dtaParam = ((DTA)PoolOperate.getResData(PoolOperate.DTA, dtaName)).getDtaParm();
            if(StringTool.isNullOrEmpty(dtaParam.getiPrtcFmt()))
                flowParam.setFlagPrtcFormat(false);//设置协议报文标识未false,不需要解析协议报文
            result = srcDtaServiceFlow.dtaRevService(flowParam);
            if(!result){
                flowParam.setTranState(SysDef.E_ERROR);//交易异常
            }
            
        } catch (Exception e) {
            e.printStackTrace(BaseLog.getExpOut());
            flowParam.setTranState(SysDef.E_ERROR);//交易异常
            new ErrLog().errlog(SysPubDef.CUP_ERR_RET, "http dta service src threadwork exception:"+e.getMessage());
            TransLog.getInstance().error("http dta service src threadwork exception:"+e.getMessage());
        } finally {
            ESAdmin admin = PoolOperate.getParmPool().getEsadmin();
            try {
                if(admin.getDtaParmMap().get(dtaName).isDupFlag()){
                    if (result) {// 正常流程：插件信息结束登记
                        DtaRunProcess.endRecStat(SysDef.E_OK,
                            dtaName, flowParam.getInstId(), flowParam
                                    .getService().getName(), flowParam
                                    .getDrq().getsDtaSeq(),DtaInstInfo.TEPE_REQ_INST);
                    } else {
                        DtaRunProcess.endRecStat(SysDef.E_ERROR,
                            dtaName, flowParam.getInstId(), flowParam.getFg().getService().getName()
                                    , DtaInfo.getInstance().getSeqNo(),DtaInstInfo.TEPE_REQ_INST);
                    }
                    
                    return;
                }else{
                    srcDtaServiceFlow.finallyDone(flowParam);
                }
                
            } catch (Exception e2) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
//              httpExchange.sendResponseHeaders(500, 0);
                flowParam.setTranState(SysDef.E_ERROR);//交易异常
                e2.printStackTrace(BaseLog.getExpOut());
            }
            finally{
                try{
                    new BaseLog().writeLogToFile();    
                }catch (Exception e3) {
                    e3.printStackTrace(BaseLog.getExpOut());
                }
                try {
                    if (!(admin.getDtaParmMap().get(dtaName).isDupFlag())) {
                        srcDtaServiceFlow.dtaSvrDown(flowParam);
                    }
                } catch (Exception e3) {
                    e3.printStackTrace(BaseLog.getExpOut());
                }finally{
                	if(dtaInstInfoId != null)
                		dtaRunInfo.addIdleDtaInstID(dtaInstInfoId);
                }
            }
        }
    }
    /**
     * @return the srcDtaServiceFlow
     */
    public ISrcDtaServiceFlow getSrcDtaServiceFlow() {
        return srcDtaServiceFlow;
    }
    /**
     * @param srcDtaServiceFlow the srcDtaServiceFlow to set
     */
    public void setSrcDtaServiceFlow(ISrcDtaServiceFlow srcDtaServiceFlow) {
        this.srcDtaServiceFlow = srcDtaServiceFlow;
    }
}