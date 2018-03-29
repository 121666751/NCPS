package com.adtec.ncps.busi.ncp.chk;

import java.io.File;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.FileTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/**
 * 银联报表下载
 * @author GuoFan
 *
 */
public class SCHK00010007 {
	/*
	 * 银联报表下载
	 */
	public static void downloadCupForm() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szFileName = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_025");
		String szChkDate = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");
		// 检查报表文件名和重对账日期检查
		if (StringTool.isNullOrEmpty(szFileName) || StringTool.isNullOrEmpty(szChkDate)) {
			SysPub.appLog("ERROR", "报表文件名或对账日期为空");
			EPOper.put(tpID, "MngChkOut[0].SCHK00010007[0].TranPrcStat", "1");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "报表文件名或对账日期不能为空");
			return;
		}
		
		szFileName = szFileName.trim();
		szChkDate = szChkDate.trim();
		String  ChkDate = szChkDate.substring(0,4)+"-"+szChkDate.substring(4,6)+"-"+szChkDate.substring(6,8);
		Object []obj = {ChkDate};
		String sql = "select chk_stat  from t_chk_sys where entr_no = '035001' and chk_date = ? ";
		int rtn = DataBaseUtils.queryToElem(sql, "T_CHK_SYS",obj);
		String chk_stat = (String) EPOper.get(tpID, "T_CHK_SYS.CHK_STAT");
		if(rtn<=0){
			SysPub.appLog("ERROR", "获取清算日期[%s]对账信息失败",ChkDate);
			//EPOper.put(tpID, "MngChkOut[0].SCHK00010007[0].TranPrcStat", "1");
			//BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "获取清算日期["+ChkDate+"]对账信息失败");
			TermPubBean.putTermRspCode("E199",  "获取清算日期["+ChkDate+"]对账信息失败");
			return;
		}
		if(!StringTool.isNullOrEmpty(chk_stat)&&!"S".equals(chk_stat.trim())){
			SysPub.appLog("ERROR", "清算日期[%s]的对账没有完成",ChkDate);
			//EPOper.put(tpID, "MngChkOut[0].SCHK00010007[0].TranPrcStat", "1");
			//BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "清算日期["+ChkDate+"]的对账没有完成");
			TermPubBean.putTermRspCode("E199", "清算日期["+ChkDate+"]的对账没有完成");
			return;
		}
		String sourdir = SysPubDef.NCP_CHK_DIR + szChkDate + "/";
		String destdir = SysDef.WORK_DIR + ResPool.configMap.get("FilePath");
		//String filePath = SysDef.WORK_DIR + ResPool.configMap.get("FilePath");
		String sourfilePath = "";
		String destfilePath = "";
		String szName="";
		if("RD1013YYMMDD99".equals(szFileName)){
			sourfilePath = sourdir + "RD1013"+ szChkDate.substring(2) + "99";
			destfilePath = destdir + "RD1013"+ szChkDate.substring(2) + "99";
			szName="RD1013"+ szChkDate.substring(2) + "99";
		}else if("RD1027YYYYMMDD99".equals(szFileName)){
			sourfilePath = sourdir + "RD1027"+ szChkDate + "99";
			destfilePath = destdir + "RD1027"+ szChkDate + "99";
			szName =  "RD1027"+ szChkDate + "99";
		}else if("YYYYMMDD_01_IS_COMTRX".equals(szFileName)){
			sourfilePath = sourdir + szChkDate + "_01_IS_COMTRX";
			destfilePath = destdir + szChkDate + "_01_IS_COMTRX";
			szName =  szChkDate + "_01_IS_COMTRX";
		}else if("YYYYMMDD_01_AC_COMTRX".equals(szFileName)){
			sourfilePath = sourdir + szChkDate + "_01_AC_COMTRX";
			destfilePath = destdir + szChkDate + "_01_AC_COMTRX";
			szName =  szChkDate + "_01_AC_COMTRX";
		}else if("YYYYMMDD_01_IS_ERRTRX".equals(szFileName)){
			sourfilePath = sourdir + szChkDate + "_01_IS_ERRTRX";
			destfilePath = destdir + szChkDate + "_01_IS_ERRTRX";
			szName = szChkDate + "_01_IS_ERRTRX";
		}else if("YYYYMMDD_01_AC_ERRTRX".equals(szFileName)){
			sourfilePath = sourdir + szChkDate + "_01_AC_ERRTRX";
			destfilePath = destdir + szChkDate + "_01_AC_ERRTRX";
			szName =  szChkDate + "_01_AC_ERRTRX";
		}else if("YYYYMMDD_99_SUM".equals(szFileName)){
			sourfilePath = sourdir + szChkDate + "_99_SUM";
			destfilePath = destdir + szChkDate + "_99_SUM";
			szName =  szChkDate + "_99_SUM";
		}
		
		File file = new File(sourfilePath);
		//判断是否存在
		if(file.isFile())
		{
			try {
				FileTool.copyFile(sourfilePath, destfilePath, "GB2312","GB2312");
			} catch (Exception e) {
				//EPOper.put(tpID,"MngChkOut[0].SCHK00010007[0].TranPrcStat","1");
				//BusiPub.setMngMsg(tpID,SysPubDef.ERR_RET,e.getMessage());
				TermPubBean.putTermRspCode("E199",  e.getMessage());
				throw e;
			}
			
			//EPOper.put(tpID,"MngChkOut[0].SCHK00010007[0].TranPrcStat","0");
			EPOper.put(tpID,"ISO_8583[0].iso_8583_025",szName);
			
			//发送文件
			TermPubBean.Sendfile();
			//BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
			TermPubBean.putTermRspCode("0000", "交易成功");
			return;
		}else
		{
			//EPOper.put(tpID,"MngChkOut[0].SCHK00010007[0].TranPrcStat","1");
			//BusiPub.setMngMsg(tpID,SysPubDef.ERR_RET,"银联报表不存在");
			TermPubBean.putTermRspCode("E999", "银联报表不存在");
			return;
		}
		
		
	}
}
