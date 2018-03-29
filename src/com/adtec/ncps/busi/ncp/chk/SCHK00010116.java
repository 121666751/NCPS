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
 * @author dingjunbo 银联差错手工调账附加说明
 *******************************************************/
public class SCHK00010116 {

	public static void chkModRmrk() throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szLqdDate = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].LqdDate");
		String szCupSeqNo = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].CupSeqNo");
		String szCupTranDt = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].CupTranDt");
		String szCupTranTm = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].CupTranTm");
		String szAdjAmt = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].AdjAmt");
		String szPayFee = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].PayFee");
		String szErrFlg = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].ErrFlg");
		String szErrType = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].ErrType");
		String szSndBrchNo = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].SndBrchNo");
		String szRmrk = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010116[0].Rmrk");
		Object []value = {szRmrk,szErrType,szCupSeqNo,szSndBrchNo};
		String sql = "update t_ncp_err_detail set rmrk = ? where err_type = ? and oth_seq = ? and snd_brch_no = ? ";
		int iCount = -1;
		iCount = DataBaseUtils.execute(sql, value);
		if (iCount <= 0) {
			SysPub.appLog("ERROR", "数据库错误");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010116[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID,SysPubDef.ERR_RET,"数据库错误");
			return;
		}
		EPOper.put(tpID,"MngChkOut[0].SCHK00010116[0].TranPrcStat","0");
		BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
