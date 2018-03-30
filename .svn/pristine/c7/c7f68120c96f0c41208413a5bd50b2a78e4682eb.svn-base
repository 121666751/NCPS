package com.adtec.ncps.busi.ncp.autodo;

import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.respool.StarringSeq;
import com.adtec.starring.struct.dta.DtaInfo;

public class AutoDay {

	/**
	 * 更新冲正状态
	 * 
	 * @author dingjunbo
	 * @createAt 2017年6月30日
	 * @throws Exception
	 */
	public static int dayCut() throws Exception {
		try {
			int iRet = 0;
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				// 赋值为0，退出循环
				EPOper.put(tpID, "T_AUTO_PARA[0].DEAL_NUM", 0);
				return -1;
			} else if (1 == iRet) {
				// 跳过本步骤
				return 0;
			}

			// 下次日切日期和时间
			String szPlatdate = (String) EPOper.get(tpID,"T_PLAT_PARA[0].PLAT_DATE");
			String szEndDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].DAYEND_DATE");
			String szEndTm = (String) EPOper.get(tpID, "T_PLAT_PARA[0].DAYEND_TIME");
			String szEndDateTm = szEndDate + szEndTm;
			// String szEndDateTm = "20170823112135";
			String reg = "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";
			szEndDateTm = szEndDateTm.replaceAll(reg, "$1-$2-$3T$4:$5:$6");
			// 本地日切和时间
			String szLocDate = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");
			long lDateSub = PubTool.subDate(szLocDate, szEndDateTm, "yyyy-MM-dd'T'HH:mm:ss");
			SysPub.appLog("INFO", "本地时间[%s]下次日切时间[%s]时间间隔[%s]（时间间隔大于1可以执行日切）", szLocDate, szEndDateTm, lDateSub);
			if (1 < lDateSub) {
				String szPlatNo = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_NO");
				// 计算下次日切的时间
				int iSec = (Integer) EPOper.get(tpID, "T_PLAT_PARA[0].DAYEND_SEC");
				szEndDateTm = PubTool.calDateAdd(szLocDate, "yyyy-MM-dd'T'HH:mm:ss", iSec);

				szEndDate = szEndDateTm.substring(0, 4) + szEndDateTm.substring(5, 7) + szEndDateTm.substring(8, 10);
				szEndTm = szEndDateTm.substring(11, 13) + szEndDateTm.substring(14, 16) + szEndDateTm.substring(17, 19);
				SysPub.appLog("INFO", "更新平台日期[%s]下次日切时间[%s][%s]", szEndDate, szEndDate, szEndTm);

				DBExecuter executer = DataBaseUtils.conn();
				String szSqlStr = " UPDATE t_plat_para "//
						+ " SET PLAT_DATE=?, DAYEND_FLAG =0,"//
						+ " DAYEND_DATE = ?, DAYEND_TIME=?,"//
						+ " CLEAR_DATE = ?, ACT_DATE = ?"
						+ " WHERE PLAT_NO = ? ";
				Object[] value = new Object[6];
				value[0] = szEndDate;
				value[1] = szEndDate;
				value[2] = szEndTm;
				value[3] = szEndDate;
				value[4] = szPlatdate;
				value[5] = szPlatNo;
				
				try {
					iRet = DataBaseUtils.executenotr(executer, szSqlStr, value);
				} catch (Exception e) {
					if (iRet <= 0) {
						iRet = DataBaseUtils.rollback(executer);
						SysPub.appLog("ERROR", "更新数据库错误");
						return -1;
					}
				}
				// 更新日终步骤
				szSqlStr = " UPDATE t_end_step SET BAT_STAT='I' ";
				try {
					iRet = DataBaseUtils.executenotr(executer, szSqlStr, null);
				} catch (Exception e) {
					if (iRet <= 0) {
						iRet = DataBaseUtils.rollback(executer);
						SysPub.appLog("ERROR", "更新数据库错误");
						return -1;
					}
				}
				iRet = DataBaseUtils.commit(executer);
				SysPub.appLog("INFO", "日切成功");
			} else {
				SysPub.appLog("INFO", "日切时间未到");
				return 0;
			}
			//重置客户化流水
			StarringSeq.clearCustomSeq("1");
			StarringSeq.clearCustomSeq("2");
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", e.getMessage());
			throw e;
		}
	}

	/**
	 * 执行日终步骤
	 * 
	 * @author chenshx
	 * @createAt 2017年8月24日 _iStep 步骤号 _szShell 执行脚本
	 * @throws Exception
	 */
	public static int endStepDeal(int _iStep, String _szShell) throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}
		try {
			iRet = chkEndStep(tpID, _iStep);
			if (0 != iRet) {
				// 未通过检查，跳过当前步骤
				return 0;
			}

			SysPub.appLog("INFO", "执行日终步骤[%d] ", _iStep);
			// 更新步骤的开始时间
			uptEndStep(tpID, _iStep, "0");
			SysPub.appLog("INFO", "执行SHELL脚本[%s] ", _szShell);
			iRet = SysPub.callShell(_szShell);
			if (0 != iRet) {
				SysPub.appLog("ERROR", "执行SHELL脚本失败");
				return -1;
			}
			// 更新步骤的结束时间和状态
			uptEndStep(tpID, _iStep, "1");
			SysPub.appLog("INFO", "日终步骤[%d]执行成功", _iStep);
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "日终[%d]处理失败", _iStep);
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年8月24日
	 * 
	 * @version 1.0 检查是否是当前处理步骤
	 * 
	 * @返回值 0 可以继续处理 1当前步骤已经处理
	 */
	public static int chkEndStep(String _sztpID, int _iStep) throws Exception {
		// 获取数据源
		try {
			String szSql = "SELECT * FROM t_end_step "//
					+ " WHERE BAT_STAT='I' AND rownum=1 "//
					+ " ORDER BY BAT_NO asc ";
			int iRet = DataBaseUtils.queryToElem(szSql, "T_END_STEP", null);
			if (iRet == 0) {
				EPOper.put(_sztpID, "INIT[0].StepStat", "Continue");
				SysPub.appLog("DEBUG", "无待处理日终步骤");
				return -1;
			}
			int iStep = (Integer) EPOper.get(_sztpID, "T_END_STEP[0].BAT_NO");
			SysPub.appLog("DEBUG", "当前步骤[%d]处理步骤[%d]", iStep, _iStep);
			if (iStep > _iStep) {
				SysPub.appLog("INFO", "当前步骤已经处理");
				return 1;
			} else if (iStep < _iStep) {
				SysPub.appLog("ERROR", "还未到当前处理步骤");
				return -1;
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			throw e;
		}
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年8月24日
	 * 
	 * @version 1.0 更新日终步骤 szFlag 0--开始时间 1-结束时间
	 */
	public static int uptEndStep(String _sztpID, int _iStep, String _szFlag) throws Exception {
		// 获取数据源
		try {
			int iRet = 0;
			String szUptSql = "";
			String szDate = PubTool.getDate8();
			String szTime = PubTool.getTime();
			if ("0".equals(_szFlag)) {
				szUptSql = "BEG_DATE=?, BEG_TIME=? ";
			} else {
				szUptSql = "END_DATE=?, END_TIME=?,BAT_STAT='Y' ";
			}
			String szSqlStr = " UPDATE t_end_step " //
					+ " SET " + szUptSql //
					+ " WHERE bat_no = ?";
			Object[] value = new Object[3];
			value[0] = szDate;
			value[1] = szTime;
			value[2] = _iStep;
			try {
				iRet = DataBaseUtils.execute(szSqlStr, value);
				if (iRet <= 0) {
					SysPub.appLog("ERROR", "更新数据库错误");
					return -1;
				}
				return 0;
			} catch (Exception e) {
				SysPub.appLog("ERROR", "更新数据库错误");
				return -1;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			throw e;
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long lDateSub = 6124390L;
		if (1 < lDateSub) {
			System.out.println("1 < " + lDateSub);
		}

	}
}
