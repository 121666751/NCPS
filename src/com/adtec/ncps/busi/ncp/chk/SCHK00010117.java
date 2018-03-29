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
 * @author dingjunbo 清算汇总信息表
 *******************************************************/
public class SCHK00010117 {
	/**
	 * 清算汇总信息表
	 * @throws Exception
	 */
	public static void sumQry() throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szLqdDate = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");
		// 检查查询条件
		if (StringTool.isNullOrEmpty(szLqdDate)) 
		{
			SysPub.appLog("ERROR", "清算日期为空");
			TermPubBean.putTermRspCode("E101",  "清算日期不能为空");
			return;
		}
		String szDate = szLqdDate.substring(0, 4) +"-"+ szLqdDate.substring(4, 6) +"-"+ szLqdDate.substring(6, 8);
		Object []value = {szDate};
		String sql = "select * from t_ncp_sett_tot where clear_date = ? ";
		int iCount = -1;
		iCount = DataBaseUtils.queryToElem(sql, "T_NCP_SETT_TOT", value);
		if (iCount < 0) {
			SysPub.appLog("ERROR", "数据库错误");
			TermPubBean.putTermRspCode("E102",  "数据库错误");
			return;
		}
		if (iCount <= 0) {
			SysPub.appLog("ERROR", "查询不到清算汇总信息");
			TermPubBean.putTermRspCode("E103",  "查询不到清算汇总信息");
			return;
		}
		//报文体赋值
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].PAY_NUM", "ISO_8583[0].iso_8583_041");   //总付款成功笔数
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].OUT_FEE", "ISO_8583[0].iso_8583_040");   //总付款成功金额
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].AC_OUT_FEE", "ISO_8583[0].iso_8583_039"); //收单总付款业务参与价
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].IS_OUT_FEE", "ISO_8583[0].iso_8583_042"); //发卡总付款业务参与价
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].PAYEE_NUM", "ISO_8583[0].iso_8583_043"); //总收款成功笔数
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].IN_FEE", "ISO_8583[0].iso_8583_056");    //总收款成功金额
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].AC_IN_FEE", "ISO_8583[0].iso_8583_057");  //收单总收款业务参与价
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].IS_IN_FEE", "ISO_8583[0].iso_8583_058");  //发卡总收款业务参与价
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].CHARGE_FEE", "ISO_8583[0].iso_8583_059");  //总网络服务费
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].BRAND_FEE", "ISO_8583[0].iso_8583_060");   //总品牌费
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].ERR_FEE", "ISO_8583[0].iso_8583_061");  //总差错处理费
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].AMT1", "ISO_8583[0].iso_8583_062");  //备用金额1
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].AMT2", "ISO_8583[0].iso_8583_063");  //备用金额2
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].RMRK", "ISO_8583[0].iso_8583_081");   //备用字段
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].RMRK1", "ISO_8583[0].iso_8583_082");  //备用字段1
		EPOper.copy(tpID, tpID, "T_NCP_SETT_TOT[0].RMRK2", "ISO_8583[0].iso_8583_083");  //备用字段2
		TermPubBean.putTermRspCode("0000", "交易成功");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
