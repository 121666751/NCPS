package com.adtec.ncps.busi.ncp.qry;


import com.adtec.ncps.TermPubBean;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.struct.dta.DtaInfo;
public class SQRY00610004 {
	/*
	 * @author liangjr
	 * @createAt 2017年9月18日
	 * @version 1.0 银联无卡支付业务查询 
	 */
	public static int ncpBusiQry() throws Exception 
	{
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		TermPubBean.ecapTermFormat(" 查询无卡支付交易流水失败 ！");
		
		try{
			String szSqlWhere = "";
			String content = "";
			//卡号
			String szCardNo = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_025");
			if (szCardNo!=null&&!szCardNo.isEmpty())
			{
				szSqlWhere +="and ( a.pay_acct_no = '"+szCardNo+"' or a.payee_acct_no = '"+szCardNo+"' )";
			}
			
			//业务类别
			String szTranType = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_068");
			if( "1".equals(szTranType))
			{
				szSqlWhere +="and a.tx_type in(3,4,5,6,7,8) ";
			}
			else if( "2".equals(szTranType))
			{
				szSqlWhere +="and a.tx_type in(1,2,9,'A','E') ";
			}
			
			//开户机构号
			String szBrc1 = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_002");
			if (szBrc1!=null&&!szBrc1.isEmpty())
			{
				szSqlWhere +="and a.open_brch = '"+szBrc1+"' ";
			}
			//银联流水号
			String szPaySeqNo = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_042");
			if (szPaySeqNo!=null&&!szPaySeqNo.isEmpty())
			{
				szSqlWhere +="and a.oth_seq = '"+szPaySeqNo+"' ";
			}
			//核心流水号
			String szHostSeqNo = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_004");
			if (szHostSeqNo!=null&&!szHostSeqNo.isEmpty())
			{
				szSqlWhere +="and a.host_seq = '"+szHostSeqNo+"' ";
			}
			//交易状态
			String szTranStatus = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_067");
			if ("1".equals(szTranStatus)||"2".equals(szTranStatus))
			{
				szSqlWhere +="and a.stat = '"+szTranStatus+"' ";
			}
			//交易金额
			String szTranAmt = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_040");
			if (!"0.00".equals(szTranAmt)&&szTranAmt!=null)
			{
				szSqlWhere +="and a.tx_amt = "+szTranAmt+" ";
			}
					
			// 银联开始日期
			String szBeginDate = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_044");
			if(szBeginDate!=null&&!szBeginDate.isEmpty()&&!"0000-00-00".equals(szBeginDate)&&10==szBeginDate.length())
			{
				szBeginDate = szBeginDate.replace("-", "");
				szSqlWhere +="and a.plat_date >= '"+szBeginDate+"' ";
			}
		    // 银联结束日期
			String szEndDate = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_045");
			if(szEndDate!=null&&!szEndDate.isEmpty()&&!"0000-00-00".equals(szEndDate)&&10==szEndDate.length())
			{
				szEndDate = szEndDate.replace("-", "");
				szSqlWhere +="and a.plat_date <= '"+szEndDate+"' ";
			}
			String szSql = " select a.*, b.MRCHNT_NO, b.MRCHNT_NAME, b.ORDER_DESC from t_ncp_book a, t_ncp_book_ext b" 
					+ " where a.plat_date = b.plat_date and a.seq_no = b.seq_no "+szSqlWhere+"";
			SysPub.appLog("DEBUG", szSql);
			Object[] value = {};
			int iRet = DataBaseUtils.queryToElem(szSql, "T_NCP_BOOK", value);
			if( iRet < 0)
			{
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
				TermPubBean.putTermRspCode("9999","数据库操作失败！");
				SysPub.appLog("ERROR", "数据库操作失败！");
				return -1;
			}
			if( iRet == 0)
			{
				EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "-1");
				TermPubBean.putTermRspCode("9999","查询无记录");
				SysPub.appLog("ERROR", "查询无记录！");
				return -1;
			}
			String wordSeparator = "   ";//"\"~\"";  //字段间的分隔符
		    content="{\n"
		    		+"title=无卡支付交易明细\n"
				    +"trancode=\n"
				    +"fld=BrchNo~开户机构~0~12~2~NULL\n"
				    +"fld=ComBrch~交易机构~0~12~2~NULL\n"
				    +"fld=TxDate~交易日期~0~10~2~NULL\n"
				    +"fld=TxTeller~交易柜员~0~12~2~NULL\n"
				    +"fld=Amt~金额~0~16~2~\n"
				    +"fld=TxName~交易名称~0~32~2~NULL\n"
				    +"fld=BusiSide~业务方向~0~10~2~<0>发起方 <1>接收方\n"
				    +"fld=SndBrch~发送机构号~0~12~2~NULL\n"
			        +"fld=PayAcct~付款账号~0~32~2~NULL\n"
			        +"fld=PayAcctName~付款户名~0~64~2~NULL\n"
			        +"fld=PayBrch~付款机构号~0~12~2~NULL\n"
			        +"fld=PayeeAcct~收款账号~0~32~2~NULL\n"
			        +"fld=PayeeAcctName~收款户名~0~64~2~NULL\n"
			        +"fld=PayeeBrch~收款机构号~0~12~2~NULL\n"
			        +"fld=ClearDate~银联清算日期~0~10~2~NULL\n"
			        +"fld=CupSeq~银联流水号~0~32~2~NULL\n"
			        +"fld=TranSeq~前置流水~0~32~2~NULL\n"
			        +"fld=HostDate~主机日期~0~10~2~NULL\n"
			        +"fld=HostSeq~主机流水号~0~32~2~NULL\n"
			        +"fld=BusiStat~业务状态~0~8~2~<0>预计<1>成功<2>失败<3>超时<9>退货\n"
			        +"fld=CupCode~返回银联错误码~0~12~2~NULL\n"
			        +"fld=CupMsg~返回银联信息~0~60~2~NULL\n"
			        +"fld=CupMsg~核心返回信息~0~60~2~NULL\n"
				    +"fld=BusiCode~商户编码~0~60~2~NULL\n"
			        +"fld=BusiName~商户名称~0~60~2~\n"
			        +"fld=DetNo~订单号~0~60~2~NULL\n"
			        +"fld=Detail~订单详情~0~60~2~NULL\n"
			        +"}\n";
		    content = "~开户机构 |交易机构|交易日期 |交易柜员 |金额  |交易名称 |业务方向 |发送机构号 |付款账号 |付款户名|付款机构号|收款账号|收款户名|收款机构号|银联清算日期|银联流水号|前置流水|主机日期|主机流水号|业务状态 |返回银联错误码|返回银联信息|核心返回信息|商户编码|商户名称 |订单号|订单详情\n";
			if(iRet>0){
			for(int i = 0; i < iRet; i++) { 
				String BrchNo = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].OPEN_BRCH")); 
	            String ComBrch = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].BRCH_NO")); 
		        String TxDate = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PLAT_DATE")); 
                String TxTeller = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].TELLER_NO"));
                Double Amt = (Double) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].TX_AMT"); 
                String TxName = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].TX_NAME"));
		        String BusiSide = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].RP_FLAG")); 
		        String SndBrch = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].SND_BRCH_NO"));  
		        String PayAcct = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PAY_ACCT_NO")); 
		        String PayAcctName = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PAY_ACCT_NAME"));
		        String PayBrch = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PAY_BRCH"));
		        String PayeeAcct = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PAYEE_ACCT_NO")); 
		        String PayeeAcctName = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PAYEE_ACCT_NAME")); 
		        String PayeeBrch = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].PAYEE_BRCH")); 
		        String ClearDate = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].CLEAR_DATE")); 
                String CupSeq = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].OTH_SEQ")); 
                Integer TranSeq = (Integer) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].SEQ_NO"); 
		        String HostDate = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].HOST_DATE")); 
		        String HostSeq = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].HOST_SEQ")); 
		        String BusiStat = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].STAT"));
		        String CupCode = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].RET_CODE"));
		        String CupMsg= SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].RET_MSG")); 
		        String HostMsg= SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].HOST_MSG"));
		        String BusiCode = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].MRCHNT_NO")); 
		        String BusiName = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].MRCHNT_NAME"));
		        String DetNo = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].ORDER_NO"));
		        String Detail = SQRYPub.outOfNull((String) EPOper.get(szTpID, "T_NCP_BOOK["+i+"].ORDER_DESC"));
		        	        
		                
		        //拼接每一行的数据
				content = content + BrchNo + wordSeparator 
					 + ComBrch + wordSeparator + TxDate + wordSeparator
				     + TxTeller + wordSeparator + Amt + wordSeparator + TxName 
				     + wordSeparator + BusiSide + wordSeparator 
				     + SndBrch + wordSeparator + PayAcct + wordSeparator 
				     + PayAcctName + wordSeparator + PayBrch + wordSeparator
				     + PayeeAcct + wordSeparator + PayeeAcctName + wordSeparator + PayeeBrch 
				     + wordSeparator + ClearDate + wordSeparator + CupSeq + wordSeparator 
				     + TranSeq + wordSeparator + HostDate + wordSeparator + HostSeq + wordSeparator 
				     + BusiStat + wordSeparator + CupCode + wordSeparator 
				     + CupMsg + wordSeparator + HostMsg + wordSeparator + BusiCode + wordSeparator + BusiName 
				     + wordSeparator + DetNo + wordSeparator + Detail
				     + "\n";         			                
		        }
			}
			//获取文件路径
			String filePath = SysDef.WORK_DIR + SQRYPub.getConf("FilePath");
			//获取外部交易码
			//String txCode = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].TRANSLOG_ELEMENT[0].txcode[0]");
			String txCode = "08002";
			//获取机构码
			String brc = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_003[0]");
			//获取柜员号
			String teller = (String) EPOper.get(szTpID, "ISO_8583[0].iso_8583_007[0]");
			//写文件
			SQRYPub.writeTextToFile(content, filePath, txCode+brc+teller, "GBK");
			SysPub.appLog("DEBUG", "生成文件全路径="+filePath+txCode+brc+teller);
			EPOper.put(szTpID,"ISO_8583[0].iso_8583_025[0]", txCode+brc+teller);
			//EPOper.put(szTpID,"TERM_NCP_QRY_610004_OUT[0].FileFlag[0]", "2");
			//EPOper.put(szTpID,"TERM_NCP_QRY_610004_OUT[0].InqFormid[0]", txCode);
			//发送文件
			TermPubBean.Sendfile();
			
			EPOper.put(szTpID, "INIT._FUNC_RETURN", 0, "0");
			TermPubBean.putTermRspCode("0000", "交易成功");
			return 0;
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
}
