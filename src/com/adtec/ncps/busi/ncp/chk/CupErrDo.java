package com.adtec.ncps.busi.ncp.chk;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;

/********************************************************
 * *
 * 
 * @author chenshx * 银联差错自动处理类 * *
 *******************************************************/

public class CupErrDo {

	/**
	 * @银联差错自动处理
	 *
	 */
	public static int CupErrDoMain() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szOthSeq = "";
		String szPre = "";
		String szSqlStr = "SELECT *  FROM t_ncp_err_detail " //
				+ " WHERE ERR_FLAG='A' order by sett_date ";
		try {
			int iRet = 0;
			int iCount = DataBaseUtils.queryToElem(szSqlStr, "T_NCP_ERR_DETAIL", null);
			SysPub.appLog("TRACE", "1.查询银联所有已确认的差错[%d]", iRet);
			for (int iNum = 0; iNum < iCount; iNum++) {
				szPre = "T_NCP_ERR_DETAIL[" + iNum + "].";
				szOthSeq = (String) EPOper.get(tpID, szPre + "OTH_SEQ");
				SysPub.appLog("TRACE", "开始处理第[%d]笔银联差错[%s]", iNum, szOthSeq);
				EPOper.put(tpID, szPre + "ERR_FLAG", "Y");// 为防止重复调账，更新为处理中
				UptErrDetail(szPre, "A");
				iRet = CupErrHost(szPre);
				if (iRet != 0) {
					SysPub.appLog("TRACE", "处理银联差错失败");
				}
				UptErrDetail(szPre, "Y");
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @银联差错核心调账
	 *
	 */
	public static int CupErrHost(String _szPre) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szErrMsg = "";
		String szRetMsg = "";
		try {
			String szSettDate = (String) EPOper.get(tpID, _szPre + "SETT_DATE");
			String szClrDate = (String) EPOper.get(tpID, _szPre + "CLEAR_DATE");
			if (!szSettDate.equals(szClrDate)) {
				SysPub.appLog("ERROR", "银联差错处理日期与差错日期不一致");
				EPOper.put(tpID, _szPre + "ERR_MSG", "银联差错处理日期与差错日期不一致");
				EPOper.put(tpID, _szPre + "ERR_FLAG", "F");
				return -1;
			}
			String szFileType = (String) EPOper.get(tpID, _szPre + "FILE_TYPE");
			String szErrType = (String) EPOper.get(tpID, _szPre + "ERR_TYPE");
			String HostPre = "HostErrOut[0].HOST_CLI_S801011_Req[0]."; //银联差错调账也调用801011
			String HostPreIn = "HostErrIn[0].HOST_CLI_PUB_Rsp[0].";
			if ("IS_ERRTRX".equals(szFileType)) {
				// 发卡机构差错交易明细文件 它代本
				// 依据业务要求、暂时只允许贷记调账-E30，E29，E23，E25，E32，E74，E84，E81，E73
				if ("E30".equals(szErrType) || "E29".equals(szErrType) || //
						"E23".equals(szErrType) || "E25".equals(szErrType) || //
						"E32".equals(szErrType) || "E74".equals(szErrType) || //
						"E84".equals(szErrType) || "E81".equals(szErrType) || //
						"E73".equals(szErrType)) {
					EPOper.put(tpID, HostPre + "Flag1", "0"); // 0-需要清算，1不需要清算 
					EPOper.put(tpID, HostPre + "Flag3", "0"); // 0客户帐贷方记帐
					EPOper.copy(tpID, tpID, _szPre + "PAYEE_ACCT_NO", HostPre + "AcctNo2");
				} else {
					szErrMsg = "该差错类型" + szErrType + "不允许调整挂账";
					SysPub.appLog("ERROR", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
					return -1;
				}
			} else if ("AC_ERRTRX".equals(szFileType)) {
				// 收单机构差错交易明细文件 本代它 暂无
				EPOper.put(tpID, _szPre + "ERR_MSG", "不支持该文件类型差错处理");
				EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
				return -1;
			} else {
				EPOper.put(tpID, _szPre + "ERR_MSG", "不支持该文件类型差错处理");
				EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
				return -1;
			}

			// 手续费判断
			double dChangeFee = (Double) EPOper.get(tpID, _szPre + "CHARGE_FEE");
			double dBrandFee = (Double) EPOper.get(tpID, _szPre + "LOGO_FEE");
			double dErrFee = (Double) EPOper.get(tpID, _szPre + "ERR_FEE");
			double dOutFee = (Double) EPOper.get(tpID, _szPre + "OUT_FEE");
			double dInFee = (Double) EPOper.get(tpID, _szPre + "IN_FEE");

			double dTotFee = Math.abs(dInFee - dOutFee + dChangeFee + dBrandFee + dErrFee); // 合计手续费
			/*
			if (dTotFee > 0.001) {
				EPOper.put(tpID, HostPre + "Flag3", "1"); // 0-无手续费 1-有手续费
				EPOper.put(tpID, HostPre + "Flag2", "0"); // 0-借:银联挂帐户 贷:错账挂账户
				EPOper.put(tpID, HostPre + "Flag6", "2"); // 手续费贷/借方向 2-贷手续费收入账户
			} else if (dTotFee < -0.001) {
				EPOper.put(tpID, HostPre + "Flag3", "1");
				EPOper.put(tpID, HostPre + "Flag2", "1"); // 1-贷::银联挂帐户 借:错账挂帐户
				EPOper.put(tpID, HostPre + "Flag6", "3"); // 手续费贷/借方向
				dTotFee = 0 - dTotFee; // 3-贷手续费收入账户红字
			} else {
				EPOper.put(tpID, HostPre + "Flag3", "0");
			}
			*/
			String szErrReason = (String) EPOper.get(tpID, _szPre + "ERR_REASON");
			String szSumry = "银联调整" + szErrReason;
			EPOper.put(tpID, HostPre + "MemoCode", "1130");// 摘要码
			String szClearDate = (String)EPOper.get(tpID, _szPre + "CLEAR_DATE");
			String szTemp = szClearDate.replaceAll("-", "");
			String szCupTxCode = (String)EPOper.get(tpID, _szPre + "TX_TYPE_CUP");
			EPOper.put(tpID, HostPre + "MsgCont", szTemp + "交易" + szCupTxCode + "对账差错"); //备注

			EPOper.copy(tpID, tpID, _szPre + "IN_AMT", HostPre + "Amt1");// 贷记本金
			EPOper.put(tpID, HostPre + "Amt2", dTotFee);// 清算手续费
			//手续费标识需要判断，有无手续费,无就不送
			if("".equals(dTotFee) || "null".equals(dTotFee) || "0.00".equals(dTotFee)){
				EPOper.put(tpID, HostPre + "Flag4", "");
			}else{
				EPOper.put(tpID, HostPre + "Flag4", "0"); // 手续费处理标识
			}

			BusiPub.getPlatSeq();
			EPOper.copy(tpID,tpID, "INIT[0].SeqNo", _szPre+"ERR_PLAT_SEQ" );
			int iSeqNo = (Integer) EPOper.get(tpID, _szPre + "ERR_PLAT_SEQ");
			EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_DATE", _szPre + "ERR_PLAT_DATE");
			String szPlatDate = (String) EPOper.get(tpID, _szPre + "ERR_PLAT_DATE");
			SysPub.appLog("INFO", "差错处理流水号[%s][%d]",szPlatDate,iSeqNo);

			EPOper.copy(tpID, tpID, _szPre + "ERR_BRCH_NO",  "INIT[0].BrchNo");
			EPOper.copy(tpID, tpID, _szPre + "RCK_TELLER",  "INIT[0].TlrNo");
			EPOper.copy(tpID, tpID, _szPre + "CLEAR_DATE", HostPre + "ClearDate");
			EPOper.put(tpID,  HostPre + "Ccy1", "01");
			EPOper.put(tpID,  HostPre + "Ccy2", "01");
			BusiMsgProc.headHost("HostErrOut", "S801011");
			EPOper.put(tpID, HostPre + "Desc2", "银联差错调整挂账");
			SysPub.appLog("INFO", "调用S801011服务开始");
			DtaTool.call("HOST_CLI", "S801011");
			String szRetCd = (String) EPOper.get(tpID, HostPreIn + "RspCode"); // 响应代码
			SysPub.appLog("INFO", "S801011响应码[%s]", szRetCd);
			if (!"000000".equals(szRetCd)) {
				szRetMsg = (String) EPOper.get(tpID, HostPreIn + "RspMsg"); // 响应信息
				SysPub.appLog("ERROR", "银联差错调整挂账[%s][%s]", szRetCd, szRetMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szRetCd +":"+ szRetMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "F");
				return -1;
			}
			EPOper.copy(tpID, tpID, HostPreIn + "SerSeqNo", _szPre + "ERR_HOST_SEQ");
			EPOper.copy(tpID, tpID, HostPreIn + "TranDate", _szPre + "ERR_HOST_DATE");
			EPOper.copy(tpID, tpID, HostPreIn + "Brc", _szPre + "ERR_BRCH_NO");
			EPOper.put(tpID, _szPre + "ERR_FLAG", "T");
			EPOper.put(tpID, _szPre + "ERR_MSG", "自动银联差错调整挂账成功");
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库错误");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @更新银联差错信息
	 * @_szPre 表数据对象前缀
	 * @_szOldFlag 原处理标志
	 */
	public static int UptErrDetail(String _szPre, String _szOldFlag) throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		// 更新银联差错信息
		String szSqlStr = "UPDATE t_ncp_err_detail " //
				+ " SET err_flag= ?, err_plat_date=?, err_plat_seq=?, " //
				+ "     err_host_date=?, err_host_seq=?, err_brch_no=?, err_msg = ? " //
				+ " where err_type=? AND snd_brch_no= ?  "//
				+ " AND oth_seq = ? AND ERR_FLAG=? ";
		try {
			String szErrFlag = (String) EPOper.get(tpID, _szPre + "ERR_FLAG");
			String szErrPlatDate = (String) EPOper.get(tpID, _szPre + "ERR_PLAT_DATE");
			int iErrPlatSeq = (Integer) EPOper.get(tpID, _szPre + "ERR_PLAT_SEQ");
			String szErrHostDate = (String) EPOper.get(tpID, _szPre + "ERR_HOST_DATE");
			String szErrHostSeq = (String) EPOper.get(tpID, _szPre + "ERR_HOST_SEQ");
			String szErrMsg = (String) EPOper.get(tpID, _szPre + "ERR_MSG");
			String szErrType = (String) EPOper.get(tpID, _szPre + "ERR_TYPE");
			String szSndBrchNo = (String) EPOper.get(tpID, _szPre + "SND_BRCH_NO");
			String szOthSeq = (String) EPOper.get(tpID, _szPre + "OTH_SEQ");
			String szErrBrchNo = (String) EPOper.get(tpID, _szPre + "ERR_BRCH_NO");

			Object[] value = { szErrFlag, szErrPlatDate, iErrPlatSeq, szErrHostDate, szErrHostSeq, 
					szErrBrchNo, szErrMsg, szErrType,szSndBrchNo, szOthSeq, _szOldFlag };
			// 更新对账控制表
			iRet = DataBaseUtils.execute(szSqlStr, value);
			SysPub.appLog("INFO", "更新银联差错信息[%d]",iRet);
			if (0 == iRet) {
				SysPub.appLog("ERROR", "更新对账控制表失败");
				return -1;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "更新银联差错信息失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
}
