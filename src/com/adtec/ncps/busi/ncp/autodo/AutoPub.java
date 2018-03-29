package com.adtec.ncps.busi.ncp.autodo;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

public class AutoPub {
	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年7月17日
	 * 
	 * @version 1.0 查询自动任务参数
	 */
	public static int qryAutoPara(String _dta_name, String _svc_name) throws Exception {
		try {
			String szSql = "SELECT * FROM t_auto_para WHERE dta_name=? AND svc_name=? ";
			Object[] value = { _dta_name, _svc_name };
			int iRet = DataBaseUtils.queryToElem(szSql, "T_AUTO_PARA", value);
			if (iRet <= 0) {
				SysPub.appLog("ERROR", "查询T_AUTO_PARA失败", iRet);
				return -1;
			}

			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			Integer iDealNum = (Integer) EPOper.get(tpID, "T_AUTO_PARA[0].DEAL_NUM");
			if (iDealNum == null || iDealNum == 0) {
				// 默认为30次
				iDealNum = 30;
			}
			EPOper.put(tpID, "T_AUTO_PARA[0].DEAL_NUM", iDealNum);

		} catch (Exception e) {
			throw e;
		}

		return 0;
	}

	/**
	 * @author dingjunbo 自动任务执行判断
	 * @return 1-大于时间间隔 2-小于时间间隔 3-超过最大次数 -1-失败
	 * @throws Exception
	 * @注意：字段名必须一样（SND_TIMES SND_DATE）
	 */
	public static int autoTaskChk(String _szFormName) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			int iInterval = (Integer) EPOper.get(tpID, "T_AUTO_PARA[0].INTVL");
			int iAdd = (Integer) EPOper.get(tpID, "T_AUTO_PARA[0].INTVL_ADD");
			int iTimes = (Integer) EPOper.get(tpID, _szFormName+"[0].SND_TIMES");
			int imax_times = (Integer) EPOper.get(tpID, "T_AUTO_PARA[0].MAX_TIMES");
			int iTotInt = iInterval + iTimes * iAdd;
			String szDate = PubTool.getDate("yyyy-MM-dd'T'HH:mm:ss");
			String szSendDate = (String) EPOper.get(tpID, _szFormName+"[0].SND_DATE");
			long lDateSub = PubTool.subDate(szDate, szSendDate, "yyyy-MM-dd'T'HH:mm:ss");
			SysPub.appLog("DEBUG", "iTimes:%s,imax_times:%s,szDate:%s,szSendDate:%s,lDateSub:%s,iTotInt:%s", iTimes,
					imax_times, szDate, szSendDate, lDateSub, iTotInt);
			if (iTimes >= imax_times) {
				SysPub.appLog("INFO", "超过最大发送次数");
				return 3;
			}
			if (lDateSub >= (long) iTotInt) {
				SysPub.appLog("INFO", "超过时间间隔，可以继续处理");
				return 1;
			} else {
				SysPub.appLog("INFO", "未超过时间间隔，需继续等待处理");
				return 2;
			}
		} catch (Exception e) {

			throw e;
		}
	}

	public static int autoGetWaitSnd(String _szDta, String _szSvc) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				// 赋值为0，退出循环
				EPOper.put(tpID, "T_AUTO_PARA[0].DEAL_NUM", 0);
				return -1;
			}

			if (_szDta == null || _szDta.length() == 0 || _szSvc == null || _szSvc.length() == 0) {
				EPOper.put(tpID, "T_AUTO_PARA[0].DEAL_NUM", 0);
				SysPub.appLog("ERROR", "DTA[%s]和服务码[%s]不能为空", _szDta, _szSvc);
				return -1;
			}

			String sql = " SELECT * FROM t_ncp_wait_snd "//
					+ " WHERE stat ='0' AND dta_name = '" + _szDta + "' AND svc_name='" + _szSvc + "' "//
					+ " AND rownum=1 ORDER BY snd_date ASC ";
			int iCount = DataBaseUtils.queryToElem(sql, "T_NCP_WAIT_SND", null);
			// 数据库没有待冲正数据，退出循环
			if (iCount < 0) {
				SysPub.appLog("ERROR", "数据库错误");
				return -1;
			}
			if (iCount == 0) {
				SysPub.appLog("ERROR", "没有待冲正记录");
				// 赋值为0，退出循环
				return -1;
			}
			SysPub.appLog("INFO", "处理待处理任务[%s][%d]", (String) EPOper.get(tpID, "T_NCP_WAIT_SND[0].PLAT_DATE"),
					(Integer) EPOper.get(tpID, "T_NCP_WAIT_SND[0].SEQ_NO"));
			iRet = autoTaskChk( "T_NCP_WAIT_SND" );
			if (iRet == 3)// 大于最大发送次数
			{
				BusiPub.setErrMsg(tpID, "AUTO001", "超过最大发送次数");
				SysPub.appLog("INFO", "超过最大发送次数");
				uptWaitSnd("2");
				EPOper.put(tpID, "INIT[0].StepStat", "Continue");
				AutoPub.uptDealNum(tpID);
				return 0;
			} else if (iRet == 2)// 小于时间间隔
			{
				// 赋值为0，退出循环
				SysPub.appLog("ERROR", "当前处理时间小于时间间隔");
				return -1;
			} else if (iRet == 1)// 大于发送时间间隔
			{
				SysPub.appLog("INFO", "开始处理任务");
				return 0;
			}

			SysPub.appLog("ERROR", "处理异常");
			return -1;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "自动冲正处理处理错误");
			throw e;
		}
	}

	/**
	 * 更新冲正状态
	 * 
	 * @author dingjunbo
	 * @createAt 2017年6月30日
	 * @throws Exception
	 */
	public static int uptWaitSnd(String _szStat) throws Exception {
		try {
			int iRt = 0;
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szDate = PubTool.getDate("yyyy-MM-dd'T'HH:mm:ss");
			// 更新次数-时间-状态-响应信息
			String sql = " UPDATE t_ncp_wait_snd "//
					+ " SET snd_times=snd_times+1, snd_date =? , stat = ?, "//
					+ "     ret_code=?, ret_msg = ? "//
					+ " WHERE plat_date = ? AND seq_no = ?";
			Object[] value = new Object[6];
			value[0] = szDate;
			value[1] = _szStat;
			value[2] = EPOper.get(tpID, "INIT[0].__ERR_RET");
			value[3] = EPOper.get(tpID, "INIT[0].__ERR_MSG");
			value[4] = EPOper.get(tpID, "T_NCP_WAIT_SND[0].PLAT_DATE");
			value[5] = EPOper.get(tpID, "T_NCP_WAIT_SND[0].SEQ_NO");
			try {
				iRt = DataBaseUtils.execute(sql, value);
				if (iRt <= 0) {
					SysPub.appLog("ERROR", "更新数据库错误");
					return -1;
				}
				return 0;
			} catch (Exception e) {
				SysPub.appLog("ERROR", "更新数据库错误");
				return -1;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", e.getMessage());
			throw e;
		}
	}

	/**
	 * 更新循环次数
	 * 
	 * @author dingjunbo
	 * @createAt 2017年6月30日
	 * @throws Exception
	 */
	public static void uptDealNum(String _sztpID) throws Exception {
		try {
			int iDealNum = (Integer) EPOper.get(_sztpID, "T_AUTO_PARA[0].DEAL_NUM");
			iDealNum--;
			EPOper.put(_sztpID, "T_AUTO_PARA[0].DEAL_NUM", iDealNum);
			SysPub.appLog("DEBUG", "更新处理次数为[%d]", iDealNum);

		} catch (Exception e) {
			SysPub.appLog("ERROR", e.getMessage());
			throw e;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int iDealNum = 10;
		iDealNum--;
		SysPub.testLog("DEBUG", "更新处理次数为[%d]", iDealNum);
		iDealNum--;
		SysPub.testLog("DEBUG", "更新处理次数为[%d]", iDealNum);
		iDealNum--;
		SysPub.testLog("DEBUG", "更新处理次数为[%d]", iDealNum);
	}
}
