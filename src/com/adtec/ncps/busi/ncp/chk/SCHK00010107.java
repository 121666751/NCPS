package com.adtec.ncps.busi.ncp.chk;

import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

/********************************************************
 * *
 * 
 * @author dingjunbo 对账差错手工调账附加说明
 *******************************************************/
public class SCHK00010107 {

	public static void chkModRmrk() throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szHostDate = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].HostDate");
		String szHostSeqNo = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].HostSeqNo");
		String szCupDate = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].CupDate");
		String szCupSeqNo = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].CupSeqNo");
		String szPlatDt = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].PlatDt");
		String szPlatSeqNo = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].PlatSeqNo");
		String szRmrk = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010107[0].Rmrk");
		szRmrk = szRmrk.trim();
		Object []value = {szRmrk,szPlatDt,szPlatSeqNo};
		String sql = "update t_chk_err set str3 = ? where plat_date = ? and seq_no = ? ";
		int iCount = -1;
		iCount = DataBaseUtils.execute(sql, value);
		if (iCount <= 0) {
			SysPub.appLog("ERROR", "数据库错误");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010107[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID,SysPubDef.ERR_RET,"数据库错误");
			return;
		}
		EPOper.put(tpID,"MngChkOut[0].SCHK00010107[0].TranPrcStat","0");
		BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
