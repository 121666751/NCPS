package com.adtec.ncps.busi.ncp.chk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
/********************************************************
 * *
 * 
 * @author dingjunbo 银联对账查询
 *******************************************************/
public class SCHK00010114 {

	/**
	 * 银联对账查询
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void cupErrQry() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szLqdDate = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");// 清算日期
		String szErrType = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_068");// 差错类型
		String szErrStat = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_069");// 差错确认状态
		String szQryFlag = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_070");// 查询下载标志
		String szType = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_070");// 查询标志
		// 检查查询条件
		if (StringTool.isNullOrEmpty(szLqdDate)) {
			SysPub.appLog("ERROR", "清算日期为空");
			TermPubBean.putTermRspCode("E101",  "清算日期不能为空");
			return;
		}else{
			szLqdDate = szLqdDate.substring(0,4)+"-"+szLqdDate.substring(4,6)+"-"+szLqdDate.substring(6,8);
		}
		/*if (StringTool.isNullOrEmpty(szErrType)) {
			SysPub.appLog("ERROR", "差错类型为空");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "差错类型不能为空");
			return;
		}
		if (StringTool.isNullOrEmpty(szErrStat)) {
			SysPub.appLog("ERROR", "差错确认状态为空");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "差错确认状态不能为空");
			return;
		}*/
		// 查询记录数
		int iRowNum = ChkPub.getRowNum(tpID, "ISO_8583[0].iso_8583_042");
		int szBeginRec = 0;
		// 查询起始记录数
		String BeginRec = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_041");
		if (StringTool.isNullOrEmpty(BeginRec)){
			szBeginRec = 0;
		}else{
			szBeginRec = Integer.parseInt(BeginRec.trim());
		}	
		szLqdDate = szLqdDate.trim();
		szQryFlag = szQryFlag.trim();
		szErrType = szErrType.trim();
		szErrStat = szErrStat.trim();
		if( "0".equals(szErrType) )
			szErrType = "IS_ERRTRX";
		else if( "1".equals(szErrType) )
			szErrType = "AC_ERRTRX";
		String sqlStr = "select SETT_DATE, FILE_TYPE, TX_CODE, TX_TYPE_CUP, OTH_SEQ, TX_AMT, BUSI_TYPE, "
					+ "CLEAR_DATE, ERR_TYPE, ERR_REASON, ORI_OTH_SEQ, ORI_TX_AMT, SND_BRCH_NO, PAY_BRCH, "
					+ "PAY_ACCT_TYPE, PAY_ACCT_NO, CHNL_BRCH, SIGN_NO, PAYEE_BRCH, PAYEE_ACCT_TYPE, "
					+ "PAYEE_ACCT_NO, INST_BRCH, INST_ACCT_NO, PRODUCT_TYPE, PRODUCT_DESC, MRCHNT_NO, "
					+ "MRCHNT_TYPE, SUBMRCHNT_NO, SUBMRCHNT_TYPE, TERM_TYPE, OUT_AMT, IN_AMT, CHARGE_FEE, "
					+ "LOGO_FEE, ERR_FEE, OUT_FEE, IN_FEE, ERR_FLAG, PROC_FLAG, ERR_PLAT_DATE, ERR_PLAT_SEQ, "
					+ "ERR_HOST_DATE, ERR_HOST_SEQ, OPEN_BRCH, ERR_BRCH_NO, TELLER_NO, RCK_TELLER, ERR_MSG, "
					+ "AMT1, AMT2, RMRK, RMRK1, RMRK2 ";
		// szType 1-银联差错查询,2-银联差错批量调账前查询
		szType="1";
		if("1".equals(szType.trim())||"3".equals(szType.trim())){
			// QryFlag 0—查询,1—文件
			szQryFlag = "1";
			if ("0".equals(szQryFlag.trim())) {
				String sql ="";
				String sqlTot = "";
				int iCount = -1;
				int iTotCount = 0;
				if(szErrType==null||"".equals(szErrType.trim())){
					if(szErrStat==null||"".equals(szErrStat.trim())){
						sql = sqlStr
								+ "from ( select rownum as r,t.* from t_ncp_err_detail t where sett_date = ? "
								+ "and  rownum <= ? order by sett_date asc ) "
								+ " where r >= ?";
						sqlTot = "select * from t_ncp_err_detail t where sett_date = ? "
								+ " order by sett_date asc ";
						// SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
						Object[] obj = { szLqdDate, iRowNum, szBeginRec };
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj);
						Object[] objTol = { szLqdDate};
						iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
					}else{
						sql = sqlStr
								+ "from ( select rownum as r,t.* from t_ncp_err_detail t where sett_date = ? "
								+ "and ERR_FLAG = ? and rownum <= ? order by sett_date asc ) "
								+ " where r >= ?";
						sqlTot = "select * from t_ncp_err_detail t where sett_date = ? "
								+ "and ERR_FLAG = ? order by sett_date asc ";
						// SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
						Object[] obj = { szLqdDate, szErrStat,iRowNum, szBeginRec };
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj);
						Object[] objTol = { szLqdDate,szErrStat};
						iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
					}
				}else{
					if(szErrStat==null||"".equals(szErrStat.trim())){
						sql = sqlStr
								+ "from ( select rownum as r,t.* from t_ncp_err_detail t where sett_date = ? "
								+ "and file_type = ? and rownum <= ? order by sett_date asc ) "
								+ " where r >= ?";
						sqlTot = "select * from t_ncp_err_detail t where sett_date = ? "
								+ "and file_type = ? order by sett_date asc ";
						// SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
						Object[] obj = { szLqdDate, szErrType,iRowNum, szBeginRec };
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj);
						Object[] objTol = { szLqdDate,szErrType};
						iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
					}else{
						sql = sqlStr
								+ "from ( select rownum as r,t.* from t_ncp_err_detail t where sett_date = ? "
								+ "and file_type = ? and ERR_FLAG = ? and rownum <= ? order by sett_date asc ) "
								+ " where r >= ?";
						sqlTot = "select * from t_ncp_err_detail t where sett_date = ? "
								+ "and file_type = ? and ERR_FLAG = ? order by sett_date asc ";
						// SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
						Object[] obj = { szLqdDate, szErrType,szErrStat,iRowNum, szBeginRec };
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj);
						Object[] objTol = { szLqdDate,szErrType,szErrStat};
						iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
					}
				}
				
				if (iCount < 0) {
					SysPub.appLog("ERROR", "数据库错误");
					TermPubBean.putTermRspCode("E101",  "数据库错误");
					return;
				}
				if (iCount == 0) {
					SysPub.appLog("DEBUG", "查询记录为空");
					TermPubBean.putTermRspCode("E103",  "查询不到记录");
					return;
				}
				// 循环报文赋值
				putMsg(tpID, iCount);
				EPOper.put(tpID, "ISO_8583[0].iso_8583_041", iTotCount);
				EPOper.put(tpID, "ISO_8583[0].iso_8583_042", iCount);
				TermPubBean.putTermRspCode("0000", "交易成功");
				// 文件下载
			} else if ("1".equals(szQryFlag.trim())) {
				String sql = "";
				int iCount = -1;
				if(szErrType==null||"".equals(szErrType.trim())){
					if(szErrStat==null||"".equals(szErrStat.trim())){
						sql = "select * from t_ncp_err_detail t where sett_date = ? "
								+ " order by sett_date asc ";
						// SysPub.appLog("DEBUG", "%s", sql);
						Object[] obj2 = { szLqdDate};				
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj2);
					}else{
						sql = "select * from t_ncp_err_detail t where sett_date = ? "
								+ "and ERR_FLAG = ? order by sett_date asc ";
						// SysPub.appLog("DEBUG", "%s", sql);
						Object[] obj2 = { szLqdDate, szErrStat };				
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj2);
					}
				}else{
					if(szErrStat==null||"".equals(szErrStat.trim())){
						sql = "select * from t_ncp_err_detail t where sett_date = ? "
								+ "and file_type = ? order by sett_date asc ";
						// SysPub.appLog("DEBUG", "%s", sql);
						Object[] obj2 = { szLqdDate, szErrType};				
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj2);
					}else{
						sql = "select * from t_ncp_err_detail t where sett_date = ? "
								+ "and file_type = ? and ERR_FLAG = ? order by sett_date asc ";
						// SysPub.appLog("DEBUG", "%s", sql);
						Object[] obj2 = { szLqdDate, szErrType,szErrStat };				
						iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj2);
					}
				}
				
				if (iCount < 0) {
					SysPub.appLog("ERROR", "数据库错误");
					TermPubBean.putTermRspCode("E101",  "数据库错误");
					return;
				}
				if (iCount == 0) {
					SysPub.appLog("DEBUG", "查询记录为空");
					TermPubBean.putTermRspCode("E103",  "查询不到记录");
					return;
				}
				createFile(tpID, iCount);		
				TermPubBean.putTermRspCode("0000", "交易成功");
			}
		//银联差错批量调账前查询
		}else if("2".equals(szType.trim())){
			String sql ="";
			String sqlTot = "";
			int iCount = -1;
			int iTotCount = 0;
			if(szErrType==null||"".equals(szErrType.trim())){		
					sql = sqlStr
							+ "from ( select rownum as r,t.* from t_ncp_err_detail t where sett_date = ? "
							+ "and ERR_FLAG = ? and rownum <= ? order by sett_date asc ) "
							+ " where r >= ?";
					sqlTot = "select * from t_ncp_err_detail t where sett_date = ? "
							+ "and ERR_FLAG = ? order by sett_date asc ";
					// SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
					Object[] obj = { szLqdDate, szErrStat,iRowNum, szBeginRec };
					iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj);
					Object[] objTol = { szLqdDate,szErrStat};
					iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
			}else{
					sql = sqlStr
							+ "from ( select rownum as r,t.* from t_ncp_err_detail t where sett_date = ? "
							+ "and file_type = ? and ERR_FLAG = ? and rownum <= ? order by sett_date asc ) "
							+ " where r >= ?";
					sqlTot = "select * from t_ncp_err_detail t where sett_date = ? "
							+ "and file_type = ? and ERR_FLAG = ? order by sett_date asc ";
					// SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
					Object[] obj = { szLqdDate, szErrType,szErrStat,iRowNum, szBeginRec };
					iCount = DataBaseUtils.queryToElem(sql, "T_NCP_ERR_DETAIL", obj);
					Object[] objTol = { szLqdDate,szErrType,szErrStat};
					iTotCount = DataBaseUtils.queryToCount(sqlTot, objTol);
			}
			
			if (iCount < 0) {
				SysPub.appLog("ERROR", "数据库错误");
				TermPubBean.putTermRspCode("E101",  "数据库错误");
				return;
			}
			if (iCount == 0) {
				SysPub.appLog("DEBUG", "查询记录为空");
				TermPubBean.putTermRspCode("E103",  "查询不到记录");
				return;
			}
			// 循环报文赋值
			putMsg(tpID, iCount);
			EPOper.put(tpID, "ISO_8583[0].iso_8583_041", iTotCount);
			EPOper.put(tpID, "ISO_8583[0].iso_8583_042", iCount);

			TermPubBean.putTermRspCode("0000", "交易成功");
		}
	}

	/**
	 * 差错类型 代码码转转换
	 */
	public static String changErrType(String tpID,int i)
	{
		String szErrType = (String)EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].FILE_TYPE");
		if(StringTool.isNullOrEmpty(szErrType))
			return "";
		else if("IS_ERRTRX".equals(szErrType))
			return "1";
		else if("AC_ERRTRX".equals(szErrType))
			return "2";
		else return "";	
	}
	/**
	 * 数据对象的值赋值到报文数据
	 * 
	 * @param tpID
	 *            数据池ID
	 */
	public static void putMsg(String tpID, int iCount) {
		// int iCount = EPOper.getSuffixNo(tpID, "T_CHK_ERR");
		int i = 0;
		String szSrc = "T_NCP_ERR_DETAIL[", szDes = "MngChkOut[0].SCHK00010114[0].Loop[";
		while (i < iCount) {
			String szSettDate = (String)EPOper.get(tpID, szSrc + i + "].SETT_DATE");
			szSettDate = szSettDate.substring(0,4)+szSettDate.substring(5,7)+szSettDate.substring(8,10);
			EPOper.put(tpID, szDes + i + "].LqdDate",szSettDate);
			//String szErrType = changErrType(tpID,i);
			//EPOper.put(tpID, szDes + i + "].ErrType",szErrType);
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_TYPE", szDes + i + "].ErrType");
			EPOper.copy(tpID, tpID, szSrc + i + "].SND_BRCH_NO", szDes + i + "].SndBrchNo");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_REASON", szDes + i + "].ErrCap");
			EPOper.copy(tpID, tpID, szSrc + i + "].PAY_BRCH", szDes + i + "].ResvBrchFlg");
			EPOper.copy(tpID, tpID, szSrc + i + "].CHNL_BRCH", szDes + i + "].AgtBrchFlg");
			EPOper.copy(tpID, tpID, szSrc + i + "].OTH_SEQ", szDes + i + "].CupSeqNo");
			String CupTranDt = (String)EPOper.get(tpID, szSrc + i + "].CLEAR_DATE");
			CupTranDt = CupTranDt.substring(0,4)+CupTranDt.substring(5,7)+CupTranDt.substring(8,10);
			EPOper.put(tpID, szDes + i + "].CupTranDt",CupTranDt);
			//EPOper.copy(tpID, tpID, szSrc + i + "].CLEAR_DATE", szDes + i + "].CupTranDt");
			//EPOper.copy(tpID, tpID, szSrc + i + "].", szDes + i + "].CupTranTm");交易时间表没有，后期再加上
			//EPOper.copy(tpID, tpID, szSrc + i + "].", szDes + i + "].TrmNo");//终端号
			//EPOper.copy(tpID, tpID, szSrc + i + "].", szDes + i + "].AuthNo");//预授权号
			String TranCrdNo = (String)EPOper.get(tpID, szSrc + i + "].PAY_ACCT_NO");
			EPOper.copy(tpID, tpID, szSrc + i + "].PAY_ACCT_NO", szDes + i + "].TranCrdNo");
			if(TranCrdNo==null||TranCrdNo.trim().isEmpty()){
				EPOper.copy(tpID, tpID, szSrc + i + "].PAYEE_ACCT_NO", szDes + i + "].TranCrdNo");
			}

			double inAmt = 0.00;
			double outAmt = 0.00;
			
			Double szinAmt = (Double) EPOper.get(tpID, szSrc +  i + "].IN_AMT");
			if(szinAmt!=null){
				inAmt = szinAmt;
			}
			Double szOutAmt = (Double) EPOper.get(tpID, szSrc +  i + "].OUT_AMT");
			if(szOutAmt!=null){
				outAmt = szOutAmt;
			}
			double trAmt = inAmt;
			if(inAmt <= 0.00){
				trAmt = outAmt;
			}
		
			EPOper.put(tpID, szDes + i + "].AdjAmt",trAmt);//调整金额
			
			double inFee = 0.00;	//应收业务参与价
			double outFee = 0.00;	//应付业务参与价
			double chargeFee = 0.00; //网络服务费
			double logoFee = 0.00;	//品牌费
			double errFee = 0.00;	//差错处理费
			double PayFee = 0.00;	//应付手续费
			double RcvFee = 0.00;	//应收手续费
			Double szinFee = (Double) EPOper.get(tpID, szSrc +  i + "].IN_FEE");
			if(szinFee!=null){
				inFee = szinFee;
			}
			Double szoutFee = (Double) EPOper.get(tpID, szSrc +  i + "].OUT_FEE");
			if(szoutFee!=null){
				outFee = szoutFee;
			}
			Double szchargeFee = (Double) EPOper.get(tpID, szSrc +  i + "].CHARGE_FEE");
			if(szchargeFee!=null){
				chargeFee = szchargeFee;
			}
			Double szlogoFee = (Double) EPOper.get(tpID, szSrc +  i + "].LOGO_FEE");
			if(szlogoFee!=null){
				logoFee = szlogoFee;
			}
			Double szerrFee = (Double) EPOper.get(tpID, szSrc +  i + "].ERR_FEE");
			if(szerrFee!=null){
				errFee = szerrFee;
			}
			if(inFee - outFee + chargeFee + logoFee + errFee >0){
				RcvFee = inFee - outFee + chargeFee + logoFee + errFee; //应收手续费
			}
			if(inFee - outFee + chargeFee + logoFee + inFee + errFee <0){
				PayFee = -(inFee - outFee + chargeFee + logoFee+inFee); // 应付手续费
			}
			if(inFee - outFee + chargeFee + logoFee + errFee >0){
				RcvFee = inFee - outFee + chargeFee + logoFee + errFee;	//应收手续费
			}
			if(inFee - outFee + chargeFee + logoFee+inFee + errFee <0){
				PayFee = -(inFee - outFee + chargeFee + logoFee + inFee + errFee);	//应付手续费
			}
			EPOper.put(tpID, szDes + i + "].PayFee",PayFee);
			EPOper.put(tpID, szDes + i + "].RcvFee",RcvFee);
			//EPOper.copy(tpID, tpID, szSrc + i + "].TX_TIME", szDes + i + "].CustRcvFee");//客户应收手续费
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_FLAG", szDes + i + "].DebtsPrcStat");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_PLAT_DATE", szDes + i + "].FrntErrPrcDt");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_PLAT_SEQ", szDes + i + "].FrntErrPrcSeqNo");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_HOST_DATE", szDes + i + "].HostErrPrcDt");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_HOST_SEQ", szDes + i + "].HostErrPrcSeqNo");
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_MSG", szDes + i + "].Rmrk");
			EPOper.copy(tpID, tpID, szSrc + i + "].TELLER_NO", szDes + i + "].CnTeller");
			EPOper.copy(tpID, tpID, szSrc + i + "].RCK_TELLER", szDes + i + "].ChkTeller");
			i++;
		}
	}

	/**
	 * 根据数据对象生成文件
	 * 
	 * @param 数据对象
	 * @throws Exception
	 */
	public static void createFile(String tpID, int iCount) throws Exception {
		FileOutputStream fos = null;  
		try {
			String date = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[0].SETT_DATE");
			// 文件名
			String nowTime = PubTool.getTime();
			//文件名
			String fileName = "UNCPS_CUP_ERR_" + date + "_" + nowTime + ".txt";
			String filePath = SysDef.WORK_DIR + ResPool.configMap.get("FilePath");
			//String filePath = SysPubDef.MNG_FILE_DIR+date.substring(0, 4)+"/"+date.substring(5,7)+"/"+date.substring(8,10)+"/";
			String szFile = filePath + fileName;
			File path = new File(filePath);
			if(!path.exists()) {
        		path.mkdirs();
			}
			fos = new FileOutputStream(new File(szFile));
			int i = 0;
			while (i < iCount) {
				StringBuffer sb = new StringBuffer();
				String szSettDate = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].SETT_DATE");
				szSettDate = szSettDate.substring(0,4)+szSettDate.substring(5,7)+szSettDate.substring(8,10);
				String szClearDate = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].CLEAR_DATE");
				szClearDate = szClearDate.substring(0,4)+szClearDate.substring(5,7)+szClearDate.substring(8,10);
				//String szFileType = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].FILE_TYPE");
				//szFileType = changErrType(tpID,i);
				String szErrType = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_TYPE");
				String szSendBrchNo = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].SND_BRCH_NO");
				String szErrReason = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_REASON");
				Double szTxAmt = 0.0;
				Double szInAmt = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].IN_AMT");
				Double szOutAmt = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].OUT_AMT");
				if(szInAmt==0){
					szTxAmt = szOutAmt;
				}
				String szPayBrch = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].PAY_BRCH");
				String szChnlBrch = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].CHNL_BRCH");
				String szOthSeq = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].OTH_SEQ");				
				String szPayAcctNo = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].PAY_ACCT_NO");
				String szPayEeAcctNo = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].PAYEE_ACCT_NO");
				if(szPayAcctNo==null||szPayAcctNo.trim().isEmpty()){
					szPayAcctNo = szPayEeAcctNo;
				}
				Double szTranFee = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].FEE");
				Double inFee = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].IN_FEE");
				Double outFee = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].OUT_FEE");
				Double chargeFee = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].CHARGE_FEE");
				Double logoFee = (Double) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].LOGO_FEE");
				Double PayFee = 0.00;
				Double RcvFee = 0.00;
				if(inFee - outFee + chargeFee + logoFee >0){
					RcvFee = inFee - outFee + chargeFee + logoFee;
				}
				if(inFee - outFee + chargeFee + logoFee+inFee<0){
					PayFee = -(inFee - outFee + chargeFee + logoFee+inFee);
				}
				String szErrFlag = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_FLAG");
				String szErrPlatDate = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_PLAT_DATE");
				String szErrPlatSeq = String.valueOf((Integer) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_PLAT_SEQ"));
				String szErrHostDate = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_HOST_DATE");
				String szErrHostSeq = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_HOST_SEQ");
				String szErrMsg = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].ERR_MSG");
				String szTellerNo = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].TELLER_NO");
				String szRckTeller = (String) EPOper.get(tpID, "T_NCP_ERR_DETAIL[" + i + "].RCK_TELLER");
                if(i==0)
                	sb.append("~清算日期 |差错原因|付款账号|交易金额|银联流水号|清算日期|差错类型|差错标志|柜面号|付款机构|渠道方机构标识|借方金额|贷方金额|手续费|差错平台日期|差错平台流水号|核心日期|核心流水号|错误信息|\n");
				sb.append(szSettDate).append("|").append(szErrReason).append("|").append(szPayAcctNo).append("|").append(szTxAmt).append("|")
				.append(szOthSeq).append("|").append(szClearDate).append("|").append(szErrType).append("|").append(szErrFlag).append("|").append(szRckTeller).append("|")
				.append(szPayBrch).append("|").append(szChnlBrch).append("|").append(RcvFee).append("|").append(PayFee).append("|").append(szTranFee).append("|")
				.append(szErrPlatDate).append("|").append(szErrPlatSeq).append("|").append(szErrHostDate).append("|").append(szErrHostSeq).append("|").append(szErrMsg).append("|");
				sb.append("\n");
				fos.write(sb.toString().getBytes("GBK"));
				i++;
			}
			fos.flush();
			fos.close();
			
			EPOper.put(tpID, "ISO_8583[0].iso_8583_025", fileName);
			//发送文件
			TermPubBean.Sendfile();
			
		} catch (Exception e) {
			SysPub.appLog("ERROR", "生成文件异常");
			throw e;
		} finally {
			if (fos != null) {
                try {
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace(BaseLog.getExpOut());
                }
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace(BaseLog.getExpOut());
                }
            }
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
