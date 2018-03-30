package com.adtec.ncps;

import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.flow.ExtCommPart;
import com.adtec.starring.tool.DTATool;
import com.adtec.starring.util.SpringUtil;

/********************************************************
 * *
 * 
 * @author dingjunbo * DTA处理类 * *
 *******************************************************/
public class DtaTool {

	/**
	 * 同步调用DTA服务
	 * 
	 * @param dtaName
	 *            DTA名称
	 * @param svcName
	 *            服务名称
	 */
	public static void call(String dtaName, String svcName) {
		int type = SysDef.TYTE_DTA;
		int rspType = ExtCommPart.SYNC;// 同步请求
		int asynMatch = ExtCommPart.SYNC;
		String asynMatchFlag = "";
		DTATool dtaTool = (DTATool) SpringUtil.getBean("dtaTool");
		TrcLog.info("dta_call.log", "type=[%d]dtaName=[%s]svcName=[%s]rspType=[%d]asynMatch=[%d]asynMatchFlag[%s]",
				type, dtaName, svcName, rspType, asynMatch, asynMatchFlag);
		dtaTool.call(type, dtaName, svcName, rspType, asynMatch, asynMatchFlag);
	}

	/**
	 * 异步调用DTA服务
	 * 
	 * @param dtaName
	 *            DTA名称
	 * @param svcName
	 *            服务名称
	 */
	public static void aSyncCall(String dtaName, String svcName) {
		DTATool dtaTool = (DTATool) SpringUtil.getBean("dtaTool");
		dtaTool.call(dtaName, svcName);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
