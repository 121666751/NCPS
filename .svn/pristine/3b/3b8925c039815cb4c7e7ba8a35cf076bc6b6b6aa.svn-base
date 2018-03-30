package com.adtec.ncps.busi.ncp;

import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.ParseException;

import javax.sql.DataSource;
import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.dao.BookDaoTool;
import com.adtec.ncps.busi.ncp.dao.BookExtDaoTool;
import com.adtec.ncps.busi.ncp.qry.SQRYPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
import com.adtec.starring.util.ftp.FtpToolkit;
import com.adtec.starring.file.util.FileUtil;



public class BusiPub {

	/*
	 * @author
	 * 
	 * @createAt 2017年8月8日
	 * 
	 * @version
	 */
	public static int getPlatSeq() {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int iSeqNo = PubTool.sys_get_seq();
		EPOper.put(tpID, "INIT[0].SeqNo", iSeqNo);
		return 0;
	}
//{'d','h','c','c','n','e','w'}
	private static byte BAL_COMM_PASSWD[]={'e','w','d','h','c','c','n'};
	private static byte BAL_COMM_PASSWDd[]={'d','h','c','c','n','e','w'};
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月17日
	 * 
	 * @version 1.0 获取账户类型（判断贷记卡还是借记卡） card_type为1-4返回
	 * 0-借记卡;card_type为5-6返回5-贷记卡; -1失败
	 */
	public static int getAcctType(String _szAcctNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// 判断数据合法性，卡号必须大于6位
			if (_szAcctNo == null || "".equals(_szAcctNo) || _szAcctNo.length() < 6)
				return -1;
			String sCardBin = _szAcctNo.substring(0, 6);
			String sql = "select * from t_card_bin where fit_code = ? ";
			try {
				pstmt = (PreparedStatement) executer.bind(sql);
				pstmt.setString(1, sCardBin);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					String card_type = rs.getString("card_type");
					if ("1".equals(card_type) || "2".equals(card_type) || "3".equals(card_type)
							|| "4".equals(card_type))
						return 0;
					else if ("5".equals(card_type) || "6".equals(card_type))
						return 5;
				}
				return -1;
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月17日
	 * 
	 * @version 1.0 INIT._CARD_TYPE 0-借记卡 5-贷记卡 0-成功 -1-失败
	 */
	public static int cupGetAcctType(String _szAcctNo) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 获取数据源
		try {
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			// 判断数据合法性，卡号必须大于6位
			if (_szAcctNo == null || "".equals(_szAcctNo) || _szAcctNo.length() < 6) {
				SysPub.appLog("ERROR", "无效卡号（发卡方无此主账号）");
				setCupMsg("PB014X01", "无效卡号（发卡方无此主账号）", "2");
				return -1;
			}
			int iCardType = getAcctType(_szAcctNo);
			if (iCardType == -1) {
				SysPub.appLog("ERROR", "无效卡号（发卡方无此主账号）");
				setCupMsg("PB014X01", "无效卡号（发卡方无此主账号）", "2");
				return -1;
			}
			EPOper.delete(tpID, "INIT[0]._CARD_TYPE");
			String szCardType = Integer.toString(iCardType);
			EPOper.put(tpID, "INIT[0]._CARD_TYPE", szCardType);
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年8月19日
	 * 
	 * @version 1.0 1-使用新的流水号 2-使用登记薄流水号
	 */
	public static int getAcctInfo(String _szAcctNo, String _szFlag) throws Exception {
		// 0-借记卡 1-贷记卡
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 若上一步骤返回失败，本步骤也返回失败
		int iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			return -1;
		}

		try {
			int iSeqNo = 0;
			if ("1".equals(_szFlag)) {
				iSeqNo = (Integer) EPOper.get(tpID, "INIT[0].SeqNo");
				getPlatSeq();// 获取新的流水号
			}

			String szCardType = (String) EPOper.get(tpID, "INIT[0]._CARD_TYPE");
			if ("0".equals(szCardType))// 借记卡
			{
				BusiMsgProc.headHost("S805070");
				
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_002", "00110");// 机构号
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_003", "00110");// 机构号
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_007", "000433");// 柜员号
				
				String szPlatDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_005", szPlatDate);
				
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_016", "6258");
				
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_030", _szAcctNo);
			
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_068", "N");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_073", "00111000");	
						
				//核心校验身份信
		        /* 1身份证 */
				String szCrdNo = "1"+(String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");
				EPOper.put(tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_062", szCrdNo);
				
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm",
						"fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_025");
				
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo",
						"fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_061");
				// 调度 核心 S805070服务
				SysPub.appLog("INFO", "调用S805070服务开始");
				DtaTool.call("HOST_CLI", "S805070");
				// 主机信息复制到登记簿中
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_002",
						"T_NCP_BOOK[0].OPEN_BRCH");
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_004",
						"T_NCP_BOOK[0].HOST_SEQ");
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_005",
						"T_NCP_BOOK[0].HOST_DATE");

				String szRetCd = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_012"); // 响应代码
				SysPub.appLog("INFO", "S805070响应码[%s]", szRetCd);
				chanMsg("S805070");

			} else if ("5".equals(szCardType))// 贷记
			{
				SysPub.appLog("ERROR", "贷记卡暂不支持");
				setCupMsg("PB040X07", "贷记卡暂不支持", "2");
				return -1;
			}

			// 还原流水号
			if ("1".equals(_szFlag)) {
				EPOper.put(tpID, "INIT[0].SeqNo", iSeqNo);
				getPlatSeq();// 获取新的流水号
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 1.0 查询账户信息 返回值：成功 0,失败 -1
	 */
	public static String acctLmtChk(String szAcctNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		try {
			PreparedStatement pstmt = null, pstmp2 = null;
			ResultSet rs = null, rs2 = null;
			String sql = "select acct_no,amt_day,amt_mon,num_day,num_mon from t_acct_lmt_tot where acct_no='?";
			String sql1 = "select lmt_amt,lmt_amt_day,lmt_amt_mon,lmt_num_day,lmt_num_mon from t_acct_lmt where acct_no=?";
			try {
				pstmt = (PreparedStatement) executer.bind(sql);
				pstmt.setString(1, szAcctNo);
				rs = pstmt.executeQuery();
				double amt_day = 0.0, amt_mon = 0.0;
				int num_day = 0, num_mon = 0;
				if (rs.next()) {
					String acct_no = rs.getString("acct_no");
					amt_day = rs.getDouble("amt_day");// 日累计发生额
					amt_mon = rs.getDouble("amt_mon");// 月累计发生额
					num_day = rs.getInt("num_day");// 日累计交易次数
					num_mon = rs.getInt("num_mon");// 月累计交易次数
					if (acct_no != null && !"".equals(acct_no))
						EPOper.put(tpID, "_INIT[0]._LMT_ACCT_FLG", "1");
					else
						EPOper.put(tpID, "_INIT[0]._LMT_ACCT_FLG", "0");
				} else {
					EPOper.put(tpID, "_INIT[0]._LMT_ACCT_FLG", "0");
				}
				pstmp2 = (PreparedStatement) executer.bind(sql1);
				pstmp2.setString(1, szAcctNo);
				rs2 = pstmt.executeQuery();
				if (rs.next()) {
					double lmt_amt = rs2.getDouble("lmt_amt");// 单笔限额
					double lmt_amt_day = rs2.getDouble("lmt_amt_day");// 日限额
					double lmt_amt_mon = rs2.getDouble("lmt_amt_mon");// 月限额
					int lmt_num_day = rs2.getInt("lmt_amt_mon");// 日交易次数限制
					int lmt_num_mon = rs2.getInt("lmt_num_mon");// 月交易次数限制
					double TrxAmt = (Double) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt");// 交易金额
					if (Double.compare(TrxAmt, lmt_amt) > 0 && Double.compare(lmt_amt, 0) > 0) {
						setCupMsg("00011", "付款账户单笔交易金额超过付款行限制", "2");
						return "-1";
					}
					if (num_day > lmt_num_day) {
						setCupMsg("00011", "付款账户当日累计交易次数超过付款行限制", "2");
						return "-1";
					}
					if (num_mon > lmt_num_mon) {
						setCupMsg("00011", "付款账户当月交易次数超过付款行限制", "2");
						return "-1";
					}
					if (Double.compare(amt_day, lmt_amt_day) > 0) {
						setCupMsg("00011", "付款账户当日交易金额超过付款行限制", "12");
						return "-1";
					}
					if (Double.compare(TrxAmt + amt_mon, lmt_amt_mon) > 0) {
						setCupMsg("00011", "付款账户当月累计交易金额超过付款行限制", "2");
						return "-1";
					}
				} else
					return "0";
			} finally {
				if (rs != null)
					rs.close();
				if (rs2 != null)
					rs2.close();
				if (pstmt != null)
					pstmt.close();
				if (pstmp2 != null)
					pstmp2.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
		return "0";
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 1.0 机构限额检查：成功 0,失败 -1
	 */
	public static String brchLmtChk(String szBrchNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		if (szBrchNo == null || "".equals(szBrchNo.trim()))
			return "-1";
		try {
			PreparedStatement pstmt = null, pstmp2 = null;
			ResultSet rs = null, rs2 = null;
			String sql = "select brch_no,num_day,amt_day from t_brch_lmt_tot where brch_no= ? ";
			String sql1 = "select brch_no,lmt_num_day,lmt_amt_day from t_brch_lmt where brch_no='" + szBrchNo + "'";
			try {
				pstmt = (PreparedStatement) executer.bind(sql);
				pstmt.setString(1, szBrchNo);
				rs = pstmt.executeQuery();
				double amt_day = 0.0;
				int num_day = 0;
				if (rs.next()) {
					amt_day = rs.getDouble("amt_day");// 日累计发生额
					num_day = rs.getInt("num_day");// 日累计交易次数
					EPOper.put(tpID, "_INIT[0]._LMT_BRCH_FLG", "1");

				} else {
					EPOper.put(tpID, "_INIT[0]._LMT_BRCH_FLG", "0");
				}
				pstmp2 = (PreparedStatement) executer.bind(sql1);
				pstmp2.setString(1, szBrchNo);
				rs2 = pstmt.executeQuery();
				if (rs.next()) {
					double lmt_amt_day = rs2.getDouble("lmt_amt_day");// 日累计限额
					int lmt_num_day = rs2.getInt("lmt_num_day");// 日累计交易限制次数
					double TrxAmt = (Double) EPOper.get(tpID, "fmt_CUP_SVR_IN.Rsp_Body[0].TrxInf[0].TrxAmt");// 交易金额
					if (num_day >= lmt_num_day) {
						setCupMsg("00011", "支付机构当日交易次数超过付款行限制", "2");
						return "-1";
					}
					if (Double.compare(TrxAmt + amt_day, lmt_amt_day) > 0) {
						setCupMsg("00011", "付款账户当月累计交易金额超过付款行限制", "2");
						return "-1";
					}
				} else
					return "-1";
			} finally {
				if (rs != null)
					rs.close();
				if (rs2 != null)
					rs2.close();
				if (pstmt != null)
					pstmt.close();
				if (pstmp2 != null)
					pstmp2.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
		return "0";
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月23日
	 * 
	 * @version 1.0 账号服务错误次数检查：成功 0,失败 -1
	 */
	public static String acctSvcFailChk(String szAcctNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
		if (szAcctNo == null || "".equals(szAcctNo.trim()))
			return "-1";
		try {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql = "select amt_day,amt_mon,num_day,num_mon,num_fail1,num_fail2,num_fail3,num_fail4 from t_acct_lmt_tot where acct_no= ? ";
			try {
				pstmt = (PreparedStatement) executer.bind(sql);
				pstmt.setString(1, szAcctNo);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					EPOper.put(tpID, "_INIT[0]._LMT_BRCH_FLG", "1");
					int _ACCT_FAIL_NUM = (Integer) EPOper.get(tpID, "_INIT[0]._ACCT_FAIL_NUM");
					// 根据不同交易类型判断
					if ("SACCT0021001".equals(svcName))// 协议支付
					{
						int num_fail = rs.getInt("num_fail");
						if (num_fail > _ACCT_FAIL_NUM) {
							setCupMsg("00011", "协议支付交易错误次数超过收付款行限制", "2");
							return "-1";
						}

					} else if ("SACCT0021002".equals(svcName))// 直接支付
					{
						int num_fai2 = rs.getInt("num_fail2");
						if (num_fai2 > _ACCT_FAIL_NUM) {
							setCupMsg("00011", "直接支付交易错误次数超过收付款行限制", "2");
							return "-1";
						}
					} else if ("SACCT0021003".equals(svcName))// 借记转账
					{
						int num_fai3 = rs.getInt("num_fail3");
						if (num_fai3 > _ACCT_FAIL_NUM) {
							setCupMsg("00011", "借记转账交易错误次数超过收付款行限制", "2");
							return "-1";
						}
					} else if ("SACCT0022001".equals(svcName))// 贷记付款
					{
						int num_fai4 = rs.getInt("num_fail4");
						if (num_fai4 > _ACCT_FAIL_NUM) {
							setCupMsg("00011", "借记转账交易错误次数超过收付款行限制", "2");
							return "-1";
						}
					}
				} else {
					EPOper.put(tpID, "_INIT[0]._LMT_BRCH_FLG", "0");
					return "0";
				}
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
		return "0";
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月24日
	 * 
	 * @version 1.0 机构服务错误次数检查：成功 0,失败 -1
	 */
	public static int brchSvcFailChk(String szBrchNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
		try {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			// 机构限额统计
			String sql = "select amt_day,num_day,num_fail1,num_fail2,num_fail3,num_fail4 from t_brch_lmt_tot where brch_no= ? ";
			try {
				pstmt = (PreparedStatement) executer.bind(sql);
				pstmt.setString(1, szBrchNo);
				rs = pstmt.executeQuery();
				if (rs.next())// 存在记录
				{
					EPOper.put(tpID, "_INIT[0]._LMT_ACCT_FLG[0]", "1");
					int _BRCH_FAIL_NUM = (Integer) EPOper.get(tpID, "_INIT[0]._BRCH_FAIL_NUM");
					// 机构限额控制
					int num_fail = 0;
					// 根据不同交易类型判断
					if ("SACCT0021001".equals(svcName))// 协议支付
					{
						num_fail = rs.getInt("num_fail1");
						if (num_fail > _BRCH_FAIL_NUM) {
							setCupMsg("00011", "协议支付交易错误次数超过收付款行限制", "2");
							return -1;
						}

					} else if ("SACCT0021002".equals(svcName))// 直接支付
					{
						num_fail = rs.getInt("num_fail2");
						if (num_fail > _BRCH_FAIL_NUM) {
							setCupMsg("00011", "直接支付交易错误次数超过收付款行限制", "2");
							return -1;
						}
					} else if ("SACCT0021003".equals(svcName))// 借记转账
					{
						num_fail = rs.getInt("num_fail3");
						if (num_fail > _BRCH_FAIL_NUM) {
							setCupMsg("00011", "借记转账交易错误次数超过收付款行限制", "2");
							return -1;
						}
					} else if ("SACCT0022001".equals(svcName))// 贷记付款
					{
						num_fail = rs.getInt("num_fail4");
						if (num_fail > _BRCH_FAIL_NUM) {
							setCupMsg("00011", "借记转账交易错误次数超过收付款行限制", "2");
							return -1;
						}
					}
				} else// 不存在记录
				{
					EPOper.put(tpID, "_INIT[0]._LMT_BRCH_FLG[0]", "0");
					return 0;
				}
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月31日
	 * 
	 * @version 1.0 账号限额更新：成功 0,失败 -1
	 */
	public static String acctLmtUpt(String szAcctNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
		// 查询条件为空，报错
		if (szAcctNo == null || "".equals(szAcctNo.trim()))
			return "-1";
		try {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				String LMT_ACCT_FLG = (String) EPOper.get(tpID, "_INIT[0]._LMT_ACCT_FLG[0]");
				String ERR_RET = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd");
				if ("0".equals(LMT_ACCT_FLG))//
				{
					String sql = " insert into t_acct_lmt_tot (szAcctNo,";
					if (!"000000".equals(ERR_RET))// 交易失败
					{
						if ("SACCT0021001".equals(svcName))// 协议支付
						{
							sql += "num_fail1) values( ?,? )";
						} else if ("SACCT0021002".equals(svcName))// 直接支付
						{
							sql += "num_fail2) values(?,? )";
						} else if ("SACCT0021003".equals(svcName))// 借记转账
						{
							sql += "num_fail3) values(?,? )";
						} else if ("SACCT0022001".equals(svcName))// 贷记付款
						{
							sql += "num_fail4) values (?,?)";
						}
						Object[] value = { szAcctNo, "1" };
						DataBaseUtils.execute(sql, value);
						return "0";
					} else if ("000000".equals(ERR_RET))// 交易成功
					{
						double TxnAmt = 0.00;
						TxnAmt = (Double) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt");
						sql += "amt_day,num_day,num_mon) value ( ?, ?, ?, ? )";
						Object[] value = { szAcctNo, TxnAmt, "1", "1" };
						DataBaseUtils.execute(sql, value);
						return "0";
					}
				} else if ("1".equals(LMT_ACCT_FLG)) {
					String sql = " update t_acct_lmt_tot ";
					String querySql = "select amt_day,amt_mon,num_day,num_mon,num_fail1,num_fail2,num_fail3,num_fail4 from t_acct_lmt_tot where acct_no= ?";
					pstmt = (PreparedStatement) executer.bind(querySql);
					pstmt.setString(1, szAcctNo);
					rs = pstmt.executeQuery();
					int num_fail1 = 0, num_fail2 = 0, num_fail3 = 0, num_fail4 = 0, num_day = 0, num_mon = 0;
					double amt_day = 0.00;
					if (rs.next()) {
						num_fail1 = rs.getInt("num_fail1");
						num_fail2 = rs.getInt("num_fail2");
						num_fail3 = rs.getInt("num_fail3");
						num_fail4 = rs.getInt("num_fail4");
						num_day = rs.getInt("num_day");
						num_mon = rs.getInt("num_mon");
					}
					if (!"000000".equals(ERR_RET))// 交易失败
					{
						Object[] value = new Object[2];
						if ("SACCT0021001".equals(svcName))// 协议支付
						{
							num_fail1 += 1;
							sql += "set num_fail1=?";
							value[0] = num_fail1;
						} else if ("SACCT0021002".equals(svcName))// 直接支付
						{
							num_fail2 += 1;
							sql += "set num_fail2=?";
							value[0] = num_fail2;
						} else if ("SACCT0021003".equals(svcName))// 借记转账
						{
							num_fail3 += 1;
							sql += "set num_fail3=?";
							value[0] = num_fail3;
						} else if ("SACCT0022001".equals(svcName))// 贷记付款
						{
							num_fail4 += 1;
							sql += "set num_fail4=?";
							value[0] = num_fail4;
						}
						sql += " where acct_no=?";
						value[1] = szAcctNo;
						DataBaseUtils.execute(sql, value);
						return "0";
					} else if ("000000".equals(ERR_RET))// 交易成功
					{
						double TxnAmt = 0.00;
						TxnAmt = (Double) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt");
						amt_day = TxnAmt + amt_day;
						num_day = num_day + 1;
						num_mon = num_mon + 1;
						sql += "set amt_day= ?,num_day= ?,num_mon= ? where acct_no= ? ";
						Object[] value = { amt_day, num_day, num_mon, szAcctNo };
						DataBaseUtils.execute(sql, value);
						return "0";
					}
				}
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
		return "0";
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月31日
	 * 
	 * @version 1.0 机构限额更新：成功 0,失败 -1
	 */
	public static String brchLmtUpt(String szBrchNo) throws Exception {
		// 获取数据源
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", true);
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 查询条件为空，报错
		if (szBrchNo == null || "".equals(szBrchNo.trim()))
			return "-1";
		try {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				String LMT_ACCT_FLG = (String) EPOper.get(tpID, "_INIT[0]._LMT_ACCT_FLG[0]");
				String ERR_RET = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd");
				if ("0".equals(LMT_ACCT_FLG))//
				{
					String sql = " insert into t_brch_lmt_tot (brch_no,";
					if (!"000000".equals(ERR_RET))// 交易失败
					{
						return "E_FAIL";
					} else if ("000000".equals(ERR_RET))// 交易成功
					{
						// double TxnAmt = 0.00;
						// TxnAmt = (Double) EPOper.get(tpID,
						// "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt");
						sql += "amt_day,num_day) value (?,?,?)";
						Object[] value = { szBrchNo, szBrchNo, "1" };
						DataBaseUtils.execute(sql, value);
						return "0";
					}
				} else if ("1".equals(LMT_ACCT_FLG)) {
					String sql = " update t_brch_lmt_tot";
					String querySql = "select amt_day,num_day,num_fail1,num_fail2,num_fail3,num_fail4 from t_brch_lmt_tot where brch_no'="
							+ szBrchNo + "'";
					pstmt = (PreparedStatement) executer.bind(querySql);
					rs = pstmt.executeQuery();
					int num_day = 0;
					double amt_day = 0.00;
					if (rs.next()) {
						amt_day = rs.getDouble("amt_day");
						num_day = rs.getInt("num_day");
					}
					if (!"000000".equals(ERR_RET))// 交易失败
					{
						return "E_FAIL";
					} else if ("000000".equals(ERR_RET))// 交易成功
					{
						double TxnAmt = 0.00;
						TxnAmt = (Double) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxAmt");
						amt_day = TxnAmt + amt_day;
						num_day = num_day + 1;
						sql += " set amt_day= ?, num_day= ? where brch_no= ?";
						Object[] value = { amt_day, num_day, szBrchNo };
						DataBaseUtils.execute(sql, value);
						return "0";
					}
				}
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
		} finally {
			if (executer != null)
				executer.close();
		}
		return "0";
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年7月31日
	 * 
	 * @version 银联服务转换为平台服务码
	 */
	public static String transCupCode(String _szCupCode) throws Exception {

		if (_szCupCode == null || "".equals(_szCupCode)) {
			SysPub.appLog("ERROR", "银联交易码不能为空");
			return null;
		}
		String szTxCOde = "";

		if ("00".equals(_szCupCode.substring(0, 2)) //
				|| "01".equals(_szCupCode.substring(0, 2))//
				|| "04".equals(_szCupCode.substring(0, 2))//
				|| "3".equals(_szCupCode.substring(0, 1))) {
			szTxCOde = "SQRY0002" + _szCupCode;
		} else if ("1".equals(_szCupCode.substring(0, 1))//
				|| "2".equals(_szCupCode.substring(0, 1))) {
			szTxCOde = "SACCT002" + _szCupCode;
		} else if ("0".equals(_szCupCode.substring(0, 1))//
				|| "2".equals(_szCupCode.substring(0, 1))//
				|| "3".equals(_szCupCode.substring(0, 1))) {
			szTxCOde = "SSIGN002" + _szCupCode;
		}
		else if (_szCupCode.length()>=4 && "SCHK".equals(_szCupCode.substring(0, 4))){
			szTxCOde = _szCupCode;
		}
		else {
			szTxCOde = _szCupCode;
		}
		SysPub.appLog("DEBUG", "银联交易码[%s]行内交易码[%s]", _szCupCode, szTxCOde);
		return szTxCOde;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月24日
	 * 
	 * @version 拼接服务逻辑名称
	 */
	public static String getLogicSvcName() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String svcName = dtaInfo.getSvcName();
		String router = transCupCode(svcName);
		// SysPub.appLog("DEBUG", "svcName:%s--router:%s", svcName, router);
		return router;
	}

	
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月24日
	 * 
	 * @version 拼接服务逻辑名称
	 */
	public static String getMngSvrSvcName() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_016");
		String router = transCupCode(svcName);
		// SysPub.appLog("DEBUG", "svcName:%s--router:%s", svcName, router);
		return router;
	}
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月27日
	 * 
	 * @version 接收的标记报文格式化
	 */
	public static void xmlSingFormatIn() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		// int iLength = (Integer) EPOper.get(tpID,
		// "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");

		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		// SysPub.appLog("DEBUG", "srcXml.length=[%d]", srcXml.length);

		int iLen = srcXml.length;
		SysPub.appLog("DEBUG", "iLen[%d]", iLen);
		byte[] msgXml = new byte[iLen + 1];
		System.arraycopy(srcXml, 0, msgXml, 0, iLen);

		msgXml[iLen] = '<';// <
		SysPub.appLog("TRACE", "outXml.length=[%d]", msgXml.length);

		// 获取报文数据和长度进行解析
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");

		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", msgXml);
		// 长度+1 加了一个<字符
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", iLen + 1);
		msgXml = null;

		SysPub.appLog("TRACE", "xmlSingFormatIn 完成");
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月27日
	 * 
	 * @version 发送标记报文格式化
	 */
	public static void xmlSingFormatOut() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		// int iLength = (Integer) EPOper.get(tpID,
		// "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		int iLen = srcXml.length;
		// SysPub.appLog("TRACE", "iLen[%d]", iLen);
		byte[] msgXml = new byte[iLen - 1];
		System.arraycopy(srcXml, 0, msgXml, 0, iLen - 1);
		SysPub.appLog("TRACE", "iLen[%d]msgXml.length[%d]", iLen, msgXml.length);

		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");

		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", msgXml);
		// 前面补了一个< 长度保持不变
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", iLen);
		msgXml = null;
		SysPub.appLog("TRACE", "xmlSingFormatOut 完成");
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月31日
	 * 
	 * @version 赋值响应码和响应信息
	 */
	public static void setCupMsg(String ret, String msg, String szStat) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd", ret);
		EPOper.put(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnDesc", msg);
		EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE", ret);
		EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG", msg);
		EPOper.put(tpID, "T_NCP_BOOK[0].STAT", szStat);
	}

	public static void setErrMsg(String tpID, String ret, String msg) throws Exception {
		EPOper.put(tpID, "INIT[0].__ERR_RET", ret);
		EPOper.put(tpID, "INIT[0].__ERR_MSG", msg);
	}

	/*
	 * @author
	 * 
	 * @createAt
	 * 
	 * @version 赋值成功响应码和响应信息
	 */
	public static void setCupSuc() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		int iRet = SysPub.ChkStep(tpID);
		if (0 != iRet) {
			SysPub.appLog("INFO", "交易处理失败,不进行成功后事件");
			return;
		}
		setCupMsg(SysPubDef.CUP_SUC_RET, SysPubDef.CUP_SUC_MSG, "1");
		// EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "1");
		// EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE", SysPubDef.CUP_SUC_RET);
		// EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG", SysPubDef.CUP_SUC_MSG);
		return;
	}

	/*
	 * @author
	 * 
	 * @createAt
	 * 
	 * @version ALA前处理
	 */
	public static void chkAla() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		// 机器日期和机器时间赋值处理
		EPOper.put(tpID, "INIT[0].TRAN_DATE", PubTool.getDate8());
		EPOper.put(tpID, "INIT[0].TRAN_TIME", PubTool.getTime());
		EPOper.put(tpID, "INIT[0].TRAN_DATETM", PubTool.getDate("yyyy-MM-dd'T'HH:mm:ss"));
		EPOper.put(tpID, "INIT[0].TRAN_MS", PubTool.getDate("HH:mm:ss.SSS"));

		String szChnlNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].BkData[0].ChnlNo");

		if ("NC".equals(szChnlNo)) {
			BusiMsgProc.putCupPubMsg(tpID);
		}
		return;
	}

	/**
	 * @author dingjunbo 预计流水 2017年7月4日
	 * @throws Exception
	 */
	public static void instBook() throws Exception {
		try {
			// 初始化数据对象INIT
			BookDaoTool.instBook();
			BookExtDaoTool.instBookExt();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @author dingjunbo 更新流水
	 * @throws Exception
	 */
	public static void uptBook() throws Exception {
		try {
			BookDaoTool.uptBook();
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月15日
	 * 
	 * @version 获取收付款方 返回字符串1-付款方，2-收款方
	 */
	public static int chkRPFlg() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			EPOper.put(tpID, "INIT[0]._RPFLG", EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].RPFlg"));
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日 根据协议号，
	 * 
	 * @返回值 检查是否签约 0-未签约，1-已签约.2-已解约,3-信息变更失效
	 */
	public static int qrySignBySignNo(String _szSignNo) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			try {
				String szSqlStr = "SELECT * FROM t_ncp_sign "//
						+ " WHERE sign_no = ?";
				Object[] value = { _szSignNo };
				DataBaseUtils.queryToElem(szSqlStr, "T_NCP_SIGN", value);
				String SIGN_NO = (String) EPOper.get(tpID, "T_NCP_SIGN[0].SIGN_NO");
				String STAT = (String) EPOper.get(tpID, "T_NCP_SIGN[0].STAT");
				// 如果没有签约信息，返回成功
				if (StringTool.isNullOrEmpty(SIGN_NO)) {
					SysPub.appLog("INFO", "无该协议号[%s]信息", _szSignNo);
					return 0;
				} else if ("Y".equals(STAT.toUpperCase())) {
					SysPub.appLog("INFO", "协议号[%s]正常", _szSignNo);
					return 1;
				} else if ("N".equals(STAT.toUpperCase())) {
					SysPub.appLog("INFO", "协议号[%s]已解约", _szSignNo);
					return 2;
				} else if ("P".equals(STAT.toUpperCase())) {
					SysPub.appLog("INFO", "协议号[%s]由于信息变更失效", _szSignNo);
					return 3;
				} else {
					return 0;
				}
			} catch (Exception e) {
				SysPub.appLog("ERROR", "qrySignInfo方法处理异常");
				throw e;
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "qrySignInfo方法处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日 根据协议号，
	 * 
	 * @说明 检查签约状态是否正常
	 * 
	 * @返回值 -1 检查失败 0-检查成功
	 */
	public static int chkSign(String _szSignNo, String _szFlag) throws Exception {
		try {
			if (StringTool.isNullOrEmpty(_szSignNo)) {
				BusiPub.setCupMsg("PS500023", "签约协议号不能为空", "2");
				SysPub.appLog("ERROR", "PS500023-签约协议号不能为空");
				return -1;
			}

			// 0-未签约，1-已签约.2-已解约,3-信息变更失效
			int iRet = qrySignBySignNo(_szSignNo);
			if (1 != iRet) {
				String szCode = SysPubDef.CUP_ERR_RET, szMsg = SysPubDef.CUP_ERR_MSG;
				if (0 == iRet) {
					if ("ACCT".equals(_szFlag)) {
						szCode = "PB521014";
						szMsg = "接收方机构查无此签约协议号";
					} else {
						szCode = "PB512001";
						szMsg = "接收方机构查无此签约协议号";
					}
				} else if (2 == iRet) {
					if ("ACCT".equals(_szFlag)) {
						szCode = "PB521013";
						szMsg = "签约协议号对应支付协议已解约";
					} else {
						szCode = "PB512002";
						szMsg = "协议状态为已解约";
					}
				} else if (3 == iRet) {
					if ("ACCT".equals(_szFlag)) {
						szCode = "PB521016";
						szMsg = "签约协议号对应支付协议已失效（签约信息变更）";
					} else {
						szCode = "PB512098";
						szMsg = "签约协议号对应支付协议已失效（签约信息变更）";
					}
				} else {
					// -1 数据库错误
				}
				// 组响应报文
				BusiPub.setCupMsg(szCode, szMsg, "2");
				SysPub.appLog("ERROR", "%s-%s", szCode, szMsg);
				return -1;
			}
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "qrySignInfo方法处理异常");
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日
	 * 
	 * @version 动态验证码校验 0-通过，否则不通过 szKey 关联码
	 */
	public static int chkSMSVrfy(String _szKey, String _szVery) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			if (_szKey.length() < 8) {
				BusiPub.setCupMsg("PB005X12", "动态关联码信息错误", "2");
				SysPub.appLog("ERROR", "PB005X12-动态关联码信息错误");
				return -1;
			}
			String szSqlStr = "";

			String sql = "select * from t_sms_confirm where plat_date = ? and seq_no = ?";
			Object[] value = new Object[2];
			String date = _szKey.substring(0, 8);
			String seq = _szKey.substring(8);
			value[0] = date;
			value[1] = seq;
			int iRet = DataBaseUtils.queryToElem(sql, "T_SMS_CONFIRM", value);
			if (iRet == 0) {
				BusiPub.setCupMsg("PB511027", "动态短信关联码错误", "2");
				SysPub.appLog("ERROR", "PB511027-动态短信关联码错误");
				return -1;
			}
			String szFlag = (String) EPOper.get(tpID, "T_SMS_CONFIRM[0].FLAG");
			if ("1".equals(szFlag)) {
				BusiPub.setCupMsg("PB511028", "短信验证码已失效", "2");
				SysPub.appLog("ERROR", "PB511028-短信验证码已失效");
				return -1;
			}
			int iFailNum = (Integer) EPOper.get(tpID, "T_SMS_CONFIRM[0].FAIL_TIMES");
			if (iFailNum > 3) {
				BusiPub.setCupMsg("PB005X09", "身份认证失败(动态码错误次数超限)", "2");
				SysPub.appLog("ERROR", "PB005X09-身份认证失败(动态码错误次数超限)");
				return -1;
			}

			int szLocVrfy = (Integer) EPOper.get(tpID, "T_SMS_CONFIRM[0].VRFY_NO");// 验证码
			// 验证码不一致
			if (!(String.valueOf(szLocVrfy).equals(_szVery))) {
				szSqlStr = " UPDATE t_sms_confirm SET fail_times = fail_times + 1 "//
						+ " WHERE plat_date = ? and seq_no = ? ";
				BusiPub.setCupMsg("PB511027", "短信验证码不符", "2");
				//BusiPub.setCupMsg("PB520011", "短信验证码不符", "2");
				SysPub.appLog("ERROR", "PB511027-短信验证码不符");
				DataBaseUtils.execute(szSqlStr, value);
				return -1;
			}
			//String szLocTime = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");
			String szLocTime = SysPub.getDataBaseTime();
			szLocTime = szLocTime.substring(0,10) + "T" + szLocTime.substring(11);
			String szInvlTime = (String) EPOper.get(tpID, "T_SMS_CONFIRM[0].INVL_DATE");// 失效时间
			SysPub.appLog("DEBUG", "szLocTime=[%s]-szInvlTime=[%s]", szLocTime, szInvlTime);
			if (PubTool.subDate(szLocTime, szInvlTime, "yyyy-MM-dd'T'HH:mm:ss") > 0) {
				// 验证码已失效
				szSqlStr = " UPDATE t_sms_confirm SET Flag = '1' "//
						+ " WHERE plat_date = ? AND seq_no = ? ";
				DataBaseUtils.execute(szSqlStr, value);
				BusiPub.setCupMsg("PB511028", "短信验证码已超过有效时间", "2");
				SysPub.appLog("ERROR", "PB511028-短信验证码已超过有效时间");
				return -1;
			}
			SysPub.appLog("INFO", "短信校验通过");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日
	 * 
	 * @version 更新短信校验码为失效
	 */
	public static int uptSMSInvl(String tpID, String _szKey) throws Exception {
		try {
			String szLocTime = (String) EPOper.get(tpID, "INIT[0].TRAN_DATETM");
			;

			Object[] value = new Object[3];
			String date = _szKey.substring(0, 8);
			String seq = _szKey.substring(8);
			value[0] = szLocTime;
			value[1] = date;
			value[2] = seq;
			SysPub.appLog("INFO", "短信验证码平台日期[%s]短信验证码平台流水号[%s]", date, seq);

			// 验证码已失效
			String szSqlStr = " UPDATE t_sms_confirm "//
					+ " SET Flag = '1' ,invl_date=? "//
					+ " WHERE plat_date = ? AND seq_no = ? ";
			DataBaseUtils.execute(szSqlStr, value);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月19日
	 * 
	 * @version 检查原交易信息和当前交易信息是否一致
	 * 
	 * @返回值 0-检查通过 1-没有找到原交易（自行判断是否要报错） -1-检查失败
	 */
	public static int chkOriInfo(String _szKey) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();

			// 比较原流水信息 证件类型 证件号码 手机号
			String szIDTp = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_CERT_TYPE");// 接收方证件类型
			String szIDNo = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_CERT_NO");// 接收方证件号
			String szMobNo = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_PHN");// 接收方预留手机号
			String szRcverAcctId = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_NO");// -接收方账户
			String szRcverNm = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PAY_ACCT_NAME");// 接收方名称
			Object[] value = new Object[2];
			String date = _szKey.substring(0, 8);
			String seq = _szKey.substring(8);
			value[0] = date;
			value[1] = seq;
			String szSqlStr = "select * from t_ncp_book where plat_date = ? and seq_no = ?";
			// 从流水表查询原交易信息
			int iRet = DataBaseUtils.queryToElem(szSqlStr, "T_NCP_BOOK_HIST", value);
			if (0 == iRet) {
				SysPub.appLog("INFO", "没有找到原交易信息");
				return 1;
			}
			String szCertType = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PAY_CERT_TYPE");
			String szCertNo = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PAY_CERT_NO");
			String szPayPhn = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PAY_PHN");
			String szAcctNo = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PAY_ACCT_NO");
			String szAcctName = (String) EPOper.get(tpID, "T_NCP_BOOK_HIST[0].PAY_ACCT_NAME");
			// 比较
			if (!StringTool.isNullOrEmpty(szIDTp)) {
				if (!szIDTp.equals(szCertType)) {
					BusiPub.setCupMsg("PB005X02", "身份认证失败（证件类型不符）", "2");
					SysPub.appLog("ERROR", "PB005X02-身份认证失败（证件类型不符）");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szIDNo)) {
				if (!szIDNo.equals(szCertNo)) {
					BusiPub.setCupMsg("PB005X03", "身份认证失败（证件号码不符）", "2");
					SysPub.appLog("ERROR", "PB005X03-身份认证失败（证件号码不符）");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szMobNo)) {
				if (!szMobNo.equals(szPayPhn)) {
					BusiPub.setCupMsg("PB511013", "身份认证失败（手机号不符）", "2");
					SysPub.appLog("ERROR", "PB511013-身份认证失败（手机号不符）");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szRcverAcctId)) {
				if (!szRcverAcctId.equals(szAcctNo)) {
					SysPub.appLog("ERROR", "错误码：%s,错误信息:%s", "PB511002", "接收方机构查无此账号");
					BusiPub.setCupMsg("PB511002", "接收方机构查无此账号", "2");
					return -1;
				}
			}
			if (!StringTool.isNullOrEmpty(szRcverNm)) {
				if (!szRcverNm.equals(szAcctName)) {
					SysPub.appLog("ERROR", "错误码：%s,错误信息:%s", "PB511017", "签约人账户名称与接收方机构记录不符");
					BusiPub.setCupMsg("PB511017", "签约人账户名称与接收方机构记录不符", "2");
					return -1;
				}
			}
			SysPub.appLog("INFO", "原交易信息校验成功");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 交易成功，验证码验证成功后就失效
	 * 
	 * @param szKey
	 *            短信关联码
	 * @return
	 * @throws Exception
	 */
	public static void upSmsConfirm(String szKey) throws Exception {
		try {
			if (szKey.length() < 8) {
				SysPub.appLog("ERROR", "错误信息:%s", "短信关联码错误");
			}
			Object[] value = new Object[2];
			String date = szKey.substring(0, 8);
			String seq = szKey.substring(8);
			value[0] = date;
			value[1] = seq;
			String UpSql = " update t_sms_confirm set flag = '1' where plat_date = ? and seq_no = ? ";
			Object[] value1 = new Object[2];
			value1[0] = date;
			value1[1] = seq;
			DataBaseUtils.execute(UpSql, value1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月21日
	 * 
	 * @version 1.0 插入短信发送表
	 */
	public static int init_t_sms_confirm() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 平台日期和平台流水号直接取流水表
			EPOper.put(tpID, "T_SMS_CONFIRM[0].PLAT_DATE", EPOper.get(tpID, "T_NCP_BOOK[0].PLAT_DATE"));
			EPOper.put(tpID, "T_SMS_CONFIRM[0].SEQ_NO", EPOper.get(tpID, "T_NCP_BOOK[0].SEQ_NO"));
			EPOper.put(tpID, "T_SMS_CONFIRM[0].SIGN_BRCH", EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].IssrId"));
			EPOper.put(tpID, "T_SMS_CONFIRM[0].OTH_SEQ",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxId"));
			// 6位短信验证码
			EPOper.put(tpID, "T_SMS_CONFIRM[0].VRFY_NO", PubTool.getId6());
			// 关联码=平台日期+平台流水号
			String LINK_CODE = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PLAT_DATE")
					+ (Integer) EPOper.get(tpID, "T_NCP_BOOK[0].SEQ_NO");
			EPOper.put(tpID, "T_SMS_CONFIRM[0].LINK_CODE", LINK_CODE);
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo", "T_SMS_CONFIRM[0].PHN");
			EPOper.put(tpID, "T_SMS_CONFIRM[0].TX_DATE", PubTool.getDate());
			EPOper.put(tpID, "T_SMS_CONFIRM[0].FAIL_TIMES", 0);
			EPOper.put(tpID, "T_SMS_CONFIRM[0].FLAG", "0");
			EPOper.put(tpID, "T_SMS_CONFIRM[0].RMRK", "");
			EPOper.put(tpID, "T_SMS_CONFIRM[0].RMRK1", "");
			EPOper.put(tpID, "T_SMS_CONFIRM[0].RMRK2", "");
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年6月21日 sign_chnl-渠道,NCP：无卡支付,COT：柜面
	 * 
	 * @version 1.0 短信验证管理登记簿
	 */
	public static int init_t_sms_info(String sign_chnl) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// sms_seq数据库为自增型，不需要赋值
			EPOper.put(tpID, "T_SMS_INFO[0].CHNL_NO", sign_chnl);
			String szRcverAcctId = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");
			EPOper.put(tpID, "T_SMS_INFO[0].ACCT_NO", szRcverAcctId);
			EPOper.put(tpID, "T_SMS_INFO[0].BRCH_NO",
					EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId"));
			String date = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].TrxInf[0].TrxDtTm");
			EPOper.put(tpID, "T_SMS_INFO[0].TRAN_DATE", date.substring(0, 10));
			EPOper.put(tpID, "T_SMS_INFO[0].TRAN_TIME", date.substring(11));
			String szMobNo = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo");
			EPOper.put(tpID, "T_SMS_INFO[0].PHN", szMobNo);
			StringBuffer sbMsg = new StringBuffer();
			Integer szVrfy = (Integer) EPOper.get(tpID, "T_SMS_CONFIRM[0].VRFY_NO");
			int ilen = szRcverAcctId.length();
			String tmp = szRcverAcctId.substring(ilen - 5, ilen);
			sbMsg.append("您尾号").append(tmp).append("的银行卡正在开通快捷支付功能，验证码").append(szVrfy).append("，切勿泄露该验证码！");
			EPOper.put(tpID, "T_SMS_INFO[0].SMS_MSG", sbMsg.toString());
			EPOper.put(tpID, "T_SMS_INFO[0].SND_STAT", "0");
			EPOper.put(tpID, "T_SMS_INFO[0].CRT_DATE", PubTool.getDate8());
			EPOper.put(tpID, "T_SMS_INFO[0].CRT_TIME", PubTool.getTime());
			EPOper.put(tpID, "T_SMS_INFO[0].SND_DATE", "");
			EPOper.put(tpID, "T_SMS_INFO[0].SND_TIMES", "0");
			EPOper.put(tpID, "T_SMS_INFO[0].RMRK", "");
			EPOper.put(tpID, "T_SMS_INFO[0].RMRK1", "");
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/**
	 * 清理表数据
	 * 
	 * @throws Exception
	 */
	public static int clrTabData(String _szTab, String _szWhere) throws Exception {
		int iRet = 0;
		String szSqlStr = "";
		szSqlStr = "delete from " + _szTab + _szWhere;
		int iLen = _szTab.length();
		if (0 == iLen) {
			SysPub.appLog("INFO", "表名为空");
			return -1;
		}
		try {
			SysPub.appLog("INFO", "清理表[%s]", _szTab);
			iRet = DataBaseUtils.execute(szSqlStr, null);
			if (0 > iRet) {
				SysPub.appLog("INFO", "清理表数据失败[%d]", iRet);
				return -1;
			}
			SysPub.appLog("INFO", "清理表[%s]结束[%d]", _szTab, iRet);
		} catch (Exception e) {
			SysPub.appLog("ERROR", "清理表数据失败");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/**
	 * 核心应答码转换为银联应答码和应答信息
	 * 
	 * @throws Exception
	 * @返回值 0-核心成功 -1-失败
	 */
	public static int chanMsg(String szSvcNo) throws Exception {
		try {
			int iRet = chanMsg(szSvcNo, "fmt_CUP_SVR_OUT");
			return iRet;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "核心响应码转换为 银联响应码失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 核心应答码转换为银联应答码和应答信息
	 * 
	 * @throws Exception
	 * @返回值 0-核心成功 -1-失败
	 */
	public static int chanMsg(String _szSvcNo, String _szObj) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szFmt1 = _szObj + "[0].ISO_8583[0].iso_8583_012";
			String szFmt2 = _szObj + "[0].ISO_8583[0].iso_8583_013";
			String szFmt3 = "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_016";
			String szErrRet = (String) EPOper.get(tpID, szFmt1);
			String szErrMsg = (String) EPOper.get(tpID, szFmt2);
			EPOper.put(tpID, "T_NCP_BOOK[0].HOST_MSG", szErrMsg);//登记核心错误信息
			String txcode = (String) EPOper.get(tpID, szFmt3); //区分签约和记账返回银联响应码
			String szRetCd = "";
			String szRetMsg = "";
			if ("P102".equals(szErrRet) ) {
				szRetCd = "PB511002";
				szRetMsg = "接收方机构查无此账号";
			} else if ("O084".equals(szErrRet) || ("P110".equals(szErrRet))) {
				if("6258".equals(txcode)){
					szRetCd = "PB511006";
					szRetMsg = "签约账户状态为已冻结";
				}else{
					szRetCd = "PB520006";
					szRetMsg = "接收方账户状态为已冻结";
				}
			} else if ("D012".equals(szErrRet) ) {
				szRetCd = "PB511008";
				szRetMsg = "签约人账户状态为已锁定";
			} else if ( "P170".equals(szErrRet)) {
				szRetCd = "PB511010";
				szRetMsg = "签约人账户状态为挂失";
				
			} else if ("A030".equals(szErrRet) ) {
				szRetCd = "PB520011";
				szRetMsg = "接收方账户可用余额不足";
			} else if ("CMS119".equals(szErrRet)) {
				szRetCd = "PB521021";
				szRetMsg = "接收方账户当日交易次数超过接收方机构限制";
			} else if ("CMS115".equals(szErrRet) || "CMS118".equals(szErrRet) || "CMS122".equals(szErrRet)
					|| "M41105".equals(szErrRet)) {
				szRetCd = "PB521023";
				szRetMsg = "接收方账户单笔交易金额超过接收方机构限制";
			} else if ("CMS117".equals(szErrRet)) {
				szRetCd = "PB521024";
				szRetMsg = "接收方账户当日累计交易金额超过接收方机构限制";
			} else if ("A003".equals(szErrRet)) {
				szRetCd = "PB521012";
				szRetMsg = "接收方账户为信用卡账户时可用额度不足";
			} else if ("B362".equals(szErrRet)) {
				szRetCd = "PB511013";
				szRetMsg = "签约人手机号不符";
			} else if("NM01".equals(szErrRet) || "NM02".equals(szErrRet)){
				if("6258".equals(txcode)){
					szRetCd = "PB511017";
					szRetMsg = "签约人户名不符";
				}else{
					szRetCd = "PB005203";
					szRetMsg = "收款户名不符";
				}
			} else if("P444".equals(szErrRet)){
				if("6258".equals(txcode)){
					szRetCd = "PB511019";
					szRetMsg = "签约人证件不符";
				}else{
					szRetCd = "PB005203";
					szRetMsg = "收款证件不符";
				}
			}else if("P172".equals(szErrRet)){
				if("6258".equals(txcode)){
					szRetCd = "PB511005";
					szRetMsg = "签约账户已注销";
				}else{
					szRetCd = "PB520005";
					szRetMsg = "接收方账户状态为已注销";
				}
			}else if("C107".equals(szErrRet)){
				szRetCd = "PB511014";
				szRetMsg = "未预留手机号码";
			}else if("P189".equals(szErrRet) || "D140".equals(szErrRet) || "M009".equals(szErrRet)){//查询
//				szRetCd = "PB003000";
//				szRetMsg = "无效商户";
				szRetCd = "PB511010";
				szRetMsg = "签约人账户状态为挂失";
			}else if("C106".equals(szErrRet)){
				szRetCd = "PB511015";
				szRetMsg = "持卡人未开通短信功能";
			}else if("NM01".equals(szErrRet)){
				szRetCd = "PB511017";
				szRetMsg = "卡姓名错误";
			}else if("P445".equals(szErrRet)){
				szRetCd = "PB511019";
				szRetMsg = "证件错误";
			}else if ("0000".equals(szErrRet)) {
				// szRetCd = SysPubDef.CUP_SUC_RET;
				// szRetMsg = SysPubDef.CUP_SUC_MSG;
				// 注意：成功不转响应吗 由应用成功后处理调度setCupSuc完成响应码赋值
				SysPub.appLog("INFO", "核心处理成功");
				return 0;
			} else if (szErrRet == null || 0 == szErrRet.length()) {
				// szRetCd = SysPubDef.CUP_TIME_RET;
				// szRetMsg = SysPubDef.CUP_TIME_MSG;
				szRetCd = "";
				szRetMsg = "核心服务超时";
			} else {
				szRetCd = SysPubDef.CUP_ERR_RET;
				if(StringTool.isNullOrEmpty(szErrMsg)){
					szRetMsg = "调度" + _szSvcNo + "服务失败";
				}else{
					szRetMsg = szErrMsg; //返回核心错误信息
				}
			}
			// 超时流水状态为3
			// SysPub.appLog("DEBUG", "szErrRet-----:%s",szRetCd);
			if (StringTool.isNullOrEmpty(szRetCd)) {
				setCupMsg(szRetCd, szRetMsg, "3");
			}
			else{
				setCupMsg(szRetCd, szRetMsg, "2");
			}
			SysPub.appLog("ERROR", "核心返回码:%s返回信息%s", szErrRet, szErrMsg);
			SysPub.appLog("ERROR", "银联返回码:%s返回信息%s", szRetCd, szRetMsg);
			return -1;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "核心响应码转换为 银联响应码失败");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 核心证件类型转换为银联证件类型
	 * 
	 * @throws Exception
	 */
	public static String chanIdType(String szIdType) throws Exception {
		try {
			if (szIdType == null)
				return "99";
			else if ("01".equals(szIdType))// 身份证
				return "01";
			else if ("02".equals(szIdType))// 军官证
				return "02";
			else if ("03".equals(szIdType))// 户口簿
				return "08";
			else if ("04".equals(szIdType))// 护照
				return "03";
			else if ("05".equals(szIdType))// 其他
				return "99";
			else if ("06".equals(szIdType))// 其他
				return "99";
			else if ("07".equals(szIdType))// 其他
				return "09";
			else if ("49".equals(szIdType))// 其他
				return "99";
			else
				return "99";
		} catch (Exception e) {
			SysPub.appLog("ERROR", "核心证件类型转换为银联证件类型失败");
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * 银联不支持交易处理
	 */
	public static int cupNoSup() throws Exception {
		// DtaInfo dtaInfo = DtaInfo.getInstance();
		// String tpID = dtaInfo.getTpId();

		BusiPub.setCupMsg("PB040000", "请求的功能尚不支持", "9");
		// EPOper.put(tpID, "T_NCP_BOOK[0].STAT", "9");
		// EPOper.put(tpID, "T_NCP_BOOK[0].RET_CODE", "PB040000");
		// EPOper.put(tpID, "T_NCP_BOOK[0].RET_MSG", "请求的功能尚不支持");
		return 0;
	}

	/**
	 * 根据银联交易信息查询
	 * 
	 * @throws Exception
	 *             _szSnd 发起方代码 _szCupSeq 银联流水号 _szFlag 0-不查询EXT信息 1-查询EXT信息
	 */
	public static int qryCupBook(String _szSnd, String _szCupSeq, String _szFlag) throws Exception {
		int iRet = 0;
		String szSqlStr = "";
		szSqlStr = " SELECT * FROM T_NCP_BOOK " //
				+ " WHERE snd_brch_no = ? and oth_seq = ? ";
		Object[] value = { _szSnd, _szCupSeq };
		try {
			iRet = DataBaseUtils.queryToElem(szSqlStr, "T_NCP_BOOK_HIST", value);
			if (0 > iRet) {
				SysPub.appLog("INFO", "查询T_NCP_BOOK失败[%d]");
				return -1;
			} else if (0 == iRet) {
				// 查询不到 查找历史表
				szSqlStr = " SELECT * FROM T_NCP_BOOK_HIST " //
						+ " WHERE snd_brch_no = ? and oth_seq = ? ";
				Object[] hisvalue = { _szSnd, _szCupSeq };
				iRet = DataBaseUtils.queryToElem(szSqlStr, "T_NCP_BOOK_HIST", hisvalue);
				if (0 > iRet) {
					SysPub.appLog("INFO", "查询T_NCP_BOOK失败[%d]");
					return -1;
				} else if (0 == iRet) {
					SysPub.appLog("ERROR", "T_NCP_BOOK无记录");
					return 0;
				}
			}

			// 判断是否查询辅助信息表
			if (!"1".equals(_szFlag)) {
				SysPub.appLog("DEBUG", "不查询T_NCP_BOOK_EXT");
				return iRet;
			}
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szPlatDate = (String) EPOper.get(tpID, "T_NCP_BOOK[0].PLAT_DATE");
			int iSeqNo = (Integer) EPOper.get(tpID, "T_NCP_BOOK[0].SEQ_NO");
			szSqlStr = " SELECT * FROM T_NCP_BOOK_EXT " //
					+ " WHERE plat_date = ? and seq_no = ? ";
			Object[] value1 = { szPlatDate, iSeqNo };
			iRet = DataBaseUtils.queryToElem(szSqlStr, "T_NCP_BOOK_EXT_HIST", value1);
			if (0 >= iRet) {
				SysPub.appLog("INFO", "查询T_NCP_BOOK失败[%d]");
				return -1;
			}

		} catch (Exception e) {
			SysPub.appLog("ERROR", "清理表数据失败");
			e.printStackTrace();
			throw e;
		}
		return iRet;
	}

    
/**   
     * 将元为单位的转换为分 （乘100）  
     *   
     * @param amount  
     * @return       */    
    public static String changeY2F(Long amount){    
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toString();    
    } 

	/**
	 * 插入冲正信息(szFormName 只能是T_NCP_BOOK[N]或T_NCP_BOOK_HIST[N])
	 * 
	 * @param szfmtIn
	 *            对象名称
	 * @throws Exception
	 * @格式说明 _szFormName：登记簿的FROM名 _szReason：冲正原因
	 */
	public static int insHostRevData(String _szFormName, String _szReason) throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szPlatDate = (String) EPOper.get(tpID, _szFormName + ".PLAT_DATE");
			int iSeqNo = (Integer) EPOper.get(tpID, _szFormName + ".SEQ_NO");

			Object[] value = new Object[15];

			value[0] = szPlatDate;
			value[1] = iSeqNo;
			value[2] = "HOST_CLI";// 冲正DTA
			value[3] = "S818888";// 冲正服务码
			value[4] = "HOST_REV";// 交易类型-冲正
			value[5] = "0"; // 发送状态 0-初始化
			StringBuffer sb = new StringBuffer();
			// 原前置交易日期
			if (!StringTool.isNullOrEmpty(szPlatDate)) {
				sb.append(szPlatDate).append("|");
			} else {
				sb.append("|");
			}
			// 原前置流水号
			sb.append(iSeqNo).append("|");
			// 原渠道代码
			String szChannelId = (String) EPOper.get(tpID, _szFormName + ".CHNL_NO");
			if (!StringTool.isNullOrEmpty(szChannelId)) {
				sb.append(szChannelId).append("|");
			} else {
				sb.append("|");
			}
			// 预授权标志
			sb.append("").append("UNCPS001|");
			// 原前置机编号
			
			// 预授权标志
			sb.append("").append("|");
			// 冲正标志
			sb.append("2").append("|");
			String dzFlag ="";
			String szPayNo = (String) EPOper.get(tpID, _szFormName + ".PAY_ACCT_NO");
			if (!StringTool.isNullOrEmpty(szPayNo)) {
				sb.append(szPayNo).append("|");
				dzFlag = "2";
			} else {
				sb.append("9300100100630|");
				dzFlag = "1";
			}
			
			Double TxnAmt = (Double) EPOper.get(tpID, _szFormName + ".TX_AMT");
			String szTxnAmt = AmountUtils.changeY2F(TxnAmt.toString());
			if (!StringTool.isNullOrEmpty(szTxnAmt)) {
				sb.append(szTxnAmt).append("|");
			} else {
				sb.append("|");
			}
			
			
			String szPayEENo = (String) EPOper.get(tpID, _szFormName + ".PAYEE_ACCT_NO");
			if (!StringTool.isNullOrEmpty(szPayEENo)) {
				sb.append(szPayEENo).append("|");
			} else {
				sb.append("9300100100630|");
			}
			sb.append(dzFlag).append("|");
			
			value[6] = sb.toString();
			value[7] = PubTool.getDate("yyyy-MM-dd'T'HH:mm:ss");
			value[8] = 0;
			value[9] = "";
			value[10] = "";
			value[11] = _szReason;
			value[12] = "";
			value[13] = "";
			value[14] = "";
			String sql = "insert into t_ncp_wait_snd values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

			int iRet = DataBaseUtils.execute(sql, value);
			if (1 != iRet) {
				SysPub.appLog("ERROR", "登记冲正信息失败 ");
				return -1;
			}

		} catch (Exception e) {
			SysPub.appLog("ERROR", e.getMessage());
			throw e;
		}
		return 0;
	}

	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年6月23日
	 * 
	 * @version 1.0 判断银联上送的账户信息与核心账户信息是否一致
	 */
	public static int ChkAcctInfo() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String szTpID = dtaInfo.getTpId();

			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(szTpID);
			if (-1 == iRet) {
				return -1;
			}

			// 获取银联上送的户名、证件类型、证件号码、手机号
			String szAcctNameIn = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverNm");
			String szCertTypeIn = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDTp");
			String szCertNoIn = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].IDNo");
			// String szPhoneIn = (String)EPOper.get(szTpID,
			// "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].MobNo");
			String szAcctName = "";
			String szCertType = "";
			String szCertNo = "";

			String szCardType = (String) EPOper.get(szTpID, "INIT[0]._CARD_TYPE");
			if ("0".equals(szCardType))// 借记卡
			{
				// 取借记卡账户户名、证件类型、证件号码、手机号
				szAcctName = (String) EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_025");
				//szCertType = (String) EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].IdType");
				szCertNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_062");
			} else {
				// 取贷记卡账户户名、证件类型、证件号码、手机号
				szAcctName = (String) EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].CREDIT_CLI_030517_Rsp.NAME");
				szCertType = (String) EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].CREDIT_CLI_030517_Rsp.KEYTYPE");
				szCertNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_OUT[0].CREDIT_CLI_030517_Rsp.CUSTID");
			}

			// 判断账户户名、证件类型、证件号码、手机号与银联上送的是否一致
			// dingjun junbo mod 20170830账户信息验证由核心验证
			/*
			 * if (!szAcctName.equals(szAcctNameIn)) { EPOper.put(szTpID,
			 * "INIT[0]._FUNC_RETURN", "1"); // 组响应报文 SysPub.appLog("INFO",
			 * "szAcctName=[%s]szAcctNameIn=[%s]", szAcctName, szAcctNameIn);
			 * setCupMsg("PB511017", "签约人账户名称与接收方机构记录不符", "2");
			 * SysPub.appLog("ERROR", "错误码：%s,错误信息:%s", "PB511017",
			 * "签约人账户名称与接收方机构记录不符"); return -1; }
			 * 
			 * if (!szCertType.equals(szCertTypeIn) ||
			 * !szCertNo.equals(szCertNoIn)) { SysPub.appLog("INFO",
			 * "szCertType=[%s]szCertTypeIn=[%s]", szCertType, szCertTypeIn);
			 * SysPub.appLog("INFO", "szCertNo=[%s]szCertNoIn=[%s]", szCertNo,
			 * szCertNoIn); EPOper.put(szTpID, "INIT[0]._FUNC_RETURN", "1"); //
			 * 组响应报文 setCupMsg("PB511019", "签约人证件号与接收方机构记录不符", "2");
			 * SysPub.appLog("ERROR", "错误码：%s,错误信息:%s", "PB511019",
			 * "签约人证件号与接收方机构记录不符"); return -1; }
			 */
			SysPub.appLog("INFO", "银联账户信息与核心账户信息一致");
			return 0;
		} catch (Exception e) {
			throw e;
		}
		// return 0;
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年8月27日
	 * 
	 * @para _szSvcName 服务码 _szFlag NOREV-不冲正 REV-冲正 _szObj对象前缀（程序中补充_IN或_OUT）
	 * 
	 * @version 1.0 调度主机服务
	 */
	public static int callHostSvc(String _szSvcName, String _szFlag, String _szPreObj) throws Exception {
		try {
			// 0-借记卡 1-贷记卡
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			String szObjIn = _szPreObj + "_IN";
			String szObjOut = _szPreObj + "_OUT";
			

			BusiMsgProc.headHost(szObjIn, _szSvcName);
			String szFmtName = szObjOut + "[0].HOST_CLI_" + _szSvcName + "_Rsp[0].";
			
			EPOper.put(tpID, "HOST_CLT_OUT[0]._BRCH_NO[0]", "00110");
			EPOper.put(tpID, "HOST_CLT_OUT[0].TERM_NO[0]", "001");
			//EPOper.put(tpID, "HOST_CLT_OUT[0].TERM_NO[0]", "001");
			
			SysPub.appLog("INFO", "调用[%s]服务开始", _szSvcName);
			try {
				DtaTool.call("HOST_CLI", _szSvcName);
			} catch (Exception e) {
				SysPub.appLog("ERROR", "调用核心服务失败");
			}
			String szRetCd = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_012"); // 响应代码
			SysPub.appLog("INFO", "[%s]响应码[%s]", _szSvcName, szRetCd);
			if (StringTool.isNullOrEmpty(szRetCd)) {
				SysPub.appLog("ERROR", "核心超时");
				setCupMsg(SysPubDef.CUP_TIME_RET, SysPubDef.CUP_TIME_MSG, "3");//更新为超时
				if ("REV".equals(_szFlag)) {
					insHostRevData("T_NCP_BOOK", "核心交易超时冲正");
				}
				return -1;
			}
			// 主机信息复制到登记簿中
			if("S801003".equals(_szSvcName)){
				EPOper.copy(tpID, tpID, szFmtName + "Brc2", "T_NCP_BOOK[0].OPEN_BRCH");
			}else{
				EPOper.copy(tpID, tpID, "fmt_CUP_SVR_IN[0].ISO_8583[0].iso_8583_002", "T_NCP_BOOK[0].OPEN_BRCH");
			}
			//String szPlatDate = (String) EPOper.get(tpID, "T_PLAT_PARA[0].PLAT_DATE");
			//EPOper.put(tpID, fmt + "TermDate", szPlatDate);
			
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_004", "T_NCP_BOOK[0].HOST_SEQ");
			
			EPOper.copy(tpID, tpID, "fmt_CUP_SVR_OUT[0].ISO_8583[0].iso_8583_046", "T_NCP_BOOK[0].HOST_DATE");
			iRet = chanMsg(_szSvcName, szObjOut);
			return iRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年8月31日
	 * 
	 * @para _szSvcName 服务码 _szObj对象前缀（程序中补充_IN或_OUT）
	 * 
	 * @version 1.0 调度短信服务
	 */
	public static int callSmsSvc(String _szSvcName, String _szPreObj) throws Exception {
		try {
			// 0-借记卡 1-贷记卡
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			String szObjIn = _szPreObj + "_IN";
			String szObjOut = _szPreObj + "_OUT";
			// 短信平台报文头赋值
			BusiMsgProc.headSms(_szSvcName);
			String szFmtName = szObjOut + "[0].HOST_CLI_" + _szSvcName + "_Rsp[0].";
			SysPub.appLog("INFO", "调用[%s]服务开始", _szSvcName);
			DtaTool.call("SMS_CLI", _szSvcName);
			String szRetCd = (String) EPOper.get(tpID, szFmtName + "RspCode"); // 响应代码
			SysPub.appLog("INFO", "[%s]响应码[%s]", _szSvcName, szRetCd);
			if (StringTool.isNullOrEmpty(szRetCd)) {
				SysPub.appLog("ERROR", "调度短信服务超时");
			}
			// 主机信息复制到登记簿中
			// EPOper.copy(tpID, tpID, szFmtName + "SerSeqNo",
			// "T_NCP_BOOK[0].HOST_SEQ");
			// EPOper.copy(tpID, tpID, szFmtName + "TranDate",
			// "T_NCP_BOOK[0].HOST_DATE");
			// iRet = chanMsg(_szSvcName, szObjOut);
			/*
			 * 增加短信服务处理
			 */
			return iRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年8月31日
	 * 
	 * @version 1.0 账务交易超时，不给银联返回报文
	 */
	public static void chkTimeOut() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String szRet = (String) EPOper.get(tpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].SysRtnInf[0].SysRtnCd");
			if (StringTool.isNullOrEmpty(szRet))
				EPOper.delete(tpID, "fmt_CUP_SVR_OUT");
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * @author chenshx
	 * 
	 * @createAt 2017年9月6日
	 * 
	 * @version 1.0 查找渠道方机构标识中指定的机构号
	 * 
	 * @para
	 */
	public static String findChnlBrch(String _szChnlBrch, String _szTag) throws Exception {
		try {
			String szChnlBrch=_szChnlBrch+",";
			String[] szTmp = szChnlBrch.split(",");
			SysPub.appLog("TRACE", "%d", szTmp.length);
			for (String szBrch : szTmp) {
				SysPub.appLog("DEBUG", "%s", szBrch);
				if (_szTag.equals(szBrch.subSequence(0, 2))) {
					SysPub.appLog("DEBUG", "%s", szBrch.substring(2));
					return szBrch.substring(2);
				}
			}
			return  "";
		} catch (Exception e) {
			throw e;
		}
	}
/*
	 * @version 管理端赋值响应码和响应信息
	 */
	public static void setMngMsg(String tpID,String ret, String msg) throws Exception {
		EPOper.put(tpID,"MngChkOut[0].MsgHead[0].RspCode",ret);
		EPOper.put(tpID,"MngChkOut[0].MsgHead[0].RspMsg",msg);
	}

	/*
	 * @version 对账分类编号
	 * @Para _szSvcNa 交易码
	 * @Para _szEntrNo 业务编号
	 * @Para _szClearDate 清算日期
	 */
	public static String crtChkActNo(String _szSvcNa,String _szEntrNo, String _szClearDate) throws Exception {
		String szChkActNo="";
		String szDate="";
		szDate=_szClearDate.substring(0, 4)+_szClearDate.substring(5, 7)+_szClearDate.substring(8, 10);
		if("SACCT0021001".equals(_szSvcNa) || "SACCT0021002".equals(_szSvcNa) || "SACCT0021003".equals(_szSvcNa)
		|| "SACCT0022001".equals(_szSvcNa) || "SACCT0021101".equals(_szSvcNa)){
			szChkActNo=_szEntrNo+szDate+"01";
		}else if("SQRY00020001".equals(_szSvcNa) || "SQRY00020003".equals(_szSvcNa) || "SQRY00020101".equals(_szSvcNa)
		|| "SSIGN0020202  ".equals(_szSvcNa) ){
			szChkActNo=_szEntrNo+szDate+"03";
		}
		SysPub.appLog("TRACE", "%s", szChkActNo);

		return szChkActNo;
	}
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("开始");
		String brch=findChnlBrch("01FQJGDM01,02FQJGDM02,03FQJGDM03,04FQJGDM04,05FQJGDM05", "01");
		System.out.println("结束"+brch);
		return;
	}
	

	/**
	 * 返回的标记报文格式化
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static void signFMTFormatOut() throws UnsupportedEncodingException{

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID,"__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		//byte srcXml[] = (byte[]) EPOper.get(tpID,"__GDTA_FORMAT[0].__ITEMDATA[0]");
		String xmlStr = new String(srcXml,"GBK");
		String sTmp = ("<" + xmlStr).substring(0, xmlStr.length());
		byte[] xTmp = sTmp.getBytes("GBK");
		// 组报文从对象
		// 获取报文数据和长度
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");

		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", xTmp);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", xTmp.length);
		
	}
	
	/**
	 * 接收的标记报文格式化
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static void signFMTFormatIn() throws UnsupportedEncodingException{		

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		//byte srcXml[] = (byte[]) EPOper.get(tpID,"__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		byte srcXml[] = (byte[]) EPOper.get(tpID,"__GDTA_FORMAT[0].__ITEMDATA[0]");
		String xmlStr = new String(srcXml,"GBK");
		String sTmp = xmlStr.substring(1, xmlStr.length()) + "<";
		byte[] xTmp = sTmp.getBytes("GBK");
		// 报文解析从对象
		// 获取报文数据和长度进行解析
		//EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]");
		//EPOper.delete(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		EPOper.delete(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");

		//EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", xTmp);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", xTmp);
		//EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", xTmp.length);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", xTmp.length);
		
	}

	
	
	/**
	 * 返回的标记报文格式化
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static void sign8583FMTFormatOut() throws UnsupportedEncodingException{

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID,"__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]");
		int len =(Integer) EPOper.get(tpID,"__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]");
		//TrcLog.log("Tongeasy.log", "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0] len=="+len+"]" , new Object[0]);

		byte[] buf1 = new byte[len];
		
		//TrcLog.log("Tongeasy.log", "["+srcXml.toString()+"]" , new Object[0]);
		buf1=snccbEncFmt(srcXml,len,BAL_COMM_PASSWD, 1);
		
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA[0]", buf1);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__GDTA_ITEMDATA_LENGTH[0]", len);
		EPOper.put(tpID, "HOST_CLT_OUT[0].__GDTA_ITEMDATA_LENGTH[0]", len);
		EPOper.put(tpID, "HOST_CLT_OUT[0].__GDTA_ITEMDATA[0]", buf1);
		
		
	}
	

	
	private static byte[] snccbEncFmt(byte[] buf, int len, byte[] passwd, int flag)
	{
	
		 int klen = passwd.length;
		 
		     for( int i=0;i< len;i++)
		     {
		    	 if( flag==1 )
		    	     buf[i]=(byte) (buf[i]+passwd[i%klen]);
		    	 else
		    		 buf[i]=(byte) (buf[i]-passwd[i%klen]); 
		     }
		     
			return buf;
	}

		
		/**
	     * byte[] 转为16进制String
	     */
	    public static String Bytes2HexString(byte[] b) { 
	        String ret = ""; 
	        for (int i = 0; i < b.length; i++) { 
	            String hex = Integer.toHexString(b[i] & 0xFF); 
	            if (hex.length() == 1) { 
	                hex = '0' + hex; 
	            } 
	            ret += hex.toUpperCase(); 
	        } 
	        return ret; 
	    } 
	    
	    /**
	     * 从一个byte[]数组中截取一部分
	     * @param src
	     * @param begin
	     * @param count
	     * @return
	     */
	    public static byte[] subBytes(byte[] src, int begin, int count) {
	        byte[] bs = new byte[count];
	        for (int i=begin;i<begin+count; i++) bs[i-begin] = src[i];
	        return bs;
	    }
	    
	    //     转化十六进制编码为字符串
	    public static String toStringHex(String s)
	    {
	        byte[] baKeyword = new byte[s.length()/2];
	        for(int i = 0; i < baKeyword.length; i++)
	        {
	          try
	          {
	              baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
	          }
	          catch(Exception e)
	          {
	              e.printStackTrace();
	          }
	        }
	     
	        try 
	        {
	            s = new String(baKeyword, "utf-8");//UTF-16le:Not
	        } 
	        catch (Exception e1) 
	        {
	            e1.printStackTrace();
	        } 
	        return s;
	    }
	/**
	 * 接收的标记报文格式化
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static void sign8583FMTFormatIn() throws UnsupportedEncodingException{		

		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		byte srcXml[] = (byte[]) EPOper.get(tpID,"__GDTA_FORMAT[0].__ITEMDATA[0]");
		//byte srcXml[] = (byte[]) EPOper.get(tpID,"HOST_CLT_IN[0].__GDTA_ITEMDATA[0]");
		//int len = (Integer) EPOper.get(tpID,"HOST_CLT_IN[0].__GDTA_ITEMDATA_LENGTH[0]");
		int len = (Integer) EPOper.get(tpID,"__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]");

		byte [] buf1 =new byte[len]; 
		
		String res = Bytes2HexString(srcXml);
		//TrcLog.log("Tongeasy.log", " ss=="+res+"]" , new Object[0]);
		buf1 = snccbEncFmt(srcXml,len,BAL_COMM_PASSWDd, 0);
		res = Bytes2HexString(buf1);
		//TrcLog.log("Tongeasy.log", " ss=="+res+"]" , new Object[0]);
		byte[]  buf2 = new byte[len-5];

		System.arraycopy(buf1, 5, buf2, 0, len-5);
		res = Bytes2HexString(buf2);
	    //TrcLog.log("Tongeasy.log", " ss=="+res+"]" , new Object[0]);

		
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA[0]", buf2);
		EPOper.put(tpID, "__GDTA_FORMAT[0].__ITEMDATA_LENGTH[0]", len-5);
		
	}
	/**
	 * 获取国密用户标识
	 */
	public static String getUnionUserID(){
		String unionUserID = ResPool.configMap.get("UNION_userID");//国密用户标识
		return unionUserID;
	}
	
	
	/**
	 * 返回柜面时判断是否有文件，有文件则需要给相应处理
	 */
	public static int sendFile(){
	
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		

		//文件名
		String fileName = (String) EPOper.get(tpID,"ISO_8583[0].iso_8583_27");
	    
		FtpToolkit ftp = new FtpToolkit();
		String host = ResPool.configMap.get("MNG_SVR_ipAddress");
		String szport = ResPool.configMap.get("MNG_SVR_port");
		String szuser = ResPool.configMap.get("MNG_SVR_userName");
		String szpwd = ResPool.configMap.get("MNG_SVR_userPassword");
		Integer it = new Integer(szport);
		int port = it.intValue();
        ftp.makeFtpConnection(host, port, szuser, szpwd);
        
		//获取文件路径
		String filePath = SysDef.WORK_DIR + SQRYPub.getConf("FilePath");
		
        String szLoaclFile = filePath + fileName;
        String szRemoteFile = "/home/snqt/tmp/"+fileName;
        
        ftp.upload(szLoaclFile,szRemoteFile);
        
        ftp.closeConnection();
        
				
		return 0;
	}
	
	
	/**
	 * 设置HTTP报文头信息
	 * @param element 报文数据对象
	 * @param svrCode 服务码
	 */
	public static void setCupHttpHead(String element,String svrCode){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		EPOper.put(tpID, element+".MsgTp",svrCode);
		EPOper.put(tpID,element+".OriIssrId",ResPool.configMap.get("CUP_OriIssrId"));
	}

	/**
	 * 银联HTTP报文头赋值
	 * 
	 * @throws Exception
	 */
	public static void putCupHttpHead(){
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		String svcName = dtaInfo.getSvcName();
		String svrName = svcName.substring(3);
		EPOper.copy(tpID,tpID,"CUP_CLI_"+svrName+"_OUT[0].MsgHeader[0].CupHttpHead[0].MsgTp","CUP_HTTP_HEAD[0].MsgTp");
		EPOper.copy(tpID,tpID,"CUP_CLI_"+svrName+"_OUT[0].MsgHeader[0].CupHttpHead[0].OriIssrId","CUP_HTTP_HEAD[0].OriIssrId");
		EPOper.copy(tpID,tpID,"CUP_CLI_"+svrName+"_OUT[0].MsgHeader[0].CupHttpHead[0].SderReserved","CUP_HTTP_HEAD[0].SderReserved");
		EPOper.copy(tpID,tpID,"CUP_CLI_"+svrName+"_OUT[0].MsgHeader[0].CupHttpHead[0].RcverReserved","CUP_HTTP_HEAD[0].RcverReserved");
		EPOper.copy(tpID,tpID,"CUP_CLI_"+svrName+"_OUT[0].MsgHeader[0].CupHttpHead[0].CupsReserved","CUP_HTTP_HEAD[0].CupsReserved");
	}
	
	/*
	 *打印translog日志
	 */
	public static void translog() throws Exception{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		
		// 判断上一步骤是否失败，用于ALA打印日志时区分自动任务是否成功
		int txflag = 0;
		int iRet = SysPub.ChkStep(tpID);
		if (-1 == iRet) {
			txflag = -1;
		}
		
		String nodename = dtaInfo.getMachineName(); //机器名
		String txtime = PubTool.getTime(); //交易时间
		String dtaname = dtaInfo.getDtaName(); //渠道
		String platseq = dtaInfo.getSeqNo(); //平台流水
		String platdate = PubTool.getDate8(); //前置日期
		String svcName = dtaInfo.getSvcName();
		
		String szFilaflag = "0";
		String content = "";
		if(!"CHK".equals(dtaname) && !"AUTODO".equals(dtaname)){
			//DTA下打印日志
			String txcode = "";
			String retcode = "";
			String seqno = "";
			String retmsg = "";
			String brch = "";
			String teller = "";
			String trxid = "";
			if("CUP_SVR".equals(dtaname)){
				txcode = transCupCode(svcName);
				String type = txcode.substring(0, 5);
				String subtxcode = txcode.substring(7,12); 
				SysPub.appLog("INFO", "code:%s", subtxcode);
				//前置流水 
				seqno = String.valueOf(EPOper.get(tpID, txcode+"_Rsp[0].MsgHeader[0].TRANSLOG_ELEMENT[0].seqno"));
				retcode = (String)EPOper.get(tpID, txcode + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnCd");
				retmsg = (String)EPOper.get(tpID, txcode + "_Rsp[0].MsgBody[0].SysRtnInf[0].SysRtnDesc");
				trxid = (String)EPOper.get(tpID, txcode + "_Req[0].MsgBody[0].TrxInf[0].TrxId");
				EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM1[0]", "银联交易流水号:"+ trxid);
				
				if("SACCT".equals(type)){
					String trxamt = (String)EPOper.get(tpID, txcode + "_Rsp[0].MsgBody[0].TrxInf[0].TrxAmt");
					if("21001".equals(subtxcode)||"21002".equals(subtxcode)){
						String pyeracctid = (String)EPOper.get(tpID, txcode + "_Req[0].MsgBody[0].PyerInf[0].PyerAcctId");//卡号				
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM2[0]", "卡号:"+ pyeracctid);
					}
					if("21101".equals(subtxcode)||"22001".equals(subtxcode)){
						String pyeeacctid = (String)EPOper.get(tpID, txcode + "_Req[0].MsgBody[0].PyeeInf[0].PyeeAcctId");//卡号				
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM2[0]", "卡号:"+ pyeeacctid);
					}
					EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM3[0]", "银联交易金额:"+ trxamt);
				}
				else{
					String oritrxid = "";
					String oribiztp = "";
					String trxstatus = "";
					String rcveracctid = "";
					if("20001".equals(subtxcode)||"20003".equals(subtxcode)||"20201".equals(subtxcode)||"20301".equals(subtxcode)){
						rcveracctid = (String)EPOper.get(tpID, txcode + "_Req[0].MsgBody[0].RcverInf[0].RcverAcctId");//卡号
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM2[0]", "卡号:"+ rcveracctid);
					}
					if("23101".equals(subtxcode)){
						//交易状态及信息查询 
						oritrxid = (String)EPOper.get(tpID, txcode + "_Rsp[0].MsgBody[0].BizInf[0].OriTrxId");//原交易流水号
						oribiztp = (String)EPOper.get(tpID, txcode + "_Rsp[0].MsgBody[0].BizInf[0].OriBizTp");//原交易类型
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM4[0]", "原交易流水号:"+ oritrxid);
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM5[0]", "原交易类型:"+ oribiztp);
					}
					if("23002".equals(subtxcode)){
						//交易状态及信息查询 
						oritrxid = (String)EPOper.get(tpID, txcode + "_Req[0].MsgBody[0].BizInf[0].OriTrxId");//原交易流水号
						trxstatus = (String)EPOper.get(tpID, txcode + "_Req[0].MsgBody[0].BizInf[0].TrxStatus");//原交易状态
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM4[0]", "原交易流水号:"+ oritrxid);
						EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM6[0]", "原交易状态:"+ trxstatus);
					}							
				}	
			}else if("TERM_SVR".equals(dtaname)){
				String str1[] = svcName.split("_");
				txcode = "S" + str1[1] + "00" + str1[2];
				seqno = (String)EPOper.get(tpID, "TERM_" + svcName + "_OUT.TermSeq");
				retcode = (String)EPOper.get(tpID, "TERM_" + svcName + "_OUT.RspCode");
				retmsg = (String)EPOper.get(tpID, "TERM_" + svcName + "_OUT.RspMsg");
				brch = (String)EPOper.get(tpID, "TERM_" + svcName + "_IN.Brc");
				teller = (String)EPOper.get(tpID, "TERM_" + svcName + "_IN.Teller");
				if("610002".equals(str1[2])){
					String signno = (String)EPOper.get(tpID, "TERM_" + svcName + "_IN.BMSDFXY");
					EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM1[0]", "协议号:"+ signno);
					String cupseq = (String)EPOper.get(tpID, "TERM_" + svcName + "_OUT.SerSeqNo");
					EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM2[0]", "银联流水号:"+cupseq);
				}
				else{
					String cardno = (String)EPOper.get(tpID, "TERM_" + svcName + "_IN.CardNo");
					EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM3[0]", "账号:"+ cardno);
				}
			}
		
			//交易名
			String txname = "";
			if("CUP_SVR".equals(dtaname)){
				txname = (String)EPOper.get(tpID, txcode+"_Rsp[0].MsgHeader[0].TRANSLOG_ELEMENT[0].txname");
			}else{
				txname = (String)EPOper.get(tpID, "TERM_SVR_IN_OUT[0].TRANSLOG_ELEMENT[0].txname");
			}
			
			//交易开始时间
			long startime = (Long)EPOper.get(tpID,"TRANSLOG_ELEMENT[0].startime[0]");
			//交易结束时间
			long endtime = PubTool.gettimems();
			//耗时
			double usetime = (endtime - startime)/1000.000;
		
			//自定义参数
			String parm1 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM1[0]");
			String parm2 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM2[0]");
			String parm3 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM3[0]");
			String parm4 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM4[0]");
			String parm5 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM5[0]");
			String parm6 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM6[0]");
			
			content = nodename + "|" + txtime + "|" + dtaname + "|" + platseq + "|" + platdate 
					+ "|" + seqno + "|" + txcode + "|" + txname + "|" + brch + "|" + teller + "|" + retcode 
					+ "|" + retmsg + "|" + "耗时" + usetime + "|"
					+ parm1 + "|" + parm2 + "|" + parm3 + "|" + parm4 + "|" + parm5 + "|" + parm6 + "|\n";
		}else{
			//ALA下打印日志
			//查询t_tx表
			String szTmpe = (String)EPOper.get(tpID, "__GDTA_FORMAT[0].__GDTA_SVCNAME");
			String szSql_Str = " select * from t_tx where tx_code = '" + szTmpe + "' ";
			try {
				// SysPub.appLog("TRACE", "查询交易名称[%s]", szSqlStr);
				DataBaseUtils.queryToElem(szSql_Str, "T_TX", null);
			} catch (Exception e) {
				SysPub.appLog("ERROR", "数据库错误");
				e.printStackTrace();
				throw e;
			}
			
			String seqno = String.valueOf(EPOper.get(tpID, "INIT[0].SeqNo"));
			String txcode = (String)EPOper.get(tpID, "T_TX[0].TX_CODE");
			String txname = (String)EPOper.get(tpID, "T_TX[0].TX_NAME");
			String brch = (String)EPOper.get(tpID, "INIT[0].BrchNo");
			String teller = (String)EPOper.get(tpID, "INIT[0].TlrNo");
			
			String retcode = "";
			String retmsg = "";
			if( -1 == txflag ){
				retcode = "999999";
				retmsg = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].errmsg");
			}else{
				retcode = "000000";
				retmsg = "自动任务完成";
			}
			
			//交易开始时间
			long startime = (Long)EPOper.get(tpID,"TRANSLOG_ELEMENT[0].startime[0]");
			//交易结束时间
			long endtime = PubTool.gettimems();
			//耗时
			double usetime = (endtime - startime)/1000.000;
			
			if("AUTO_NCP_CHK".equals(txcode)){
				String cleardate = (String)EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
				if(cleardate == null || "".equals(cleardate)){
					retmsg = "无需要对账记录";
					szFilaflag = "1";
				}else{
					EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM1[0]", "对账日期：" + cleardate);
				}
			}else if("AUTO_CLEAR".equals(txcode)){
				if("请注意：待清算记录为0".equals(retmsg)){
					szFilaflag = "1";
				}
				String cleardate = (String)EPOper.get(tpID, "T_CHK_SYS[0].CHK_DATE");
				EPOper.put(tpID, "TRANSLOG_ELEMENT[0].PARM1[0]", "清算日期：" + cleardate);
			}else if("AUTO_CHK_ERR".equals(txcode) || "AUTO_CUP_ERR".equals(txcode)){
				szFilaflag = "1";
			}else{
				szFilaflag = "1";
			}
			/*else if("AUTO_CHK_ERR".equals(txcode)){
				String szSeqNo = "";
				String szPre = "";
				for (int iNum = 0; ; iNum++){
					szPre = "T_CHK_ERR[" + iNum + "].";
					szSeqNo = String.valueOf(EPOper.get(tpID, szPre + "SEQ_NO"));
					if(szSeqNo == null || "".equals(szSeqNo)){
					}
				}
			}
			*/
			//自定义参数
			String parm1 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM1[0]");
			String parm2 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM2[0]");
			String parm3 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM3[0]");
			String parm4 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM4[0]");
			String parm5 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM5[0]");
			String parm6 = (String)EPOper.get(tpID, "TRANSLOG_ELEMENT[0].PARM6[0]");
			
			content = nodename + "|" + txtime + "|" + dtaname + "|" + platseq + "|" + platdate 
					+ "|" + seqno + "|" + txcode + "|" + txname + "|" + brch + "|" + teller 
					+ "|" + retcode + "|" + retmsg + "|" + "耗时" + usetime + "|"
					+ parm1 + "|" + parm2 + "|" + parm3 + "|" + parm4 + "|" + parm5 + "|" + parm6+ "|\n";
		}
		//将null替换为空
		String szTemp = content.replaceAll("null", "");
		
		String filepath =  SysDef.WORK_DIR+"/tracelog";
		String filename = "Trans_" + nodename + "_" + platdate + ".log";
		//写文件
		if("0".equals(szFilaflag)){
			FileTool.writeFileontheend(szTemp, filepath, filename,"UTF-8");
		}else{
			SysPub.appLog("INF", "不打印translog日志[%s]", iRet);
		}
	}
	
}
