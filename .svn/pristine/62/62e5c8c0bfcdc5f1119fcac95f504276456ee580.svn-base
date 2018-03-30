package com.adtec.ncps.busi.ncp.autodo;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

public class AutoSmsSnd {
	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年8月27日
	 * 
	 * @version 1.0 获取待发送的短信记录
	 */
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

			String sql = " SELECT * FROM t_sms_info "//
					+ " WHERE snd_stat ='0' order by snd_date,snd_times ";
			int iCount = DataBaseUtils.queryToElem(sql, "T_SMS_INFO", null);
			// 数据库没有待冲正数据，退出循环
			if (iCount < 0) {
				SysPub.appLog("ERROR", "数据库错误");
				return -1;
			}
			if (iCount == 0) {
				SysPub.appLog("ERROR", "没有待发送的短信记录");
				// 赋值为0，退出循环
				return -1;
			}
			SysPub.appLog("INFO", "处理待处理任务[%d]", (Integer) EPOper.get(tpID, "T_SMS_INFO[0].SMS_SEQ"));
			iRet = AutoPub.autoTaskChk("T_SMS_INFO");
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
			String sql = " UPDATE t_sms_info "//
					+ " SET snd_times=snd_times+1, snd_date =? , snd_stat = ?, "//
					+ " WHERE plat_date = ? AND seq_no = ?";
			Object[] value = new Object[6];
			value[0] = szDate;
			value[1] = _szStat;
			value[2] = EPOper.get(tpID, "INIT[0].__ERR_RET");
			value[3] = EPOper.get(tpID, "INIT[0].__ERR_MSG");
			value[4] = EPOper.get(tpID, "T_SMS_INFO[0].SMS_SEQ");
			value[5] = EPOper.get(tpID, "T_SMS_INFO[0].CHNL_NO");
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
	 * 自动发送短信平台处理
	 * 
	 * @author xiangjun
	 * @createAt 2017年8月27日
	 * @throws Exception
	 */
	public static int autoSmsDeal() throws Exception {
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
			else if( 1 == iRet )
			{
				//跳过本步骤
				return 0;
			}
			
			SysPub.appLog("INFO", "发送S610001到短信平台");
			
			BusiMsgProc.headSms("S610001");
			String szSmsData = (String) EPOper.get(tpID, "T_SMS_INFO[0].SMS_MSG");
			if (StringTool.isNullOrEmpty(szSmsData)) {
				BusiPub.setErrMsg(tpID, "AUTO002", "待发送短信内空为空");
				SysPub.appLog("ERROR", "待发送短信内空为空");
				AutoPub.uptWaitSnd("2");
				return -1;
			}
			String szFmtOut = "SndMsgIn[0].SMS_CLI_S610001_Rsp[0].Body.";
			EPOper.copy(tpID, tpID, "T_SMS_INFO[0].PHN", szFmtOut + "mobile");// 手机号
			EPOper.copy(tpID, tpID, "T_SMS_INFO[0].SMS_MSG", szFmtOut + "msg");// 短信内容
			EPOper.put(tpID, szFmtOut + "sendlevel", "6");//优先级别
			//业务ID
			//业务名称
			//运营商服务号码
			
			SysPub.appLog("INFO", "调用S610001服务开始");
			DtaTool.call("SMS_CLI", "S610001");
			String szFmtIn = "SndMsgIn[0].SMS_CLI_S610001_Req[0].Body.";
			String szRetCd = (String) EPOper.get(tpID, szFmtIn + "RspCode"); // 响应代码
			String szRetMsg = (String) EPOper.get(tpID, szFmtIn + "RspMsg"); // 响应信息
			SysPub.appLog("INFO", "S610001响应码[%s][%s]", szRetCd, szRetMsg);
			if (null == szRetCd || 0 == szRetCd.length()) {
				AutoPub.uptWaitSnd("0");// 次数++
				SysPub.appLog("ERROR", "短信平台处理超时");
				return -1;
			}
			if (!"000000".equals(szRetCd)) {
				BusiPub.setErrMsg(tpID, szRetCd, szRetMsg);
				AutoPub.uptWaitSnd("2");
				SysPub.appLog("ERROR", "短信平台处理失败[%s][%s]", szRetCd, szRetMsg);
				return -1;
			}

			if ( null == szRetMsg || 0 == szRetMsg.length()) {
				szRetMsg = "短信发送成功";
			}
			BusiPub.setErrMsg(tpID, szRetCd, szRetMsg);
			AutoPub.uptWaitSnd("1");
			SysPub.appLog("INFO", "无卡支付短信发送成功");
			AutoPub.uptDealNum(tpID);
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", e.getMessage());
			throw e;
		}
		//return 0;
	}
}
