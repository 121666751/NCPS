package com.adtec.ncps.busi.ncp.chk;

import java.io.File;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.FileTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo 汇总查询
 *******************************************************/
public class SCHK00010005 {

	/**
	 * 汇总查询
	 * @param args
	 */
	public static void sumQry()throws Exception
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szFlag = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_069");
		String TranReqDt = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");
		if( StringTool.isNullOrEmpty(TranReqDt))
		{
			SysPub.appLog("ERROR", "清算日期为空");
			TermPubBean.putTermRspCode("E101", "清算日期不能为空");
			return;
		}
		if( StringTool.isNullOrEmpty(szFlag))
		{
			SysPub.appLog("ERROR", "汇总查询标志为空");
			TermPubBean.putTermRspCode("E102", "汇总查询标志不能为空");
			return;
		}
		String szFileName = "";
		String newFileNameString = "";
		if("0".equals(szFlag)){//汇总文件
			szFileName = SysPubDef.SUM_CLR_DIR + TranReqDt.trim() + "/NCPS_SETT_SUM_"+TranReqDt.trim();
			newFileNameString = SysPubDef.MNG_FILE_DIR + TranReqDt.trim() + "/NCPS_SETT_SUM_"+TranReqDt.trim();
		}else if("1".equals(szFlag)){//清算文件
			szFileName = SysPubDef.SUM_CLR_DIR + TranReqDt.trim() + "/NCPS_BRCH_FEE_"+TranReqDt.trim();
			newFileNameString = SysPubDef.MNG_FILE_DIR + TranReqDt.trim() + "/NCPS_BRCH_FEE_"+TranReqDt.trim();
		}
		
		File file = new File(szFileName);
		//判断文件是否存在
		if(file.isFile())
		{
			try {
				FileTool.copyFile(szFileName, newFileNameString,"UTF-8","GBK");
			} catch (Exception e) {
				SysPub.appLog("ERROR", e.getMessage());
				TermPubBean.putTermRspCode("E999", e.getMessage());
				throw e;
			}
			
			
			TermPubBean.putTermRspCode("0000", "交易成功");
			if("0".equals(szFlag)){
				EPOper.put(tpID,"ISO_8583[0].iso_8583_025",newFileNameString);
			}else if("1".equals(szFlag)){
				EPOper.put(tpID,"ISO_8583[0].iso_8583_026",newFileNameString);
			}
			
			
			return;
		}else
		{
			SysPub.appLog("ERROR", "文件不存在");
			TermPubBean.putTermRspCode("E103", "文件不存在");
			return;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
