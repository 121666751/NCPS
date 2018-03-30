package com.adtec.ncps.busi.ncp.qry;

import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.struct.dta.DtaInfo;

public class SQRY00610005 {
	/*
	 * @author liangjr
	 * @createAt 2017年9月18日
	 * @version 1.0 银联无卡支付业务协议查询 
	 */
	public static int ncpBusiQry() throws Exception 
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		TermPubBean.ecapTermFormat("银联无卡支付业务协议查询  ！");
		try{
			String szSqlWhere = "";
			String content = "";
			//账号
			String szCardNo = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_037");
			if (szCardNo!=null&&!szCardNo.isEmpty())
			{
				szSqlWhere +="and acct_no = '"+szCardNo+"' ";
			}
			// 签约手机
			String szPhone = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_058");
			if (szPhone!=null&&!szPhone.isEmpty())
			{
				szSqlWhere +="and phn = '"+szPhone+"' ";
			}
			// 机构号
			String szBrc1 = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_003");
			if (szBrc1!=null&&!szBrc1.isEmpty())
			{
				szSqlWhere +="and brch_no = '"+szBrc1+"' ";
			}
		    // 签约日期
			String szSignDate = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_047");
			if(szSignDate!=null&&!szSignDate.isEmpty()&&!"0000-00-00".equals(szSignDate)&&10==szSignDate.length())
			{
				//szSignDate = szSignDate.replace("-", "");
				szSqlWhere +="and substr( sign_date, 0, 10) = '"+szSignDate+"' ";
			}
			// 签约类型
			String szTranType = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_069");
			if (szTranType!=null&&!szTranType.isEmpty())
			{
				szSqlWhere +="and sign_type = '"+szTranType+"' ";
			}
			// 协议状态
			SysPub.appLog("DEBUG", szSqlWhere);
			String szSql = "";
			String szStatus = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_068");
			if(szStatus!=null&&!szStatus.isEmpty())
			{
				if( "1".equalsIgnoreCase(szStatus))
						szSqlWhere +="and stat = 'Y' ";
				else
						szSqlWhere +="and stat = 'N' ";
			}
			
			szSql = " select * from t_ncp_sign where 1=1 "+szSqlWhere+"";
					
			SysPub.appLog("DEBUG", szSql);
			Object[] value = {};
			int iRet = DataBaseUtils.queryToElem(szSql, "T_NCP_SIGN", value);
			if( iRet < 0)
			{
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
				EPOper.put(szTpID,"ISO_8583[0].iso_8583_012", "9999");
				TermPubBean.putTermRspCode("9999","数据库操作失败！");
				SysPub.appLog("ERROR", "数据库操作失败！");
				return -1;
			}
			if( iRet == 0)
			{
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
				EPOper.put(szTpID,"ISO_8583[0].iso_8583_012", "E102");
				TermPubBean.putTermRspCode("9999","查询无记录");
				SysPub.appLog("ERROR", "查询无记录！");
				return -1;
			}
			String wordSeparator = "|";// "\"~\"";  //字段间的分隔符
//		    content="{\n"
//		    		+"title=银联无卡支付业务协议查询 \n"
//				    +"trancode=\n"
//				    +"fld=CardNo~签约卡号~0~32~2~NULL\n"
//				    +"fld=Phone~签约手机号~0~18~2~NULL\n"
//				    +"fld=TranType~签约类型~0~2~2~<0>协议支付签约<1>借记转账签约\n"
//				    +"fld=BMSDFXY~协议号~0~100~2~NULL\n"
//				    +"fld=Status~协议状态~0~1~2~<Y>已签约 <N>解约\n"
//				    +"fld=IdType~证件类型~0~2~2~NULL\n"
//				    +"fld=IdNo~证件号码~0~18~2~NULL\n"
//			        +"fld=CustName~客户名称~0~70~2~NULL\n"
//			        +"fld=Brc1~开户机构~0~16~2~NULL\n"
//			        +"fld=~签约日期时间~0~19~2~NULL\n"
//			        +"fld=~解约日期时间~0~19~2~NULL\n"
//			        +"fld=~签约发起机构标识~0~11~2~NULL\n"
//			        +"fld=~业务关联账号~0~16~2~NULL\n"
//			        +"fld=~签约交易机构号~0~10~2~NULL\n"
//			        +"fld=~签约交易柜员号~0~10~2~NULL\n"
//			        +"fld=~解约机构~0~10~2~NULL\n"
//			        +"fld=~解约柜员~0~10~2~NULL\n"
//			        +"fld=~备注~0~60~2~NULL\n"
//			        +"fld=~备注1~0~60~2~NULL\n"
//			        +"fld=~备注2~0~60~2~NULL\n"
//			        +"}\n";
			content = "~签约卡号 |签约手机号|签约类型|协议号|协议状态|证件类型|证件号码|客户名称 |开户机构 |签约日期时间 |解约日期时间 |签约发起机构标识  |业务关联账号 |签约交易机构号 |签约交易柜员号 |解约机构|解约柜员 |备注 |备注1|备注2\n";
		
			//content = String.format("%-16s %10s %8s %40s %8s %18s %10s %16s %19s %19s %11s %10s %10s %10s %10s %10s %10s %10s\n", "签约卡号","签约手机号","签约类型","协议号","协议状态","证件类型","证件号码","客户名称","开户机构","签约日期时间","解约日期时间","签约发起机构标识","业务关联账号","签约交易机构号","签约交易柜员号","解约机构","解约柜员","备注","备注1","备注2" );
			if(iRet>0){
			for(int i = 0; i < iRet; i++) {
	            String CardNo = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].ACCT_NO")); 
	            String Phone = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].PHN")); 
		        String TranType = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].SIGN_TYPE")); 
                String BMSDFXY = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].SIGN_NO")); 
                String Status = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].STAT")); 
		        String IdType = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].CERT_TYPE")); 
		        String IdNo = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].CERT_NO")); 
		        String CustName = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].ACCT_NAME")); 
		        String Brc1 = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].OPEN_BRCH")); 
		        String SignNoDate = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].SIGN_DATE")); 
		        String UnSignDate = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].UNSIGN_DATE")); 
		        String SndFlag = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].SIGN_BRCH")); 
		        String AcctNo2 = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].ACCT_NO2")); 
		        String SignBrch = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].BRCH_NO")); 
		        String SignTel = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].SIGN_TELLER")); 
		        String UnSignBrch = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].UNSIGN_BRCH")); 
		        String UnSignTel = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].UNSIGN_TELLER"));
		        String Rmrk = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].RMRK")); 
		        String Rmrk1 = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].RMRK1")); 
		        String Rmrk2 = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_SIGN["+i+"].RMRK2")); 
		        
		                
		        //拼接每一行的数据
				content = content + CardNo + wordSeparator + Phone + wordSeparator + TranType + wordSeparator
				     + BMSDFXY + wordSeparator + Status + wordSeparator + IdType + wordSeparator 
				     + IdNo + wordSeparator + CustName + wordSeparator + Brc1 + wordSeparator 
				     + SignNoDate + wordSeparator + UnSignDate + wordSeparator + SndFlag + wordSeparator 
				     + AcctNo2 + wordSeparator + SignBrch + wordSeparator + SignTel
				     + wordSeparator + UnSignBrch + wordSeparator + UnSignTel + wordSeparator + Rmrk
				     + wordSeparator + Rmrk1 + wordSeparator + Rmrk2
				     + "\n";
				                			                
		        }
			}

			//获取文件路径
			String filePath = SysDef.WORK_DIR + SQRYPub.getConf("FilePath");
			//获取外部交易码
			//String txCode = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].TRANSLOG_ELEMENT[0].txcode[0]");
			String txCode = "08003";
			//获取机构码
			String brc = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_002[0]");
			//获取柜员号
			String teller = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_007[0]");
			//写文件
			SQRYPub.writeTextToFile(content, filePath, txCode+brc+teller, "GBK");
			SysPub.appLog("DEBUG", "生成文件全路径="+filePath+txCode+brc+teller);
			EPOper.put(szTpID,"ISO_8583[0].iso_8583_025[0]", txCode+brc+teller);
			//EPOper.put(szTpID,"TERM_NCP_QRY_610005_OUT[0].FileFlag[0]", "2");
			//EPOper.put(szTpID,"TERM_NCP_QRY_610005_OUT[0].InqFormid[0]", txCode);
			
			TermPubBean.Sendfile();
			EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "0");
			TermPubBean.putTermRspCode("0000", "交易成功");
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
}
