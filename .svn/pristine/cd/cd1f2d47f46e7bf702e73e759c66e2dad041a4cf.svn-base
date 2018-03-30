package com.adtec.ncps.busi.ncp.chk;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 对账查询 * *
 *******************************************************/
public class SCHK00010001 {
	/**
	 * 对账查询
	 * 
	 * @throws Exception
	 */
	public static void qry() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		//报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szBusiType = (String)EPOper.get(tpID, "ISO_8583[0].iso_8583_019");
		//检查业务类型
		if( StringTool.isNullOrEmpty(szBusiType) )
		{
			SysPub.appLog("ERROR", "业务类型为空");

			TermPubBean.putTermRspCode("E100", "业务类型不能为空");
			return;
		}
		szBusiType = szBusiType.trim();
		EPOper.delete(tpID, "T_CHK_SYS");
		// 查询 最近清算成功日期
		
		String sql1 = "select max(chk_date) as chk_date from t_chk_sys where clear_stat = 'S' ";
		int iCount = DataBaseUtils.queryToElem(sql1, "T_CHK_SYS", null);
		// 查询不到数据返回
		if (iCount < 0) {
			SysPub.appLog("ERROR", "数据库错误");
			TermPubBean.putTermRspCode("E102", "数据库错误");
			return;
		}
		if (iCount == 0) {
			SysPub.appLog("DEBUG", "查询不到对账信息");
			TermPubBean.putTermRspCode("E103", "查询不到对账信息");
			return;
		}
		//EPOper.copy(tpID, tpID, "T_CHK_SYS[0].CHK_DATE", "ISO_8583[0].iso_8583_045");
	    // 清算日期
		String szDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
		if(szDate!=null&&!szDate.isEmpty()&&!"0000-00-00".equals(szDate)&&10==szDate.length())
		{
			szDate = szDate.replace("-", "");
		}
		EPOper.put(tpID, "ISO_8583[0].iso_8583_045", szDate);
		
		EPOper.delete(tpID, "T_CHK_SYS");
		// 查询 银联对账成功日期

		String sql2 = "select max(chk_date) as chk_date from t_chk_sys where chk_stat = 'S' ";
		iCount = -1;
		iCount = DataBaseUtils.queryToElem(sql2, "T_CHK_SYS",null);
		//  查询不到数据返回
		if (iCount < 0) {
			SysPub.appLog("ERROR", "数据库错误");
			TermPubBean.putTermRspCode("E102", "数据库错误");
			return;
		}
		if (iCount == 0) {
			SysPub.appLog("DEBUG", "查询不到对账信息");
			
			TermPubBean.putTermRspCode("E103", "查询不到对账信息");
			
			return;
		}
		//银联对账成功日期
		//EPOper.copy(tpID, tpID, "T_CHK_SYS[0].CHK_DATE", "ISO_8583[0].iso_8583_046");
		szDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
		if(szDate!=null&&!szDate.isEmpty()&&!"0000-00-00".equals(szDate)&&10==szDate.length())
		{
			szDate = szDate.replace("-", "");
		}
		EPOper.put(tpID, "ISO_8583[0].iso_8583_046", szDate);
		//平台状态和交易状态
		String sql3 = "select plat_stat from t_plat_para";
		DataBaseUtils.queryToElem(sql3, "T_PLAT_PARA", null);
		String szStat = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_STAT");
		EPOper.put(tpID,"ISO_8583[0].iso_8583_069",szStat);
		
		String sql4 = "select * from (select chk_date,loc_stat,host_stat,cup_stat,chk_stat,clear_stat from t_chk_sys order by chk_date desc)  where rownum = 1 ";
		iCount = DataBaseUtils.queryToElem(sql4, "T_CHK_SYS", null);
		// 查询不到数据返回
		if (iCount < 0) {
			SysPub.appLog("ERROR", "数据库错误");
			TermPubBean.putTermRspCode("E104", "数据库错误");
			return;
		}
		if (iCount == 0) {
			SysPub.appLog("DEBUG", "查询不到对账信息");
			TermPubBean.putTermRspCode("E102", "查询不到对账信息");
			return;
		}
		
		szDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
		if(szDate!=null&&!szDate.isEmpty()&&!"0000-00-00".equals(szDate)&&10==szDate.length())
		{
			szDate = szDate.replace("-", "");
		}
		EPOper.put(tpID, "ISO_8583[0].iso_8583_047", szDate);
		EPOper.copy(tpID, tpID, "T_CHK_SYS[0].LOC_STAT", "ISO_8583[0].iso_8583_070");
		EPOper.copy(tpID, tpID, "T_CHK_SYS[0].HOST_STAT", "ISO_8583[0].iso_8583_071");
		EPOper.copy(tpID, tpID, "T_CHK_SYS[0].CUP_STAT", "ISO_8583[0].iso_8583_072");
		EPOper.copy(tpID, tpID, "T_CHK_SYS[0].CHK_STAT", "ISO_8583[0].iso_8583_073");
		EPOper.copy(tpID, tpID, "T_CHK_SYS[0].CLEAR_STAT", "ISO_8583[0].iso_8583_068");
		
		
		TermPubBean.putTermRspCode("0000", "交易成功");
	
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
