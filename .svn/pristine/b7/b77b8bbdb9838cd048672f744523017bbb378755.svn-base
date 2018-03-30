package com.adtec.ncps.busi.ncp.chk;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo 清算汇总明细信息表
 *******************************************************/
public class SCHK00010118 {

	/**
	 * 清算汇总明细信息表
	 * @throws Exception
	 */
	public static void detQry() throws Exception 
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szLqdDate = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");// 清算日期
		String szTxType = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_069");// 差错类型
		// 检查查询条件
		if (StringTool.isNullOrEmpty(szLqdDate)) 
		{
			SysPub.appLog("ERROR", "清算日期为空");
			TermPubBean.putTermRspCode("E101",  "清算日期不能为空");
			return;
		}
		// 查询记录数
		int iRowNum = ChkPub.getRowNum(tpID, "MngChkIn[0].SCHK00010118");
		// 查询起始记录数
		String szBeginRec = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_041");
		szLqdDate = szLqdDate.trim();
		if(StringTool.isNullOrEmpty(szTxType))
			szTxType = "";
		else
			szTxType = szTxType.trim();
		
		String sqlStr = "select clear_date,tx_type,pay_num,out_fee,ac_out_fee,is_out_fee,payee_num,"
						+ "in_fee,ac_in_fee,is_in_fee,charge_fee,brand_fee,err_fee,amt1,amt2,rmrk,"
						+ "rmrk1,rmrk2 ";
		String sql = sqlStr + " from ( select rownum as r,t.* from t_ncp_sett_det t where clear_date = ? ";
		String sqlTot = "select * from t_ncp_sett_det t where clear_date = ? ";
		int iCount = -1,iTotCount = -1;
		if("".equals(szTxType))
		{
			sql = sql + " and rownum <= ? order by clear_date asc ) " 
					  + " where r >= ? ";
			Object[] obj = { szLqdDate, iRowNum, szBeginRec };
			iCount = DataBaseUtils.queryToElem(sql, "T_NCP_SETT_DET", obj);
			sqlTot = sqlTot + " order by clear_date asc ";
			Object[] objTol = { szLqdDate };
			iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
		}else
		{
			sql = sql + " and tx_type = ? and rownum <= ? order by clear_date asc ) " 
					  + " where r >= ?";
			Object[] obj = { szLqdDate, szTxType, iRowNum, szBeginRec };
			iCount = DataBaseUtils.queryToElem(sql, "T_NCP_SETT_DET", obj);
			sqlTot = sqlTot + " and tx_type = ? order by clear_date asc ";
			Object[] objTol = { szLqdDate, szTxType };
			iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
		}

		if (iCount < 0) {
			SysPub.appLog("ERROR", "数据库错误");
			TermPubBean.putTermRspCode("E102",  "数据库错误");
			return;
		}
		if (iCount <= 0) {
			SysPub.appLog("ERROR", "查询不到记录");
			TermPubBean.putTermRspCode("E103",  "查询不到记录");
			return;
		}
		// 循环报文赋值
		putMsg(tpID, iCount);
		EPOper.put(tpID, "ISO_8583[0].iso_8583_041", iTotCount);
		EPOper.put(tpID, "ISO_8583[0].iso_8583_042", iCount);
		
		TermPubBean.putTermRspCode("0000", "交易成功");

	}
	public static void putMsg(String tpID, int iCount) {
		int i = 0;
		String szSrc = "T_NCP_SETT_DET[", szDes = "MngChkOut[0].SCHK00010118[0].Loop[";
		while (i < iCount) {
			EPOper.copy(tpID, tpID, szSrc + i + "].TX_TYPE", szDes + i + "].TxType");
			EPOper.copy(tpID, tpID, szSrc + i + "].PAY_NUM", szDes + i + "].PayNum");
			EPOper.copy(tpID, tpID, szSrc + i + "].OUT_FEE", szDes + i + "].OutFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].AC_OUT_FEE", szDes + i + "].AcOutFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].IS_OUT_FEE", szDes + i + "].IsOutFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].PAYEE_NUM", szDes + i + "].PayeeNum");
			EPOper.copy(tpID, tpID, szSrc + i + "].IN_FEE", szDes + i + "].InFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].AC_IN_FEE", szDes + i + "].AcInFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].IS_IN_FEE", szDes + i + "].IsInFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].CHARGE_FEE", szDes + i + "].ChargeFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].BRAND_FEE", szDes + i + "].BrandFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_FEE", szDes + i + "].ErrFee");
			EPOper.copy(tpID, tpID, szSrc + i + "].AMT1", szDes + i + "].Amt1");
			EPOper.copy(tpID, tpID, szSrc + i + "].AMT2", szDes + i + "].Amt2");
			EPOper.copy(tpID, tpID, szSrc + i + "].RMRK", szDes + i + "].Rmrk");
			EPOper.copy(tpID, tpID, szSrc + i + "].RMRK1", szDes + i + "].Rmrk1");
			EPOper.copy(tpID, tpID, szSrc + i + "].RMRK2", szDes + i + "].Rmrk2");
			i++;
		}
	}	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
