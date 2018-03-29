package com.adtec.ncps.busi.ncp.qry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import com.adtec.starring.exception.BaseException;
import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.ncp.BusiMsgProc;
import com.adtec.ncps.busi.ncp.BusiPub;
import com.adtec.ncps.busi.ncp.DataBaseUtils;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.ncps.busi.ncp.SysPubDef;
import com.adtec.ncps.busi.ncp.autodo.AutoPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;
import com.adtec.starring.log.BaseLog;

public class SQRYPub {
	/*
	 * @author xiangjun
	 * 
	 * @createAt 2017年6月10日
	 * 
	 * @version 1.0 生成短信验证码和关联码，生成待发送短信信息
	 */
	public static int crtSmsInfo() throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		try {
			
			//DtaInfo dtaInfo = DtaInfo.getInstance();
			//String szTpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int  iRet = SysPub.ChkStep(szTpID);
			if (-1 == iRet) {
				return -1;
			}

			String svcName = dtaInfo.getSvcName();
			String szSderIssrId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].SderInf[0].SderIssrId");// 发起方机构标识
			// String szRcverAcctIssrId = (String) EPOper.get(szTpID,
			// "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctIssrId");
			String szRcverAcctId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");// 接收方机构
			String szMobNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].MobNo");// 手机号
			String szTrxId = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxId"); // 交易流水号			
			String szTxAmt = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxAmt");  //交易金额

			double dTxAmt=SysPub.tranStrToD(szTxAmt,"CCY");

			String szPlatDate = (String) EPOper.get(szTpID, "T_PLAT_PARA[0].PLAT_DATE");
			int iPlatSeq = (Integer) EPOper.get(szTpID, "INIT[0].SeqNo");

			// 6位随机数
			int iVrfyNo = PubTool.getId6();

			// 关联码
			String szLinkCode = szPlatDate + iPlatSeq;
			EPOper.put(szTpID, "fmt_CUP_SVR_OUT[0].Rsp_Body[0].RcverInf[0].Smskey", szLinkCode);
			
			//String szTxTime = (String) EPOper.get(szTpID, "INIT[0].TRAN_DATETM");
			String szTxTime = SysPub.getDataBaseTime();
			szTxTime = szTxTime.substring(0,10) + "T" + szTxTime.substring(11);
			//2分钟失效
			String  szInvlDate=PubTool.calDateAdd(szTxTime, "yyyy-MM-dd'T'HH:mm:ss", SysPubDef.iInvlSec);
			SysPub.appLog("INFO", "szTxTime=[%s]szInvlDate=[%s][%d]",szTxTime,szInvlDate,SysPubDef.iInvlSec);
			// 插入短信验证管理登记簿
			String szSql1 = "insert into t_sms_confirm values (?,?,?,?,?,?,?,?,?,0,'0','','','')";
			Object[] value1 = { szPlatDate, iPlatSeq, szSderIssrId, szTrxId, iVrfyNo, szLinkCode, szMobNo, szTxTime,szInvlDate };
			iRet = DataBaseUtils.execute(szSql1, value1);
			SysPub.appLog("INFO", "插入短信验证管理登记簿成功");
			//add by liangjr 20170915 begin
			sendSmsPub(iVrfyNo);
			//add by liangjr 20170915 end
			/* dingjunbo 20170831 不插入短信，直接发送到短信平台
			// 插入短信发送表
			String szChnlNo = (String) EPOper.get(szTpID, "T_CHANNEL[0].CHN_NO");	
			String szBrchNo = (String) EPOper.get(szTpID, "INIT[0].BrchNo");
			String szTxDateTm = (String) EPOper.get(szTpID, "INIT[0].TRAN_DATETM");
			String szSmsMsg = "";
			
			// 银行卡4位尾数
			String szAcctNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].RcverAcctId");  
			int iLen = szAcctNo.length();
			String szAcctNoEnd = szAcctNo.substring(iLen - 4, iLen);
			szSmsMsg=smsInfoForm(svcName,szAcctNoEnd,iVrfyNo,dTxAmt);
			SysPub.appLog("DEBUG", szSmsMsg);

			String szSql2 = "insert into t_sms_info values (?,?,?,?,?,'0',?,?,0,'','')";
			Object[] value2 = { szChnlNo, szRcverAcctId, szBrchNo,  szMobNo, szSmsMsg, szTxDateTm, szTxDateTm  };
			DataBaseUtils.execute(szSql2, value2);
			SysPub.appLog("INFO", "插入短信发送表成功");
			*/

			//Thread.sleep(40000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	/*
	 * @author dingjunbo
	 * @createAt 2017年8月31日
	 * @version 1.0 发送短信
	 */
	public static int callSms() throws Exception {
		try {
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			// 若上一步骤返回失败，本步骤也返回失败
			int iRet = SysPub.ChkStep(tpID);
			if (-1 == iRet) {
				return -1;
			}
			BusiMsgProc.msgBody610001(tpID);
			iRet = BusiPub.callSmsSvc("S610001","fmt_CUP_SVR");
			if (0 == iRet) {
				SysPub.appLog("INFO", "调度短信服务成功");
				BusiPub.setCupMsg(SysPubDef.CUP_SUC_RET, SysPubDef.CUP_SUC_MSG, "1");
			}
			return iRet;

		} catch (Exception e) {
			SysPub.appLog("ERROR", "callHost 方法处理异常");
			throw e;
		}
	}

	public static String smsInfoForm(String _szTxCode, String _szAcctNoEnd, int _iVrfyNo, double _dAmt) throws Exception {
		try {
			String szSmsMsg="";
			String szDate = PubTool.getDate();
			String szMonth = szDate.substring(4,6);
			String szDay = szDate.substring(6,8);
			String szHours = szDate.substring(8,10);
			String szMin = szDate.substring(10,12);
			String szSecond = szDate.substring(12,14);
			if ("SQRY00020001".equals(_szTxCode)) {
				szSmsMsg="验证码"+_iVrfyNo+"（泄漏有风险），您于"+szMonth+"月"+szDay+"日"+szHours+"时"+szMin+"分"+szSecond+"秒申请开通尾号"+_szAcctNoEnd+"银行卡的银联无卡快捷支付。";
			}
			else if ("SQRY00020003".equals(_szTxCode)) {
				szSmsMsg="验证码"+_iVrfyNo+"（泄漏有风险），您于"+szMonth+"月"+szDay+"日"+szHours+"时"+szMin+"分"+szSecond+"秒对尾号"+_szAcctNoEnd+"银行卡进行银联无卡快捷支付，金额"+_dAmt+"元。";
			}
			return szSmsMsg;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SysPub.appLog("ERROR", "生成短信信息错误");
			throw e;
		}
	}

	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String szAcctNo="6288888888880001";
		int iLen = szAcctNo.length();
		String szAcctNoEnd = szAcctNo.substring(iLen - 4, iLen);
		SysPub.testLog("DEBUG", szAcctNoEnd);
	}
	
	
	/*
	 * @author Liangjr
	 * 
	 * @createAt 2017年9月15日
	 * 
	 * @version 1.0 发送短信平台请求报文头赋值
	 */
	public static void newHeadSms(String svcName,String szChannelId) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();

		//int TermSeq = PubTool.sys_get_seq2();
		String TermTime = PubTool.getTime();
//		String fmt = "fmt_CUP_SVR_IN[0].SMS_CLI_" + svcName + "_IN[0].Head[0].";
//		
//		EPOper.put(tpID, fmt + "userid", "4");// 用户ID
//		EPOper.copy(tpID, tpID, "INIT[0].BrchNo", fmt + "brc");// 交易机构
//		EPOper.copy(tpID, tpID, "INIT[0].TlrNo", fmt + "teller");// 交易柜员
//		EPOper.put(tpID, fmt + "channelid", szChannelId);// 渠道ID
//		EPOper.copy(tpID, tpID, "INIT[0].SeqNo", fmt + "channelseq");// 渠道流水
//		EPOper.copy(tpID, tpID, "T_PLAT_PARA[0].PLAT_DATE", fmt + "channeldate");//渠道日期
//		EPOper.put(tpID, fmt + "channeltime", TermTime);// 渠道时间
	}
	
	
	/*
	 * @author Liangjr
	 * 
	 * @createAt 2017年9月18日
	 * 
	 * @version 1.0 发送短信平台赋值
	 */
	public static void sendSmsPub(int iVrfyNo) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String szTpID = dtaInfo.getTpId();
		try
		{
			SysPub.appLog("INFO", "发送S610001到短信平台");
			String szChannelId = (String) EPOper.get(szTpID,
				"fmt_CUP_SVR_IN[0].MsgHeader[0].BkData[0].ChnlNo");
			SQRYPub.newHeadSms("S610001", szChannelId);
			String svcName = dtaInfo.getSvcName();	
			// 6位随机数
			String szMobNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].RcverInf[0].MobNo");// 手机号		
			String szTxAmt = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN.Req_Body[0].TrxInf[0].TrxAmt");  //交易金额
			double dTxAmt=SysPub.tranStrToD(szTxAmt,"CCY");
			// 银行卡4位尾数
			String szAcctNo = (String) EPOper.get(szTpID, "fmt_CUP_SVR_IN[0].Req_Body[0].RcverInf[0].RcverAcctId");  
			int iLen = szAcctNo.length();
			String szAcctNoEnd = szAcctNo.substring(iLen - 4, iLen);
			String szSmsMsg = "";
			szSmsMsg=smsInfoForm(svcName,szAcctNoEnd,iVrfyNo,dTxAmt);
			if (StringTool.isNullOrEmpty(szSmsMsg)) {
				BusiPub.setErrMsg(szTpID, "AUTO002", "待发送短信内空为空");
				SysPub.appLog("ERROR", "待发送短信内空为空");
				AutoPub.uptWaitSnd("2");
			}
			String szFmtOut = "fmt_CUP_SVR_IN[0].SMS_CLT_OUT[0].";
			EPOper.put(szTpID,  szFmtOut + "phone_num", szMobNo);// 手机号
			EPOper.put(szTpID, szFmtOut + "content", szSmsMsg);// 短信内容
			//EPOper.put(szTpID, szFmtOut + "sendlevel", "6");//优先级别
			SysPub.appLog("INFO", "调用S610001服务开始");
			DtaTool.call("SMS_CLI", "S610001");
	    }
		catch (Exception e) {
			// TODO Auto-generated catch block
			//返回给柜面
			String msg = (String) EPOper.get(szTpID, "__GDTA_FORMAT[0].__ERR_MSG[0]");
			String code = (String) EPOper.get(szTpID, "__GDTA_FORMAT[0].__ERR_RET[0]");
			String stat = "";
			if(code=="0000")
			{
				stat = "1";
			}
			else
			{
				stat = "2";
			}
			
			BusiPub.setCupMsg(code, msg, stat);
			SysPub.appLog("INFO", "code=%s, msg=%s, stat=%s", code, msg, stat);
			e.printStackTrace();
			throw e;
		}
	}
	
    /**
     * 获取配置文件etc/config.zh_cn.utf8.properties里的参数值
     * @param name
     * @return
     */
	public static Map<String, String> filePathMap = new HashMap<String, String>();
	public static String getConf(String name) {
		String value = null;
		if (filePathMap.get(name) != null) {
			value = filePathMap.get(name);
		} else {
			String code = "utf-8";
			String path = String.format(SysDef.WORK_DIR
					+ "/etc/config.zh_cn.utf8.properties");
			try {

				Properties prop = new Properties();
				Reader reader = null;
				reader = new InputStreamReader(new FileInputStream(path), code);
				prop.load(reader);
				Set keyValue = prop.keySet();

				for (Iterator it = keyValue.iterator(); it.hasNext();) {
					String key = (String) it.next();
					String aa = (String) prop.get(key);
					filePathMap.put(key, aa);
				}

			} catch (IOException e) {
				throw new BaseException("P10310", e.getMessage());
			}
			value = filePathMap.get(name);
		}
		return value;
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
    
    /**
     * 获取配置文件etc/config.zh_cn.utf8.properties里的参数值
     * @author liangjr
     * @param szElem
     * @createAt 2017年10月27日
     * @return
     */
	public static String outOfNull(String szElem) {
		if (szElem==null||"Null".equals(szElem)){
			szElem = "";
		} 
		return szElem;
	}

}
