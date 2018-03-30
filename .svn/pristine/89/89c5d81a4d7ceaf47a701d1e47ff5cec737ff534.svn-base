package com.adtec.tcp;

import java.io.File;



import teadapter.DataHandle;
import teadapter.UPNODE_INFO;
import teadapter.tongeasy;

public class TongEasyFileTran {
	
	//定义静态实例
    public static TongEasyFileTran Instance =null;
   
    /// <summary>
    ///静态实例初始化函数
    /// </summary>
    static public TongEasyFileTran GetInstance()
    {
        if (Instance == null)
        {
            Instance = new TongEasyFileTran();
        }
        return Instance;
    }
    
	//tongeasy对象
	private tongeasy TongEasy=null;
	//UPNODE_INFO对象
	private UPNODE_INFO uInfo=null;
	//

	/**
	 * 初始化与tongeasy服务器的连接
	 * @return TE_tpinit返回的全局变量地址空间地址指针
	 */
	public long TongEasyInit()
	{	
		//TE_tpinit成功后返回的全局变量地址空间地址指针 
		long id=0;	      
		try
		{
			//只初始化一次，防止多次调用导致tomcat死掉
			if (TongEasy ==null)
			{
				//初始化tongeasy对象
				TongEasy=new tongeasy();		
				//TongEasy初始化
				TongEasy.TEAppInit();
			}
		}
		catch(Exception e)
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Error, "TongEasyInit错误信息："+e.getMessage());
			return id;
		}		
		
		//初始化UPNODE_INFO对象
		uInfo=new UPNODE_INFO();	
		
		//上级结点名
		uInfo.UName[0]="";
		//端口号
		uInfo.UPort[0]=Integer.parseInt("50011");
		
		//初始一笔事务
		id=TongEasy.TE_tpinit(0, 0, uInfo);
		//初始化失败
		if(id==0)
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Error, "TE_tpinit失败！");
			return id;
		}		
		return id;
	}
	
	/**
	 * 关闭与tongeasy服务器的连接
	 * @param id	连接ID号
	 */
	public void TongEasyClose(long id)
	{
		TongEasy.TE_tpterm(id);
		//TongEasy.TEAppTerm();
		id=0;
		//TongEasy=null;
		uInfo=null;
	}
	
	/**
	 * 向tongeasy服务器发送数据
	 * @param IntegrateData		需要发送的数据
	 * @param id		连接ID号
	 * @return tongeasy服务器返回的数据
	 */
	public byte[] SendData(byte[] IntegrateData,long id)
	{
		//CLogWrite.GetInstance().WriteLogfile(Level.Debug,"调用SendData开始");
		//test start
		//CLogWrite.GetInstance().WriteLogfile(Level.Debug,"事务"+id+"SendData开始");
		//test end
		//事务标志，用于设定事务属性，取值为下面定义的各宏的一个或多个的组合
		//PKTIS_XA:XA 事务
		//P_ACKKEENACKAND：确认需要确认应答
		//P_MST_NEEDACK:需要确认
		int flag=0;
		//发送的数据的长度
		int dataLen=0;
		//函数返回值
		int res=0;
		//DataHandle对象
		DataHandle dataHandle=null;
		
		//edit by Liucb start 201009008 标志位如果为P_MST_NEEDACK会导致后台不释放资源
		//flag=TongEasy.P_MST_NEEDACK;
		flag=TongEasy.PKTNEEDANS|TongEasy.TENEEDNSFWD;
		//edit by Liucb end 201009008
		//开始一个事务
		int TID=TongEasy.TE_tpbegin(flag, 30, id);				
		if(TID >=0)
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpbegin success "+TID);
		}
		else
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendData--TE_tpbegin Fail rtn:"+TID);
			return null;
		}
		//初始化DataHandle对象
		dataHandle=new DataHandle();
		
		//事务处理										
		//设置要发送的数据
		dataHandle.SetSendData(IntegrateData);
		//要发送的数据的长度
		dataLen=IntegrateData.length;
		
		//发送或者接收一笔业务
		TongEasy.TE_SetBranchMsg(flag, 30, id);
		res=TongEasy.TE_tpcall("_balmain", dataLen, dataHandle, id);		
		if(res >=0)
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Debug,"TE_tpcall Sucess rtn:"+res);
		}
		else
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Error,"SendData--TE_tpcall Fail rtn:"+res);
			res = TongEasy.TE_tpabort(id);
			//if(res < 0)
				//CLogWrite.GetInstance().WriteLogfile(Level.Error,"SendData--TE_tpabort Fail rtn:"+res);
			return null;
		}
			
		//获取返回的数据
		byte[] resultData=dataHandle.GetRecvData();	
		//System.out.println(resultData.length);
	   
		//提交事务结果
		res=TongEasy.TE_tpcommit(id);			
		if(res!=0)
		{
			//CLogWrite.GetInstance().WriteLogfile(Level.Error , "SendData--TE_tpcommit失败！res="+res);
			return null;
		}
		else
		{
		//	CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpcommit成功！");		
		}	
		//test start
		//CLogWrite.GetInstance().WriteLogfile(Level.Debug,"事务"+id+"SendData结束");
		//test end
	//	CLogWrite.GetInstance().WriteLogfile(Level.Debug,"调用SendData结束！");
		return resultData;
	}	
	
	/**
	 * 发送附件
	 * @param filename		发送的附件名
	 * @return	是否成功
	 */
	 public boolean SendFile(String filename,long id)
	 {
		//CLogWrite.GetInstance().WriteLogfile(Level.Debug,"调用SendFile开始");
		//test start
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,"事务"+id+"SendFile开始");
//		//test end
//		ForXmlServiceImpl xmlServiceImpl=  new ForXmlServiceImpl();
//		
//		if(filename==null)
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error,
//					"发送文件附件不成功，filename=null");
//			return false;
//		}
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TONGEASY模式上传文件名称"+CComDef.ConfigPath + "tmp/" + filename);
//		File file = new File(CComDef.ConfigPath + "tmp/" + filename);	
//		//察看文件是否存在
//		if(!file.exists())
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error,
//					"发送文件附件不存在，filename=" + filename);
//			return false;
//		}	
//		//文件长度
//		int filesize=(int)file.length();
//		//文件修改时间
//		int modtime=(int)(file.lastModified()/1000);	
//		
//		byte[] header=GetTongEasyHeader('2',filename,filesize,modtime,0,filesize,'1');
//		if(header==null)
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendFile获得文件头部错误");
//			return false;
//		}	
//		//获得文件文件数据
//		byte[] data= xmlServiceImpl.GetFiletxt("tmp/" + filename);
//		
//		if(data ==null)
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error,
//					"发送文件附件数据为空，filename=" + filename);
//			return false;
//		}
//		
//		//组合后字节数组  文件头+文件内容
//		byte[] sendData=new byte[header.length+data.length];
//		
//		System.arraycopy(header, 0, sendData, 0, header.length);
//		System.arraycopy(data, 0, sendData, header.length,data.length);
//		
//		/******组包结束 开始调用 tongeasy函数进行发包********/
//		//事务标志，用于设定事务属性，取值为下面定义的各宏的一个或多个的组合
//		//PKTIS_XA:XA 事务
//		//P_ACKKEENACKAND：确认需要确认应答
//		//P_MST_NEEDACK:需要确认
//		int flag=0;
//		//函数返回值
//		int res=0;
//		//发送的数据长度
//		int dataLen=0;
//		//DataHandle对象
//		DataHandle dataHandle=new DataHandle();
//	
//		//事务处理
//		try
//		{
//			//edit by Liucb start 201009008 标志位如果为P_MST_NEEDACK会导致后台不释放资源
//			//flag=TongEasy.P_MST_NEEDACK;
//			flag=TongEasy.PKTNEEDANS|TongEasy.TENEEDNSFWD;
//			//edit by Liucb end 201009008
//			int TID=TongEasy.TE_tpbegin(flag, CComDef.TimeOut, id);
//			if(TID >=0)
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpbegin sucess rtn:"+TID);
//			}
//			else
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendFile--TE_tpbegin Fail rtn:"+TID);
//				return false;
//			}	
//			
//			//设置发送数据
//			dataHandle.SetSendData(sendData);
//			dataLen=sendData.length;
//			
//			TongEasy.TE_SetBranchMsg(flag, CComDef.TimeOut, id);
//			res=TongEasy.TE_tpcall(CComDef.ftpServiceName, dataLen, dataHandle, id);
//			if(res >=0)
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpcall Sucess rtn:"+res);
//			}
//			else
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendFile--TE_tpcall Fail rtn:"+res);
//				res = TongEasy.TE_tpabort(id);
//				if(res < 0)
//					CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendFile--TE_tpabort Fail rtn:"+res);
//				return false;
//			}
//			
//			res=TongEasy.TE_tpcommit(id);
//			if(res!=0)
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendFile--TE_tpcommit失败！res="+res);
//				return false;
//			}
//			else
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpcommit成功！");
//		}
//		catch(Exception e)
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error, "SendFile 错误信息："+e.getMessage());
//			return false;
//		}
//		finally
//		{
//			dataHandle=null;
//		}	
//		//test start
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,"事务"+id+"SendFile结束");
//		//test end
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,"调用SendFile结束！");
		return true;
	 }
	 
	 /**
     * 接收文件附件
     * @param filename
     * @return
     */
    public byte[] RecvFile(String filename,long id)
    {
//    	CLogWrite.GetInstance().WriteLogfile(Level.Debug,"调用RecvFile开始！");
//		//test start
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,"事务"+id+"RecvFile开始");
//		//test end
//		byte[] header=GetTongEasyHeader('1',filename,0,0,0,0,'0');
//		if(header==null)
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error,"RecvFile获得文件头部错误");
//			return null;
//		}
//		
//		//组合后字节数组  文件头+文件内容
//		byte[] sendData=new byte[header.length];
//		
//		System.arraycopy(header, 0, sendData, 0, header.length);
//		
//		/******组包结束 开始调用 tongeasy函数进行发包********/
//		//事务标志，用于设定事务属性，取值为下面定义的各宏的一个或多个的组合
//		//PKTIS_XA:XA 事务
//		//P_ACKKEENACKAND：确认需要确认应答
//		//P_MST_NEEDACK:需要确认
//		int flag=0;
//		//函数返回值
//		int res=0;
//		//发送的数据的长度
//		int dataLen=0;
//		//返回的数据
		byte[] resultData=null;
//		//DataHandle对象
//		DataHandle dataHandle=new DataHandle();
//		try
//		{		
//			//edit by Liucb start 201009008 标志位如果为P_MST_NEEDACK会导致后台不释放资源
//			//flag=TongEasy.P_MST_NEEDACK;	
//			flag=TongEasy.PKTNEEDANS|TongEasy.TENEEDNSFWD;
//			//edit by Liucb end 201009008
//			//开始一个事务
//			int TID=TongEasy.TE_tpbegin(flag, CComDef.TimeOut, id);				
//			if(TID >=0)
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpbegin success "+TID);
//			}
//			else
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Error, "RecvFile--TE_tpbegin Fail rtn:"+TID);
//				return null;
//			}
//			
//			//事务循环				
//			//设置发送数据
//			dataHandle.SetSendData(sendData);
//			//设置发送数据的长度
//			dataLen=sendData.length;
//			
//			//发送或者接收一笔事务
//			TongEasy.TE_SetBranchMsg(flag, CComDef.TimeOut, id);
//			res=TongEasy.TE_tpcall(CComDef.ftpServiceName, dataLen, dataHandle, id);
//			if(res >=0)
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpcall Sucess rtn:"+res);
//			}
//			else
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Error, "RecvFile--TE_tpcall Fail rtn:"+res);
//				res = TongEasy.TE_tpabort(id);
//				if(res < 0)
//					CLogWrite.GetInstance().WriteLogfile(Level.Error, "RecvFile--TE_tpabort Fail rtn:"+res);
//				return null;
//			}
//			//获取tongeasy服务器返回的数据
//			resultData=dataHandle.GetRecvData();
//			
//			//提交事务结果
//			res=TongEasy.TE_tpcommit(id);
//			if(res!=0)
//			{
//				CLogWrite.GetInstance().WriteLogfile(Level.Error, "RecvFile--TE_tpcommit失败！res="+res);
//				return null;
//			}
//			else
//				CLogWrite.GetInstance().WriteLogfile(Level.Debug, "TE_tpcommit成功！");
//		}
//		catch(Exception ex)
//		{
//			CLogWrite.GetInstance().WriteLogfile(Level.Error, "RecvFile 错误信息："+ex.getMessage());
//			return null;
//		}
//		finally
//		{	
//			dataHandle=null;
//		}
//		//test start
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,"事务"+id+"RecvFile结束");
//		//test end
//		CLogWrite.GetInstance().WriteLogfile(Level.Debug,"调用RecvFile结束！");
		return resultData;
    }
    
	 /**
	 * @param mesgtype		请求种类:0-查询,1-下载,2-上传   1位
	 * @param name			文件名                       129位
	 * @param filesize		文件大小                      4位
	 * @param modtime       修改时间                      4位
	 * @param offset        文件偏移量                    4位
	 * @param datasize      需要的数据长度                 4位
	 * @param flag          写文件标志,0-普通,1-覆盖        1位
	 * @return				文件头字节数组
	 * 功能：生成长度为147位的文件头
	 */
	 byte[] GetTongEasyHeader(char mesgtype,String name,int filesize,int modtime,int offset,int datasize,char flag)
	 {		 
		byte[] header=new byte[152];
		byte[] tmp=null;
		//将mesgtype信息加入头字节数组 起始位置0 长度 1
		header[0]=(byte)mesgtype;
		
		//将name信息加入头字节数组 起始位置1 长度 129
		if(name!=null)
		{//文件名存在
			tmp=name.getBytes();
			System.arraycopy(tmp, 0, header,1, tmp.length);
			for(int j=0;j<tmp.length;j++)
			{
				if(j%10==0)
				{
					System.out.println();
				}
				System.out.print("  "+j+":"+header[j]+"  "+(char)header[j]+"  ");
			}
		}
		else
		{//文件名不存在
			return null;
		}			
		//将filessize信息加入头字节数组 起始位置130 长度 4		
		tmp=toByteArray(filesize);
		if(tmp!=null)
		{
			System.arraycopy(tmp, 0, header,132, tmp.length);
		}			
		//将modtime信息加入头字节数组 起始位置134 长度 4		
		tmp=toByteArray(modtime);
		if(tmp!=null)
		{
			System.arraycopy(tmp, 0, header,136, tmp.length);
			
		}			
		//将offset信息加入头字节数组 起始位置138 长度 4		
		tmp=toByteArray(offset);
		if(tmp!=null)
		{
			System.arraycopy(tmp, 0, header,140, tmp.length);
		}			
		//将datasize信息加入头字节数组 起始位置142 长度 4		
		tmp=toByteArray(datasize);
		if(tmp!=null)
		{
			System.arraycopy(tmp, 0, header,144, tmp.length);
		}				
		//将flag信息加入头字节数组 起始位置146 长度 1
		header[148]=(byte)flag;
			
		return header;
	}
	 
	 /**
	 *@功能： 整型数字转化为四字节的字节数组
	 */
	  static   byte[]  toByteArray(int   number)   
	 {   
		 int   temp   =   number;   
		 byte[]   b=new   byte[4];   
		 for   (int   i   =   b.length   -   1;   i   >   -1;   i--)   
		 {   
			 b[i]   =   new   Integer(temp   &   0xff).byteValue();   
			 temp   =   temp   >>   8;   
		 }   
		 return   b;   
	  }   
	
	 /**
	  * @功能： 将四字节的字节数组转化为整型数字
	  */
	  static   int   toInteger(byte[]   b)   
	  {   
		  int   s   =   0;   
		  for   (int   i   =   0;   i   <   3;   i++)   
		  {   
			  if   (b[i]   >=   0)   
				  s   =   s   +   b[i];   
			  else   
				  s   =   s   +   256   +   b[i];   
			  s   =   s   *   256;   
		  }   
		  if   (b[3]   >=   0)   
			  s   =   s   +   b[3];   
		  else   
			  s   =   s   +   256   +   b[3];   
		  return   s;   
	  }   
	  
}