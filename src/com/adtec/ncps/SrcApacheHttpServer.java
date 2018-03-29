package com.adtec.ncps;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.dta.BaseDTA;
import com.adtec.starring.dta.IDTA;
import com.adtec.starring.dta.ISrcDtaServiceFlow;
import com.adtec.starring.dta.ScanTimeOutThread;
import com.adtec.starring.dta.SrcDtaAsyncRspInst;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.log.ErrLog;
import com.adtec.starring.log.TransLog;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.PoolOperate;
import com.adtec.starring.struct.admin.IPInfo;

public class SrcApacheHttpServer extends BaseDTA implements IDTA {

    private List<SrcDtaAsyncRspInst> instList = new ArrayList<SrcDtaAsyncRspInst>();
    private ScanTimeOutThread scanTimeOutThread;
    private ThreadPoolExecutor threadPoolExecutor;
    private RequestListenerThread server[];
    private ThreadPoolExecutor threadPool;
    		
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
        
        List<IPInfo> al = new ArrayList<IPInfo>();
        for(IPInfo info:addressList){
            int portTemp = info.getPort();
            if(portTemp!=-1){
                al.add(info);
            }else{
                new ErrLog().errlog(SysPubDef.CUP_ERR_RET, "监听地址为["+info.getIpAddr()+"]的服务，端口是["+portTemp+"]启动失败！"); 
                TransLog.getInstance().error("监听地址为["+info.getIpAddr()+"]的服务，端口是["+portTemp+"]启动失败！"); 
            }
        }
        
        if(dupFlag){//如果是异步全双工
            threadPoolExecutor = new ThreadPoolExecutor(sendNum, sendNum, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
                        (sendNum + sendNum) / 2),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
                    public void rejectedExecution(Runnable r,
                            ThreadPoolExecutor executor) {
                        new ErrLog().errlog(SysErr.E_THREADPOOL_FULL);
                    }
                });
            SrcDtaAsyncRspInst inst ;
            for(Long instId:cacheRspMap.keySet()){
                inst = new SrcDtaAsyncRspInst(dtaName,instId,dtaServiceFlow,this);
                threadPoolExecutor.execute(inst);
                instList.add(inst);
            }
            //启动监听超时线程
            scanTimeOutThread = new ScanTimeOutThread(dtaName,dtaServiceFlow);
            scanTimeOutThread.start();
        }
        
        
        try {
            //监听多个地址或端口
            int i = 0;
            server = new RequestListenerThread[al.size()];
            for (IPInfo info : al) {    
                ip = info.getIpAddr();
                port = info.getPort();
                //maxNum = info.getMaxConn();
                RequestListenerThread t = null;
        		try {

        			long timeout = PoolOperate.getCurrVerResPool().getDtaHashMap().get(dtaName).getDtaParm().getTimeOut();
        			t = new RequestListenerThread(dtaServiceFlow,this, port, maxNum, InetAddress.getByName(ip), dtaName, timeout,threadPool);
                    
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
                    e.printStackTrace(BaseLog.getExpOut());
        			throw e;
        		}
        		t.setDaemon(false);
        		t.start();
        		server[i] = t;
                i++;
            }
            String out = "适配器-----" + dtaName + "----启动成功!" + "最小实例数--[" + minNum + "]" + "--最大实例数--[" + maxNum +"]--监听地址为--";
            for(int j = 0;j < al.size();j++){
                IPInfo info = al.get(j);
                out += info.getIpAddr() + ":" + info.getPort() + "最大连接数--" + info.getMaxConn();
            }
            System.out.println(out);
        } catch (Exception e) {
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
        for(RequestListenerThread th :server){
//        	th.interrupt();
        	th.stopServer();
        }
        threadPool.shutdown();
    }


	static class RequestListenerThread extends Thread {

		private final ServerSocket serversocket;
		private final HttpParams params;
		private final HttpService httpService;
		private WorkerThread workThead;
		private String dtaName;
        private ThreadPoolExecutor threadPool;
        
        public ThreadPoolExecutor getThreadPool() {
            return threadPool;
        }


        public void setThreadPool(ThreadPoolExecutor threadPool) {
            this.threadPool = threadPool;
        }


		public void stopServer(){
			if(workThead != null){
				workThead.setRunStat(false);
        	}
			try {
				serversocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.interrupt();
		}
		
		
		public WorkerThread getWorkThead() {
			return workThead;
		}

		public void setWorkThead(WorkerThread workThead) {
			this.workThead = workThead;
		}

		public String getDtaName() {
			return dtaName;
		}

		public void setDtaName(String dtaName) {
			this.dtaName = dtaName;
		}

		public RequestListenerThread(ISrcDtaServiceFlow dtaServiceFlow, 
				SrcApacheHttpServer srcApacheHttpServer, int port, int maxNum, 
				InetAddress addr, String dtaName, long timeout, ThreadPoolExecutor threadPool)
				throws IOException {
			this.serversocket = new ServerSocket(port, maxNum, addr);
			this.params = new BasicHttpParams();
			this.threadPool = threadPool;
			this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.valueOf(Long.toString(timeout*1000)))
					.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 20 * 1024)
					.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
					.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
					.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");
			// Set up the HTTP protocol processor
			BasicHttpProcessor httpproc = new BasicHttpProcessor();
			httpproc.addInterceptor(new ResponseDate());
			httpproc.addInterceptor(new ResponseServer());
			httpproc.addInterceptor(new ResponseContent());
			httpproc.addInterceptor(new ResponseConnControl());
			
			setDtaName(dtaName);
			// Set up request handlers
			HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();

            SrcApacheHttpHandler myhandler = new SrcApacheHttpHandler(dtaName,srcApacheHttpServer);
            myhandler.setSrcDtaServiceFlow(dtaServiceFlow);
			reqistry.register("*", myhandler);

			// Set up the HTTP service
			this.httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(),
					new DefaultHttpResponseFactory());
			this.httpService.setParams(this.params);
			this.httpService.setHandlerResolver(reqistry);
		}

		public void run() {

	        Thread thread = Thread.currentThread();
	        thread.setName(dtaName + " apacheHttpServer thread id is: " + thread.getId());
			while (!Thread.interrupted()) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					
					//TrcLog.log("comm.log", "RemoteSocketAddress=%s",socket.getRemoteSocketAddress().toString());
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					conn.bind(socket, this.params);

					// Start worker thread
					WorkerThread workThead = new WorkerThread(this.httpService, conn, dtaName);
					threadPool.execute(workThead);
                    
				} catch (Exception e) {
					throw new BaseException(SysErr.E_MESSAGE,"DTA[" + getDtaName() + "]异常：" + e.getMessage());
				}
			}
		}
	}

	static class WorkerThread implements Runnable {

		private final HttpService httpservice;
		private final HttpServerConnection conn;
		private boolean runStat = true;
		private String dtaName;

		
		public String getDtaName() {
			return dtaName;
		}

		public void setDtaName(String dtaName) {
			this.dtaName = dtaName;
		}

		public HttpServerConnection getConn() {
			return conn;
		}

		public boolean isRunStat() {
			return runStat;
		}

		public void setRunStat(boolean runStat) {
			this.runStat = runStat;
		}

		public WorkerThread(final HttpService httpservice, final HttpServerConnection conn, String dtaName) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
			this.dtaName = dtaName;
		}

		public void run() {
	        Thread thread = Thread.currentThread();
	        thread.setName(dtaName + " thread id is: " + thread.getId());
			HttpContext context = new BasicHttpContext(null);
			try {
                if (!Thread.interrupted() && this.conn.isOpen() && runStat) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                ex.printStackTrace(BaseLog.getExpOut());
                throw new BaseException(SysErr.E_DTA_JMS_RECVERR,ex,"DTA[" + dtaName + 
                        "]异常：Client closed connection " + ex.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace(BaseLog.getExpOut());
                throw new BaseException(SysErr.E_DTA_JMS_RECVERR,ex,"DTA[" + dtaName + 
                        "]异常：I/O error " + ex.getMessage());
            } catch (HttpException ex) {
                ex.printStackTrace(BaseLog.getExpOut());
                throw new BaseException(SysErr.E_DTA_JMS_RECVERR,ex,"DTA[" + dtaName + 
                        "]异常：Unrecoverable HTTP protocol violation " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                    Thread.currentThread().interrupt();
                } catch (IOException ignore) {
                    ignore.printStackTrace(BaseLog.getExpOut());
                    throw new BaseException(SysErr.E_MESSAGE, dtaName + 
                            "：" + ignore.getMessage());
                }
            }
		}
	}
}