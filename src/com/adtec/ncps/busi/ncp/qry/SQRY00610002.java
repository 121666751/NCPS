package com.adtec.ncps.busi.ncp.qry;

import java.sql.PreparedStatement;

import javax.sql.DataSource;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.TermPubBean;
public class SQRY00610002 {
	/*
	 * @author liangjr
	 * @createAt 2017年9月8日
	 * @version 1.0 检查协议号有没有签约 返回值：0-未签约，1-已签约
	 */
	public static int Chk() throws Exception 
	{
		SysPub.appLog("INFO", "协议号检查开始");
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			//失败预处理
			TermPubBean.ecapTermFormat("协议支付解约失败！");
			EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "0");
			// 从报文获取签约信息
			EPOper.copy(szTpID,szTpID,"ISO_8583[0].iso_8583_025","T_NCP_BOOK[0].SIGN_NO");
			String szBMSDFXY = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_025");// 协议号
			SysPub.appLog("INFO", "检查账号有没有签约%s", szBMSDFXY);
			if (szBMSDFXY == null || "".equals(szBMSDFXY.trim()))
			{
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
				SysPub.appLog("ERROR", "协议号不能为空");
				TermPubBean.putTermRspCode("9999","协议号不能为空");
				return -1;
			}
			
			String szSql = " select * from t_ncp_sign where sign_no = '"+szBMSDFXY+"' and stat='Y'";
			SysPub.appLog("DEBUG", szSql);
			Object[] value = {};
			int iRet = DataBaseUtils.queryToElem(szSql, "T_NCP_SIGN", value);
			
			if( iRet != 1)
			{
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
				SysPub.appLog("ERROR", "该协议号不符合解约条件");
				TermPubBean.putTermRspCode("9999","该协议号不符合解约条件");
				return -1;
			}
						
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		SysPub.appLog("INFO", "协议号检查结束");
		return 0;	
	}
	
	/*
	 * @author liangjr
	 * @createAt 2017年9月8日
	 * @version 1.0 更新表状态并插入历史表和待发送表  返回值：0-成功，-1失败
	 */
	public static int Insert_Sign() throws Exception 
	{
		try
		{
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(szTpID);
			if ( iRet == -1) {
				SysPub.appLog("INFO", "检查账号有没有签约");
				return -1;
			}
			// 从报文获取签约信息
			String szBMSDFXY = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_025");// 账号
			// 从报文获取签约信息
			String szBrchNo = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_003");// 机构号
			// 从报文获取签约信息
			//String szUnSign_Chnl = (String) EPOper.get(szTpID, "TERM_NCP_QRY_610002_IN.ChannelId");// 渠道号			
			// 从报文获取签约信息
			String szUnSign_Teller = (String) EPOper.get(szTpID, "ISO_8583.iso_8583_007");// 柜员号
			// 解约时间
			String szTime6 = PubTool.getTime();
			String szTime8 = szTime6.substring(0, 2)+":"+szTime6.substring(2, 4)+":"+szTime6.substring(4, 6);
			String szUnSign_Time = PubTool.getDate10() + "T" + szTime8;	
			
			DataSource ds = DataBaseUtils.getDatasource();
			PreparedStatement pstmt = null;
			Integer rtn = -1;
			DBExecuter executer = new DBExecuter(ds, "", false);
			String szSql = " update t_ncp_sign set stat = 'N', unsign_date = '"+szUnSign_Time+"', "
					+ "unsign_brch = '"+szBrchNo+"', unsign_teller = '"+szUnSign_Teller+"'  where sign_no = ? and stat='Y'";
			pstmt = (PreparedStatement) executer.bind(szSql);
			SysPub.appLog("DEBUG", szSql);
			pstmt.setObject(1, szBMSDFXY);
			rtn = pstmt.executeUpdate();
			if (1 != rtn) {
				executer.rollback();
				SysPub.appLog("ERROR", "更新签约表信息失败 ");
				TermPubBean.putTermRspCode("9999","更新签约表信息失败");
				return -1;
			}
			//EPOper.copy(szTpID, szTpID, "TERM_NCP_QRY_610002_OUT[0].SerSeqNo", "T_NCP_BOOK[0].OTH_SEQ");
			TermPubBean.putTermRspCode("0000", "交易成功");
			executer.commit();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return 0;	
	}
	
	/*
	 * @author liangjr
	 * @createAt 2017年9月8日
	 * @version 1.0 更新表状态并插入历史表和待发送表  返回值：0-成功，-1失败
	 */
	public static int Send_Cups() throws Exception 
	{
		SysPub.appLog("INFO", "发往银联开始");
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		int iRet = SysPub.ChkStep(tpID);
		if ( iRet == -1) {
			SysPub.appLog("INFO", "检查账号有没有签约");
			EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "-1");
			return -1;
		}
		try {
			/*报文头赋值*/
			BusiPub.setCupHttpHead("TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].CupHttpHead[0]","0303");
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].MsgVer", "1000");
			// 报文发起日期时间
			EPOper.copy(tpID, tpID, "INIT[0].TRAN_DATETM",
					"TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].SndDt");
			// 交易类型
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].Trxtyp", 
					"0303");
			// 发起方所属机构标识
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].IssrId", 
					SysPubDef.BRANKNO);
			// 报文方向
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].Drctn", "11");
			// 签名证书序列号
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].SignSN",
					BusiPub.getUnionUserID());
			//摘要算法类型
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].MDAlgo", "1");
			//签名和密钥加密算法类
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgHeader[0].SignEncAlgo", "1");
			//业务种类
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].BizTp", "100001");
			
			/* 发起方信息 */
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].SderInf[0].SderIssrId", ResPool.configMap.get("CUP_OriIssrId"));
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].SderInf[0].SderAcctIssrId", ResPool.configMap.get("CUP_OriIssrId"));
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].ACCT_NO",
					"TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].SderInf[0].SderAcctId");
			/* 接收方信息 */
			EPOper.copy(tpID, tpID, "T_NCP_SIGN[0].SIGN_BRCH",
					"TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].RcverInf[0].RcverAcctIssrId");
			EPOper.copy(tpID, tpID, "T_NCP_SIGN.SIGN_NO",
					"TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].RcverInf[0].SgnNo");
			/* 交易信息 */
			// 生成平台流水号
			String szDate10 = PubTool.getDate();
			int iSeqNo = PubTool.sys_get_seq6();
			String szTrxId = szDate10.substring(4) + iSeqNo;
			SysPub.appLog("INFO", "szTrxId=%s", szTrxId);
			EPOper.put(tpID, "TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].TrxInf[0].TrxId", szTrxId);
			EPOper.copy(tpID, tpID, "INIT[0].TRAN_DATETM",
					"TERM_NCP_QRY_610002_IN[0].CUP_CLI_0303_OUT[0].MsgBody[0].TrxInf[0].TrxDtTm");
		
			// 调度银联CUP0303服务
			SysPub.appLog("INFO", "调用CUP0303服务开始");
			EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "0");
			DtaTool.call("CUP_CLI", "CUP0303");
									
		} catch (Exception e) {
			EPOper.put(tpID, "INIT._FUNC_RETURN", 0, "-1");
			SysPub.appLog("ERROR", "调用银联服务CUP0303失败");
			TermPubBean.putTermRspCode("9999", "调用银联服务CUP0303失败");
		}
		return 0;
	}
}
