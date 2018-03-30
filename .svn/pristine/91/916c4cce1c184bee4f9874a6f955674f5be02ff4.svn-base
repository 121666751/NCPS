package com.adtec.ncps.busi.ncp.sign;

import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo * 签约业务公共类 * *
 *******************************************************/
public class SSIGNPub {

	/**
	 * @author dingjunbo
	 * @createAt 2017年6月19日
	 * @version 动态验证码校验 0-通过，否则不通过 szKey 关联码 @协议号规则：
	 *          借记转账签约：发起机构的机构代码+服务机构的机构代码+YYYYMMDD（平台日期）+10位流水号（2步骤生成，左补零）。
	 *          其他：UP+发卡标识码（8字节）+签约发起机构标识码（8字节）+账户类型（2字节）+生成渠道类别(1字节-一般为0)
	 *          +签约时间（8字节，平台日期）+签约时间（6位）+10个0+10位流水号（2步骤生成，左补零）
	 */
	public static String crtSignNo() throws Exception {
		StringBuffer signNo = new StringBuffer();
		String szSignNo=null;
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			String svcName = dtaInfo.getSvcName();
			Integer seq = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
			// 流水号左补足10位，左补0
			String szSeq = String.format("%010d", seq);

			// 根据业务规则产生协议号编号
			String szSderIssrId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].SND_ACCT_BRCH");// 发起方账户所属机构标识
			String szRcverAcctIssrId = (String) EPOper.get(tpID,
					"fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId");// 接收方账户所属机构标识
			SysPub.appLog("DEBUG", "szRcverAcctIssrId[%s]",szRcverAcctIssrId);
			String szCupsReserved = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].CupHttpHead[0].CupsReserved");// 账户类型
			SysPub.appLog("DEBUG", "szRcverAcctTp：[%s]", szCupsReserved);
			String szRcverAcctTp = "01";// 若银联没有送，默认使用01
			if( !StringTool.isNullOrEmpty(szCupsReserved) && szCupsReserved.length() >= 2)
			{
				SysPub.appLog("DEBUG", "szCupsReserved[%s]", szCupsReserved);
				szRcverAcctTp = szCupsReserved.substring(0,2);
			}
			String szDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");// 平台日期
			String szTime = (String) EPOper.get(tpID, "INIT[0].TRAN_TIME");// 机器时间
			SysPub.appLog("DEBUG", "svcName：[%s]", svcName);
			if ("SSIGN0020202".equals(svcName))// 借记转账签约
			{
				if (!StringTool.isNullOrEmpty(szSderIssrId))
					signNo.append(szSderIssrId);
				if (!StringTool.isNullOrEmpty(szRcverAcctIssrId))
					signNo.append(szRcverAcctIssrId);
				signNo.append(szDate).append(szSeq);
			} else {
				signNo.append("UP");
				if (!StringTool.isNullOrEmpty(szRcverAcctIssrId))
					signNo.append(szRcverAcctIssrId);
				if (!StringTool.isNullOrEmpty(szSderIssrId))
					signNo.append(szSderIssrId);
				if (!StringTool.isNullOrEmpty(szRcverAcctTp))
					signNo.append(szRcverAcctTp);
				signNo.append("0").append(szDate).append(szTime).append("0000000000").append(szSeq);
			}
			szSignNo = signNo.toString();
			SysPub.appLog("INFO", "签约号：[%s]", szSignNo);
		} catch (Exception e) {
			e.printStackTrace();
			SysPub.appLog("ERROR", "调度crtSignNo方法错误");
		}
		return szSignNo;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月11日 sign_no-签约协议号 sign_chnl-渠道
	 * 
	 * @version 1.0 初始化签约登记簿
	 */
	public static int Init_t_ncp_sign(String sign_no, String sign_chnl) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szRcverAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");
			if (StringTool.isNullOrEmpty(szRcverAcctId))
				szRcverAcctId = "Null";
			EPOper.put(tpID, "T_NCP_SIGN[0].ACCT_NO", szRcverAcctId);
			EPOper.put(tpID, "T_NCP_SIGN[0].SIGN_BRCH", EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].IssrId"));
			// 空值和null都插入Null，方便查询
			String szSderAcctInf = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctInf");
			if (StringTool.isNullOrEmpty(szSderAcctInf))
				szSderAcctInf = "Null";
			;
			EPOper.put(tpID, "T_NCP_SIGN[0].PAY_ACCT_INFO", szSderAcctInf);
			EPOper.put(tpID, "T_NCP_SIGN[0].PHN", EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo"));
			String svcName = dtaInfo.getSvcName();
			// 0-协议支付签约 1-借记转账签约
			if ("SSIGN0020201".equals(svcName)) {
				EPOper.put(tpID, "T_NCP_SIGN[0].SIGN_TYPE", "0");
			} else if ("SSIGN0020202".equals(svcName)) {
				EPOper.put(tpID, "T_NCP_SIGN[0].SIGN_TYPE", "1");
			} else {
				EPOper.put(tpID, "T_NCP_SIGN[0].SIGN_TYPE", "");
			}
			EPOper.put(tpID, "T_NCP_SIGN[0].SIGN_NO", sign_no);
			EPOper.put(tpID, "T_NCP_SIGN[0].ACCT_NAME",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm"));
			EPOper.put(tpID, "T_NCP_SIGN[0].STAT", "Y");
			EPOper.copy(tpID, tpID, "INIT[0].TRAN_DATETM", "T_NCP_SIGN[0].SIGN_DATE");
			EPOper.put(tpID, "T_NCP_SIGN[0].UNSIGN_DATE", "");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp", "T_NCP_SIGN[0].CERT_TYPE");
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo", "T_NCP_SIGN[0].CERT_NO");
			String szSderAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctId");
			if (StringTool.isNullOrEmpty(szSderAcctId))
				szSderAcctId = "Null";
			EPOper.put(tpID, "T_NCP_SIGN[0].ACCT_NO2", szSderAcctId);
			EPOper.put(tpID, "T_NCP_SIGN[0].SIGN_CHNL", sign_chnl);
			EPOper.copy(tpID, tpID, "INIT[0].BrchNo", "T_NCP_SIGN[0].BRCH_NO");
			EPOper.copy(tpID, tpID, "INIT[0].TlrNo", "T_NCP_SIGN[0].SIGN_TELLER");
			EPOper.put(tpID, "T_NCP_SIGN[0].RMRK", "");
			EPOper.put(tpID, "T_NCP_SIGN[0].RMRK1", "");
			EPOper.put(tpID, "T_NCP_SIGN[0].RMRK2", "");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 Init_t_ncp_sign 方法失败");
			throw e;
		}
		return 0;
	}

	/**
	 * @author dingjunbo
	 * @param szOldSignNo
	 *            协议号
	 * @param szFlag
	 *            UPT-更新协议号，DEL-删除协议号，重新签约 INS-插入新的签约信息
	 * @version 1.0 旧签约号不为空更新旧的签约协议号，插入新的签约协议号
	 */
	public static int signInst(String _szSignNo, String szFlag) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			int iRet = -1;
			String szSqlStr = "";

			String szNewSignNo = SSIGNPub.crtSignNo();
			EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].BizInf[0].SgnNo", szNewSignNo);// 签约协议号
			EPOper.put(tpID, "T_NCP_BOOK[0].SIGN_NO", szNewSignNo);// 签约协议号

			// 更新已签约记录
			if ("UPT".equals(szFlag)) {
				szSqlStr = "update t_ncp_sign set sign_no = ? where sign_no = ?";
				Object[] Value = { szNewSignNo, _szSignNo };
				iRet = DataBaseUtils.execute(szSqlStr, Value);
				if (iRet <= 0) {
					SysPub.appLog("ERROR", "更新签约表(新的签约号错误)失败");
					return -1;
				}
				SysPub.appLog("INFO", "更新签约表(新的签约号错误)成功");
				return 0;
			} else if ("DEL".equals(szFlag)) {
				// 删除已签约记录--因为签约历史表有记录，不用事务直接删除就可以了
				szSqlStr = "delete from t_ncp_sign where sign_no = ?";
				Object[] Value = { _szSignNo };
				iRet = DataBaseUtils.execute(szSqlStr, Value);
				if (iRet <= 0) {
					SysPub.appLog("ERROR", "删除签约表信息失败");
					return -1;
				}
				SysPub.appLog("INFO", "删除旧签约信息成功，插入新的签约");
			} else if (!"INS".equals(szFlag)) {
				SysPub.appLog("ERROR", "标志位错误");
				return -1;
			}

			// 插入新的签约录入
			String szRcverAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");// 卡号/账号
//			String szIssrId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderIssrId");// 签约机构（发送机构标识）
			String SderAcctIssrId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctIssrId");// 发起方账户所属机构标识
			// 空值和null都插入Null，方便查询
			String szSderAcctInf = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctInf");// 支付账号信息
			if (StringTool.isNullOrEmpty(szSderAcctInf))
				szSderAcctInf = "Null";
			String szMobNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo");// 签约手机号
			// 0-协议支付签约 1-借记转账签约
			String szSigTye = "";// 签约类型
			String svcName = dtaInfo.getSvcName();
			if ("SSIGN0020201".equals(svcName)) {
				szSigTye = "0";
			} else if ("SSIGN0020202".equals(svcName)) {
				szSigTye = "1";
			}
			String szRcverNm = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm");// 账户名称
			String szStat = "Y";// 状态
			String szSignDate = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");// 签约时间
			String szUnSignDate = "";// 解约时间
			String szIDTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");// 证件类型
			String szIDNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");// 证件号码
			String szSderAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctId");// 卡号/账号2(转账签约发起方卡号)
			if (StringTool.isNullOrEmpty(szSderAcctId))
				szSderAcctId = "Null";
			String szSignChnl = (String) EPOper.get(tpID, "INIT[0].ChnlNo");// 签约渠道
			String szBrchNo = (String) EPOper.get(tpID, "INIT[0].BrchNo");// 签约机构
			String szSignTeller = (String) EPOper.get(tpID, "INIT[0].TlrNo");// 签约柜员
			String szOpenBrch = (String)EPOper.get(tpID, "T_NCP_BOOK_HIST[0].OPEN_BRCH");//开户机构
			String szlinkNo = (String)EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].Smskey");
			szSqlStr = "insert into t_ncp_sign values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'','',?,'','','')";
			Object[] istValue = { szRcverAcctId, SderAcctIssrId, szSderAcctInf, szMobNo, szSigTye, szNewSignNo, szRcverNm,
					szOpenBrch, szStat, szSignDate, szUnSignDate, szIDTp, szIDNo, szSderAcctId, szSignChnl, szBrchNo,
					szSignTeller, szlinkNo };
			iRet = DataBaseUtils.execute(szSqlStr, istValue);
			if (iRet <= 0) {
				SysPub.appLog("ERROR", "插入签约表失败");
				return -1;
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 signInst 方法失败");
			throw e;
		}
	}

	/**
	 * @author dingjunbo
	 * @param
	 * @version 1.0 取消签约处理
	 */
	public static int cancelSign(String _szStat) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			if( !"N".equals(_szStat) && !"P".equals(_szStat)){
				SysPub.appLog("ERROR", "签约状态错误");
				return -1;
			}
			/*
			SysPub.appLog("INFO", "转移签约信息到签约历史表");
			String szSqlStr = "";
			String szAccTno = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO");
			String szSignBrch = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_BRCH");
			String szPayAcctInfo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].PAY_ACCT_INFO");
			String szPhn = (String) EPOper.get(tpID, "T_NCP_SIGN[0].PHN");
			String szSignType = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_TYPE");
			String szSignNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
			String szAcctName = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NAME");
			String szStat = _szStat;
			String szSignDate = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_DATE");
			String szUnSignDate = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");;
			String szCertType = (String) EPOper.get(tpID, "T_NCP_SIGN[0].CERT_TYPE");
			String szCertNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].CERT_NO");
			String szAcctNo2 = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO2");
			String szSignChnl = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_CHNL");
			String szBrchNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].BRCH_NO");
			String szSignTeller = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_TELLER");
			String szUnSignChnl = (String) EPOper.get(tpID, "INIT[0].ChnlNo");
			String szUnSignBrch = (String) EPOper.get(tpID, "INIT[0].BrchNo");
			String szUnSignTeller = (String) EPOper.get(tpID, "INIT[0].TlrNo");
			szSqlStr = "insert into t_ncp_sign_hist values(?,?,?,?,?,?,?,0.00,0.00,?,?,?,?,?,?,?,?,?,?,?,?,'','','')";
			Object[] istValue = { szAccTno, szSignBrch, szPayAcctInfo, szPhn, szSignType, szSignNo, szAcctName, //
					szStat, szSignDate, szUnSignDate, szCertType, szCertNo, szAcctNo2, //
					szSignChnl, szBrchNo, szSignTeller, szUnSignChnl, szUnSignBrch, szUnSignTeller };
			iRet = DataBaseUtils.execute(szSqlStr, istValue);
			if (iRet <= 0) {
				SysPub.appLog("ERROR", "插入签约历史表失败");
				return -1;
			}
			*/
			String szUnSignDate = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");
			String szSignNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
			SysPub.appLog("INFO", "更改签约状态为已解约");
			String szSqlStr = " UPDATE t_ncp_sign SET stat=?, unsign_date=? "//
					+ " , unsign_brch='0088', unsign_teller='YLWKZF' where sign_no = ? ";
			Object[] uptValue = { _szStat, szUnSignDate,szSignNo };
			iRet = DataBaseUtils.execute(szSqlStr, uptValue);
			if (iRet <= 0) {
				SysPub.appLog("ERROR", "更新签约状态失败");
				return -1;
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 cancelSign 方法失败");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月11日 调用方法前，必须给T_NCP_SIGN对象赋值 sign_no-签约协议号 sign_chnl-渠道
	 * 
	 * @version 1.0 初始化签约历史登记簿
	 */
	public static int Init_t_ncp_sign_hist(String UNSIGN_CHNL) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO", "T_NCP_SIGN_HIST[0].ACCT_NO");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_BRCH", "T_NCP_SIGN[0].SIGN_BRCH");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].PHN", "T_NCP_SIGN_HIST[0].PHN");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_TYPE", "T_NCP_SIGN_HIST[0].SIGN_TYPE");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_NO", "T_NCP_SIGN_HIST[0].SIGN_NO");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NAME", "T_NCP_SIGN_HIST[0].ACCT_NAME");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].MIN_AMT", "T_NCP_SIGN_HIST[0].MIN_AMT");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].MAX_AMT", "T_NCP_SIGN_HIST[0].MAX_AMT");
			EPOper.put(tpID, "T_NCP_SIGN_HIST[0].STAT", "N");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_DATE", "T_NCP_SIGN_HIST[0].SIGN_DATE");
			EPOper.copy(tpID, tpID, "INIT[0].TRAN_DATETM", "T_NCP_SIGN_HIST[0].UNSIGN_DATE");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].CERT_TYPE", "T_NCP_SIGN_HIST[0].CERT_TYPE");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].CERT_NO", "T_NCP_SIGN_HIST[0].CERT_NO");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_CHNL", "T_NCP_SIGN_HIST[0].SIGN_CHNL");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].BRCH_NO", "T_NCP_SIGN_HIST[0].BRCH_NO");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_TELLER", "T_NCP_SIGN_HIST[0].SIGN_TELLER");
			EPOper.put(tpID, "T_NCP_SIGN_HIST[0].UNSIGN_CHNL", UNSIGN_CHNL);
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId",
					"T_NCP_SIGN_HIST[0].UNSIGN_BRCH");
			EPOper.put(tpID, "T_NCP_SIGN_HIST[0].UNSIGN_TELLER", "");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].RMRK", "T_NCP_SIGN_HIST[0].RMRK");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].RMRK1", "T_NCP_SIGN_HIST[0].RMRK");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].RMRK2", "T_NCP_SIGN_HIST[0].RMRK");
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}


	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日
	 * 
	 * @version 1.0 检查签约表有没有该账号及收单机构的签约信息， 若有且状态不是签约‘Y’状态，则删除该条记录，否则失败退出
	 * 0-未签约，1-已签约.2-已解约,3-信息变更失效 协议支付签约使用
	 */
	public static int qrySignByPay(String _szAcctNo, String _szSderBrch, String _szPayInfo) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szPayInfo = "";
			try {
				String sql = "SELECT * FROM t_ncp_sign "//
						+ " WHERE sign_type = '0' AND  acct_no = ? "//
						+ " AND sign_brch = ? AND pay_acct_info = ? ";
				// pay_acct_info标记为可送字段，取出来的值有可能为NULL
				if (StringTool.isNullOrEmpty(_szPayInfo)) {
					szPayInfo = "Null";
				} else {
					szPayInfo = _szPayInfo;
				}
				Object[] value = { _szAcctNo, _szSderBrch, szPayInfo };
				DataBaseUtils.queryToElem(sql, "T_NCP_SIGN", value);
				String szSignNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
				String szStat = (String) EPOper.get(tpID, "T_NCP_SIGN[0].STAT");
				// 如果没有签约信息，返回成功
				if (StringTool.isNullOrEmpty(szSignNo)) {
					SysPub.appLog("INFO", "该用户没有签约");
					return 0;
				} else if ("Y".equals(szStat.toUpperCase())) {
					SysPub.appLog("INFO", "该用户已签约[%s]", szSignNo);
					return 1;
				} else if ("N".equals(szStat.toUpperCase())) {
					SysPub.appLog("INFO", "该用户已解约[%s]", szSignNo);
					return 2;
				} else if ("P".equals(szStat.toUpperCase())) {
					SysPub.appLog("INFO", "该用户由于信息变更失效[%s]", szSignNo);
					return 3;
				} else {
					return 0;
				}
			} catch (Exception e) {
				SysPub.appLog("ERROR", "chkSign方法处理异常");
				throw e;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "chkSign方法处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日
	 * 
	 * @version 1.0 检查签约表有没有该账号及收单机构的签约信息， 若有且状态不是签约‘Y’状态，则删除该条记录，否则失败退出
	 * 0-未签约，1-已签约.2-已解约,3-信息变更失效 借记转账签约使用
	 */
	public static String qrySignByAcct(String acct_no, String acct_no2, String idTp, String idNo, String mobNo)
			throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			try {
				String sql = "SELECT * FROM t_ncp_sign "//
						+ " WHERE sign_type = '1' AND acct_no = ? AND acct_no2 = ? "//
						+ " AND cert_type = ? AND cert_no = ? AND phn = ?";
				if (StringTool.isNullOrEmpty(acct_no2))
					acct_no2 = "Null";
				Object[] value = { acct_no, acct_no2, idTp, idNo, mobNo };
				DataBaseUtils.queryToElem(sql, "T_NCP_SIGN", value);
				String SIGN_NO = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
				String STAT = (String) EPOper.get(tpID, "T_NCP_SIGN[0].STAT");
				// 如果没有签约信息，返回成功
				if (StringTool.isNullOrEmpty(SIGN_NO)) {
					return "0";
				} else if ("Y".equals(STAT.toUpperCase())) {
					return "1";
				} else if ("N".equals(STAT.toUpperCase())) {
					return "2";
				} else if ("P".equals(STAT.toUpperCase())) {
					return "3";
				} else {
					return "0";
				}
			} catch (Exception e) {
				SysPub.appLog("ERROR", "chkSign方法处理异常");
				throw e;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "chkSign方法处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月6日
	 * 
	 * @version 1.0 验证身份证信息
	 */
	public static int chkCustInfo() throws Exception {
		try {
			// 比较证件类型和号码
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szAcctNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NO");
			String szAcctName = (String) EPOper.get(tpID, "T_NCP_SIGN[0].ACCT_NAME");
			String szCertType = (String) EPOper.get(tpID, "T_NCP_SIGN[0].CERT_TYPE");
			String szCertNo = (String) EPOper.get(tpID, "T_NCP_SIGN[0].CERT_NO");
			String szIDTp = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");
			String szIDNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");
			String szRcverAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");
			String szRcverNm = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm");
			if (!StringTool.isNullOrEmpty(szRcverAcctId)) {
				if (!szRcverAcctId.equals(szAcctNo)) {
					BusiPub.setCupMsg("PB005X03", "身份认证失败-账号不符", "2");
					SysPub.appLog("ERROR", "PB005X03-身份认证失败-账号不符");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szRcverNm)) {
				if (!szRcverNm.equals(szAcctName)) {
					BusiPub.setCupMsg("PB005X03", "身份认证失败-户名不符", "2");
					SysPub.appLog("ERROR", "PB005X03-身份认证失败-户名不符");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szIDTp)) {
				if (!szIDTp.equals(szCertType)) {
					BusiPub.setCupMsg("PB005X02", "身份认证失败-证件类型不符", "2");
					SysPub.appLog("ERROR", "PB005X02-身份认证失败-证件类型不符");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szIDNo)) {
				if (!szIDNo.equals(szCertNo)) {
					BusiPub.setCupMsg("PB005X03", "身份认证失败-证件号码不符", "2");
					SysPub.appLog("ERROR", "PB005X03-身份认证失败-证件号码不符");
					return -1;
				}
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 checkBusiLogic 方法失败");
			throw e;
		}
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}
}
