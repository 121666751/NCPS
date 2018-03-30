package com.adtec.ncps.busi.ncp.acct;

import java.sql.SQLException;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 退款处理类 * *
 *******************************************************/
public class SACCT0021101 {
	/**
	 * @公共报文赋值
	 * @throws Exception
	 */
	public static void chk() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			BusiMsgProc.putCupBizInfMsg(tpID);
			BusiMsgProc.putCupPyerInfMsg(tpID);
			BusiMsgProc.putCupPyeeInfMsg(tpID);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "公共报文赋值处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月7日
	 * 
	 * @version 1.0 查找原交易信息，并核对原交易信息
	 */
	public static int chkRetBusi() throws Exception {

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			// 根据原交易信息查询流水表
			String szSndBrch = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyerInf[0].PyeeIssrId");// 发送机构标识
			String szOriTrxId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriTrxId");// 原支付交易流水号
			// 查询原交易流水信息
			iRet = BusiPub.qryCupBook(szSndBrch, szOriTrxId, "0");
			if (-1 == iRet) {
				SysPub.appLog("ERROR", "查询原交易流水信息失败");
				return -1;
			} else if (0 == iRet) {
				BusiPub.setCupMsg("PB622021", "原支付交易流水号不存在", "2");
				SysPub.appLog("ERROR", "PB622021-原支付交易流水号不存在");
				return -1;
			}

			// 检查签约协议号
			String szSignNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].SgnNo");// 协议号
			String szPyeeAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctId");// 收款账号
			if (!StringTool.isNullOrEmpty(szSignNo)) {
				// 检查协议号 0-未签约，1-已签约.2-已解约,3-信息变更失效
				iRet = BusiPub.qrySignBySignNo(szSignNo);
				if (0 == iRet) {
					BusiPub.setCupMsg("PB521014", "接收方机构查无此签约协议号", "2");
					SysPub.appLog("ERROR", "PB521014-接收方机构查无此签约协议号");
					return -1;
				}
				/*移到外层if
				String szPyeeAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].PyeeInf[0].PyeeAcctId");// 收款账号
				*/
				String szAcctNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO");// 签约账号
				if (!StringTool.isNullOrEmpty(szPyeeAcctId)) {

					if (!szAcctNo.equals(szPyeeAcctId)) {
						BusiPub.setCupMsg("PB520097", "收款账号非签约账号", "2");
						SysPub.appLog("ERROR", "PB520097-收款账号非签约账");
						return -1;
					}
				} else {
					// 银联未送账号，则需要返回账号
					EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].PyeeInf[0].PyeeAcctId", szAcctNo);
				}
			}else{
				//银联不送协议号时，检查收款方账户
				if(StringTool.isNullOrEmpty(szPyeeAcctId)){
					SysPub.appLog("ERROR", "PB030X01-协议号和账号不能同时为空");
					BusiPub.setCupMsg("PB030X01", "协议号和账号不能同时为空", "2");
					return -1;
				}
				//银联不送协议号时，收款账号赋值
				EPOper.put(tpID, "T_NCP_SIGN[0].ACCT_NO", szPyeeAcctId );
			}

			// 原订单号
			String szOriOrdrId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriOrdrId");
			String szOrderNo = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].ORDER_NO");
			if (!szOrderNo.equals(szOriOrdrId)) {
				BusiPub.setCupMsg("PB520097", "原订单号不正确", "2");
				SysPub.appLog("ERROR", "PB520097-原订单号不正确");
				return -1;
			}

			// 原交易日期时间
			String szOriTrxDtTm = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].OriTrxDtTm");
			String szOthSeq = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].OTH_DATE");
			if (!szOriTrxDtTm.equals(szOthSeq)) {
				BusiPub.setCupMsg("PB520097", "原交易日期时间不正确", "2");
				SysPub.appLog("ERROR", "PB520097-原交易日期时间不正确");
				return -1;
			}

			// 若原交易日期为366天前，则拒绝该笔交易
			String szOldDate = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PLAT_DATE");
			String currDate = PubTool.getDate8();
			if (PubTool.compare_date3(currDate, szOldDate) > 366) {
				BusiPub.setCupMsg("PB520007", "退货超过有效期", "2");
				SysPub.appLog("ERROR", "PB520007-退货超过有效期");
				return -1;
			}

			// 退货金额检查
			double dOriTxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].TX_AMT");
			double szTxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].TX_AMT");
			if (Double.compare(szTxAmt, dOriTxAmt) > 0) {
				BusiPub.setCupMsg("PB521023", "退货金额超过原交易金额", "2");
				SysPub.appLog("ERROR", "PB521023-退货金额超过原交易金额");
				return -1;
			}

			// 原交易状态为9,退货总金额+本次退货金额>原交易金额
			String STAT = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].STAT");// 交易状态为退货
			if ("9".equals(STAT)) {
				// 已退货金额
				Double dRrefundAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].REFUND_AMT");
				if (dRrefundAmt == null) {
					dRrefundAmt = 0.00;
				}
				double sumSmt = dRrefundAmt + szTxAmt;
				SysPub.appLog("INFO", "已退货金额：%s,本次退货金额:%s,原支付金额:%s", dRrefundAmt, szTxAmt, dOriTxAmt);
				if (Double.compare(sumSmt, dOriTxAmt) > 0) {
					BusiPub.setCupMsg("PB521023", "退货金额超过原交易金额", "2");
					SysPub.appLog("ERROR", "PB521023-退货金额超过原交易金额");
					return -1;
				}
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "checkTran 方法处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 退货主机记账
	 */
	public static int callHost() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].Desc2", "银联退货");
			EPOper.put(tpID, "fmt_CUP_SVR_IN[0].HOST_CLI_S805016_Req[0].MemoCode", "1128");
			BusiMsgProc.HostS805016ByCup(tpID);
			iRet = BusiPub.callHostSvc("S805016", "NOREV", "fmt_CUP_SVR");
			if (0 == iRet) {
				SysPub.appLog("INFO", "主机记账成功");
				BusiPub.setCupMsg(SysPubDef.CUP_SUC_RET, SysPubDef.CUP_SUC_MSG, "1");
				uptRefundAmt();
			}
			return iRet;

		} catch (Exception e) {
			SysPub.appLog("ERROR", "hostMsg 方法处理异常");
			throw e;
		}
	}

	/**
	 * @deprecated更新退货金额
	 * @return 更新记录数
	 * @throws SQLException
	 * @throws Exception
	 */
	public static int uptRefundAmt() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String sql = "update t_ncp_book "//
				+ " set stat = '9',refund_amt = refund_amt + ? "//
				+ " where plat_date = ? and seq_no = ? ";
		int iRt = 0;
		try {
			// 原交易信息保存在T_NCP_BOOK_HIST，直接根据流水号和日期更新原交易信息
			String szPlatDate = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PLAT_DATE");
			int iSeqNo = (Integer) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].SEQ_NO");
			double szTxAmt = (Double) EPOper.get(tpID, "T_NCP_BOOK[0].TX_AMT");
			Object[] value = { szTxAmt, szPlatDate, iSeqNo };
			iRt = DataBaseUtils.execute(sql, value);
			SysPub.appLog("INFO", "更新[%s][%d]退货金额+[%s]", szPlatDate, iSeqNo, szTxAmt);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return iRt;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 贷记报文赋值
	 */
	public static int credMsg() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.delete(tpID, "INIT[0].TxnCd");
			EPOper.put(tpID, "INIT[0].TxnCd", "030105");
			BusiMsgProc.headCred("030105");
			BusiMsgProc.msgBody030105();
			SysPub.appLog("INFO", "调用030105服务开始");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "credMsg 方法处理异常");
			throw e;
		}
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
