package com.adtec.ncps.busi.ncp.dao;

import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.bean.Book_ext;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/**************************************************
 * 
 * @author dingjunbo 流水辅助表处理类
 *
 **************************************************/
public class BookExtDaoTool {

	/**
	 * 更新流水表辅助信息登记簿表，返回更新记录数
	 * 
	 * @return int
	 */
	public static int instBookExt() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		int iResult = 0;
		String tpID = dtaInfo.getTpId();
		try {
			// 判断是否登记流水
			String INS_JRNL = (String) EPOper.get(tpID, "T_TX[0].INS_JRNL");
			if ("N".equals(INS_JRNL))
				return 0;
			Book_ext book_ext = new Book_ext();
			// 更新t_ncp_book表
			book_ext.setPlat_date((String) EPOper.get(tpID, "T_NCP_BOOK[0].PLAT_DATE"));
			book_ext.setSeq_no((Integer) EPOper.get(tpID, "T_NCP_BOOK[0].SEQ_NO"));
			book_ext.setTx_code((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].MsgHeader[0].Trxtyp"));
			// 发起方信息
			book_ext.setSnd_acct_no((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctId"));
			book_ext.setPay_acct_info((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderAcctInf"));
			book_ext.setEntr_acct_name((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].CorpCard"));
			book_ext.setRgst_No((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].CorpName"));
			//产品信息
			book_ext.setGood_type((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ProductInf[0].ProductTp"));
			String szProduct_desc = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ProductInf[0].ProductAssInformation");
			if( StringTool.isNullOrEmpty(szProduct_desc) )
				szProduct_desc = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OriTrxInf[0].ProductAssInformation");
			book_ext.setProduct_desc(szProduct_desc);
			//风险监控信息
			book_ext.setDev_des((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceMode"));
			book_ext.setDev_lang((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceLanguage"));
			book_ext.setIp((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].sourceIP"));
			book_ext.setMac((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].MAC"));
			book_ext.setDev_no((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].devId"));
			book_ext.setGps((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].extensiveDeviceLocation"));
			book_ext.setSim_no((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceNumber"));
			String szSim_num = (String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].deviceSIMNumber");
			if( !StringTool.isNullOrEmpty(szSim_num) )
				book_ext.setSim_num(Integer.valueOf(szSim_num));
			else
				book_ext.setSim_num(0);
			book_ext.setUser_id((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].accountIDHash"));
			book_ext.setRisk((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].riskScore"));
			book_ext.setReason((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].riskReasonCode"));
			book_ext.setReg_date((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].mchntUsrRgstrTm"));
			book_ext.setEmail((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].mchntUsrRgstrEmail"));
			book_ext.setPro_code((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].rcvProvince"));
			book_ext.setCity_code((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].rcvCity"));
			book_ext.setGood_type((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RskInf[0].goodsClass"));
			//备付金信息
			book_ext.setInst_brch((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ResfdInf[0].ResfdAcctIssrId"));
			book_ext.setInst_acct_no((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ResfdInf[0].InstgAcctId"));
			book_ext.setInst_acct_name((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ResfdInf[0].InstgAcctNm"));
			// 渠道方信息
			book_ext.setChnl_brch((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].ChannelIssrInf[0].ChannelIssrId"));
			//订单信息
			book_ext.setOrder_desc((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].OrdrInf[0].OrdrDesc"));
			// 商户信息
			book_ext.setMrchnt_no((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].MrchntInf[0].MrchntNo"));
			book_ext.setMrchnt_type((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].MrchntInf[0].MrchntTpId"));
			book_ext.setMrchnt_name((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].MrchntInf[0].MrchntPltfrmNm"));
			// 二级商户信息
			book_ext.setSubmrchnt_no((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SubMrchntInf[0].SubMrchntNo"));
			book_ext.setSubmrchnt_type((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SubMrchntInf[0].SubMrchntTpId"));
			book_ext.setSubmrchnt_name((String) EPOper.get(tpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SubMrchntInf[0].SubMrchntPltfrmNm"));
			book_ext.setAmt1(0.00);
			book_ext.setAmt2(0.00);
			iResult = BookExtDao.insert(book_ext);
			if( iResult <= 0 )
			{
				SysPub.appLog("ERROR", "插入t_ncp_book_ext表失败");
			}
		} catch (Exception e) {
			SysPub.appLog("ERROR", "插入t_ncp_book_ext表失败");
			e.printStackTrace();
			throw e;
		}
		return iResult;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Double.valueOf(""));

	}

}
