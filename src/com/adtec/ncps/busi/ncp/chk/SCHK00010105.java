package com.adtec.ncps.busi.ncp.chk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.ncps.busi.ncp.qry.SQRYPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.log.BaseLog;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/********************************************************
 * *
 * 
 * @author dingjunbo 对账差错查询
 *******************************************************/
public class SCHK00010105 {
	/**
	 * 对账查询
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void chkErrQry() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		// 报文头赋值
		BusiMsgProc.putMngHeadMsg(tpID);
		String szLqdDate = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_046");// 清算日期
		String szTranName = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_027");// 交易名称
		//String szErrBrchNo = (String) EPOper.get(tpID, "MngChkIn[0].SCHK00010105[0].ErrBrchNo");// 差错机构号
		String szType = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_070");// 类别
		String szQryFlag = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_071");// QryFlag
		String szErrType = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_068");// 差错类型 对应表字段公司编号
		szErrType = "035001";
		String szErrStat = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_069");// 差错确认状态
		// 检查查询条件
		if (StringTool.isNullOrEmpty(szLqdDate)) {
			SysPub.appLog("ERROR", "清算日期为空");
			TermPubBean.putTermRspCode("E101", "清算日期不能为空");
			return;
		}
		/*if (StringTool.isNullOrEmpty(szTranName)) {
			SysPub.appLog("ERROR", "交易名称为空");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "交易名称不能为空");
			return;
		}
		if (StringTool.isNullOrEmpty(szErrBrchNo)) {
			SysPub.appLog("ERROR", "差错机构号为空");
			BusiPub.setMngMsg(tpID, SysPubDef.ERR_RET, "差错机构号不能为空");
			return;
		}*/
		if (StringTool.isNullOrEmpty(szType)) {
			SysPub.appLog("ERROR", "对账差错类别为空");
			TermPubBean.putTermRspCode("E101", "对账差错类别不能为空");
			return;
		}
		if (StringTool.isNullOrEmpty(szQryFlag)) {
			SysPub.appLog("ERROR", "查询下载标志为空");
			TermPubBean.putTermRspCode("E101", "查询下载标志不能为空");
			return;
		}
		// 查询记录数
		int szBeginRec = 0;
		String beginRec = (String) EPOper.get(tpID, "ISO_8583[0].iso_8583_041");
		if (StringTool.isNullOrEmpty(beginRec)){
			szBeginRec = 0;
		}else{
			szBeginRec = Integer.parseInt(beginRec.trim());
		}			
		int iRowNum = ChkPub.getRowNum(tpID, "");
		szLqdDate = szLqdDate.trim();
		szLqdDate = szLqdDate.substring(0,4)+"-"+szLqdDate.substring(4,6)+"-"+szLqdDate.substring(6,8);
		szTranName = szTranName.trim();
		//szErrBrchNo = szErrBrchNo.trim();
		szType = szType.trim();
		szQryFlag = szQryFlag.trim();
		szErrType = szErrType.trim();
		szErrStat = szErrStat.trim();
		SysPub.appLog("DEBUG", "szLqdDate=%s, szErrType=%s, szErrStat=%s, iRowNum=%s, szBeginRec=%s",szLqdDate, szErrType, szErrStat, iRowNum, szBeginRec);
		//银联对应业务类型为035001
		if( "2".equals(szErrType) )
		{
			szErrType = "035001";
		}
		szQryFlag ="1";
		
		// QryFlag 0—查询,1—文件
		if ("0".equals(szQryFlag.trim())) {
			String sql ="";
			String sqlTot = "";
			int iCount = -1;
			int iTotCount = 0;
			if(szTranName==null||"".equals(szTranName.trim())){
				if(szErrStat==null||"".equals(szErrStat.trim())){
					sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
							+ "brch_no,teller_no,rck_teller,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
							+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
							+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
							+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,err_msg,"
							+ "amt1,amt2,amt3,in_fee,charge_fee,logo_fee,out_fee,num1,num2,num3,str1,str2,str3 "
							+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? "
							+ "and rownum <= ? order by clear_date asc ) where r >= ?";
					sqlTot = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? "
							+ "order by clear_date asc ";
					Object[] obj = { szLqdDate, szErrType, iRowNum, szBeginRec };
					Object[] objTol = { szLqdDate, szErrType};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);				
					iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
				}else{
					sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
							+ "brch_no,teller_no,rck_teller,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
							+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
							+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
							+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,err_msg,"
							+ "amt1,amt2,amt3,in_fee,charge_fee,logo_fee,out_fee,num1,num2,num3,str1,str2,str3 "
							+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and err_flag = ? "
							+ "and rownum <= ? order by clear_date asc ) where r >= ?";
					sqlTot = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and err_flag = ? "
							+ "order by clear_date asc ";
					Object[] obj = { szLqdDate, szErrType, szErrStat, iRowNum, szBeginRec };
					Object[] objTol = { szLqdDate, szErrType, szErrStat};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);				
					iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
				}
			}else{
				if(szErrStat==null||"".equals(szErrStat.trim())){
					sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
							+ "brch_no,teller_no,rck_teller,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
							+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
							+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
							+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,err_msg,"
							+ "amt1,amt2,amt3,in_fee,charge_fee,logo_fee,out_fee,num1,num2,num3,str1,str2,str3 "
							+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and tx_code = ? "
							+ "and rownum <= ? order by clear_date asc ) where r >= ?";
					sqlTot = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and tx_code = ? "
							+ "order by clear_date asc ";
					Object[] obj = { szLqdDate, szErrType, szTranName, iRowNum, szBeginRec };
					Object[] objTol = { szLqdDate, szErrType, szTranName};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);
					
					iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
				}else{
					sql = "select clear_date,entr_no,busi_no,busi_idx_type,plat_date,seq_no,"
							+ "brch_no,teller_no,rck_teller,area_no,tx_code,host_date,host_seq,oth_date,oth_seq,"
							+ "busi_idx,acct_brch,acct_no1,acct_no2,tx_date,tx_time,tx_amt,fee,err_flag,"
							+ "curr_no,host_chk_flag,oth_chk_flag,proc_flag,host_msg,oth_msg,chk_msg,"
							+ "err_plat_date,err_plat_seq,err_host_date,err_host_seq,err_msg,"
							+ "amt1,amt2,amt3,num1,num2,num3,in_fee,charge_fee,logo_fee,out_fee,str1,str2,str3 "
							+ "from ( select rownum as r,t.* from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and err_flag = ? and tx_code = ? "
							+ "and rownum <= ? order by clear_date asc ) where r >= ?";
					sqlTot = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and err_flag = ? and tx_code = ? "
							+ "order by clear_date asc ";
					Object[] obj = { szLqdDate, szErrType, szErrStat, szTranName, iRowNum, szBeginRec };
					Object[] objTol = { szLqdDate, szErrType, szErrStat, szTranName};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj);
					
					iTotCount =  DataBaseUtils.queryToCount(sqlTot, objTol);
				}					
			}
			
			//SysPub.appLog("DEBUG", "sql:%s,sqlCount:%s", sql,sqlTot);
			if (iCount < 0) {
				SysPub.appLog("ERROR", "数据库错误");
				TermPubBean.putTermRspCode("E101",  "数据库错误");
				return;
			}
			if (iCount == 0) {
				SysPub.appLog("ERROR", "查询不到记录");
				TermPubBean.putTermRspCode("E103",  "查询不到记录");
				return;
			}
			//循环报文赋值
			putMsg(tpID, iCount,"SCHK00010105");
			EPOper.put(tpID,"ISO_8583[0].iso_8583_041",iTotCount);
			EPOper.put(tpID,"ISO_8583[0].iso_8583_042",iCount);
			BusiPub.setMngMsg(tpID,SysPubDef.SUC_RET,SysPubDef.SUC_MSG);
			//文件下载
		} else if ("1".equals(szQryFlag.trim())) {
			String sql ="";
			int iCount = -1;
			if(szTranName==null||"".equals(szTranName.trim())){
				if(szErrStat==null||"".equals(szErrStat.trim())){
					sql = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? "
							+ "order by clear_date asc ";
					//SysPub.appLog("DEBUG", "%s", sql);
					Object[] obj2 = { szLqdDate, szErrType};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj2);
				}else{
					sql = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and err_flag = ? "
							+ "order by clear_date asc ";
					//SysPub.appLog("DEBUG", "%s", sql);
					Object[] obj2 = { szLqdDate, szErrType, szErrStat};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj2);
				}
			}else{
				if(szErrStat==null||"".equals(szErrStat.trim())){
					sql = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and tx_code = ? "
							+ "order by clear_date asc ";
					//SysPub.appLog("DEBUG", "%s", sql);
					Object[] obj2 = { szLqdDate, szErrType,szTranName};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj2);
				}else{
					sql = "select * from t_chk_err t where clear_date = ? "
							+ "and entr_no = ? and err_flag = ? and tx_code = ? "
							+ "order by clear_date asc ";
					//SysPub.appLog("DEBUG", "%s", sql);
					Object[] obj2 = { szLqdDate, szErrType, szErrStat, szTranName};
					iCount = DataBaseUtils.queryToElem(sql, "T_CHK_ERR", obj2);
				}
			}												
			if (iCount < 0) {
				SysPub.appLog("ERROR", "数据库错误");
				TermPubBean.putTermRspCode("E101",  "数据库错误");
				return;
			}
			if (iCount == 0) {
				SysPub.appLog("ERROR", "查询不到记录");
				TermPubBean.putTermRspCode("E103",  "查询不到记录");
				return;
			}
			createFile(tpID,iCount);
			TermPubBean.putTermRspCode("0000", "交易成功");
			
		}
	}

	/**
	 * 数据对象的值赋值到报文数据
	 * @param tpID	 数据池ID  数据对象element
	 * @throws Exception 
	 */
	public static double putMsg(String tpID, int iCount,String element) throws Exception
	{
		double totAmt = 0;
		//int iCount = EPOper.getSuffixNo(tpID, "T_CHK_ERR");
		int i = 0;
		String szSrc = "T_CHK_ERR[", szDes = "MngChkOut[0]."+element+"[0].Loop[";
		while (i < iCount)
		{
			//EPOper.copy(tpID, tpID, szSrc + i + "].CLEAR_DATE", szDes + i + "].LqdDate1");
			String CLEAR_DATE = (String)EPOper.get(tpID,szSrc + i + "].CLEAR_DATE");
			CLEAR_DATE = CLEAR_DATE.substring(0,4)+CLEAR_DATE.substring(5,7)+CLEAR_DATE.substring(8,10);
			EPOper.put(tpID, szDes + i + "].LqdDate1", CLEAR_DATE);
			EPOper.copy(tpID, tpID, szSrc + i + "].ENTR_NO", szDes + i + "].EntrNo");
			EPOper.copy(tpID, tpID, szSrc + i + "].PLAT_DATE", szDes + i + "].PlatDt");
			EPOper.copy(tpID, tpID, szSrc + i + "].SEQ_NO", szDes + i + "].PlatSeqNo");
			String acctNo1 = (String)EPOper.get(tpID,szSrc + i + "].ACCT_NO1");
			String acctNo2 = (String)EPOper.get(tpID,szSrc + i + "].ACCT_NO2");
			EPOper.copy(tpID, tpID, szSrc + i + "].ACCT_NO1", szDes + i + "].TranAcct");
			if(acctNo1==null||acctNo1.trim().isEmpty()){
				EPOper.copy(tpID, tpID, szSrc + i + "].ACCT_NO2", szDes + i + "].TranAcct");
			}
			
			EPOper.copy(tpID, tpID, szSrc + i + "].CURR_NO", szDes + i + "].Ccy");
			EPOper.copy(tpID, tpID, szSrc + i + "].TX_AMT", szDes + i + "].TranAmt");
			double TranAmt = (Double)EPOper.get(tpID, szSrc + i + "].TX_AMT");
			totAmt = totAmt + TranAmt;
			EPOper.copy(tpID, tpID, szSrc + i + "].OTH_DATE", szDes + i + "].CupDate");
			//EPOper.copy(tpID, tpID, szSrc + i + "].ERR_FLAG", szDes + i + "].ErrFlg");
			EPOper.copy(tpID, tpID, szSrc + i + "].CHK_MSG", szDes + i + "].ErrFlg");
			//EPOper.copy(tpID, tpID, szSrc + i + "].TX_CODE", szDes + i + "].TranName1");
			String TX_CODE = (String)EPOper.get(tpID, szSrc + i + "].TX_CODE");
			if(TX_CODE!=null||!TX_CODE.trim().isEmpty()){
				TX_CODE = SysPub.getTxName(TX_CODE.trim());
			}
			EPOper.put(tpID, szDes + i + "].TranName1",TX_CODE);
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_MSG", szDes + i + "].ErrStatCap");
			//EPOper.copy(tpID, tpID, szSrc + i + "].ACCT_NO2", szDes + i + "].AcctNo");
			EPOper.copy(tpID, tpID, szSrc + i + "].HOST_DATE", szDes + i + "].HostDate");
			EPOper.copy(tpID, tpID, szSrc + i + "].HOST_SEQ", szDes + i + "].HostSeqNo");
			EPOper.copy(tpID, tpID, szSrc + i + "].TX_TIME", szDes + i + "].CupTime");
			EPOper.copy(tpID, tpID, szSrc + i + "].OTH_SEQ", szDes + i + "].CupSeqNo");
			//EPOper.copy(tpID, tpID, szSrc + i + "].TX_DATE", szDes + i + "].TranDate");
			String TX_DATE = (String)EPOper.get(tpID,szSrc + i + "].TX_DATE");
			if(TX_DATE!=null && !TX_DATE.isEmpty()){
				TX_DATE = TX_DATE.substring(0,4)+TX_DATE.substring(5,7)+TX_DATE.substring(8,10);
			}
			
			EPOper.put(tpID, szDes + i + "].TranDate", TX_DATE);
			EPOper.copy(tpID, tpID, szSrc + i + "].ERR_FLAG", szDes + i + "].ErrCnStat");
			if("SCHK00010105".equals(element)){
				EPOper.copy(tpID, tpID, szSrc + i + "].ERR_PLAT_DATE", szDes + i + "].FrntErrPrcDt");
				EPOper.copy(tpID, tpID, szSrc + i + "].ERR_PLAT_SEQ", szDes + i + "].FrntErrPrcSeqNo");
				EPOper.copy(tpID, tpID, szSrc + i + "].ERR_HOST_DATE", szDes + i + "].HostErrPrcDt");
				EPOper.copy(tpID, tpID, szSrc + i + "].ERR_HOST_SEQ", szDes + i + "].HostErrPrcSeqNo");
			}		
			EPOper.copy(tpID, tpID, szSrc + i + "].FEE", szDes + i + "].TranFee");
			double inFee = 0.00;
			double outFee = 0.00;
			double chargeFee = 0.00;
			double logoFee = 0.00;
			double PayFee = 0.00;
			double RcvFee = 0.00;
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
			
			if(inFee - outFee + chargeFee + logoFee >0){
				RcvFee = inFee - outFee + chargeFee + logoFee;
			}
			if(inFee - outFee + chargeFee + logoFee+inFee<0){
				PayFee = -(inFee - outFee + chargeFee + logoFee+inFee);
			}
			EPOper.put(tpID, szDes + i + "].PayFee",PayFee);
			EPOper.put(tpID, szDes + i + "].RcvFee",RcvFee);
			//EPOper.copy(tpID, tpID, szSrc + i + "].TELLER_NO", szDes + i + "].CnTeller");
			String CnTeller = (String)EPOper.get(tpID, szSrc + i + "].TELLER_NO");
			if(CnTeller==null||"".equals(CnTeller)||CnTeller.length()<6){
				EPOper.put(tpID, szDes + i + "].CnTeller", CnTeller);
			}else{
				EPOper.put(tpID, szDes + i + "].CnTeller", CnTeller.substring(0,6));
			}			
			//EPOper.copy(tpID, tpID, szSrc + i + "].RCK_TELLER", szDes + i + "].ChkTeller");
			String ChkTeller = (String)EPOper.get(tpID, szSrc + i + "].RCK_TELLER");
			if(ChkTeller==null||"".equals(ChkTeller)||ChkTeller.length()<6){
				EPOper.put(tpID, szDes + i + "].ChkTeller", ChkTeller);
			}else{
				EPOper.put(tpID, szDes + i + "].ChkTeller", ChkTeller.substring(0,6));
			}	
			i++;
		}
		return totAmt;
	}
	/**
	 * 根据数据对象生成文件
	 * @param  数据对象
	 * @throws Exception
	 */
	public static void createFile(String tpID, int iCount ) throws Exception {
		FileOutputStream fos = null;  
		try {
			String date = (String) EPOper.get(tpID, "T_CHK_ERR[0].CLEAR_DATE");
			date = date.substring(0,4)+date.substring(5,7)+date.substring(8,10);
			String nowTime = PubTool.getTime();
			//文件名
			String fileName = "UNCPS_CHK_ERR_" + date + "_" + nowTime + ".txt";
			//String filePath = SysPubDef.MNG_FILE_DIR+date.substring(0, 4)+"/"+date.substring(4,6)+"/"+date.substring(6,8)+"/";
			String filePath = SysDef.WORK_DIR + ResPool.configMap.get("FilePath");
			String szFile = filePath + fileName;
			File path = new File(filePath);
			if(!path.exists()) {
        		path.mkdirs();
			}
			fos = new FileOutputStream(new File(szFile));
			
			int i = 0;
			while (i < iCount) {
				StringBuffer sb = new StringBuffer();
				String szLqdDate1 = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].CLEAR_DATE");
				szLqdDate1 = szLqdDate1.substring(0,4)+szLqdDate1.substring(5,7)+szLqdDate1.substring(8,10);
				String szEntrNo = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ENTR_NO");
				String szPlatDt = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].PLAT_DATE");
				Integer iPlatSeqNo = (Integer) EPOper.get(tpID, "T_CHK_ERR[" + i + "].SEQ_NO");
				String szTranAcct = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ACCT_NO1");
				String szAcctNo = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ACCT_NO2");
				if(szTranAcct==null||szTranAcct.trim().isEmpty()){
					szTranAcct = szAcctNo;
				}
				String szCcy = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].CURR_NO");
				Double dTranAmt = (Double) EPOper.get(tpID, "T_CHK_ERR[" + i + "].TX_AMT");
				//String szAgtFag 代理行标识;
				String szCupDate = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].OTH_DATE");
				String szErrFlg = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ERR_FLAG");
				String szChkMsg =(String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].CHK_MSG");
				String szTranCode = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].TX_CODE");
				if(szTranCode!=null||!szTranCode.trim().isEmpty()){
					szTranCode = SysPub.getTxName(szTranCode.trim());
				}
				String szErrStatCap = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ERR_MSG");
				
				String szHostDate = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].HOST_DATE");
				String szHostSeqNo = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].HOST_SEQ");
				String szCupTime = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].TX_TIME");
				String szCupSeqNo = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].OTH_SEQ");
				String szTranDate = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].TX_DATE");
				if(!StringTool.isNullOrEmpty(szTranDate))
				szTranDate = szTranDate.substring(0,4)+szTranDate.substring(5,7)+szTranDate.substring(8,10);
				//String szVchNo
				//String szResvVchNo1 ;
				//String szResvVchNo2 ;
				String szErrCnStat = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ErrCnStat");
				String szFrntErrPrcDt = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ERR_PLAT_DATE");
				int szFrntErrPrcSeqNo = (Integer) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ERR_PLAT_SEQ");
				String szHostErrPrcDt = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ERR_HOST_DATE");
				String szHostErrPrcSeqNo = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].ERR_HOST_SEQ");
				Double szTranFee = (Double) EPOper.get(tpID, "T_CHK_ERR[" + i + "].FEE");
				Double inFee = (Double) EPOper.get(tpID, "T_CHK_ERR[" + i + "].IN_FEE");
				Double outFee = (Double) EPOper.get(tpID, "T_CHK_ERR[" + i + "].OUT_FEE");
				Double chargeFee = (Double) EPOper.get(tpID, "T_CHK_ERR[" + i + "].CHARGE_FEE");
				Double logoFee = (Double) EPOper.get(tpID, "T_CHK_ERR[" + i + "].LOGO_FEE");
				Double PayFee = 0.00;
				Double RcvFee = 0.00;
				if(inFee - outFee + chargeFee + logoFee >0){
					RcvFee = inFee - outFee + chargeFee + logoFee;
				}
				if(inFee - outFee + chargeFee + logoFee+inFee<0){
					PayFee = -(inFee - outFee + chargeFee + logoFee+inFee);
				}
				String szTellerNo = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].TELLER_NO");
				String szRckTeller = (String) EPOper.get(tpID, "T_CHK_ERR[" + i + "].RCK_TELLER");
				if(i==0)
					sb.append("~清算日期|交易编码|交易账号|交易金额|银联日期|银联流水号|核心流水号|对账信息|错误标志|柜员|企业编号|平台日期|平台流水|手续费|借方金额|贷方金额|核心日期|银联日期||差错标志|差错处理日期|差错处理流水阿华哦|核心差错日期|核心差错流水|\n");
				sb.append(szLqdDate1).append("|").append(szTranCode).append("|").append(szTranAcct).append("|").append(dTranAmt).append("|")
				.append(szCupDate).append("|").append(szCupSeqNo).append("|").append(szHostSeqNo).append("|").append(szChkMsg).append("|")
				.append(szErrFlg).append("|").append(szRckTeller).append("|").append(szEntrNo).append("|").append(szPlatDt).append("|")
				.append(iPlatSeqNo).append("|").append(szTranFee).append("|").append(RcvFee).append("|").append(PayFee).append("|").append(szHostDate).append("|")
				.append(szCupDate).append("|").append("").append("|").append(szErrFlg).append("|").append(szFrntErrPrcDt).append("|").append(szFrntErrPrcSeqNo).append("|")
				.append(szHostErrPrcDt).append("|").append(szHostErrPrcSeqNo);
				sb.append("\n");
				fos.write(sb.toString().getBytes("GBK"));
				i++;
			}
			fos.flush();
			fos.close();
			
			EPOper.put(tpID,"ISO_8583[0].iso_8583_025",fileName);
			
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
	/**
	 * 把文件内容写入指定路劲的文件
	 * @param text 文本内容
	 * @param filePath 文件路径
	 * @param fileName 文件名
	 * @param unicode 编码集 如： GBK UTF-8
	 * @throws Exception 
	 */
    public static void writeTextToFile(String text,String filePath,String fileName,String unicode) throws Exception{
		FileOutputStream fos = null;  
        try {
        	//判断文件路劲格式是否正确
            if(filePath != null && !filePath.endsWith("\\")&& !filePath.endsWith("/")) {
            	filePath += "/";
            	
            	// 若文件夹不存在则生成
            	File path = new File(filePath);
            	if(!path.exists()) {
            		// 判断创建文件夹是否成功
            		boolean creatFlag = path.mkdirs();
            		
            		if(creatFlag == false) {
            			SysPub.appLog("INFO", "创建文件夹["+filePath+"]失败！");
            			throw new BaseException("P10311", "创建文件夹["+filePath+"]失败！");
            		}
            	}
            }
            //创建文件
            File file = new File(filePath+fileName);
            fos = new FileOutputStream(file);
            //把内容写入文件
            fos.write(text.getBytes(unicode)); 
        }catch (FileNotFoundException e) {
            e.printStackTrace(BaseLog.getExpOut());
            throw e;
        } catch (IOException e) {
            e.printStackTrace(BaseLog.getExpOut());
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
