package com.adtec.ncps.busi.chnl;

import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class GW3005 {

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
		

			String szSql1 = "insert into t_jrnl values (?,?,?,?,?,?,'','','',?,?,'',?,?,?,'','','','','','','','','',0,0,'','','','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
			Object[] value1 = { platDate, platSeq, brchNo, tellerNo, termNO, chNo, estwSeq, txCode, txDate, txTime,
					reqNo };
			DataBaseUtils.execute(szSql1, value1);

			SysPub.appLog("INFO", "预计流水完成！");

		

			/* 登记批次信息表 */
			String szSql2 = "select * from  t_bat_ctl where bat_no = ?   ";
			Object[] value2 = { batNo };
			int ret = DataBaseUtils.queryToElem(szSql2,"T_BAT_CTL", value2 );
			if (ret !=1 ) {

				EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
				String msg = String.format("该批次不存在");
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
			String msg = "";
			if("0".equals(stat))
			{
				msg = "交易成功，批次开始处理";
			}
			else if("1".equals(stat))
			{
				msg = "交易成功，批次正在处理";
			}
			else if("2".equals(stat))
			{
				msg = "交易成功，批次处理完毕";
			}
				
			else if("3".equals(stat))
			{
				msg = "交易成功，批次处理失败";
			}
			else
			{
				msg = "交易成功，请提交批次";
			}
				
	
			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", msg);
			/* 报文体 */ 
			EPOper.copy(tpID,tpID, "T_BAT_CTL[0].STAT",svrRes + "[0].cd[0].batch_state");

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

}
