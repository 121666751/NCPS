package com.adtec.ncps.busi.ncp.chk;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/**
 * 对账差错
 * @author GuoFan
 *
 */
public class SCHK00010108 {
	/**
	 * 对账差错批量调账前查询
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void chkErrTranQry() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szLqdDate = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010108[0].LqdDate");// 清算日期
		String szTranName = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010108[0].TranName");// 交易名称
		String szErrType = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010108[0].ErrType");// 差错类型 对应表字段公司编号
		String szErrStat = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010108[0].ErrStat");// 差错确认状态
		// 检查查询条件
		if (StringTool.isNullOrEmpty(szLqdDate)) {
			SysPub.appLog("ERROR", "清算日期为空");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "清算日期不能为空");
			return;
		}else{
			szLqdDate = szLqdDate.substring(0,4)+"-"+szLqdDate.substring(4,6)+"-"+szLqdDate.substring(6,8);
		}

		// 查询记录数
		int iRowNum = ChkPub.getRowNum(tpID, "MngChkIn[0].SCHK00010108");
		// 查询起始记录数
		int szBeginRec = 0;
		String beginRec = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010108[0].BeginRec");
		if (StringTool.isNullOrEmpty(beginRec)){
			szBeginRec = 0;
		}else{
			szBeginRec = Integer.parseInt(beginRec.trim());
		}	
		szLqdDate = szLqdDate.trim();
		szTranName = szTranName.trim();
		szErrType = szErrType.trim();
		szErrStat = szErrStat.trim();
		SysPub.appLog("DEBUG", "szLqdDate=%s, szErrType=%s, szErrStat=%s, iRowNum=%s, szBeginRec=%s",szLqdDate, szErrType, szErrStat, iRowNum, szBeginRec);
		//银联对应业务类型为035001
		if( "2".equals(szErrType) )
		{
			szErrType = "035001";
		}
		int iCount = -1;
		int iTotCount = 0;
		String sql = "";
		String sqlTot = "";
		if(szTranName==null||"".equals(szTranName.trim())){
			if(szErrStat==null||"".equals(szErrStat.trim())){
				sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
						+ "brch_no,teller_no,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
						+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
						+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
						+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,"
						+ "amt1,amt2,amt3,num1,num2,num3,str1,str2,str3 "
						+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and (err_flag = 'N' or err_flag = 'B') "
						+ "and rownum <= ? order by clear_date asc ) where r >= ?";
				sqlTot = "select * from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and (err_flag = 'N' or err_flag = 'B') "
						+ "order by clear_date asc ";
				//SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
				Object[] obj = { szLqdDate, szErrType, iRowNum, szBeginRec };
				
				iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);
				Object[] objTol = { szLqdDate, szErrType};
				iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
			}else{
				sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
						+ "brch_no,teller_no,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
						+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
						+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
						+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,"
						+ "amt1,amt2,amt3,num1,num2,num3,str1,str2,str3 "
						+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and err_flag = ? "
						+ "and rownum <= ? order by clear_date asc ) where r >= ?";
				sqlTot = "select * from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and err_flag = ? "
						+ "order by clear_date asc ";
				//SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
				Object[] obj = { szLqdDate, szErrType, szErrStat,iRowNum, szBeginRec };
				
				iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);
				Object[] objTol = { szLqdDate, szErrType, szErrStat};
				iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
			}
		}else{
			if(szErrStat==null||"".equals(szErrStat.trim())){
				sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
						+ "brch_no,teller_no,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
						+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
						+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
						+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,"
						+ "amt1,amt2,amt3,num1,num2,num3,str1,str2,str3 "
						+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and tx_code = ? and (err_flag = 'N' or err_flag = 'B') "
						+ "and rownum <= ? order by clear_date asc ) where r >= ?";
				sqlTot = "select * from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and tx_code = ? and (err_flag = 'N' or err_flag = 'B') "
						+ "order by clear_date asc ";
				//SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
				Object[] obj = { szLqdDate, szErrType,szTranName, iRowNum, szBeginRec };
				
				iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);
				Object[] objTol = { szLqdDate, szErrType,szTranName};
				iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
			}else{
				sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
						+ "brch_no,teller_no,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
						+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
						+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
						+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,"
						+ "amt1,amt2,amt3,num1,num2,num3,str1,str2,str3 "
						+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and tx_code = ? and err_flag = ? "
						+ "and rownum <= ? order by clear_date asc ) where r >= ?";
				sqlTot = "select * from t_chk_err t where clear_date = ? "
						+ "and entr_no = ? and tx_code = ? and err_flag = ? "
						+ "order by clear_date asc ";
				//SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
				Object[] obj = { szLqdDate, szErrType,szTranName,szErrStat,iRowNum, szBeginRec };
				
				iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);
				Object[] objTol = { szLqdDate, szErrType,szTranName,szErrStat};
				iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
			}
		}
		
		if (iCount < 0) {
			SysPub.appLog("ERROR", "数据库错误");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "数据库错误");
			return;
		}
		if (iCount == 0) {
			SysPub.appLog("ERROR", "查询不到记录");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "查询不到记录");
			return;
		}
		//循环报文赋值
		double totAmt = SCHK00010105.putMsg(tpID, iCount,"SCHK00010108");
		EPOper.put(tpID,"MngChkOut[0].SCHK00010108[0].TotNum",iTotCount);
		EPOper.put(tpID,"MngChkOut[0].SCHK00010108[0].RecNum",iCount);
		EPOper.put(tpID,"MngChkOut[0].SCHK00010108[0].TotAmt",totAmt);
		BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
	}
}
