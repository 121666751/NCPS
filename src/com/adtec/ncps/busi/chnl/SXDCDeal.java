package com.adtec.ncps.busi.chnl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
import com.alibaba.fastjson.JSONObject;

public class 	SXDCDeal {
	public static int sxdcDEAL() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			SysPub.appLog("INFO", "执行 signDeal 方法开始");
			// String svcName = dtaInfo.getSvcName();

			String svcName1 = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
			svcName1 = svcName1.toUpperCase();
			SysPub.appLog("INFO", "svcName1=" + svcName1);
			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			SysPub.appLog("INFO", "svcName2=" + svcName);
			svcName = svcName.toUpperCase();

			String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
			String cltReq = "OBJ_SXDC_" + svcName + "_REQ";
			String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";
			String cltRes = "OBJ_SXDC_" + svcName + "_RES";

			SysPub.appLog("INFO", "svcName=" + svcName);
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);
			String txDate = PubTool.getDate8();
			EPOper.put(tpID, cltReq + "[0].tx_date",txDate );
			EPOper.put(tpID, cltReq + "[0].tx_code", "W01");
			String txTimestamp = PubTool.getTime();
			EPOper.put(tpID, cltReq + "[0].tx_time",txTimestamp );
			Object[] value1 = null;
			/* 登记流水 */
			String platDate = PubTool.getDate8();

			int platSeq = PubTool.sys_get_seq10();

			
			
			String termNO = "XDSWITCH";
			String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");

			/* 报文体 */
		        
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].custNo", cltReq + "[0].customerNo");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].custName", cltReq + "[0].customerName");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].certNo", cltReq + "[0].certNo");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].mobileNo", cltReq + "[0].phone");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].address", cltReq + "[0].location");
				EPOper.copy(tpID, tpID, svrReq + "[0].cd[0].applicantInstitution", cltReq + "[0].applyBranch");
				String custNo = (String) EPOper.get(tpID, cltReq + "[0].custNo");
				Object[] tmp = { platDate, platSeq, termNO, estwSeq, svcName, txDate,custNo };

				String szSql1 = "insert into t_jrnl values (?,?,'','',?,'','','','',?,?,'',?,'','','','','','','','','','','',0,0,?,'','','','','','','','',0,'','','','','','','','',0,'','','','','',0,0,0,0)";
				SysPub.appLog("INFO", szSql1);
				DataBaseUtils.execute(szSql1, tmp);
			
			
			
			
			EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
			try {
				DtaTool.call("SXDC_CLI", svcName);

			} catch (Exception e) {
				SysPub.appLog("ERROR", "调用电票服务失败");
			}
			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);

//			String szRetCd = (String) EPOper.get(tpID, cltRes + "[0].header[0].retCode"); // 响应代码
//			if (StringTool.isNullOrEmpty(szRetCd)) {
//
//				EPOper.copy(tpID, tpID, "__PLAT_FLOW.__ERR_CODE", cltRes + "[0].header[0].retCode");
//				EPOper.copy(tpID, tpID, "__PLAT_FLOW.__ERR_MSG", cltRes + "[0].header[0].retMessage");
//			}
//			/* 报文头 */
//			EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
//			EPOper.copy(tpID, tpID, cltRes + "[0].check_result", svrRes + "[0].hostReturnCode");
//			EPOper.copy(tpID, tpID, cltRes + "[0].header[0].retMessage", svrRes + "[0].hostErrorMessage");

			/* 报文体 */

		

				EPOper.copy(tpID, tpID, cltRes + "[0].returnCod", svrRes + "[0].cd.hostReturnCode");
				EPOper.copy(tpID, tpID, cltRes + "[0].hostErrorMessage", svrRes + "[0].cd.hostErrorMessage");


			
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			/* 更新返回码 */
			String retCode = (String) EPOper.get(tpID, cltRes + "[0].check_result");
			// String retMsg = (String) EPOper.get(tpID, cltRes +
			// "[0].header[0].retMessage");
			String szSqlStr = "UPDATE t_jrnl  SET ret_code=?  WHERE plat_date = ? and seq_no = ? 	";
			Object[] value = { retCode, platDate, platSeq };
			DataBaseUtils.execute(szSqlStr, value);

			SysPub.appLog("INFO", "更新业务状态完成");

			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 deal_jrnl 方法失败");
			throw e;
		}
	}
}

	




	

		// EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", buf2);
		// EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", len-5);

	

