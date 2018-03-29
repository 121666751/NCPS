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

/********************************************************
 * *
 * 
 * @author dingjunbo 银联差错批量调账
 *******************************************************/
public class SCHK00010115 {
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
		String szStat = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010115[0].Stat");//审核状态
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
			EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","0");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "确认调账标识不能为空");
			return;
		}
		//int iIndex = (Integer) EPOper.getSuffixNo(tpID, "MngChkIn[0].SCHK00010115[0].Loop[0].LqdDate");
		int iIndex = 0;
		int szRemNum =  (Integer) EPOper.get(tpID, "MngChkIn[0].SCHK00010115[0].RemNum");
		
		iIndex = szRemNum;
		//检查循环记录数
		if( iIndex <= 0 )
		{
			SysPub.appLog("ERROR", "调账汇总笔数为空");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "调账汇总笔数不能为空");
			return;
		}
		
		int iTotNum = (Integer) EPOper.get(tpID, "MngChkIn[0].SCHK00010115[0].TotNum");
		if(szStat.length()!=iTotNum){
			SysPub.appLog("ERROR","确认调账标识长度[%d],记录数[%d]",szStat.length(),iTotNum);
			EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "标识长度和记录数不符!");
	        return;
		}
		
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
	    
	    if ( iIndex != num )
	    {
			SysPub.appLog("ERROR", "确认调账汇总笔数与确认标识确认条数不符!");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "确认调账汇总笔数与确认标识确认条数不符!");
	        return;
	    }
	    
		String szEle = "MngChkIn[0].SCHK00010115[0].Loop[";
		String szLqdDate = (String) EPOper.get(tpID,szEle + 0 + "].LqdDate");
		szLqdDate = szLqdDate.substring(0,4)+"-"+szLqdDate.substring(4,6)+"-"+szLqdDate.substring(6,8);
		//检查对账状态
		String chk_stat = ChkPub.chk_chk_stat(tpID,szLqdDate);
		SysPub.appLog("DEBUG", "对账状态为[%s]", chk_stat);
		if(!"S".equals(chk_stat)){
			SysPub.appLog("ERROR", "对账未完成!");
			EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "对账未完成!");
	        return;
		}
		for(int i = 0; i< iTotNum; i++)
		{
			if ( stats[i] != '1' )
	        {
				continue;
	        }
			String sql = "select err_flag,teller_no,rck_teller from t_ncp_err_detail where err_type = ? "
					+ "and snd_brch_no = ? and oth_seq = ? ";

			String szCupSeqNo = (String) EPOper.get( tpID,szEle + i + "].CupSeqNo");//银联流水号
			//String szCupTranDt = (String) EPOper.get(tpID,szEle + i + "].CupTranDt");//银联交易日期
			//szCupTranDt = szCupTranDt.substring(0,4)+"-"+szCupTranDt.substring(4,6)+"-"+szCupTranDt.substring(6,8);
			//String szPlatSeqNo = (String) EPOper.get(tpID,szEle + i + "].CupTranTm");//银联交易时间
			//String szErrFlg = (String) EPOper.get(tpID,szEle + i + "].ErrFlg");//0-发送方,1-接收方
			String szErrType = (String) EPOper.get(tpID,szEle + i + "].ErrType");//差错标识
			String szSndBrchNo = (String) EPOper.get(tpID,szEle + i + "].SndBrchNo");//发送机构标识
			Object[] value = {szErrType,szSndBrchNo,szCupSeqNo};
			DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL",value);
			
			int iCount = -1;
			String szErrFlag = (String) EPOper.get(tpID,"T_NCP_ERR_DETAIL.ERR_FLAG");
			String szTellerNo = (String) EPOper.get(tpID,"T_NCP_ERR_DETAIL.TELLER_NO");

			if("B".equals(szErrFlag.trim())){
				szErrFlag = "A";
				if(szTeller!=null&&szTeller.equals(szTellerNo)){
					SysPub.appLog("ERROR", "确认柜员[%s]和审核柜员[%s]不能相同",szTeller,szTellerNo);
					EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","1");
					BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "确认柜员和审核柜员不能相同");
					return;
				}				
				sql = "update t_ncp_err_detail set err_flag = ?,rck_teller = ?,err_brch_no = ? where err_type = ? "
						+ "and snd_brch_no = ? and oth_seq = ? ";
				Object[] value1 = {szErrFlag,szTeller,szBrc,szErrType,szSndBrchNo,szCupSeqNo};
				iCount = DataBaseUtils.execute(sql, value1);
			}else{
				szErrFlag = "B";
				
				sql = "update t_ncp_err_detail set err_flag = ?,teller_no = ? where err_type = ? "
						+ "and snd_brch_no = ? and oth_seq = ? ";
				Object[] value1 = {szErrFlag,szTeller,szErrType,szSndBrchNo,szCupSeqNo};
				iCount = DataBaseUtils.execute(sql, value1);
			}	
			
			//更新失败退出
			if (iCount <= 0) {
				SysPub.appLog("ERROR", "数据库错误");
				EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","1");
				BusiPub.setMngMsg(tpID,SysPubDef.ERR_RET,"数据库错误");
				return;
			}
		}
		EPOper.put(tpID,"MngChkOut[0].SCHK00010115[0].TranPrcStat","0");
		BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
