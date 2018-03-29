package com.adtec.ncps.busi.ncp.autodo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;

public class Auto_day00204 {
	
	
	
	public void clearUpStepOne() throws Exception{
		
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		
		SysPub.appLog("INFO" ,"======数据表清理开始======");

		//由个性语句获取平台状态和日期
		String szSqlStr = "selecr * from t_plat_para";
		DataBaseUtils.queryToElem(szSqlStr, "T_PLAT_PARA", null);
		String plat_stat = (String)(String)EPOper.get(tpID,"T_PLAT_PARA[0].plat_stat");//平台状态
		if(plat_stat == null||plat_stat.isEmpty()||!"1".equals(plat_stat)){
			SysPub.appLog("INFO","系统状态["+plat_stat+"],不能日终!!!");
			return;
		}
		SysPub.appLog("INFO","检查平台状态成功["+plat_stat+"]");
		
		//清理T_AUTO_DATA表
		SysPub.appLog("INFO","清理T_AUTO_DATA表开始......");
		
		//sql清理T_AUTO_DATA表语句
		String szSqlStr1 = "delete from t_auto_data a where not exists(select thd_seq_no from t_auto_rev b where b.thd_seq_no=a.thd_seq_no)";
		//获取数据源
		try{
			
		    DBExecuter executer = DataBaseUtils.conn();
		    iRet = DataBaseUtils.executenotr(executer, szSqlStr1, null);
		    if(iRet<0){
		    	iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新数据库错误");
				return ;
		    }
		    iRet = DataBaseUtils.commit(executer);
			SysPub.appLog("INFO","清理T_AUTO_DATA表记录数["+iRet+"]");
			
		}catch(SQLException e){
			SysPub.appLog("ERROR",e.getMessage());
			throw e;
	    }
		SysPub.appLog("INFO","清理T_AUTO_DATA表成功");	
	}
	
	
	public void clearUpStepTwo() throws Exception{
		
		int N = 512;
		int count = 0;
		boolean flag = false;
		String ErrMsg="";
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		
		//迁移和整理T_MOV_TAB表
		SysPub.appLog("INFO","循环处理T_MOV_TAB表数据开始。。。。。。");
		//获取数据源
		DataSource datasource =  DataBaseUtils.getDatasource();
		//创建数据库连接
		Connection connection = null;
		//创建sql语句执行对象
		Statement stmt = null;
		Statement stmt1 = null;
		//sql语句
	    String sql = "select * from t_mov_tab where stat <> '00' order by seq";
		
		try{
			connection = datasource.getConnection();
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			ResultSet rsremit = stmt.executeQuery(sql);
			
			while(rsremit.next()){
				count++;
				String seq = rsremit.getString("seq");//序号
				String stat = rsremit.getString("stat");//清理标志
				String src_tab = rsremit.getString("src_tab");//源表名
				String bak_tab = rsremit.getString("bak_tab");//备份表名
				String move_sql = rsremit.getString("move_sql");//其他清理条件
				String clr_sql = rsremit.getString("clr_sql");//清理条件
				String expr = rsremit.getString("expr");//条件混合值
				
				SysPub.appLog("INFO","对表["+src_tab+"]数据进行转移");
				SysPub.appLog("INFO","条件混合值expr："+expr);
				
				//stat的长度不等于2，则跳过
				if(stat.length() != 2 ){
					SysPub.appLog("INFO","序号["+seq+"]stat["+stat+"]长度不为2,不处理");
					continue;
				}
				
				//分解条件混合值
				String[] part = new String[N];
				if(!expr.isEmpty()){
					part = expr.split("\\|");
				}
				String part1 = part[0];//日期字段
				String part2 = part[1];//间隔天数
				String part3 = part[2];//取余函数中被除数,分批处理时使用
				
				//根据间隔天数，取得日期
				String ChkDate = new String();
				String plat_date  =(String)EPOper.get(tpID,"T_PLAT_PARA[0].plat_date");//平台时间
				if(!part2.isEmpty()){
					int num = Integer.parseInt(part2);
					//根据间隔天数取得的日期
					ChkDate = PubTool.calDateAdd(plat_date, "yyyyMMdd", -1*num*24*60*60);
					SysPub.appLog("INFO","根据间隔天数取得的日期："+ChkDate);
				}
				
				stmt1 = connection.createStatement();
				//根据条件拼接sql语句
				//第一位为1表示转移和删除数据
				
				if("1".equals(stat.substring(0,1))){
					String tmp = new String();
					String sql1 = "insert into "+bak_tab+ " select * from "+src_tab+" where 1=1";
					String sql2 = "delete from "+src_tab+" where 1=1";
					
					if(expr.length() != 0){
						tmp = " and "+part1+" < '"+ChkDate+"'";//平台日期
					}
					if(move_sql.length() != 0)
					{
						tmp += " and "+move_sql;//其他条件
					}
					sql1 += tmp;//拼接转移sql
					sql2 += tmp;//拼接删除sql
					SysPub.appLog("INFO","转移sql:"+sql1);
					SysPub.appLog("INFO","删除sq2:"+sql2);
					
					//需要分批处理
					int num1 = Integer.parseInt(part3);
					if(clr_sql.length() != 0){
						int iret1 = 0;
						int iret2 = 0;
						for(int i = 0;i < num1;i++){
							String sql11 = sql1+" and mod("+clr_sql+", "+num1+")="+i;
							SysPub.appLog("INFO","分批转移sql:"+sql11);
							iret1 = stmt1.executeUpdate(sql11);
							
							String sql22 = sql2+" and mod("+clr_sql+", "+num1+")="+i;
							TrcLog.log("INFO","分批删除sq2:"+sql22);
							iret2 = stmt1.executeUpdate(sql22);
							SysPub.appLog("INFO","转移记录数["+iret1+"]删除记录数["+iret2+"]");
						}
						SysPub.appLog("INFO","对表["+src_tab+"]数据进行分批转移和删除成功");
					}
					else{
						int iret1 = 0;
						int iret2 = 0;
						iret1 = stmt1.executeUpdate(sql1);
						iret2 = stmt1.executeUpdate(sql2);
						SysPub.appLog("INFO","对表["+src_tab+"]数据进行转移和删除成功,转移记录数["+iret1+"]删除记录数["+iret2+"]");
					}
				}
				
				//第二位为1表示只删除数据
				if("1".equals(stat.substring(1,stat.length()))){
					String tmp = new String();
                    String sql2 = "delete from "+src_tab+" where 1=1";
					
					if(expr.length() != 0){
						tmp = " and "+part1+" < '"+ChkDate+"'";//平台日期
					}
					if(move_sql.length() != 0)
					{
						tmp += "and "+move_sql;//其他条件
					}
					sql2 += tmp;//拼接删除sql
					SysPub.appLog("INFO","删除sql:"+sql2);
					//需要分批处理
					int num1 = Integer.parseInt(part3);
					if(clr_sql.length() != 0){
						for(int i = 0;i < num1;i++){
							int iret2 = 0;
							String sql22 =sql2+" and mod("+clr_sql+", "+num1+")="+i;
							iret2 = stmt1.executeUpdate(sql22);
							SysPub.appLog("INFO","分批删除数据记录数["+iret2+"]");
						}
						SysPub.appLog("INFO","对表["+src_tab+"]数据进行分批删除成功");
					}
					else{
						int iret2 = 0;
						iret2 = stmt1.executeUpdate(sql2);
						SysPub.appLog("INFO","对表["+src_tab+"]数据进行删除成功,记录数:"+iret2);
					}
				}
				connection.commit();
			}
			SysPub.appLog("INFO","循环处理T_MOV_TAB表数据结束,记录数:"+count);
			SysPub.appLog("INFO",  "======数据表清理结束======");
		}catch(SQLException e){
			try{
				connection.rollback(); 
				e.printStackTrace();
				ErrMsg = e.getMessage();
				SysPub.appLog("INFO","SQL执行出错:"+e.getMessage());
			}catch(SQLException e1){
				e1.printStackTrace();
			}
		}finally{
			if(stmt != null){
				try{
					stmt.close();
				}catch(Exception e){
					e.printStackTrace(BaseLog.getExpOut());	
				}
			}
			if(stmt1 != null){
				try{
					stmt1.close();
				}catch(Exception e){
					e.printStackTrace(BaseLog.getExpOut());	
				}
			}
			if(connection != null){
				try {
					DataSourceUtils.doReleaseConnection(connection, datasource);
				} catch (Exception e) {
					e.printStackTrace(BaseLog.getExpOut());
				}
			}	
		}
    }
}
