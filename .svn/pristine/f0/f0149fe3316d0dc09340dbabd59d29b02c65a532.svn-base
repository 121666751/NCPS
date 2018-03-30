package com.adtec.ncps.busi.chnl;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class IDcheckDeal {

	
	
	/**
	 * 身份验证逻辑处理方法
	 * 
	 * **/
	public static int idchkDEAL() throws Exception{
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			SysPub.appLog("INFO", "执行 signDeal 方法开始");
			//String svcName = dtaInfo.getSvcName();
		
			String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
			SysPub.appLog("INFO", "svcName1=" + svcName1);
			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			SysPub.appLog("INFO", "svcName2=" + svcName);
			
			
				//String svcName = (String) EPOper.get(tpID, svrReq + "[0].cd[0].serviceCode");
				String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
				String cltReq = "OBJ_IDENTY_" + svcName + "_REQ";
				String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";
				String cltRes = "OBJ_IDENTY_" + svcName + "_RES";
		
				SysPub.appLog("INFO", "svcName=" + svcName);
				SysPub.appLog("INFO", "svrReq= " + svrReq);
				EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);
				
				SysPub.appLog("INFO", "复制svrReq成功" );
				/* 报文头 */
				//EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].requestBranchId", cltReq + "[0].header[0].requestBranchId");
				EPOper.put(tpID, cltReq + "[0].length", "00110"); //报文长度
//				EPOper.put(tpID, cltReq + "[0].header[0].requestBranchId", "00110"); //交易机构
				String txDate = PubTool.getDate8();
//				EPOper.put(tpID, cltReq + "[0].header[0].requestBusiDate", txDate); //交易机构
				String txTimestamp = PubTool.getDate17();
//				EPOper.put(tpID, cltReq + "[0].header[0].requestTimestamp", txTimestamp); //交易机构h
				
				EPOper.put(tpID, cltReq + "[0].business_code", "01"); //业务种类
				
				EPOper.put(tpID, cltReq + "[0].req_brno", "50001"); //发起机构
				EPOper.put(tpID, cltReq + "[0].user_name", "900015"); //操作柜员
				EPOper.put(tpID, cltReq + "[0].photo_flag", "0"); //是否传照片
				
				EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", cltReq + "[0].tx_code");
				SysPub.appLog("INFO", "复制cltReq[0].tx_code成功" );
				/* 报文体 */
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].ID", cltReq + "[0].id_no");
				SysPub.appLog("INFO", "复制cltReq[0].id_no成功" );
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].NAME", cltReq + "[0].name");
				SysPub.appLog("INFO", "复制cltReq[0].name成功" );
				
				/*登记流水*/
				String platDate = PubTool.getDate8();
				//String strTime = PubTool.getTime();
				int  platSeq =  PubTool.sys_get_seq10();
//				String brchNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestBranchId");
//				String txDate = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestBusiDate");
//				String txTime = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestTimestamp");
//				String tellerNo = (String) EPOper.get(tpID, cltReq + "[0].header[0].teller");
				String txCode = (String) EPOper.get(tpID, svrReq + "[0].tx_code");
//				String chNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].channelNo");
//				String reqNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestSeqNo");
				String termNO ="IDCK";
				String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");
				 
				String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
				SysPub.appLog("INFO", szSql1);
		
//				Object[] value1 = { platDate, platSeq, brchNo, tellerNo, termNO, chNo, estwSeq, svcName,txDate,txTime,reqNo };
				Object[] value1 = { platDate, platSeq,   termNO,  estwSeq, svcName,txDate };
				DataBaseUtils.execute(szSql1, value1);
				SysPub.appLog("INFO", "预计流水完成！"+txCode);
				EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
				SysPub.appLog("INFO", "复制OBJ_ALA_abstarct_REQ[0].req成功" );
				try {
					DtaTool.call("IDCK_CLI", svcName);

				} catch (Exception e) {
					SysPub.appLog("ERROR", "调用身份核查失败");
				}
				EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
				
//				String szRetCd = (String) EPOper.get(tpID, cltRes + "[0].header[0].retCode"); // 响应代码
//				if (StringTool.isNullOrEmpty(szRetCd)) {
//				
//					
//					EPOper.copy(tpID, tpID,"__PLAT_FLOW.__ERR_CODE", cltRes + "[0].header[0].retCode");
//					EPOper.copy(tpID, tpID, "__PLAT_FLOW.__ERR_MSG",cltRes + "[0].header[0].retMessage");
//				}
				/* 报文头 */
				EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
				EPOper.copy(tpID, tpID, cltRes + "[0].check_result", svrRes + "[0].hostReturnCode");
				//EPOper.copy(tpID, tpID, cltRes + "[0].header[0].retMessage", svrRes + "[0].hostErrorMessage");

				/* 报文体 */
				EPOper.copy(tpID, tpID, cltRes + "[0].id_no", svrRes + "[0].cd.IDNO");
				EPOper.copy(tpID, tpID, cltRes + "[0].check_result", svrRes + "[0].cd.CHECK_RESULT");
				EPOper.copy(tpID, tpID, cltRes + "[0].name", svrRes + "[0].cd.NAME");
				EPOper.copy(tpID, tpID, cltRes + "[0].issue_office", svrRes + "[0].cd.ISSUE_OFFICE");
				
				EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
				/*更新返回码*/   
				String retCode = (String) EPOper.get(tpID, cltRes + "[0].check_result");
//				String retMsg = (String) EPOper.get(tpID, cltRes + "[0].header[0].retMessage");
				String szSqlStr = "UPDATE t_jrnl  SET ret_code=?  WHERE plat_date = ? and seq_no = ? 	";
				Object[] value = { retCode,  platDate,platSeq };
				DataBaseUtils.execute(szSqlStr, value);

				SysPub.appLog("INFO", "更新业务状态完成");
			
			
			

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 deal_jrnl 方法失败");
			throw e;
		}
		
	}
}
