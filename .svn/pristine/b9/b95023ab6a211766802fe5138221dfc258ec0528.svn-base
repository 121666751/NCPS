package com.adtec.ncps.busi.ncp.autodo;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.AmountUtils;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

public class AutoHostRev {
	/**
	 * 核心自动冲正处理
	 * 
	 * @author dingjunbo
	 * @createAt 2017年6月30日
	 * @throws Exception
	 */
	public static int autoHostDeal() throws Exception {
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

			SysPub.appLog("INFO", "发送S818888到核心");
			BusiMsgProc.headHost("HostRevIn", "S818888");
			String szTranDate = (String) EPOper.get(tpID, "T_NCP_WAIT_SND[0].DATA");
			if (StringTool.isNullOrEmpty(szTranDate)) {
				BusiPub.setErrMsg(tpID, "AUTO002", "待发送报文内空为空");
				SysPub.appLog("ERROR", "待发送报文内空为空");
				AutoPub.uptWaitSnd("2");
				return -1;
			}
			Object[] oValue = szTranDate.split("\\|");
			String szFmtOut = "HostRevIn[0].HOST_CLI_S818888_Req[0].";
			String szFmtIn = "HostRevOut[0].HOST_CLI_S818888_Rsp[0].";
			String szPlatDate = (String) oValue[0];
			String szFmtDate = szPlatDate.substring(0, 4) + "-" + szPlatDate.substring(4, 6) + "-"
					+ szPlatDate.substring(6, 8);
			EPOper.put(tpID, szFmtOut + "PrimTranDate", szFmtDate);
			EPOper.put(tpID, szFmtOut + "Srvstan", oValue[1]);
			EPOper.put(tpID, szFmtOut + "ChannelId", oValue[2]);
			EPOper.put(tpID, szFmtOut + "FrntNo", oValue[3]);
			//EPOper.put(tpID, szFmtOut + "Flag1", oValue[4]);
			EPOper.put(tpID, szFmtOut + "Flag", oValue[5]);
			
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_068", "1");// 1 借 2 贷

			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_040", oValue[7]);// 原金额
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_045", szPlatDate );// 原平台日期
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_078", oValue[1] );// 原平台流水
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_052", oValue[1] );// 原平台流水
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_030", oValue[6] );// 原平台流水
			
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_002", "00110");// 机构号
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_003", "00110");// 机构号
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_007", "000433");// 柜员号
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_010", "NCPS");// 系统编号
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_016", "6235");// 交易码
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_031", oValue[8]);// 收款帐号
		

			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_068", "2");// 现金：1  转账：2
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_066", oValue[9]);// 解：1 贷：2

			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_071", "0");// 0 不收费 1收费
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_072", "2");// 1-现金，2-转账
			// 渠道种类
			EPOper.put(tpID, "HostRevIn[0].ISO_8583[0].iso_8583_127", "N");
			EPOper.put(tpID, szFmtOut + "Desc2", "无卡支付自动冲正");

			SysPub.appLog("INFO", "调用S818888服务开始");
			DtaTool.call("HOST_CLI", "S818888");
			String szRetCd = (String) EPOper.get(tpID, "HostRevOut[0].ISO_8583[0].iso_8583_012"); // 响应代码
			String szRetMsg = (String) EPOper.get(tpID, "HostRevOut[0].ISO_8583[0].iso_8583_013"); // 响应信息
			SysPub.appLog("INFO", "S818888响应码[%s][%s]", szRetCd, szRetMsg);
			if (null == szRetCd || 0 == szRetCd.length()) {
				AutoPub.uptWaitSnd("0");// 次数++
				SysPub.appLog("ERROR", "主机处理超时");
				return -1;
			}
			if (!"0000".equals(szRetCd)) {
				BusiPub.setErrMsg(tpID, szRetCd, szRetMsg);
				AutoPub.uptWaitSnd("2");
				SysPub.appLog("ERROR", "主机处理失败[%s][%s]", szRetCd, szRetMsg);
				return -1;
			}

			if ( null == szRetMsg || 0 == szRetMsg.length()) {
				szRetMsg = "主机冲正成功";
			}
			BusiPub.setErrMsg(tpID, szRetCd, szRetMsg);
			AutoPub.uptWaitSnd("1");
			SysPub.appLog("INFO", "无卡支付自动冲正成功");
			AutoPub.uptDealNum(tpID);
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "自动冲正处理处理错误");
			throw e;
		}
	}
	


}
