package com.adtec.ncps.busi.ncp.chk;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author chenshx * 对账公共类 * *
 *******************************************************/

public class ChkPub {

	/**
	 * @查询指定状态的对账控制记录
	 *
	 */
	public static int QryChkSys(String _szEntrNo, String _szChkStat, String _szClrStat) throws Exception {
		int iRet = 0;
		String szSqlStr = "SELECT *  FROM T_CHK_SYS " //
				+ " WHERE entr_no	='" + _szEntrNo + "' "//
				+ " AND chk_stat = '" + _szChkStat + "' "//
				+ " AND clear_stat = '" + _szClrStat + "' ";
		try {
			SysPub.appLog("INFO", "查询T_CHK_SYS[%s]", szSqlStr);
			iRet = DataBaseUtils.queryToElem(szSqlStr, "T_CHK_SYS", null);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
		return iRet;
	}

	/**
	 * @获取带处理的对账控制记录
	 *
	 */
	public static int qryWaitChk(String _szEntrNo) throws Exception {
		String szSqlStr = "SELECT *  FROM T_CHK_SYS " + //
				" WHERE entr_no	='" + _szEntrNo + "' and chk_stat <> 'S' " + //
				" and rownum=1 order by chk_date ";
		try {
			SysPub.appLog("INFO", "查询T_CHK_SYS[%s]", szSqlStr);
			DataBaseUtils.queryToElem(szSqlStr, "T_CHK_SYS", null);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 更新对账控制表信息
	 * 
	 * @throws Exception
	 */
	public static int UptChkSys(DBExecuter _executer) throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		// 把流水表的数据插入本地流水临时表中
		String szSqlStr = "update t_chk_sys " + //
				" set loc_stat= ?, host_stat = ? , cup_stat = ? , chk_stat = ? , clear_stat = ?, file_name = ?" + //
				" where chk_date= ?  and entr_no = ? ";
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE"); // 对账日期
			String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO"); // 公司编号

			String szLocStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].LOC_STAT"); // 本地流水状态
			String szHostStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].HOST_STAT"); // 主机文件状态
			String szCupStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CUP_STAT"); // 银联文件状态
			String szChkStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_STAT"); // 对账状态
			String szClrStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CLEAR_STAT"); // 清算状态
			String szFileName = (String) EPOper.get(tpID, "T_CHK_SYS[0].FILE_NAME"); // 对账文件
			Object[] value = { szLocStat, szHostStat, szCupStat, szChkStat, szClrStat, szFileName, //
					szClrDate, szEntrNo };
			// 更新对账控制表
			SysPub.appLog("INFO", "更新对账控制表信息");
			iRet = DataBaseUtils.executenotr(_executer, szSqlStr, value);
			if (0 == iRet) {
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			SysPub.appLog("INFO", "更新对账控制表信息");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	// 调度核心下载文件接口服务
	public static int CallHostChkFile() throws Exception {
		int iNum = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szRetCd = "";
		String szRetMsg = "";

		try {
			SysPub.appLog("INFO", "核心流水下载请求");
			BusiPub.getPlatSeq();
			//BusiMsgProc.headHost("ChkOut", "S215071");
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_002", "50001");// 机构号
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_003", "50001");// 机构号
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_007", "900023");// 柜员号
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_010", "NCPS");// 系统编号
			// 渠道种类
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_127", "N");
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_016", "6265");
			//EPOper.copy(tpID, tpID, "T_CHK_SYS[0].CHK_DATE", "ChkOut[0].ISO_8583[0].iso_8583_044"); // 查询日期
			String szDt = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
			szDt = szDt.replaceAll("-", "");
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_065", szDt);			
			
			szDt = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
			EPOper.put(tpID, "ChkOut[0].ISO_8583[0].iso_8583_044", szDt);

			// 调度 核心 S215071服务
			SysPub.appLog("INFO", "调用S215071服务开始");
			DtaTool.call("HOST_CLI", "S215071");
			szRetCd = (String) EPOper.get(tpID, "ChkIn[0].ISO_8583[0].iso_8583_012"); // 响应代码
			SysPub.appLog("INFO", "S215071响应码[%s]", szRetCd);
			if (!"0000".equals(szRetCd)) {
				szRetMsg = (String) EPOper.get(tpID, "ChkIn[0].ISO_8583[0].iso_8583_012"); // 响应信息
				SysPub.appLog("ERROR", "核心流水下载失败[%s][%s]", szRetCd, szRetMsg);
				return -1;
			}

			SysPub.appLog("INFO", "调用S215071服务成功");

		} catch (Exception e) {
			SysPub.appLog("ERROR", "核心流水下载失败");
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/**
	 * 核心流水对账
	 * 
	 * @throws Exception
	 */
	public static int ChkHost() throws Exception {
		int iRet = 0;
		String szSqlStr = "";
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
		String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");// 对账日期
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].HOST_STAT");// 核心状态
		String szChkStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_STAT");// 对账状态
		// 主机文件状态不等于S，退出
		if (!"S".equals(szStat) || !"0".equals(szChkStat)) {
			return 0;
		}
		try {
			szSqlStr = " MERGE INTO t_loc_" + szEntrNo + " a "//
					+ " USING t_host_" + szEntrNo + " b  "//
					+ " ON (a.plat_date=b.plat_date AND a.seq_no=b.seq_no) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.open_brch=b.open_brch ";
			iRet = DataBaseUtils.execute(szSqlStr, null);
			SysPub.appLog("INFO", "==更新t_loc的主机信息[%d]", iRet);

			SysPub.appLog("INFO", "=============本地流水和主机流水比对==============");
			// 连接数据库
			DBExecuter executer = DataBaseUtils.conn();
			szSqlStr = " update t_loc_" + szEntrNo + " a "//
					+ " set a.tx_flag='1'||substr(a.tx_flag,2,4) " //
					+ " where exists ( select plat_date from t_host_" + szEntrNo + " b "//
					+ "       where a.plat_date=b.plat_date and a.seq_no=b.seq_no ) " //
					+ " and substr(a.tx_flag,1,1)='0' and substr(a.tx_flag,3,1)='1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==1.更新t_loc tx_flag第一位为1（主机有，平台有）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "主机对账失败[%d]", iRet);
				return -1;
			}

			szSqlStr = " update t_loc_" + szEntrNo + " a " //
					+ " set a.tx_flag='2'||substr(a.tx_flag,2,1)||'1'||substr(a.tx_flag,4,2), a.chk_msg='主机有，平台失败'"//
					+ " where exists (select plat_date from t_host_" + szEntrNo + " b " //
					+ "           where a.plat_date=b.plat_date and a.seq_no=b.seq_no )" //
					+ " and substr(a.tx_flag,1,1)='0' and substr(a.tx_flag,3,1)<>'1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==2.更新t_loc tx_flag第一位为2,第三位为1（主机有，平台失败）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "主机对账失败[%d]", iRet);
				return -1;
			}

			szSqlStr = " update t_loc_" + szEntrNo + " a "//
					+ " set a.tx_flag='3'||substr(a.tx_flag,2,1)||'0'||substr(a.tx_flag,4,2), a.chk_msg='主机无，平台有'"//
					+ " where not exists (select plat_date from t_host_" + szEntrNo + " b "//
					+ "           where a.plat_date=b.plat_date and a.seq_no=b.seq_no )"//
					+ " and substr(a.tx_flag,1,1)='0' and substr(a.tx_flag,3,1)='1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==3.更新t_loc tx_flag第一位为3,第三位为0（主机无，平台有）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "主机对账失败[%d]", iRet);
				return -1;
			}

			szSqlStr = " update t_loc_" + szEntrNo + " a " //
					+ " set a.tx_flag='1'||substr(a.tx_flag,2,4)" //
					+ " where not exists (select plat_date from t_host_" + szEntrNo + " b " //
					+ "        where a.plat_date=b.plat_date and a.seq_no=b.seq_no ) " //
					+ " and substr(a.tx_flag,1,1)='0' and substr(a.tx_flag,3,1)<>'1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==4.更新t_loc tx_flag第一位为1（主机无，平台失败）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "主机对账失败[%d]", iRet);
				return -1;
			}

			szSqlStr = " insert into t_chk_err "//
					+ "    (clear_date,entr_no,host_chk_flag,oth_chk_flag,host_msg,chk_msg,plat_date,seq_no,host_date,host_seq,teller_no,open_brch,acct_no1,acct_no2,tx_code,oth_seq,tx_amt) " //
					+ " select '" + szClrDate + "','" + szEntrNo + "','1','X','平台无，主机有','平台无，主机有',"//
					+ "         a.plat_date,a.seq_no,a.host_date,a.host_seq,a.teller_no, a.open_brch,a.acct_no1,a.acct_no2,a.tx_code,a.oth_seq,a.tx_amt "//
					+ "  from t_host_" + szEntrNo + " a" //
					+ " where not exists ( select plat_date from t_loc_" + szEntrNo
					+ " b where a.plat_date=b.plat_date and a.seq_no=b.seq_no) ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==5.插入t_chk_err（平台无，主机有）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "主机对账失败[%d]", iRet);
				return -1;
			}

			EPOper.put(tpID, "T_CHK_SYS[0].CHK_STAT", "1"); // 1-主机对账完成

			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);

			SysPub.appLog("INFO", "==6.主机对账完成");

		} catch (Exception e) {
			SysPub.appLog("ERROR", "主机对账失败");
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/**
	 * 银联流水对账
	 * 
	 * @throws Exception
	 */
	public static int ChkOth() throws Exception {
		int iRet = 0;
		String szSqlStr = "";
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
		String szPlatDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");// 对账日期
		String szStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CUP_STAT");// 主机状态
		String szChkStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_STAT");// 对账状态
		// 文件状态不等于2，退出
		if (!"S".equals(szStat) || !"1".equals(szChkStat)) {
			return 0;
		}
		try {
			SysPub.appLog("INFO", "=============本地流水和银联流水比对==============");
			// 连接数据库
			DBExecuter executer = DataBaseUtils.conn();
			szSqlStr = " update t_loc_" + szEntrNo + " a " //
					+ " set a.tx_flag = substr(a.tx_flag,1,1)||'1'||substr(a.tx_flag,3,3) "//
					+ " where exists ( select clear_date from t_oth_" + szEntrNo + " b "//
					+ "        where a.oth_uniq =b.oth_uniq ) and substr(a.tx_flag,4,1)='1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==1.更新t_loc tx_flag第二位为1（平台有，第三方有）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "银联流水对账[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " update t_loc_" + szEntrNo + " a "//
					+ " set a.tx_flag= substr(a.tx_flag,1,1)||'2'||substr(a.tx_flag,3,1)||'1'||substr(a.tx_flag,5,1), a.chk_msg='平台失败，第三方有' "
					+ " where exists ( select clear_date from t_oth_" + szEntrNo + " b where a.oth_uniq =b.oth_uniq  ) "
					+ " and substr(a.tx_flag,2,1)='0' and substr(a.tx_flag,4,1)<>'1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==2.更新t_loc tx_flag第二位为2,第四位为1（平台失败，第三方有）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "银联流水对账[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " update t_loc_" + szEntrNo + " a " //
					+ " set a.tx_flag= substr(a.tx_flag,1,1)||'3'||substr(a.tx_flag,3,1)||'0'||substr(a.tx_flag,5,1), a.chk_msg='平台有，第三方无' " //
					+ " where not exists (select clear_date from t_oth_" + szEntrNo
					+ " b where a.oth_uniq =b.oth_uniq  ) "//
					+ " and substr(a.tx_flag,2,1)='0' and substr(a.tx_flag,4,1)='1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==3.更新t_loc tx_flag第二位为3,第四位为0（平台有，第三方无）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "银联流水对账[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " update t_loc_" + szEntrNo + " a "//
					+ " set a.tx_flag= substr(a.tx_flag,1,1)||'1'||substr(a.tx_flag,3,3) " //
					+ " where not exists (select clear_date from t_oth_" + szEntrNo
					+ " b where a.oth_uniq =b.oth_uniq  ) " //
					+ " and substr(a.tx_flag,2,1)='0' and substr(a.tx_flag,4,1)<>'1' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==4.更新t_loc tx_flag第二位为1（平台失败，第三方无）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "银联流水对账[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " insert into t_chk_err "//
					+ " (clear_date,entr_no,host_chk_flag,oth_chk_flag,proc_flag,chk_msg,plat_date,seq_no,tx_code,acct_brch,oth_seq,tx_amt) "//
					+ " select clear_date,'" + szEntrNo + "','X','1','Y','平台无，第三方有','" + szPlatDate + "',"//
					+ "        rownum+50000000,tx_code,rtrim(substr(oth_uniq,1,11)),substr(oth_uniq,12), tx_amt " //
					+ " from t_oth_" + szEntrNo + " a " + //
					" where not exists (select plat_date from t_loc_" + szEntrNo
					+ " b where a.oth_uniq =b.oth_uniq  ) ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==5.插入t_chk_err（平台无，第三方有）[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "银联流水对账[%d]", iRet);
				return -1;
			}
			
			EPOper.put(tpID, "T_CHK_SYS[0].CHK_STAT", "2"); // 1-银联对账完成

			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
			iRet = DataBaseUtils.commit(executer);

			SysPub.appLog("INFO", "==6.银联对账完成");

		} catch (Exception e) {
			SysPub.appLog("ERROR", "银联对账失败");
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/**
	 * 对账后事件
	 * 
	 * @throws Exception
	 */
	public static int ChkAfter() throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		String szEntrNo = (String) EPOper.get(tpID, "T_CHK_SYS[0].ENTR_NO");
		String szClrDate = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");// 对账日期
		String szChkStat = (String) EPOper.get(tpID, "T_CHK_SYS[0].CHK_STAT");// 对账状态
		// 对账状态不等于2，退出
		if (!"2".equals(szChkStat)) {
			return 0;
		}
		try {
			SysPub.appLog("INFO", "=============对账后处理==============");
			// 连接数据库
			DBExecuter executer = DataBaseUtils.conn();
			String szSqlStr = " update t_loc_" + szEntrNo + " a " //
					+ " set a.tx_flag= '11111', chk_msg='主机有，第三方有' " //
					+ " where substr(a.tx_flag,1,2)<>'11' and  substr(a.tx_flag,3,2)='11' ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==1.主机记账成功，银联成功，平台失败[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " insert into t_chk_err (clear_date,entr_no,busi_no,busi_idx_type," //
					+ "                 plat_date,seq_no,open_brch,err_flag,host_chk_flag,oth_chk_flag,proc_flag,chk_msg) " //
					+ " ( select '" + szClrDate + "','" + szEntrNo+"','','',"
					+ " plat_date,seq_no,open_brch,'N','3','0','Y','主机有，第三方无' " //
					+ " from  t_loc_" + szEntrNo //
					+ " where substr(tx_flag,1,2) in ('13','21' ) ) ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==2.主机有，第三方无[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " insert into t_chk_err (clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,open_brch,"//
					+"            acct_brch,oth_seq, err_flag,host_chk_flag,oth_chk_flag,proc_flag,chk_msg)" //
					+ " ( select '" + szClrDate + "','" + szEntrNo + "','','',plat_date,seq_no,open_brch," //
					+ "           rtrim(substr(oth_uniq,1,11)),substr(oth_uniq,12),'N','2','1','Y','主机无，第三方有' " //
					+ " from t_loc_" + szEntrNo //
					+ " where substr(tx_flag,1,2) in ('31','12' ) )";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==2.主机无，第三方有[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			/*
			 * szSqlStr = " update t_chk_err a " // +
			 * " set (brch_no,open_brch,teller_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
			 * + "acct_no1,acct_no2,tx_date,tx_amt) = "// +
			 * "     (select b.brch_no,b.open_brch, b.teller_no,b.tx_code,b.host_date,b.host_seq,b.oth_date,b.oth_seq,"
			 * + "b.pay_acct_no,b.payee_acct_no,b.tx_date,b.tx_amt " // +
			 * "         from t_ncp_book b where a.plat_date=b.plat_date and a.seq_no=b.seq_no ) "
			 * // + " where a.clear_date='" + szClrDate + "' " // +
			 * " and exists (select * from t_ncp_book b where a.plat_date=b.plat_date and a.seq_no=b.seq_no ) "
			 * ; iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			 */
			szSqlStr = " MERGE INTO t_chk_err a "//
					+ " USING t_ncp_book b  "//
					+ " ON (a.plat_date=b.plat_date AND a.seq_no=b.seq_no) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.brch_no=b.brch_no, a.tx_code=b.tx_code, "//
					+ "     a.oth_date=b.oth_date, a.oth_seq=b.oth_seq,"//
					+ "     a.acct_no1=b.pay_acct_no, a.acct_no2=b.payee_acct_no,"//
					+ "     a.tx_date=b.tx_date, a.tx_amt=b.tx_amt,"//
					+ "     a.open_brch=b.open_brch, a.teller_no=b.teller_no,"//
					+ "     a.host_date=b.host_date, a.host_seq=b.host_seq ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==3.更新t_chk_err--t_ncp_book 差错信息[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " MERGE INTO t_chk_err a "//
					+ " USING t_host_" + szEntrNo + " b  "//
					+ " ON (a.plat_date=b.plat_date AND a.seq_no=b.seq_no) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.open_brch=b.open_brch, a.teller_no=b.teller_no,"//
					+ "     a.host_date=b.host_date, a.host_seq=b.host_seq ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==4.更新t_chk_err--t_host 差错信息[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " MERGE INTO t_chk_err a "//
					+ " USING t_ncp_fund_sett b  "//
					+ " ON (a.acct_brch=b.snd_brch_no and a.oth_seq=b.oth_seq) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.acct_no1=b.pay_acct_no, a.acct_no2=b.payee_acct_no,"//
					+ "     a.charge_fee=b.charge_fee, a.Logo_fee=b.LOGO_FEE, "//
					+ "     a.out_fee=b.out_fee, a.in_fee=b.in_fee ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==4.更新t_chk_err--t_fund_sett 差错信息[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			szSqlStr = " UPDATE t_chk_err a " //
					+ " SET tx_type_cup=substr(tx_code,9,4), err_plat_seq=0 "//
					+ " WHERE clear_date = '"+szClrDate+"'";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			SysPub.appLog("INFO", "==5.更新交易码为银联交易码,差错流水号为0[%d]", iRet);
			if ( iRet < 0 ) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "对账后事件[%d]", iRet);
				return -1;
			}
			
			//更新流水表
			szSqlStr = " MERGE INTO t_ncp_book a "//
					+ " USING t_loc_"+szEntrNo+" b  "//
					+ " ON (a.plat_date=b.plat_date AND a.seq_no=b.seq_no) "//
					+ " WHEN MATCHED THEN UPDATE "//
					+ " SET a.chk_flag=b.tx_flag, a.chk_msg=b.chk_msg ";
			iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
			if(iRet < 0){
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新流水表失败[%d]", iRet);
				return -1;
			}else if(0 == iRet){
				//不在流水表，更新历史流水表
				szSqlStr = " MERGE INTO t_ncp_book_hist a "//
						+ " USING t_loc_"+szEntrNo+" b  "//
						+ " ON (a.plat_date=b.plat_date AND a.seq_no=b.seq_no) "//
						+ " WHEN MATCHED THEN UPDATE "//
						+ " SET a.chk_flag=b.tx_flag, a.chk_msg=b.chk_msg ";
				iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
				if(iRet < 0){
					iRet = DataBaseUtils.rollback(executer);
					SysPub.appLog("ERROR", "更新流水表失败[%d]", iRet);
					return -1;
				}
			}
			SysPub.appLog("INFO", "更新流水表记录[%d]", iRet);
			
			EPOper.put(tpID, "T_CHK_SYS[0].CHK_STAT", "S"); // S-处理完成
			EPOper.put(tpID, "T_CHK_SYS[0].CLEAR_STAT", "0"); // 0—待处理

			iRet = ChkPub.UptChkSys(executer);
			if (0 != iRet) {
				iRet = DataBaseUtils.rollback(executer);
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}

			iRet = DataBaseUtils.commit(executer);
			SysPub.appLog("INFO", "更新对账控制clear_stat状态为0");
			SysPub.appLog("INFO", "[%s]对账完成0", szClrDate);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行对账后事件失败");
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/**
	 * 
	 * @param eleNa
	 *            数据对象名称
	 * @param tpId
	 *            数据池ID
	 * @return 查询行数
	 * @throws Exception
	 */
	public static int getRowNum(String tpId, String eleNa) throws Exception {
		// 查询起始行
		String szBeginRec = (String) EPOper.get(tpId, "ISO_8583[0].iso_8583_041");

		// 每次查询行数
		String szNum = (String)EPOper.get(tpId, "ISO_8583[0].iso_8583_042");
		if (StringTool.isNullOrEmpty(szBeginRec))
			szBeginRec = "0";
		if (StringTool.isNullOrEmpty(szNum))
			szNum = "" + SysPubDef.MNG_QRY_COUNT;
		int iRowNum = Integer.parseInt(szBeginRec.trim()) + Integer.parseInt(szNum.trim()) - 1;
		return iRowNum;
	}

	/**
	 * 检查对账状态
	 * @param tpId
	 * @param chk_date
	 * @param entr_no
	 * @return
	 * @throws Exception 
	 */
	public static String chk_chk_stat(String tpId,String chk_date) throws Exception{
		String sql = "select chk_stat from t_chk_sys where chk_date = ? ";
		SysPub.appLog("INFO", "查询对账状态[%s]", sql);
		Object[] value = {chk_date};
		DataBaseUtils.queryToElem(sql, "T_CHK_SYS",value);
		String chk_stat = (String) EPOper.get(tpId,"T_CHK_SYS.CHK_STAT");
		return chk_stat;
	}
	
	public static void main(String[] args) throws Exception {
		String szClrDate1 = "20170909";
		String szNextDate = PubTool.calDateAdd(szClrDate1, "yyyyMMdd", 86400);
		SysPub.testLog("INFO", "%s : %s", szClrDate1, szNextDate);
		String szClrDate = "2017-09-09";
		String szDate = szClrDate.substring(0, 4) + szClrDate.substring(5, 7) + szClrDate.substring(8, 10);
		SysPub.testLog("INFO", "%s : %s", szClrDate, szDate);
	}
}
