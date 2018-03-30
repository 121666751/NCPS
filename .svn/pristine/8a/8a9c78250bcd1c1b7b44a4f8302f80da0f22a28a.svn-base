/*
 *    Copyright (c) 2013 ADTEC
 *    All rights reserved
 *
 *    本程序为自由软件；您可依据自由软件基金会所发表的GNU通用公共授权条款规定，就本程序再为发布与／或修改；无论您依据的是本授权的第二版或（您自行选择的）任一日后发行的版本。
 *    本程序是基于使用目的而加以发布，然而不负任何担保责任；亦无对适售性或特定目的适用性所为的默示性担保。详情请参照GNU通用公共授权。
 */

package com.adtec.ncps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.adtec.starring.dta.BaseDTA;
import com.adtec.starring.dta.IDTA;
import com.adtec.starring.dta.ISrcDtaServiceFlow;
import com.adtec.starring.dta.ScanTimeOutThread;
import com.adtec.starring.dta.SrcDtaAsyncRspInst;
import com.adtec.ncps.SrcHttpHandlerNoURL;
import com.adtec.starring.esb.drq.DRQ;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.log.ErrLog;
import com.adtec.starring.log.TransLog;
import com.adtec.starring.respool.ParmPool;
import com.adtec.starring.respool.PoolOperate;
import com.adtec.starring.struct.admin.ESAdmin;
import com.adtec.starring.struct.admin.IPInfo;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

/**
 * <p>SrcHttpServer</p>
 * <p>源DTA，Http协议客户化实现类</p>
 * <p>Copyright: Copyright (c) 2013</p> 
 * <p>Company: 北京先进数通信息技术股份公司</p> 
 * @author  zhangmm
 * @version 1.0 2014年3月4日 zhangmm 
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public class SrcHttpServerNoURL extends BaseDTA implements IDTA {
    private ISrcDtaServiceFlow dtaServiceFlow;;
    /*private String dtaName;     // dta名称
    private String parmVersion; // 参数资源池版本号
    private String resVersion;  //业务资源版本号
*/    private HttpServer server[];  //实现Http协议的服务端对象,多个监听地址

    private ThreadPoolExecutor threadPool;
//    private boolean dupFlag = false;//异步全双工标识  true是异步全双工
    private List<SrcDtaAsyncRspInst> instList = new ArrayList<SrcDtaAsyncRspInst>();
    private ScanTimeOutThread scanTimeOutThread;
    private ThreadPoolExecutor threadPoolExecutor;
    
    
    /**
     * @return the dtaName
     */
    public String getDtaName() {
        return dtaName;
    }

    /**
     * @return the parmVersion
     */
    public String getParmVersion() {
        return parmVersion;
    }

    /**
     * @param parmVersion the parmVersion to set
     */
    public void setParmVersion(String parmVersion) {
        this.parmVersion = parmVersion;
    }

   
    
    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.IDTA#start()
     */
    @Override
    public void start() {
        int port = 8888;// 端口号
        String ip = "127.0.0.1";
    	super.start();
        
        
     // 设定一个线程池
        // 参数说明：最小实例数，最大实例数 空闲时间 3秒

        threadPool = new ThreadPoolExecutor(minNum,
            maxNum, 3, TimeUnit.SECONDS,
                // 缓冲队列为5
                new ArrayBlockingQueue<Runnable>((minNum+maxNum)/2),

                Executors.defaultThreadFactory(), 
                // 抛弃旧的任务
                new ThreadPoolExecutor.DiscardOldestPolicy());
        
        HttpServerProvider provider = HttpServerProvider.provider();
        
        List<IPInfo> al = new ArrayList<IPInfo>();
        for(IPInfo info:addressList){
            int portTemp = info.getPort();
            if(portTemp!=-1){
                al.add(info);
            }else{
                new ErrLog().errlog(SysErr.E_MESSAGE, "监听地址为["+info.getIpAddr()+"]的服务，端口是["+portTemp+"]启动失败！"); 
                TransLog.getInstance().error("监听地址为["+info.getIpAddr()+"]的服务，端口是["+portTemp+"]启动失败！"); 
            }
        }
        
        if(dupFlag){//如果是异步全双工
            threadPoolExecutor = new ThreadPoolExecutor(sendNum, sendNum, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
                        (sendNum + sendNum) / 2),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r,
                            ThreadPoolExecutor executor) {
                        new ErrLog().errlog(SysErr.E_THREADPOOL_FULL);
                    }
                });
            SrcDtaAsyncRspInst inst ;
            for(Long instId:cacheRspMap.keySet()){
            //for(int i = 1;i <= sendNum;i++){
                inst = new SrcDtaAsyncRspInst(dtaName,instId,dtaServiceFlow,this);
                threadPoolExecutor.execute(inst);
                instList.add(inst);
            }
            //启动监听超时线程
            scanTimeOutThread = new ScanTimeOutThread(dtaName,dtaServiceFlow);
            scanTimeOutThread.start();
        }
        
        server = new HttpServer[al.size()];
        //监听端口port,能同时接受 maxLinkNum个请求
        try {
            //监听多个地址或端口            
            int i = 0;
            for (IPInfo info : al) {    
                ip = info.getIpAddr();
                port = info.getPort();
                maxNum = info.getMaxConn();
                HttpServer serverTemp = provider.createHttpServer(new InetSocketAddress(ip,
                        port), maxNum);
                SrcHttpHandlerNoURL handler = new SrcHttpHandlerNoURL(dtaName,this);
                handler.setSrcDtaServiceFlow(dtaServiceFlow);
                serverTemp.createContext("/" ,  handler);
//                server.setExecutor(null); // creates a default executor
                serverTemp.setExecutor(threadPool);
                serverTemp.start();
//                System.out.println("启动httpserver服务成功：地址是：" + ip + "----端口："
//                        + port + "---dta名称：" + dtaName);
                server[i] =serverTemp;
                i++;
            }
            String out = "适配器-----" + dtaName + "----启动成功!" + "最小实例 数--[" + minNum + "]" + "--最大实例数--[" + maxNum +"]--监听地址为--";
            for(int j = 0;j < al.size();j++){
                IPInfo info = al.get(j);
                out += info.getIpAddr() + ":" + info.getPort() + "最大连接数--" + info.getMaxConn();
            }
            System.out.println(out);
        } catch (IOException e) {
            e.printStackTrace(BaseLog.getExpOut());
            new ErrLog().errlog(SysErr.E_DTA_SERVICE_LISTEN_FAILED,dtaName,ip,port);
          //启动DTA[%s]的监听服务（IP[%s]:PORT[%s]）失败
            throw new BaseException(SysErr.E_DTA_SERVICE_LISTEN_FAILED,e,dtaName,ip,port);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.IDTA#stop()
     */
    @Override
    public void stop() {
        // TODO Auto-generated method stub
        for(HttpServer ser :server){
            ser.stop(0);
        }
        ParmPool parmPool = PoolOperate.getParmPool();
        ESAdmin admin = parmPool.getEsadmin();
        dupFlag = admin.getDtaParm(dtaName).isDupFlag();
        if(dupFlag){
            List<SrcDtaAsyncRspInst> tempList = new ArrayList<SrcDtaAsyncRspInst>();
            for(SrcDtaAsyncRspInst inst : instList){
                inst.setFlag(false);
                DRQ.getMsgGroup(dtaName).popResponse(inst.getThread());
                tempList.add(inst);
            }
            while(tempList.size() > 0){
                for(SrcDtaAsyncRspInst inst : instList){
                    if(!inst.isState())
                        tempList.remove(inst);
                }
            }
            if(threadPoolExecutor!=null){
                threadPoolExecutor.shutdown();
            }
            if(scanTimeOutThread != null){
                Object object = scanTimeOutThread.getObject();
                synchronized(object){
                    scanTimeOutThread.setFlag(false);
                    object.notify();
                }
            }
        }
        threadPool.shutdown();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.IDTA#init()
     */
    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.IDTA#restart()
     */
    @Override
    public void restart() {
        // TODO Auto-generated method stub
        stop();
        start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adtec.starring.dta.IDTA#listen()
     */
    @Override
    public void listen() {
        // TODO Auto-generated method stub

    }

    
    /* (non-Javadoc)
     * @see com.adtec.starring.dta.IDTA#getDataPoolId()
     */
    public void setDtaServiceFlow(ISrcDtaServiceFlow dtaServiceFlow) {
        this.dtaServiceFlow = dtaServiceFlow;
    }
}
