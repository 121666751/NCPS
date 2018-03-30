package com.adtec.ncps.busi.qrps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.LocInfo;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.RuntimePool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.struct.dta.DtaRunInfo;

public class QrSysPub {

    /*
     * @createAt 2017年5月26日
     * 
     * @version 根据调试级别，调用不同的TrcLog方法（TRACE级别，则调用TrcLog.TRACE方法，其他类似） 参数说明，argv
     * 第一个参数为错误码，每二个参数为错误信息
     */
    public static void appLog(String level, String fmt, Object... argv) throws Exception {
	DtaInfo dtaInfo = DtaInfo.getInstance();
	String tpID = dtaInfo.getTpId();
	String svcName = (String) EPOper.get(tpID, "__GDTA_FORMAT.__GDTA_SVCNAME");
	String alaSvcName = dtaInfo.getSvcName();
	String logFileName = "";

	LocInfo loc = new LocInfo(new Throwable(), 1);
	String szFileInfo = loc.getFileName() + ":" + loc.getLineNum() + "::";

	String szMsg = szFileInfo + String.format(fmt, argv);

	// 日志文件名优先取DTA的服务名，如果取不到，则取ALA服务逻辑名
	if (svcName != null && !"".equals(svcName))
	    logFileName = svcName + ".log";
	else if (alaSvcName != null && !"".equals(alaSvcName))
	    logFileName = alaSvcName + ".log";
	if ("TRACE".equals(level))
	    TrcLog.trace(logFileName, szMsg);
	else if ("INFO".equals(level))
	    TrcLog.info(logFileName, szMsg);
	else if ("WARN".equals(level))
	    TrcLog.warn(logFileName, szMsg);
	else if ("ERROR".equals(level)) {
	    // 使用ERROR级别，步骤状态设置为失败
	    EPOper.put(tpID, "INIT[0].StepStat", "FAIL");
	    TrcLog.error(logFileName, szMsg);
	    String errmsg = String.format(fmt, argv);
	    EPOper.put(tpID, "TRANSLOG_ELEMENT[0].errmsg", errmsg); // 用于自动任务日志打印
	} else if ("DEBUG".equals(level))
	    TrcLog.log(logFileName, szMsg);
	else
	    TrcLog.log(logFileName, szMsg);
    }

}
