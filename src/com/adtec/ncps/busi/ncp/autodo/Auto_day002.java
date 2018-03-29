package com.adtec.ncps.busi.ncp.autodo;

import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.struct.dta.DtaInfo;

public class Auto_day002 {
	public boolean chkBatFlag() throws Exception{
		int iRet = 0;
		boolean flag = false;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try{
			SysPub.appLog("INFO", "========================================日终开始======================================");
			String bat_flag = (String)EPOper.get(tpID,"T_END_STEP.bat_flag");
			String bat_name = (String)EPOper.get(tpID,"T_END_STEP.bat_name");
			int bat_no = Integer.parseInt((String)EPOper.get(tpID,"T_END_STEP.bat_no"));
			
			if(!"Y".equals(bat_flag)){
				
				bat_no = bat_no+1;
				DBExecuter executer = DataBaseUtils.conn();
				String szSqlStr = "update t_plat_para set dayend_flag = bat_no";
				iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
				if(iRet <= 0){
					iRet = DataBaseUtils.rollback(executer);
					SysPub.appLog("ERROR", "更新数据库错误");
					return flag;
				}
				iRet = DataBaseUtils.commit(executer);	
				SysPub.appLog("INFO", "步骤["+bat_no+"]["+bat_name+"]为无效["+bat_flag+"],跳过该步骤");
				flag = true;
			}
			}catch(Exception e){
				SysPub.appLog("ERROR", e.getMessage());
				throw e;
			}
			return flag;
		}
  
	
	public void chkEndStepStat() throws Exception{
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		
		String bat_stat = (String)EPOper.get(tpID,"T_END_STEP.bat_stat");
		String bat_name = (String)EPOper.get(tpID,"T_END_STEP.bat_name");
		int bat_no = Integer.parseInt((String)EPOper.get(tpID,"T_END_STEP.bat_no"));
		
	    if("P".equals(bat_stat)){
			SysPub.appLog("INFO", "步骤["+bat_no+"]["+bat_name+"]正在处理中");
			return;
		}
		else if("N".equals(bat_stat)){
			SysPub.appLog("INFO", "步骤["+bat_no+"]["+bat_name+"]已经处理失败，请检查原因!!!");
			return;
		}
		else if(!"I".equals(bat_stat)){
			SysPub.appLog("INFO", "步骤["+bat_no+"]["+bat_name+"]状态["+bat_stat+"]不为初始状态I!!!");
			return;
		}
		else if("I".equals(bat_stat)){
		    bat_stat = "P";
		    String err_msg = "正在处理中";
		    String beg_date = PubTool.getDate8();
		    String beg_time = PubTool.getTime();
		    String szSqlStr = "update t_end_step set bat_stat = ?"//
		    		          +", err_msg = ?, beg_date = ?, beg_time = ?";
		    Object[] value = new Object[4];
		    value[0] = bat_stat;
		    value[1] = err_msg;
		    value[2] = beg_date;
		    value[3] = beg_time;                                     
		    
		    try{
		    	DBExecuter executer = DataBaseUtils.conn();
		    	iRet = DataBaseUtils.executenotr(executer, szSqlStr, value);
		    	if(iRet <= 0){
		    		iRet = DataBaseUtils.rollback(executer);
					SysPub.appLog("ERROR", "更新数据库错误");
					return ;
		    	}
		    	iRet = DataBaseUtils.commit(executer);
		    }catch(Exception e){
		    	SysPub.appLog("ERROR", e.getMessage());
		    }
		}
		SysPub.appLog("INFO",  "将会执行日终步骤:"+bat_no+"]["+bat_name+"]");
	}
}
