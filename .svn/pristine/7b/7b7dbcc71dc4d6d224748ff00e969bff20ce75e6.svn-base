package com.adtec.ncps.busi.ncp.chk;

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
 * @author dingjunbo 对账差错批量调账
 *******************************************************/
public class SCHK00010106 {

	/**
	 * 对账差错批量调账
	 * @throws Exception
	 */
	public static void errBatTran()throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szStat = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010106[0].Stat");//确认调账标识
		szStat = szStat.trim();
		String szTeller = (String) EPOper.get(tpID, "MngChkIn[0].MsgHead[0].Teller");
		String szBrc = (String) EPOper.get(tpID, "MngChkIn[0].MsgHead[0].Brc");
		if(szTeller!=null){
			szTeller = szTeller.trim();
		}
		if(szBrc!=null){
			szBrc = szBrc.trim();
		}
		
		// 检查查询条件
		if (StringTool.isNullOrEmpty(szStat)) {
			SysPub.appLog("ERROR", "确认调账标识为空");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "确认调账标识不能为空");
			return;
		}
		//Comm.print();
		int iTotNum = (Integer) EPOper.get(tpID, "MngChkIn[0].SCHK00010106[0].TotNum");
		if(szStat.length()!=iTotNum){
			SysPub.appLog("ERROR","确认调账标识长度[%d],记录数[%d]",szStat.length(),iTotNum);
			EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "标识长度和记录数不符!");
	        return;
		}
		
		int iIndex = (Integer) EPOper.get(tpID, "MngChkIn[0].SCHK00010106[0].RemNum");
		char[] stats = szStat.toCharArray();
		int num = 0;
		/*确认标识中确认调账标识为1的个数*/
	    for ( int i = 0;i < szStat.length(); i++ )
	    {
	        if ( stats[i] == '1' )
	        {
	            num++;
	        }
	    }
		
		//检查循环记录数
		if( iIndex <= 0 )
		{
			SysPub.appLog("ERROR", "调账汇总笔数为空");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "调账汇总笔数不能为空");
			return;
		}
		
		if ( iIndex != num )
	    {
			SysPub.appLog("ERROR", "确认调账汇总笔数与确认标识确认条数不符!");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "确认调账汇总笔数与确认标识确认条数不符!");
	        return;
	    }
		
		String szEle = "MngChkIn[0].SCHK00010106[0].Loop[";
		String szLqdDate = (String) EPOper.get(tpID,szEle + 0 + "].LqdDate1");
		szLqdDate = szLqdDate.substring(0,4)+"-"+szLqdDate.substring(4,6)+"-"+szLqdDate.substring(6,8);
		String szEntrCd = (String) EPOper.get( tpID,szEle + 0 + "].EntrNo");
		//检查对账状态
		String chk_stat = ChkPub.chk_chk_stat(tpID,szLqdDate);
		SysPub.appLog("DEBUG", "对账状态为[%s]", chk_stat);
		if(!"S".equals(chk_stat)){
			SysPub.appLog("ERROR", "对账未完成!");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "对账未完成!");
	        return;
		}
		for(int i = 0; i< iTotNum; i++)
		{
			if ( stats[i] != '1' )
	        {
				continue;
	        }
			String sql = "select err_flag,teller_no,rck_teller from t_chk_err where clear_date = ? and entr_no = ? "
					+ "and plat_date = ? and seq_no = ? ";
			String szPlatDt = (String) EPOper.get(tpID,szEle + i + "].PlatDt");
			String szPlatSeqNo = String.valueOf((Integer) EPOper.get(tpID,szEle + i + "].PlatSeqNo"));
			
			Object[] value = {szLqdDate,szEntrCd,szPlatDt,szPlatSeqNo};
			DataBaseUtils.queryToElem(sql, "T_CHK_ERR",value);
			int iCount = -1;
			String szErrFlag = (String) EPOper.get(tpID,"T_CHK_ERR.ERR_FLAG");
			String szTellerNo = (String) EPOper.get(tpID,"T_CHK_ERR.TELLER_NO");
			if(szTellerNo!=null){
				szTellerNo = szTellerNo.trim();
			}
			if("B".equals(szErrFlag.trim())){
				szErrFlag = "A";
				if(szTeller!=null&&szTeller.equals(szTellerNo)){
					SysPub.appLog("ERROR", "确认柜员[%s]和审核柜员[%s]不能相同",szTeller,szTellerNo);
					EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
					BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "确认柜员和审核柜员不能相同");
					return;
				}				
				sql = "update t_chk_err set err_flag = ?,rck_teller = ?,err_brch_no = ? where clear_date = ? and entr_no = ? "
						+ "and plat_date = ? and seq_no = ? ";		
				Object[] value1 = {szErrFlag,szTeller,szBrc,szLqdDate,szEntrCd,szPlatDt,szPlatSeqNo};
				iCount = DataBaseUtils.execute(sql, value1);
			}else{
				szErrFlag = "B";
				
				sql = "update t_chk_err set err_flag = ?,teller_no = ? where clear_date = ? and entr_no = ? "
						+ "and plat_date = ? and seq_no = ? ";
				Object[] value1 = {szErrFlag,szTeller,szLqdDate,szEntrCd,szPlatDt,szPlatSeqNo};
				iCount = DataBaseUtils.execute(sql, value1);
			}		
			
			//更新失败退出
			if (iCount <= 0) {
				SysPub.appLog("ERROR", "数据库错误");
				EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","1");
				BusiPub.setMngMsg(tpID,SysPubDef.ERR_RET,"数据库错误");
				return;
			}
		}
		EPOper.put(tpID,"MngChkOut[0].SCHK00010106[0].TranPrcStat","0");
		BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
