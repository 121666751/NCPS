package com.adtec.ncps.busi.ncp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.LocInfo;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.RuntimePool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.struct.dta.DtaRunInfo;

public class SysPub {

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 1.0 3.4.2.1. 检查平台状态：成功 0,失败 -1
	 */
	public static int sysChkStat() throws Exception {
		// 获取数据源
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		try {
			String szSql = "select * from t_plat_para";
			DataBaseUtils.queryToElem(szSql, "T_PLAT_PARA", null);
			String szStat = (String) EPOper.get(szTpID, "T_PLAT_PARA[0].PLAT_STAT");
			if (null == szStat) {
				throw new BaseException(SysPubDef.CUP_ENDDAY_RET);// 日终处理
			} else if (!"1".equals(szStat)) {
				if ("2".equals(szStat)) {
					SysPub.appLog("ERROR", "平台状态不正常，正在日切");
					BusiPub.setCupMsg(szTpID, SysPubDef.CUP_ENDDAY_RET, SysPubDef.CUP_ENDDAY_MSG);
					throw new BaseException(SysPubDef.CUP_ENDDAY_RET);
					//return -1;
				} else if ("3".equals(szStat)) {
					SysPub.appLog("ERROR", "平台状态不正常，日切失败");
					BusiPub.setCupMsg(szTpID, SysPubDef.CUP_ENDDAY_RET, SysPubDef.CUP_ENDDAY_MSG);
					throw new BaseException(SysPubDef.CUP_ENDDAY_RET);
					//return -1;
				} else {
					BusiPub.setCupMsg(szTpID, SysPubDef.CUP_ENDDAY_RET, SysPubDef.CUP_ENDDAY_MSG);
					SysPub.appLog("ERROR", "平台状态不正常");
					throw new BaseException(SysPubDef.CUP_ENDDAY_RET);
					//return -1;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 1.0 检查渠道开通状态：成功 0,失败 -1
	 */
	public static int sysChkChn(String _ch_no) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		
		String svcName = dtaInfo.getSvcName();
		if(_ch_no==null) {
		String szName = svcName.substring(0,9);
		//TrcLog.log("sys.log", "svcName==" + svcName+"["+szName+"]", new Object[0]);
		if("SQRY00610".equalsIgnoreCase(szName)||"SCHK00010".equalsIgnoreCase(szName))
			_ch_no = "12";
		}
		
		String tpID = dtaInfo.getTpId();
		try {
			String sql = "select * from t_channel where CHN_NO= ? ";
			Object[] value = { _ch_no };
			DataBaseUtils.queryToElem(sql, "T_CHANNEL", value);
			String CHN_STAT = (String) EPOper.get(tpID, "T_CHANNEL[0].CHN_STAT");
			String BEG_TIME = (String) EPOper.get(tpID, "T_CHANNEL[0].BEG_TIME");
			String END_TIME = (String) EPOper.get(tpID, "T_CHANNEL[0].END_TIME");
			if ("COT".equals(_ch_no)) {
				//柜面业务  柜员和机构赋值
			}
			else{
				//非柜面业务  柜员和机构由渠道表配置
				EPOper.copy(tpID, tpID, "T_CHANNEL[0].CHN_NO", "INIT[0].ChnlNo");
				EPOper.copy(tpID, tpID, "T_CHANNEL[0].MNG_BRCH", "INIT[0].BrchNo");
				EPOper.copy(tpID, tpID, "T_CHANNEL[0].VTELLER", "INIT[0].TlrNo");
			}
			if (CHN_STAT == null)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			if (!"Y".equals(CHN_STAT.toUpperCase()))
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			String time = PubTool.getTime();
			appLog("DEBUG", "time:%s", time);
			if (PubTool.compare_date(time, BEG_TIME) < 0 || PubTool.compare_date(time, END_TIME) > 0)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 1.0 检查业务开通状态
	 */
	public static void sysChkBusi(String _ent_no, String _busi_no) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {

			String sql = "select * from t_busi where ENTR_NO=? and BUSI_NO=?";
			Object[] value = { _ent_no, _busi_no };
			DataBaseUtils.queryToElem(sql, "T_BUSI", value);
			String BUSI_STAT = (String) EPOper.get(tpID, "T_BUSI[0].BUSI_STAT");
			if (BUSI_STAT == null)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			if (!"Y".equals(BUSI_STAT.toUpperCase()))
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			double PAY_LIMIT = (Double) EPOper.get(tpID, "T_BUSI[0].PAY_LIMIT");
			double TrxAmt = (Double) EPOper.get(tpID, "Req_IN.Req_Body[0].TrxInf[0].TrxAmt");
			if (Double.compare(TrxAmt, PAY_LIMIT) > 0)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月24日
	 * 
	 * @version 1.0 检查交易开通状态
	 */
	public static int sysChkTx() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
		try {
			String sql = "select * from t_tx where TX_CODE = ?";
			Object[] value = { svcName };
			DataBaseUtils.queryToElem(sql, "T_TX", value);
			String TX_STAT = (String) EPOper.get(tpID, "T_TX[0].TX_STAT");
			if (TX_STAT == null)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			if (!"Y".equals(TX_STAT.toUpperCase()))
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			int tx_inst_num = (Integer) EPOper.get(tpID, "T_TX[0].TX_INST_NUM");
			if(tx_inst_num==0){
				appLog("DEBUG", "没有设置并发数");
				return 0;
			}
			DtaRunInfo info = RuntimePool.getInstance().getDtaInfoHashMap().get(DtaInfo.getInstance().getDtaName());
			Map<String,Integer> map = info.getSvcHashMap();
			Integer num = map.get(DtaInfo.getInstance().getSvcName());//交易并发数
			appLog("DEBUG", "交易并发数:"+num);
			if(tx_inst_num<num){
				appLog("ERROR","并发数超过限制，请稍候再试");
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			}
			return 0;
		} catch (Exception e) {
			throw e;
		}

	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月24日
	 * 
	 * @version 1.0检查委托单位渠道开通业务状态 _ch_no 渠道编号,_entr_no 委托单位编号,_busi_no 业务编号
	 */
	public static int sysChkChnOpen(String _ch_no, String _entr_no, String _busi_no) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			String sql = "select * from t_channel_open where CHN_NO= ? and ENTR_NO = ? and BUSI_NO = ? ";
			Object[] value = { _ch_no, _entr_no, _busi_no };
			DataBaseUtils.queryToElem(sql, "T_CHANNEL_OPEN", value);
			String OPEN_FLAG = (String) EPOper.get(tpID, "T_CHANNEL_OPEN[0].OPEN_FLAG");
			if (OPEN_FLAG == null)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			if (!"Y".equals(OPEN_FLAG.toUpperCase())) {
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			}
			return 0;
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 检查委托单位状态：成功 0,失败 -1
	 */
	public static int sysChkEntr(String entr_no) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			String sql = "select * from t_entr where ENTR_NO = ?";
			Object[] value = { entr_no };
			DataBaseUtils.queryToElem(sql, "T_ENTR", value);
			String ENTR_STAT = (String) EPOper.get(tpID, "T_ENTR[0].ENTR_STAT");
			String LOGIN_FLAG = (String) EPOper.get(tpID, "T_ENTR[0].LOGIN_FLAG");
			if (ENTR_STAT == null || LOGIN_FLAG == null)
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			if (!"Y".equals(ENTR_STAT.toUpperCase()))
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
			if (!"Y".equals(LOGIN_FLAG.toUpperCase()))
				throw new BaseException(SysPubDef.CUP_ERR_RET);// 系统错
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
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
		else 
			logFileName = dtaInfo.getDtaName() + ".log";
		
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
			if (errmsg != null && errmsg.length() > 1024 ) {
				errmsg = errmsg.substring(0,1024);
			}
			EPOper.put(tpID, "TRANSLOG_ELEMENT[0].errmsg", errmsg); //用于自动任务日志打印
		} else if ("DEBUG".equals(level))
			TrcLog.log(logFileName, szMsg);
		else
			TrcLog.log(logFileName, szMsg);
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月26日
	 * 
	 * @version 测试使用testLog输出
	 */
	public static void testLog(String level, String fmt, Object... argv)  {
		LocInfo loc = new LocInfo(new Throwable(), 1);
		String szFileInfo = loc.getFileName() + ":" + loc.getLineNum() + "::";

		String szMsg = szFileInfo + String.format(fmt, argv);
		
		System.out.println(szMsg);
	}
	
	
	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年7月4日
	 * 
	 * @version 1.0 获取公共参数信息
	 */
	public static void getPubPara(String para_type, String para_no) throws Exception {
		// 获取数据源
		try {
			String szSql = "select * from t_pub_para where para_type=? and para_no=?";
			Object[] value = { para_type, para_no };
			DataBaseUtils.queryToElem(szSql, "T_PUB_PARA", value);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	public static int callShell(String _shellString) throws Exception {
		try {
			appLog("INFO", "call shell :" + _shellString);
			Process process = Runtime.getRuntime().exec(_shellString);
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((bufferedReader.readLine()) != null) {
				// appLog("INFO", szInfo);
			}
			bufferedReader.close();
			process.waitFor();
			int exitValue = process.waitFor();
			if (0 != exitValue) {
				appLog("ERROR", "call shell failed. error code is :" + exitValue);
				return -1;
			}
			return 0;
		} catch (Exception e) {
			appLog("ERROR", "call shell failed. " + e);
			throw e;
		}
	}

	/*
	 * 字符串转换为金额 支持3位币种+数字 1位借贷记标志+数字的方式
	 */
	public static double tranStrToD(String _inStr, String _szFlag) {
		Double dAmt = 0.00;
		int iDiv = 1;
		String Str = "";

		// null和空格返回0
		if (null == _inStr || 0 == _inStr.trim().length()) {
			return dAmt;
		}

		// 三位币种+金额
		if ("CCY".equals(_szFlag)) {
			Str = _inStr.substring(3);
			iDiv = 1;
		} else if ("DC".equals(_szFlag)) {// 1位借贷记+数字
			String szDC = _inStr.substring(0, 1);
			if ("D".equals(szDC)) {
				iDiv = -100;
			}
			// else if( "C".equals(szDC) ){//1位借贷记+数字
			else { // 默认为贷
				iDiv = 100;
			}
			Str = _inStr.substring(1);
		} else if ("NUM".equals(_szFlag)) {// 三位币种数字+金额 比如156+n12
			Str = _inStr.substring(3);
			iDiv = 100;
		}
		dAmt = Double.parseDouble(Str) / iDiv;
		return dAmt;
	}
	

	/*
	 * 字符串转换为数字
	 */
	public static int tranStrToI(String _inStr) {
		int iNum = 0;

		// null和空格返回0
		if (null == _inStr || 0 == _inStr.trim().length()) {
			return iNum;
		}

		iNum = Integer.parseInt(_inStr);

		return iNum;
	}

	/*
	 * 检查上一步骤是否成功
	 */
	public static int ChkStep() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		int iRet = ChkStep(tpID);

		return iRet;
	}

	/*
	 * 检查上一步骤是否成功
	 */
	public static int ChkStep(String _sztpID) throws Exception {

		String szStepStat = (String) EPOper.get(_sztpID, "INIT[0].StepStat");

		// FAIL 返回失败
		if ("FAIL".equals(szStepStat)) {
			appLog("DEBUG", "步骤失败，不进行后续的处理");
			return -1;
		}
		else if ("Continue".equals(szStepStat)) {
			appLog("DEBUG", "跳过本步骤-自动任务使用");
			return 1;
		}

		return 0;
	}
	
	/*
	 * 检查上一步骤是否成功
	 */
	public static String getDataBaseTime() throws Exception {
		String timeStr = "";
		DataSource ds = DataBaseUtils.getDatasource();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		DBExecuter executer = new DBExecuter(ds, "", true);		
		try {				
			String sql = "select to_char(sysdate, 'yyyy-mm-dd hh24:mi:ss') time from dual";	
			pstmt = (PreparedStatement) executer.bind(sql);
			rs = pstmt.executeQuery();
			if(rs.next()){
				timeStr = rs.getString("time");
			}
		} catch (Exception e) {
			throw e;
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (executer != null)
					executer.close();
			} catch (SQLException e) {
				SysPub.appLog("ERROR", "数据库操作失败!");
				e.printStackTrace();
			}
		}
		return timeStr;
	}
	
	/**
	 * 根据交易码获取交易名称
	 * @param tx_code
	 * @return
	 */
	public static String getTxName(String tx_code) throws Exception{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String sql = "select tx_code,tx_name from t_tx where tx_code = ? ";
		Object[] obj = {tx_code};
		DataBaseUtils.queryToElem(sql,"T_TX",obj);
		String tx_name = (String) EPOper.get(tpID,"T_TX[0].TX_NAME");
		return tx_name;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//double dAmt=12.2;
		
	}

}
