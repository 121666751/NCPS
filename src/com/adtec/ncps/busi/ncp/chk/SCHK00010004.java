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
 * @author dingjunbo *重对账 * *
 *******************************************************/
public class SCHK00010004 {

	/**
	 * 重对账处理
	 * @param args
	 */
	public static void douChk()throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szBusiType = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_019");
		String szChkDate = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");
		// 检查业务类型和重对账日期检查
		if (StringTool.isNullOrEmpty(szBusiType) || StringTool.isNullOrEmpty(szChkDate)) {
			SysPub.appLog("ERROR", "业务类型或对账日期为空");
		    EPOper.put(tpID, "ISO_8583[0].iso_8583_012", "E101");
			TermPubBean.putTermRspCode("E101", "业务类型或对账日期不能为空");
			return;
		}
		szBusiType = szBusiType.trim();
		szChkDate = szChkDate.trim();
		szChkDate = szChkDate.substring(0,4)+"-"+szChkDate.substring(4,6)+"-"+szChkDate.substring(6,8);
		Object []obj = {szBusiType,szChkDate};
		String sql = "update t_chk_sys set chk_stat = '0' where entr_no = ? and chk_date = ?";
		int iCount = DataBaseUtils.execute(sql, obj);
		if( iCount <= 0 )
		{
			SysPub.appLog("ERROR", "数据库错误");
			TermPubBean.putTermRspCode("E102", "数据库错误");
			return;
		}
		TermPubBean.putTermRspCode("0000", "交易成功");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
