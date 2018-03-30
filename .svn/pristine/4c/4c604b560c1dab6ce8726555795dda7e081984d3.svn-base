package com.adtec.ncps.busi.ncp.autodo;

import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.struct.dta.DtaInfo;

public class Auto_day001 {

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
						+ " SET PLAT_DATE=?, DAYEND_FLAG =0 , "//
						+ " DAYEND_DATE = ?, DAYEND_TIME=?"//
						+ " WHERE PLAT_NO = ? ";
				Object[] value = new Object[4];
				value[0] = szEndDate;
				value[1] = szEndDate;
				value[2] = szEndTm;
				value[3] = szPlatNo;
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
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", e.getMessage());
			throw e;
		}
	}
}
