package com.adtec.ncps.busi.ncp.chk;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.exception.BaseException;

public class SumXmlParse {

	private static Document doc;
	private static String szSettDate = "";

	public SumXmlParse() {
	}

	private static void init(String file) throws DocumentException, Exception {
		// if(doc == null)
		doc = getDocument(file);
	}

	private static Document getDocument(String sfileName) throws DocumentException {
		Document document = null;
		SAXReader reader = new SAXReader();

		document = reader.read(new File(sfileName));

		return document;
	}

	/**
	 * @throws BaseException
	 * @throws ServletException
	 * 
	 */
	public static int sumXmlParseDo(String _szChkdir, String _SttlDate) throws DocumentException, Exception {
		int iRet = 0;
		int iNum = 0;
		String szFileName = "";

		String szDate = _SttlDate.substring(0, 4) + _SttlDate.substring(5, 7) + _SttlDate.substring(8, 10);
		szFileName = _szChkdir + szDate + "/" + szDate + "_99_SUM";
		SysPub.appLog("INFO", "file=[%s]", szFileName);
		// 兼容无汇总文件情况
		File file = new File(szFileName);
		if (!file.exists()) {
			SysPub.appLog("WARN", "[%s]文件不存在", szFileName);
			return 0;
		}

		init(szFileName);

		if (null == doc) {
			SysPub.appLog("ERROR", "读取SUM文件失败");
			return -1;
		}

		Element root = doc.getRootElement();
		List<?> rootList = root.elements();

		for (Iterator<?> it = rootList.iterator(); it.hasNext();) {
			Element root1 = (Element) it.next();
			String tagName = root1.getName();
			SysPub.appLog("INFO", "tagName=[%s]", tagName);
			if ("SttlDate".equals(tagName)) {
				szSettDate = root1.getStringValue();
				// System.out.println(tagName + ":" + szSettDate);
				// szSettDate = szDate1.substring(0, 4) + szDate1.substring(5,
				// 7) + szDate1.substring(8, 10);
				if (!_SttlDate.equals(szSettDate)) {
					// System.out.println("对账清算日期：" + _SttlDate + "和文件清算日期" +
					// szSettDate + "不一致");
					SysPub.appLog("ERROR", "对账清算日期[%s]和文件清算日期[%s]不一致", _SttlDate, szSettDate);
					return -1;
				}
			} else if ("SummryHead".equals(tagName)) {
				iRet = xmlParseDoHead(root1);
				if (0 != iRet) {
					SysPub.appLog("ERROR", "解析SummryHead失败");
					return -1;
				}
			} else if ("SummryBody".equals(tagName)) {
				iNum++;
				iRet = xmlParseDoBody(root1, iNum);
				if (0 != iRet) {
					SysPub.appLog("ERROR", "解析SummryBody失败");
					return -1;
				}
			}
		}
		return 0;
	}

	public static int xmlParseDoHead(Element _Head) throws DocumentException, Exception {
		int iCntPyNb = 0;
		double dCntPyAmt = 0.00;
		double dAccCntPyAmt = 0.00;
		double dIssCntPyAmt = 0.00;
		int iCntPyerNb = 0;
		double dCntPyerAmt = 0.00;
		double dAccCntPyerAmt = 0.00;
		double dIssCntPyerAmt = 0.00;
		double dCntIntc = 0.00;
		double dCntLogoFee = 0.00;
		double dContErrTrxFee = 0.00;

		List<?> headlist = _Head.elements();
		for (Iterator<?> itHead = headlist.iterator(); itHead.hasNext();) {
			Element head = (Element) itHead.next();
			String tagName = head.getName();
			if ("CntPyNb".equals(tagName)) {
				String szCntPyNb = head.getStringValue();
				iCntPyNb = SysPub.tranStrToI(szCntPyNb);
			} else if ("CntPyAmt".equals(tagName)) {
				String szCntPyAmt = head.getStringValue();
				dCntPyAmt = SysPub.tranStrToD(szCntPyAmt, "CCY");
			} else if ("AccCntPyAmt".equals(tagName)) {
				String szAccCntPyAmt = head.getStringValue();
				dAccCntPyAmt = SysPub.tranStrToD(szAccCntPyAmt, "CCY");
			} else if ("IssCntPyAmt".equals(tagName)) {
				String szIssCntPyAmt = head.getStringValue();
				dIssCntPyAmt = SysPub.tranStrToD(szIssCntPyAmt, "CCY");
			} else if ("CntPyerNb".equals(tagName)) {
				String szCntPyerNb = head.getStringValue();
				iCntPyerNb = SysPub.tranStrToI(szCntPyerNb);
			} else if ("CntPyerAmt".equals(tagName)) {
				String szCntPyerAmt = head.getStringValue();
				dCntPyerAmt = SysPub.tranStrToD(szCntPyerAmt, "CCY");
			} else if ("AccCntPyerAmt".equals(tagName)) {
				String szAccCntPyerAmt = head.getStringValue();
				dAccCntPyerAmt = SysPub.tranStrToD(szAccCntPyerAmt, "CCY");
			} else if ("IssCntPyerAmt".equals(tagName)) {
				String szIssCntPyerAmt = head.getStringValue();
				dIssCntPyerAmt = SysPub.tranStrToD(szIssCntPyerAmt, "CCY");
			} else if ("CntIntc".equals(tagName)) {
				String szCntIntc = head.getStringValue();
				dCntIntc = SysPub.tranStrToD(szCntIntc, "DC");
			} else if ("CntLogoFee".equals(tagName)) {
				String szCntLogoFee = head.getStringValue();
				dCntLogoFee = SysPub.tranStrToD(szCntLogoFee, "DC");
			} else if ("ContErrTrxFee".equals(tagName)) {
				String szContErrTrxFee = head.getStringValue();
				dContErrTrxFee = SysPub.tranStrToD(szContErrTrxFee, "DC");
			}
		}
		String szSqlStr = "insert into t_ncp_sett_tot " + //
				" values (?,?,?,?,?,?,?,?,?,?,?,?,0.00,0.00,'','','') ";
		Object[] value = { szSettDate, iCntPyNb, dCntPyAmt, dAccCntPyAmt, dIssCntPyAmt, //
				iCntPyerNb, dCntPyerAmt, dAccCntPyerAmt, dIssCntPyerAmt, //
				dCntIntc, dCntLogoFee, dContErrTrxFee };

		int iRet = DataBaseUtils.execute(szSqlStr, value);
		if (0 == iRet) {
			SysPub.appLog("ERROR", "插入清算汇总信息表信息失败[%s]", iRet);
			return -1;
		}
		SysPub.appLog("INFO", "插入清算汇总信息表信息成功");

		return 0;
	}

	public static int xmlParseDoBody(Element _body, int _iNum) throws DocumentException, Exception {
		int iTrxSucsNb = 0;
		double dTrxSucsAmt = 0.00;
		double dTrxFee = 0.00;
		double dIssCntPyAmt = 0.00;
		int iCntPyerNb = 0;
		double dCntPyerAmt = 0.00;
		double dAccCntPyerAmt = 0.00;
		double dIssCntPyerAmt = 0.00;
		double dCntIntc = 0.00;
		double dCntLogoFee = 0.00;
		double dErrTrxFee = 0.00;
		String szTrxTp = "";
		String szTrxSucsNb = "";
		String szTrxSucsAmt = "";
		String szTrxFee = "";
		String szIssCntPyAmt = "";
		String szCntPyerNb = "";
		String szCntPyerAmt = "";
		String szAccCntPyerAmt = "";
		String szIssCntPyerAmt = "";
		String szCntIntc = "";
		String szCntLogoFee = "";
		String szErrTrxFee = "";

		List<?> bodylist = _body.elements();
		for (Iterator<?> itBody = bodylist.iterator(); itBody.hasNext();) {
			Element body = (Element) itBody.next();
			String tagName = body.getName();
			if ("TrxTp".equals(tagName)) {
				szTrxTp = body.getStringValue();
			} else if ("TrxSucsNb".equals(tagName)) {
				szTrxSucsNb = body.getStringValue();
				iTrxSucsNb = SysPub.tranStrToI(szTrxSucsNb);
			} else if ("TrxSucsAmt".equals(tagName)) {
				szTrxSucsAmt = body.getStringValue();
				dTrxSucsAmt = SysPub.tranStrToD(szTrxSucsAmt, "CCY");
			} else if ("TrxFee".equals(tagName)) {
				szTrxFee = body.getStringValue();
				dTrxFee = SysPub.tranStrToD(szTrxFee, "CCY");
			} else if ("IssCntPyAmt".equals(tagName)) {
				szIssCntPyAmt = body.getStringValue();
				dIssCntPyAmt = SysPub.tranStrToD(szIssCntPyAmt, "CCY");
			} else if ("CntPyerNb".equals(tagName)) {
				szCntPyerNb = body.getStringValue();
				iCntPyerNb = SysPub.tranStrToI(szCntPyerNb);
			} else if ("CntPyerAmt".equals(tagName)) {
				szCntPyerAmt = body.getStringValue();
				dCntPyerAmt = SysPub.tranStrToD(szCntPyerAmt, "CCY");
			} else if ("AccCntPyerAmt".equals(tagName)) {
				szAccCntPyerAmt = body.getStringValue();
				dAccCntPyerAmt = SysPub.tranStrToD(szAccCntPyerAmt, "CCY");
			} else if ("IssCntPyerAmt".equals(tagName)) {
				szIssCntPyerAmt = body.getStringValue();
				dIssCntPyerAmt = SysPub.tranStrToD(szIssCntPyerAmt, "CCY");
			} else if ("CntIntc".equals(tagName)) {
				szCntIntc = body.getStringValue();
				dCntIntc = SysPub.tranStrToD(szCntIntc, "DC");
			} else if ("CntLogoFee".equals(tagName)) {
				szCntLogoFee = body.getStringValue();
				dCntLogoFee = SysPub.tranStrToD(szCntLogoFee, "DC");
			} else if ("ErrTrxFee".equals(tagName)) {
				szErrTrxFee = body.getStringValue();
				dErrTrxFee = SysPub.tranStrToD(szErrTrxFee, "DC");
			}
		}
		String szSqlStr = "insert into t_ncp_sett_det " + //
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,0.00,0.00,'','','') ";
		Object[] value = { szSettDate, szTrxTp, iTrxSucsNb, dTrxSucsAmt, dTrxFee, dIssCntPyAmt, //
				iCntPyerNb, dCntPyerAmt, dAccCntPyerAmt, dIssCntPyerAmt, //
				dCntIntc, dCntLogoFee, dErrTrxFee };

		int iRet = DataBaseUtils.execute(szSqlStr, value);
		if (0 == iRet) {
			SysPub.appLog("ERROR", "插入清算汇总信息表信息失败");
			return -1;
		}
		SysPub.appLog("INFO", "插入清算明细信息表信息成功[%d]", _iNum);
		return 0;
	}

	public static void main(String[] s) {
		try {
			sumXmlParseDo(
					"E:/工程项目/湖北RCU_SVN/湖北农信SVN/2016年项目工作区（服-EAI-201609维护）/13、其他/无卡支付/无卡快捷支付报文、清算文件样例/报文样例+清算文件/发卡样例文件/",
					"20170526");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
