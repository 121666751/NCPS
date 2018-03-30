package com.adtec.ncps.busi.chnl;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class GW3006 {

	public static int deal_trans() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
		
		
			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			svcName = svcName.toUpperCase();
			SysPub.appLog("INFO", "svcName2=" + svcName);
			
			String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
			String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";

			

			SysPub.appLog("INFO", "svcName=" + svcName);
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);

			/* 登记流水 */
			String platDate = PubTool.getDate8();
			// String strTime = PubTool.getTime();
			int platSeq = PubTool.sys_get_seq10();
			String brchNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestBranchId");
			String txDate = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestBusiDate");
			String txTime = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestTimestamp");
			String tellerNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].teller");
			String txCode = (String) EPOper.get(tpID, svrReq + "[0].cd[0].serviceCode");
			String chNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].channelNo");
			String reqNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].requestSeqNo");
			String termNO = "EBANK";
			String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");

			
			String batNo = (String) EPOper.get(tpID, svrReq + "[0].cd[0].batchNo");
			String businessCode = (String) EPOper.get(tpID, svrReq + "[0].businessCode");
		

			String szSql1 = "insert into t_jrnl values (?,?,?,?,?,?,'','','',?,?,'',?,?,?,'','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
			Object[] value1 = { platDate, platSeq, brchNo, tellerNo, termNO, chNo, estwSeq, txCode, txDate, txTime,
					reqNo };
			DataBaseUtils.execute(szSql1, value1);

			SysPub.appLog("INFO", "预计流水完成！");
//			try {
//				DtaTool.call("HOST_CLI", svcName);
//
//			} catch (Exception e) {
//				SysPub.appLog("ERROR", "调用核心服务失败");
//			}

		

			/* 交易报文检查 */

			if (chk_trans(tpID, svrReq, svrRes, businessCode ) < 0) {

				/* 报文头 */
				EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");

				/* 报文体 */

				EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
				/* 更新返回码 */
				String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
				String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
				String szSqlStr = "UPDATE t_jrnl  SET ret_code=?, ret_msg =?  WHERE plat_date = ? and seq_no = ? 	";
				Object[] value = { retCode, retMsg, platDate, platSeq };
				DataBaseUtils.execute(szSqlStr, value);

				return 0;

			}

			/* 登记批次信息表 */
			String szSql2 = "select * from  t_bat_ctl where bat_no = ?   ";
			Object[] value2 = { batNo };
			int ret = DataBaseUtils.queryToElem(szSql2,"T_BAT_CTL", value2 );
			if (ret <=0 ) {

				EPOper.put(tpID, svrRes + "[0].hostReturnCode", "D003");
				String msg = String.format("批次号有误，请检查！");
				EPOper.put(tpID, svrRes + "[0].hostErrorMessage", msg);
				/* 报文头 */
				EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");

				/* 报文体 */

				EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
				String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
				String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
				String szSqlStr = "UPDATE t_jrnl  SET ret_code=?, ret_msg =?  WHERE plat_date = ? and seq_no = ? 	";
				Object[] value = { retCode, retMsg, platDate, platSeq };
				DataBaseUtils.execute(szSqlStr, value);

				return 0;
			}

			/* 报文头 */
			EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
			EPOper.put(tpID, svrRes + "[0].hostReturnCode", "0000");

			String stat = (String)EPOper.get(tpID,  "T_BAT_CTL[0].STAT");
			String msg = "交易成功";
				
	
			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", msg);
			/* 报文体 */ 
			EPOper.put(tpID,svrRes + "[0].cd[0].batchNo", batNo);
			EPOper.copy(tpID,tpID, "T_BAT_CTL[0].FILE_NAME",svrRes + "[0].cd[0].filePathName");

			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			/* 更新返回码 */
			String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
			String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
			String szSqlStr = "UPDATE t_jrnl  SET ret_code=?, ret_msg =?  WHERE plat_date = ? and seq_no = ? 	";
			Object[] value = { retCode, retMsg, platDate, platSeq };
			DataBaseUtils.execute(szSqlStr, value);

			SysPub.appLog("INFO", "更新业务状态完成");

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 deal_jrnl 方法失败");
			throw e;
		}
	}

	private static int chk_trans(String tpID, String svrReq, String svrRes, String businessCode) {
		if (null == businessCode || businessCode.length() == 0) {
			EPOper.put(tpID, svrRes + "[0].hostReturnCode", "S007");
			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "没有获取到交易代码");
			return -1;
		}
		return 0;
	}

}
