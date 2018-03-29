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
 * @author chenshx * 对账差错自动处理类 * *
 *******************************************************/

public class ChkErrDo {

	/**
	 * @对账差错自动处理
	 *
	 */
	public static int ChkErrDoMain() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int iSeqNo = 0;
		String szPre = "";
		String szSqlStr = "SELECT *  FROM t_chk_err " //
				+ " WHERE ERR_FLAG='A' order by clear_date ";
		try {
			int iRet = 0;
			int iCount = DataBaseUtils.queryToElem(szSqlStr, "T_CHK_ERR", null);
			SysPub.appLog("TRACE", "1.查询对账后所有已确认的差错[%d]", iRet);
			for (int iNum = 0; iNum < iCount; iNum++) {
				szPre = "T_CHK_ERR[" + iNum + "].";
				iSeqNo = (Integer) EPOper.get(tpID, szPre + "SEQ_NO");
				// SysPub.appLog("TRACE", "szPre[%s]", szPre);
				SysPub.appLog("TRACE", "开始处理第[%d]笔银联差错[%d]", iNum, iSeqNo);
				EPOper.put(tpID, szPre + "ERR_FLAG", "Y");// 为防止重复调账，更新为处理中
				UptChkErr(szPre, "A");
				iRet = ChkErrAdjust(szPre);
				if (iRet != 0) {
					SysPub.appLog("TRACE", "处理银联差错失败");
				}
				UptChkErr(szPre, "Y");
			}
			SysPub.appLog("TRACE", "银联差错[%d]笔处理完成", iCount);
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "对账差错自动处理失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @对账差错处理
	 *
	 */
	public static int ChkErrAdjust(String _szPre) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szErrMsg = "";
		try {
			String szProcFlag = (String) EPOper.get(tpID, _szPre + "PROC_FLAG");
			if ("Y".equals(szProcFlag)) {
				szErrMsg = "调用平台有流水部分差错处理";
				SysPub.appLog("INFO", szErrMsg);
				// 调用有流水的差错处理方法
				ChkErrAdjustSub(_szPre, "SEQ");
			} else if ("Q".equals(szProcFlag)) {
				szErrMsg = "调用平台无流水的单边帐部分差错处理";
				SysPub.appLog("INFO", szErrMsg);
				// 调用无流水的差错处理方法-银联多的情况
				ChkErrAdjustSub(_szPre, "NOSEQ");
			} else if ("N".equals(szProcFlag)) {
				szErrMsg = "该差错无需进行差错处理";
				SysPub.appLog("INFO", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "T");
			} else {
				szErrMsg = "无效的差错处理类型-" + szProcFlag + "，无法自动处理";
				SysPub.appLog("ERROR", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "F");
				return -1;
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "对账差错处理失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @对账差错处理
	 *
	 */
	public static int ChkErrAdjustSub(String _szPre, String _szSeqFlag) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szErrMsg = "";
		try {
			String szPlatDate = (String) EPOper.get(tpID, _szPre + "PLAT_DATE");
			int iSeqNo = (Integer) EPOper.get(tpID, _szPre + "SEQ_NO");
			String szCupTxCode = (String) EPOper.get(tpID, _szPre + "TX_TYPE_CUP");
			SysPub.appLog("INFO", "处理差错[%s][%d][%s]_szSeqFlag[%s]", szPlatDate, iSeqNo, szCupTxCode, _szSeqFlag);

			if ("SEQ".equals(_szSeqFlag)) {
				EPOper.delete(tpID, "T_NCP_BOOK");
				String szSqlStr = "SELECT * FROM t_ncp_book  " //
						+ " WHERE plat_date = ? AND seq_no = ? ";
				Object[] value = { szPlatDate, iSeqNo };
				int iRet = DataBaseUtils.queryToElem(szSqlStr, "T_NCP_BOOK", value);
				if (iRet == 0) {
					szErrMsg = "流水表中对应记录不存在，需要手工处理";
					SysPub.appLog("ERROR", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_FLAG", "F");
					return -1;
				}
			}

			if (!"1001".equals(szCupTxCode) && !"1002".equals(szCupTxCode) //
					&& !"1003".equals(szCupTxCode) && !"1101".equals(szCupTxCode) //
					&& !"2001".equals(szCupTxCode)) {
				szErrMsg = "未知的调账类型,需要手工处理";
				SysPub.appLog("ERROR", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
				return -1;
			}
			String szHostFlag = (String) EPOper.get(tpID, _szPre + "HOST_CHK_FLAG");
			String szOthFlag = (String) EPOper.get(tpID, _szPre + "OTH_CHK_FLAG");

			SysPub.appLog("INFO", "szHostFlag=[%s]szOthFlag=[%s]", szHostFlag, szOthFlag);
			if ((("0".equals(szOthFlag) || "2".equals(szOthFlag))
					&& ("1".equals(szHostFlag) || "3".equals(szHostFlag) || "9".equals(szHostFlag)))
					|| ("9".equals(szOthFlag) && ("1".equals(szHostFlag) || "3".equals(szHostFlag)))) {
				/* 第三方少'0','2', 主机多'1','3', 冲正 */
				/* 第三方少'0','2', 主机对账一致辞'9', 冲正 */
				/* 第三方对账一致'9', 主机多'1','3', 冲正 */
				SysPub.appLog("INFO", "自动冲账处理开始");
				ChkErrHost(_szPre, "REV");
			} else if ((("1".equals(szOthFlag) || "3".equals(szOthFlag))
					&& ("0".equals(szHostFlag) || "2".equals(szHostFlag)))
					|| ("9".equals(szOthFlag) && ("0".equals(szHostFlag) || "2".equals(szHostFlag)))) {
				/* 第三方多'1','3', 主机少'0','2', 补账 */
				/* 第三方对账一致'9', 主机少'0','2', 补账 */
				SysPub.appLog("INFO", "自动补账处理开始");
				ChkErrHost(_szPre, "ACCT");
			}else{
				szErrMsg = "未知的调账类型,需要手工处理";
				SysPub.appLog("ERROR", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "对账差错处理失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @对账差错核心调账
	 * @szAcctFlag REV 冲正 ACCT 补账
	 */
	public static int ChkErrHost(String _szPre, String _szAcctFlag) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String szErrMsg = "";
		String szRetMsg = "";
		try {
			String HostPre = "HostErrOut[0].HOST_CLI_S801011_Req[0].";
			String HostPreIn = "HostErrIn[0].HOST_CLI_PUB_Rsp[0].";
			String szCupTxCode = (String) EPOper.get(tpID, _szPre + "TX_TYPE_CUP");
			String szCleardate = (String)EPOper.get(tpID, _szPre + "CLEAR_DATE");
			// 手续费
			double dChangeFee = (Double) EPOper.get(tpID, _szPre + "CHARGE_FEE");
			double dBrandFee = (Double) EPOper.get(tpID, _szPre + "LOGO_FEE");
			double dOutFee = (Double) EPOper.get(tpID, _szPre + "OUT_FEE");
			double dInFee = (Double) EPOper.get(tpID, _szPre + "IN_FEE");
			double dFee = 0.00;

			// double dTxAmt = (Double) EPOper.get(tpID, _szPre + "TX_AMT");//
			// 交易金额

			// 冲账
			if ("REV".equals(_szAcctFlag)) {
				if ("1001".equals(szCupTxCode) || "1002".equals(szCupTxCode) //
						|| "1003".equals(szCupTxCode)) {
					// 借记类服务
					EPOper.put(tpID, HostPre + "Flag1", "1"); // 0-需要清算，1不需要清算
					EPOper.put(tpID, HostPre + "Flag3", "0"); // 0客户帐贷方记帐;1内部帐记帐;2客户账借方记账
					EPOper.copy(tpID, tpID, _szPre + "ACCT_NO1", HostPre + "AcctNo2");
					//EPOper.put(tpID, HostPre + "MsgCont", "单边帐冲账");
				} else {
					szErrMsg = "该差错类型" + szCupTxCode + "不能自动处理，要手工处理";
					SysPub.appLog("ERROR", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
					return -1;
				}
			} else if ("ACCT".equals(_szAcctFlag)) {
				if ("1101".equals(szCupTxCode) || "2001".equals(szCupTxCode)) {
					// 贷记类服务
					EPOper.put(tpID, HostPre + "Flag1", "0"); // 0-需要清算，1不需要清算
					EPOper.put(tpID, HostPre + "Flag3", "0"); // 0客户帐贷方记帐;1内部帐记帐;2客户账借方记账
					dFee = Math.abs(dInFee - dOutFee + dBrandFee + dChangeFee);
					EPOper.put(tpID, HostPre + "Amt2", dFee); // 手续费
					//手续费标识需要判断，有无手续费,无就不送
					if("".equals(dFee) || "null".equals(dFee) || "0.00".equals(dFee)){
						EPOper.put(tpID, HostPre + "Flag4", "");
					}else{
						EPOper.put(tpID, HostPre + "Flag4", "0"); // 手续费处理标识
																  // 0记手续费并且清算，1退货手续费（红字）并且清算
					}
					//EPOper.put(tpID, HostPre + "MsgCont", "单边帐调帐");
					EPOper.copy(tpID, tpID, _szPre + "ACCT_NO2", HostPre + "AcctNo2");
				} else {
					szErrMsg = "该差错类型" + szCupTxCode + "不能自动处理，要手工处理";
					SysPub.appLog("ERROR", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
					EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
					return -1;
				}
			} else {
				szErrMsg = "该差错处理类型异常，不能自动处理，要手工处理";
				SysPub.appLog("ERROR", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szErrMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "X");
				return -1;
			}

			String szTemp = szCleardate.replaceAll("-", "");
			EPOper.put(tpID, HostPre + "MsgCont", szTemp + "交易" + szCupTxCode + "对账差错"); //备注
			//String szTxTypeCup = (String) EPOper.get(tpID, _szPre + "TX_TYPE_CUP");
			//EPOper.put(tpID, HostPre + "MemoCode", szTxTypeCup);// 摘要码
			EPOper.put(tpID, HostPre + "MemoCode", "1129");// 摘要码
			EPOper.copy(tpID, tpID, _szPre + "TX_AMT", HostPre + "Amt1");// 交易金额
			EPOper.put(tpID,  HostPre + "Ccy1", "01");
			EPOper.put(tpID,  HostPre + "Ccy2", "01");

			BusiPub.getPlatSeq();
			EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_DATE", _szPre + "ERR_PLAT_DATE");
			EPOper.copy(tpID, tpID, "INIT[0].SeqNo", _szPre + "ERR_PLAT_SEQ");
			int iSeqNo = (Integer) EPOper.get(tpID, _szPre + "ERR_PLAT_SEQ");
			EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_DATE", _szPre + "ERR_PLAT_DATE");
			String szPlatDate = (String) EPOper.get(tpID, _szPre + "ERR_PLAT_DATE");
			SysPub.appLog("INFO", "差错处理流水号[%s][%d]",szPlatDate,iSeqNo);

			EPOper.copy(tpID, tpID, _szPre + "ERR_BRCH_NO", "INIT[0].BrchNo");//机构柜员取调账机构柜员
			EPOper.copy(tpID, tpID, _szPre + "RCK_TELLER", "INIT[0].TlrNo");

			BusiMsgProc.headHost("HostErrOut", "S801011");
			EPOper.put(tpID, HostPre + "Desc2", "银联差错调整");
			EPOper.copy(tpID, tpID, _szPre + "CLEAR_DATE", HostPre + "ClearDate");

			SysPub.appLog("INFO", "调用S801011服务开始[%s]",_szAcctFlag);
			DtaTool.call("HOST_CLI", "S801011");
			String szRetCd = (String) EPOper.get(tpID, HostPreIn + "RspCode"); // 响应代码
			SysPub.appLog("INFO", "S801011响应码[%s]", szRetCd);
			if (!"000000".equals(szRetCd)) {
				szRetMsg = (String) EPOper.get(tpID, HostPreIn + "RspMsg"); // 响应信息
				SysPub.appLog("ERROR", "银联差错调整挂账[%s][%s]", szRetCd, szRetMsg);
				EPOper.put(tpID, _szPre + "ERR_MSG", szRetCd + ":" + szRetMsg);
				EPOper.put(tpID, _szPre + "ERR_FLAG", "F");
				return -1;
			}
			EPOper.copy(tpID, tpID, HostPreIn + "SerSeqNo", _szPre + "ERR_HOST_SEQ");
			EPOper.copy(tpID, tpID, HostPreIn + "TranDate", _szPre + "ERR_HOST_DATE");
			EPOper.copy(tpID, tpID, HostPreIn + "Brc", _szPre + "ERR_BRCH_NO");
			EPOper.put(tpID, _szPre + "ERR_FLAG", "T");
			if ("REV".equals(_szAcctFlag)) {
				EPOper.put(tpID, _szPre + "ERR_MSG", "自动冲账成功");
			}else if ("ACCT".equals(_szAcctFlag)) {
				EPOper.put(tpID, _szPre + "ERR_MSG", "自动补账成功");
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "对账差错核心调账处理失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @更新对账差错信息
	 * @_szPre 表数据对象前缀
	 * @_szOldFlag 原处理标志
	 */
	public static int UptChkErr(String _szPre, String _szOldFlag) throws Exception {
		int iRet = 0;
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		// 更新对账差错信息
		String szSqlStr = "UPDATE t_chk_err " //
				+ " SET err_flag= ?, err_plat_date=?, err_plat_seq=?, " //
				+ "     err_host_date=?, err_host_seq=?, err_msg =? " //
				+ " where plat_date = ? AND seq_no = ? AND err_flag = ? ";
		try {
			String szErrFlag = (String) EPOper.get(tpID, _szPre + "ERR_FLAG");
			String szErrPlatDate = (String) EPOper.get(tpID, _szPre + "ERR_PLAT_DATE");
			int iErrPlatSeq = (Integer) EPOper.get(tpID, _szPre + "ERR_PLAT_SEQ");
			String szErrHostDate = (String) EPOper.get(tpID, _szPre + "ERR_HOST_DATE");
			String szErrHostSeq = (String) EPOper.get(tpID, _szPre + "ERR_HOST_SEQ");
			String szErrMsg = (String) EPOper.get(tpID, _szPre + "ERR_MSG");
			String szPlatDate = (String) EPOper.get(tpID, _szPre + "PLAT_DATE");
			int iSeqNo = (Integer) EPOper.get(tpID, _szPre + "SEQ_NO");

			Object[] value = { szErrFlag, szErrPlatDate, iErrPlatSeq, szErrHostDate, szErrHostSeq, szErrMsg, szPlatDate,
					iSeqNo, _szOldFlag };
			// 更新对账控制表
			iRet = DataBaseUtils.execute(szSqlStr, value);
			SysPub.appLog("INFO", "更新对账差错信息[%d]",iRet);
			if (0 == iRet) {
				SysPub.appLog("ERROR", "更新对账差错信息失败");
				return -1;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "更新t_chk_err信息失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
}
