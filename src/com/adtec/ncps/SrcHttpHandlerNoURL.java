/*
 *    Copyright (c) 2013 ADTEC
 *    All rights reserved
 *
 *    本程序为自由软件；您可依据自由软件基金会所发表的GNU通用公共授权条款规定，就本程序再为发布与／或修改；无论您依据的是本授权的第二版或（您自行选择的）任一日后发行的版本。
 *    本程序是基于使用目的而加以发布，然而不负任何担保责任；亦无对适售性或特定目的适用性所为的默示性担保。详情请参照GNU通用公共授权。
 */

package com.adtec.ncps;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.dta.IDTA;
import com.adtec.starring.dta.ISrcDtaServiceFlow;
import com.adtec.starring.dta.SrcFlowParam;
import com.adtec.starring.dta.plugin.DtaRunProcess;
import com.adtec.starring.dta.protocol.ICommSession;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.global.GVarContainer;
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
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * <p>SrcHttpHandler</p>
 * <p>Http请求处理类</p>
 * <p>Copyright: Copyright (c) 2013</p> 
 * <p>Company: 北京先进数通信息技术股份公司</p> 
 * @author  zhangmm
 * @version 1.0 2014年3月5日 zhangmm
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1 
 */
public class SrcHttpHandlerNoURL implements HttpHandler {
    private ISrcDtaServiceFlow srcDtaServiceFlow;
    private String dtaName;//dta名称
    
    //private String svcName = null; //服务码
    private boolean dupFlag = false;
    private int type = DtaInstInfo.TEPE_REQRSP_INST;
    private IDTA dtaBean;
    
    public SrcHttpHandlerNoURL(String dtaName,IDTA dtaBean){
        this.dtaName = dtaName;
        this.dtaBean = dtaBean;
    }
    
	public static String getSvcName(String str, int n) {
	    List<String> result = new ArrayList<String>();
	    Pattern p = Pattern.compile("\"tx_code\":\"(.+?)\"");
	    Matcher m = p.matcher(str);
	    m.find();

	    return m.group(1);
	}
    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // TODO Auto-generated method stub
        DtaInstInfo instInfo = null;
        String svcName = null;
        //modify by cuizhw at 2017/12/8 增加超限处理
        /* String name = Thread.currentThread().getName();
       	if(name.indexOf("rejected request -")>=0){
            //System.out.println(dtaName+"超并发的线程"+name);
            //TrcLog.error( "rejecte.log", dtaName+"超并发的线程:"+name);
            new ErrLog().errlog(SysErr.E_MESSAGE,dtaName+"超并发的线程:"+name);
            OutputStream out =null;
            String str = "超过系统并发控制";
            byte[] bytes = str.getBytes();
            httpExchange.sendResponseHeaders(500, bytes.length);
            out = httpExchange.getResponseBody();
            out.write(bytes);
            out.flush();
            out.close();
            GVarContainer.clearVar();
            return;
        }*/
        
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
            
            if(instInfo==null){
                String str = "无可用实例!";
                bytes = str.getBytes();
                httpExchange.sendResponseHeaders(200, bytes.length);
                OutputStream out =null;
                out = httpExchange.getResponseBody();
                out.write(bytes);
                out.flush();
                out.close();
                new ErrLog().errlog(SysErr.E_MESSAGE, str);
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
         
            String url = httpExchange.getRequestURI().toString();
            String temp [] = url.split("\\/");
//            String type = temp[1];    //该请求应该是http 的类型，url中应该符合
//            if(temp.length>=3){
//                //this.dtaName = temp[2];   //dta名称
//            }
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
//                
//            }
//            
//            if(StringTool.isNullOrEmpty(svcName)){
//                new ErrLog().errlog(SysErr.E_NULLPOINT,"服务码：svcName");
//                throw new BaseException(SysErr.E_NULLPOINT,"服务码：svcName");
//            }
            
//            if(!SysDef.HTTP_TYPE_NAME.equals(type)){
//                String str = "发送请求的类型不符合http协议!";
//                bytes = str.getBytes();
//                httpExchange.sendResponseHeaders(200, bytes.length);
//                OutputStream out =null;
//                out = httpExchange.getResponseBody();
//                out.write(bytes);
//                out.flush();
//                out.close();
//                new ErrLog().errlog(SysErr.E_MESSAGE, str);
//                TransLog.getInstance().error(str);
//                return;
//            }
            
            //交易报文的长度
            String sLen = httpExchange.getRequestHeaders().getFirst("Content-Length");
            int len = Integer.parseInt(sLen);
            //方式2
            InputStream in = httpExchange.getRequestBody(); //获得输入流   
            bytes = new byte[len];
            int recv = 0, rlen=0;  
            while (recv < len) {
                rlen = recv;
                rlen = in.read(bytes, rlen, len - rlen);
                if (rlen < 0) {
                    throw new BaseException(SysErr.E_UTIL_SOCK_READ, len - rlen);
                } else {
                    recv += rlen;
                }
            }
            
    		String szName = new String(bytes);
            System.out.println(szName);
            szName = szName.replaceAll("\r|\n| ", "");
            svcName=getSvcName(szName,1);
            System.out.println(svcName);
//    		JSONObject jsonObject = new JSONObject();
//
//    		Map map1 = jsonObject.parseObject(szName, Map.class);
//
//    		svcName = (String) map1.get("tx_code");
    		svcName = svcName.toUpperCase();
    		System.out.println(svcName);
            /*ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((len = in.read(bytes)) > 0) {
              os.write(bytes, 0, len);
              count += count;
            }*/
            //processTask(bytes, httpExchange,flowParam);
            processTask(bytes, httpExchange,flowParam,svcName,reqSeq);
            
        } catch (Exception e) {
            
            String str = "接收http请求失败!";
            byte[] bytes = str.getBytes();
            httpExchange.sendResponseHeaders(200, bytes.length);
            httpExchange.getResponseHeaders().set("Content-Length", Long.toString(bytes.length));
            OutputStream out =null;
            out = httpExchange.getResponseBody();
            out.write(bytes);
            out.flush();
            out.close();
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
     * @param svcName 服务码
     * @param 请求方流水号
     */
    private void processTask(byte bytes[],HttpExchange httpExchange,SrcFlowParam flowParam,String svcName,String reqSeq){
        ICommSession iCommSession = new ICommSession();
        iCommSession.setHttpExchange(httpExchange);
//        HttpServerComm serverComm = new HttpServerComm();
//        serverComm.setICommSession(iCommSession);
        // 源处理流程
        
        boolean result = false;
        DtaRunInfo dtaRunInfo = null;
        Long dtaInstInfoId = null;
        try {
            // // 1.实例初始化：根据dtaName实例化，
            //srcDtaServiceFlow = (SrcDtaServiceFlow) SpringUtil.getBean("srcDta");
            
            
            flowParam.setDtaName(dtaName);
            flowParam.setSession(iCommSession);
            if(!StringTool.isNullOrEmpty(flowParam.getDta().getDtaParm().getiPrtcFmt()))
                iCommSession.setBytes(bytes);
            /*
            dtaRunInfo = RuntimePool.getInstance().getDtaInfoHashMap().get(dtaName);
            dtaInstInfoId = dtaRunInfo.getIdleDtaInstID();
            dtaRunInfo.getDtaInstHashMap().get(dtaInstInfoId).setThreadId(Thread.currentThread().getId());
            if(dtaInstInfoId == null)
            	throw new BaseException(SysErr.E_DTA_IDLEINSTNONE,dtaName);
            flowParam.setInstId(dtaInstInfoId);*/
            srcDtaServiceFlow.dtaSvrInit(flowParam);
            //added by cuizhw at 2017/08/14
            DtaInfo dtaInfo = DtaInfo.getInstance();
            if(!StringTool.isNullOrEmpty(reqSeq)){
                dtaInfo.setReqSeq(reqSeq);
            }
            //added by cuizhw at 2017/8/28 将headers放于DtaInfo中
            dtaInfo.setRequestURL(httpExchange.getRequestURI());
            dtaInfo.setReqHeaders(httpExchange.getRequestHeaders());
            dtaInfo.setRspHeaders(httpExchange.getResponseHeaders());
            
            //该情况是没有协议的，不需要解析协议报文的，设置数据和服务码转换
            EPOper.put(flowParam.getTpId(),SysDef.GDTA_FORMAT+0+SysDef.SVCNAME,svcName);
            System.out.println(svcName);
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
            new ErrLog().errlog(SysErr.E_MESSAGE, "http dta service src threadwork exception:"+e.getMessage());
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
                    /*
                    if(flowParam.getTranState()==SysDef.E_ERROR){
                        try{
                            httpExchange.sendResponseHeaders(500, 0);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace(BaseLog.getExpOut());
                        }
                    }*/
                }
                
            } catch (Exception e2) {
                try {
                    httpExchange.sendResponseHeaders(500, 0);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace(BaseLog.getExpOut());
                }
                flowParam.setTranState(SysDef.E_ERROR);//交易异常
                e2.printStackTrace(BaseLog.getExpOut());
//                new ErrLog().errlog(SysErr.E_MESSAGE, "finallyDone失败:"+e2.getMessage());
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
//                    new ErrLog().errlog(SysErr.E_MESSAGE, "接收内部响应结束后dtaSvrDown失败:"+e3.getMessage());
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

