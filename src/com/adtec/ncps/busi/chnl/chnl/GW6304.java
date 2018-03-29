package com.adtec.ncps.busi.chnl.chnl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.adtec.ncps.DtaTool;
import com.adtec.ncps.busi.chnl.bean.Jrnl;
import com.adtec.ncps.busi.chnl.bean.NetbankConf;
import com.adtec.ncps.busi.chnl.bean.RetMsg;
import com.adtec.ncps.busi.chnl.dao.JrnlDao;
import com.adtec.ncps.busi.chnl.utils.EhcacheUtil;
import com.adtec.ncps.busi.ncp.PubTool;
import com.adtec.ncps.busi.ncp.SysPub;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.global.SysDef;
import com.adtec.starring.respool.ResPool;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.util.StringTool;

/**
 * 
 * @author Administrator
 *
 */
public class GW6304{

	private static ArrayList<NetbankConf> trade;// 交易属性
	private static RetMsg retmsg;// 交易属性

	private static Jrnl jrnl;// 记流水对象

	private static HashMap<String, String> sendPub;// 发送公共数据

	// private static boolean isEerror;//为true表示，已经发生报错，不进行后面的热帖Code校验，只需组空报文

	public static int deal_trans() throws Exception {
		try {
			long startTime=System.currentTimeMillis();   //获取开始时间
			SysPub.appLog("INFO", "开始执行deal_trans");
			DtaInfo dtaInfo = DtaInfo.getInstance();
			String tpID = dtaInfo.getTpId();
			String returnMsgObj = "";

			String svcName = (String) EPOper.get(tpID, "OBJ_ALA_abstarct_REQ[0].svcName");
			svcName = svcName.toUpperCase();

			EhcacheUtil ecache = EhcacheUtil.getInstance();
			trade = (ArrayList<NetbankConf>) ecache.get("MapCache", svcName);

			SysPub.appLog("INFO", "trade size" + trade.size());

			String svrReq = "OBJ_EBANK_SVR_" + svcName + "_REQ";
			String svrRes = "OBJ_EBANK_SVR_" + svcName + "_RES";

			String cltReq = null;
			String cltRes = null;
			// 给cltReq cltRes 赋初始值
			for (NetbankConf netbankConf : trade) {
				String type = netbankConf.getTYPE();
				String type_FROM = netbankConf.getTYPE_FROM();
				type_FROM = type_FROM == null ? "" : type_FROM;
				String type_TO = netbankConf.getTYPE_TO();
				if ("REQ".equals(type) && cltReq == null && type_TO.startsWith("$")) {
					cltReq = dealObj((type_TO.split("\\["))[0]);
				}
				if ("RES".equals(type) && cltRes == null && type_FROM.startsWith("$")) {
					cltRes = dealObj((type_FROM.split("\\["))[0]);
				}
			}

			SysPub.appLog("INFO", "svrReq：" + svrReq);
			;
			SysPub.appLog("INFO", "判断：" + EPOper.isExistSDO(tpID, svrReq));

			EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_REQ[0].req", svrReq);

			/* 登记流水 */
			String platDate = PubTool.getDate8();
			int platSeq = PubTool.sys_get_seq10();
			String strSeq = String.valueOf(platSeq);
			String txDate = PubTool.getDate8();// 平台日期
			String txTime = PubTool.getTime();// 平台时间
			String brchNo = "50001";// 网银机构号
			String tellerNo = "900015";// 柜员号
			String txCode = svcName;// 交易码
			String chNo = "";
			String reqNo = "";
			String termNO = "EBNK";// 终端号
			sendPub = new HashMap<String, String>();
			sendPub.put("brchNo", brchNo);
			sendPub.put("tellerNo", tellerNo);
			sendPub.put("txCode", txCode);
			sendPub.put("termNO", termNO);
			sendPub.put("txDate", txDate);
			String estwSeq = (String) EPOper.get(tpID, "__PLAT_FLOW.__FLOW_SEQ");// 平台流水号
			//赋值平台流水号，文件命名使用
			EPOper.put(tpID, "PUB_ELEMENT[0].PlatSeqNo", strSeq);
			EPOper.put(tpID, "ISO_8583[0].pub[0].PlatSeqNo", strSeq);//流水号 传递到host_cli文件传输时采用文件名(操作员号+渠道号+流水号)
			jrnl = new Jrnl();
			jrnl.setPLAT_DATE(platDate);
			jrnl.setSEQ_NO(Integer.valueOf(strSeq));
			jrnl.setBRCH_NO(brchNo);
			jrnl.setTX_DATE(txDate);
			jrnl.setTX_TIME(txTime);
			jrnl.setTELLER_NO(tellerNo);
			jrnl.setTX_CODE(txCode);
			jrnl.setCHN_NO(chNo);
			jrnl.setTERM_NO(termNO);
			jrnl.setESWT_SEQ(estwSeq);
			JrnlDao.insert(jrnl);// 记流水

			SysPub.appLog("INFO", "预计流水完成！");

			/* 交易报文检查 */
			if (chk_trans(tpID, svrReq, svrRes) < 0) {

				// /* 报文头 */
				EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
				// /* 报文体 */
				EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");

				// /* 更新返回码 */
				String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
				String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
				jrnl.setRET_CODE(retCode);
				jrnl.setRET_MSG(retMsg);
				long endTime=System.currentTimeMillis(); //获取结束时间

		        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
		        String szTmp = String.valueOf(endTime-startTime);
		        jrnl.setRMARK4(szTmp);
				JrnlDao.update(jrnl);

				return -1;

			}

			/* 验密操作 */
			if (tranOthDeal(tpID,svrReq) < 0) {
				EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
				String retCode1 = (String) EPOper.get(tpID,   "ISO_8583[0].iso_8583_012");//响应代码
				EPOper.put(tpID, svrRes + "[0].hostReturnCode", retCode1);
				EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "交易失败");
				return -1;
			}
			
			/* 调用核心对应得交易 */
			if (callHost(tpID, cltReq, cltRes, svrReq, svrRes,startTime) < 0) {
				return -1;
			}

			/* 报文头 */
			EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
			EPOper.put(tpID, svrRes + "[0].hostReturnCode", "0000");
			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "交易成功");
			
			
			/* 报文体 */
			// 组交易返回报文,并记流水
			for (NetbankConf netbankConf : trade) {
				String type = netbankConf.getTYPE();
				String type_PURPOSE = netbankConf.getTYPE_PURPOSE();
				if ("RES".equals(type) && "1".equals(type_PURPOSE)) {// 找到 ,
					

					
					// 用途为“发报文”的配置内容
					String file = netbankConf.getTYPE_FILE();
					String from = netbankConf.getTYPE_FROM();
					if (!StringTool.isNullOrEmpty(from))
						from = from.trim();
					String to = netbankConf.getTYPE_TO();
					if (!StringTool.isNullOrEmpty(to))
						to = to.trim();
					String type_EXPR = netbankConf.getTYPE_EXPR();
					if (!StringTool.isNullOrEmpty(type_EXPR))
						type_EXPR = type_EXPR.trim();
					
					String type_CHECK = netbankConf.getTYPE_CHECK();

					if (file != null && !"".equals(file.trim())) {// 为FILE时
						if (oganizeFomat(netbankConf, tpID, svrRes, svrReq,startTime) < 0) {
							EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
							EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "文件组织失败");
							updateErrorJrnl(netbankConf, tpID, svrRes, svrReq, startTime, jrnl);
							JrnlDao.update(jrnl);
							return -1;
						}
					} else if (from.startsWith("$")) {// 为from且以报文形式获得数据
						String goal = String.valueOf(EPOper.get(tpID, dealObj(from)));
						SysPub.appLog("INFO", "to:[%s] value:[%s]", dealObj(to), DealExpr(goal, type_EXPR));
						EPOper.put(tpID, dealObj(to), DealExpr(goal, type_EXPR));
					} else if(type_CHECK != null && type_CHECK.startsWith("#")) {// 当交易成功时，返回信息为空时
						String goal = String.valueOf(EPOper.get(tpID, dealObj(from)));
						if(goal != null && !"".equals(goal)){
							EPOper.put(tpID, dealObj(to), DealExpr(goal, type_EXPR));
						}
					}else{
						EPOper.put(tpID, dealObj(to), DealExpr(from, type_EXPR));
					}
					
				} else if ("RES".equals(type) && "2".equals(type_PURPOSE)) {// 找到res
																			// ,
																			// 用途为“记流水”的配置内容
					recordSeqByConf(netbankConf, tpID);
				} else if("RES".equals(type) && "3".equals(type_PURPOSE)){
					if (oganizeFomatFile(netbankConf, tpID, svrRes, svrReq) < 0) {
						EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
						EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "文件组织失败");
						updateErrorJrnl(netbankConf, tpID, svrRes, svrReq, startTime, jrnl);
						JrnlDao.update(jrnl);
						return -1;
					}
				}
			}
			
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			/* 更新返回码 */
			jrnl.setRET_CODE("0000");
			jrnl.setRET_MSG("交易成功");
			long endTime=System.currentTimeMillis(); //获取结束时间

	        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
	        String szTmp = String.valueOf(endTime-startTime);
	        jrnl.setRMARK4(szTmp);
			JrnlDao.update(jrnl);

			SysPub.appLog("INFO", "更新业务状态完成");
			return 0;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "执行 deal_jrnl 方法失败");
			throw e;
		}

	}

	public static void updateErrorJrnl(NetbankConf netbankConf, String tpID, String svrRes, String svrReq, long startTime, Jrnl jrnl)
	{

		/* 报文头 */
		EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
		// /* 报文体 */
		EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");

		// /* 更新返回码 */
		String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
		String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
		jrnl.setRET_CODE(retCode);
		jrnl.setRET_MSG(retMsg);
		long endTime=System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
        String szTmp = String.valueOf(endTime-startTime);
        jrnl.setRMARK4(szTmp);

	}
	/**
	 * 根据type_EXPR调用对应得方法处理goal 1、只支持静态方法 2、不支持方法连调，只能 类名.方法 3、方法后不需要（）
	 * 4、类名需要全路径
	 * 
	 * @param goal
	 * @param type_EXPR
	 *            PubTool.getDate8(String)
	 * 
	 *            类全名.方法名(参数类型) 若参数类型为空，则无参数
	 * @return
	 */
	public static Object DealExpr(Object goal, String type_EXPR) {
		if (type_EXPR != null && !"".equals(type_EXPR)) {
			String className = type_EXPR.substring(0, type_EXPR.lastIndexOf("."));
			String methodName = type_EXPR.substring(type_EXPR.lastIndexOf(".") + 1, type_EXPR.lastIndexOf("("));
			String type = type_EXPR.substring(type_EXPR.lastIndexOf("(") + 1, type_EXPR.lastIndexOf(")"));
			;// 参数类型
			Class cls;
			try {
				cls = Class.forName(className);
				if ("".equals(type)) {
					Method staticMethod = cls.getDeclaredMethod(methodName);
					goal = String.valueOf(staticMethod.invoke(cls));
				} else if ("String".equals(type)) {
					Method staticMethod = cls.getDeclaredMethod(methodName, String.class);
					goal = staticMethod.invoke(cls, String.valueOf(goal));
				} else if ("Integer".equals(type)) {
					Method staticMethod = cls.getDeclaredMethod(methodName, int.class);
					goal = staticMethod.invoke(cls, Integer.valueOf(String.valueOf(goal)));
				} else if ("Long".equals(type)) {
					Method staticMethod = cls.getDeclaredMethod(methodName, Long.class);
					goal = staticMethod.invoke(cls, Long.valueOf(String.valueOf(goal)));
				} else if ("Double".equals(type)) {
					Method staticMethod = cls.getDeclaredMethod(methodName, double.class);
					goal = staticMethod.invoke(cls, Double.valueOf(String.valueOf(goal)));
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return goal;
	}

	private static int oganizeFomatFile(NetbankConf netbankConf, String tpID, String svrRes, String svrReq) throws Exception{
		

        
        
		/* 读取配置文件中TYPE_CONTENTS（文件格式） */
		String type_CONTENTS = netbankConf.getTYPE_CONTENTS();
		String[] typeContentsArr = type_CONTENTS.split("\\|");
		String type_FROM = netbankConf.getTYPE_FROM();
		String type_FILE=netbankConf.getTYPE_FILE().substring(12);
		type_FILE = type_FILE.replaceAll("\\.", "[0].");
				//".transaction.body.response.list.acctInfo";
		//.transaction.body.response.currentPageRows
		//type_FROM = type_FROM.substring(1);
		String code = netbankConf.getCODE();
		// 获取到如下样式的type_ROOT：[[0].cd[0].rows]
		String type_ROOT = netbankConf.getTYPE_ROOT().replaceAll(":", "[0].");
		String typeRootRows = type_ROOT.substring(0, type_ROOT.lastIndexOf(".") + 1);
		
		String type_CONTENTSCONT = netbankConf.getTYPE_CONTENTSCONT();
		type_CONTENTSCONT=type_CONTENTSCONT.substring(12).replaceAll("\\.", "[0].");
		
		 SysPub.appLog("INFO", "rowsNum:"+"OBJ_EBILL_CLT_"+code+"_ALL_RES"+type_CONTENTSCONT);
		 Integer rowsNumInt =(Integer) EPOper.get(tpID, "OBJ_EBILL_CLT_"+code+"_ALL_RES"+type_CONTENTSCONT);
		// rowsNum)))- type_FILEBEGIN;
		String rows = netbankConf.getTYPE_TO();
		//SysPub.appLog("INFO", rowsNumInt);
		EPOper.put(tpID, rows.substring(1), rowsNumInt);// 组循环次数
		for (int j = 0; j < rowsNumInt; j++) {
		
				for (int i1 = 0; i1 < typeContentsArr.length; i1++) {
					
					SysPub.appLog("INFO", "第[%s]行，内容为：[]", j, svrRes + type_ROOT + "[" + j + "]." + typeContentsArr[i1]);
							//fileList.get(j)[i1]);
					String szDst = svrRes + type_ROOT + "[0]." +typeContentsArr[i1];
					String szSrc = "OBJ_EBILL_CLT_"+code+"_ALL_RES"+type_FILE + "[0]." +typeContentsArr[i1];
					SysPub.appLog("INFO", "value:[%s][%s]", szDst, szSrc);
					EPOper.copy(tpID, tpID, szSrc, szDst,j,j);//拷贝数据对象
					//SysPub.appLog("INFO", "value:[%s]", fileList.get(j)[i1]);
					//EPOper.put(tpID, svrRes + type_ROOT + "[" + j + "]." + typeContentsArr[i1], fileList.get(j)[i1]);
				}
			
		}
		
		return 0;
		
	}
	/**
	 * 组文件报文
	 * 
	 * @param netbankConf
	 * @param tpID
	 * @param svrRes
	 * @param svrReq
	 * @return
	 * @throws Exception
	 */
	private static int oganizeFomat(NetbankConf netbankConf, String tpID, String svrRes, String svrReq, long startTime)
			throws Exception {
		String filePath = SysDef.WORK_DIR + ResPool.configMap.get("FilePath") + "/netbank/file/";
		String type_FILE = netbankConf.getTYPE_FILE();
		String[] type_FILEs = type_FILE.split("\\|");
		String fileName = filePath;
		// 拼接文件名
		for (int i = 0; i < type_FILEs.length; i++) {
			fileName = fileName + EPOper.get(tpID, type_FILEs[i]);
		}

		File file = new File(fileName);
		// fileFlag 1 :默认文件存在 2:文件不存在
		// String fileFlag = "1";
		if (!file.exists()) {
			String fileFlag = (String) EPOper.get(tpID, "PUB_ELEMENT[0].FILE_FLAG");
			if("0".equals(fileFlag)){
				return 0;
			}
			SysPub.appLog("INFO", "[%s]不存在", fileName);
			EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "文件不存在");

			/* 报文头 */
			EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
			// /* 报文体 */
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");

			// /* 更新返回码 */
			String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
			String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
			jrnl.setRET_CODE(retCode);
			jrnl.setRET_MSG(retMsg);
			long endTime=System.currentTimeMillis(); //获取结束时间

	        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
	        String szTmp = String.valueOf(endTime-startTime);
	        jrnl.setRMARK4(szTmp);
			JrnlDao.update(jrnl);
			SysPub.appLog("INFO", "PubDel end [%s]", retMsg);
			return -1;
		}

		/* 读取配置文件中TYPE_CONTENTS（文件格式） */
		String type_CONTENTS = netbankConf.getTYPE_CONTENTS();
		String[] typeContentsArr = type_CONTENTS.split("\\|");

		/* 循环读取文件内容 */
		ArrayList<String[]> fileList = new ArrayList<String[]>();// 保存文件解析的内容

		// if("1".equals(fileFlag)){

		FileInputStream fs = new FileInputStream(file);
		InputStreamReader read = new InputStreamReader(fs, "gbk");
		BufferedReader bufferedReader = new BufferedReader(read);

		String readLine = "";
		byte[] b = { (byte) 0xff }; // 文件分隔符
		String separator = new String(b, "utf-8");

		int i = 0;
		String rowsNum = netbankConf.getTYPE_CONTENTSCONT();// 报文循环次数
		Integer type_FILEBEGIN = Integer.valueOf(netbankConf.getTYPE_FILEBEGIN());// 文件起始记录数（第一条为0）
		while ((readLine = bufferedReader.readLine()) != null) {
			SysPub.appLog("INFO", "readLine:[%s]", readLine);
			SysPub.appLog("INFO", "type_FILEBEGIN:[%s]", type_FILEBEGIN);
			SysPub.appLog("INFO", "type_FILEBEGIN:[%s]", type_FILEBEGIN <= i);
			if (type_FILEBEGIN <= i) {// 设置数据起始笔数
				String[] lineElements = readLine.split(separator);// 每行的对应数据
				if (lineElements.length != typeContentsArr.length) {// 判断数据个数是否匹配
					String msg = String.format("文件第[%s]条数，数据个数[%s],与配置文件要求个数[%s]不匹配", i, lineElements.length,
							typeContentsArr.length);
					SysPub.appLog("INFO", msg);
					EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
					EPOper.put(tpID, svrRes + "[0].hostErrorMessage", msg);

					/* 报文头 */
					EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
					// /* 报文体 */
					EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");

					// /* 更新返回码 */
					String retCode = (String) EPOper.get(tpID, svrRes + "[0].hostReturnCode");
					String retMsg = (String) EPOper.get(tpID, svrRes + "[0].hostErrorMessage");
					jrnl.setRET_CODE(retCode);
					jrnl.setRET_MSG(retMsg);
					long endTime=System.currentTimeMillis(); //获取结束时间

			        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
			        String szTmp = String.valueOf(endTime-startTime);
			        jrnl.setRMARK4(szTmp);
					JrnlDao.update(jrnl);
					bufferedReader.close();
					return -1;
				}
				SysPub.appLog("INFO", "装进list的行:[%s]", readLine);
				fileList.add(lineElements);
			}
			i++;
		}
		bufferedReader.close();
		// }
		// else if("2".equals(fileFlag)){//文件不存在时，赋空值，保证cd中有报文
		// String[] lineElements = new String[typeContentsArr.length];
		// for(int i = 0;i< lineElements.length;i++){
		// lineElements[i] = "";
		// }
		// fileList.add(lineElements);
		// }

		/* 对应typeContentsArr的内容，进行赋值 */
		// 获取到如下样式的type_ROOT：[[0].cd[0].rows]
		String type_ROOT = netbankConf.getTYPE_ROOT().replaceAll(":", "[0].");
		String typeRootRows = type_ROOT.substring(0, type_ROOT.lastIndexOf(".") + 1);
		// SysPub.appLog("INFO", "rowsNum:"+EPOper.get(tpID, rowsNum));
		// int rowsNumInt = Integer.valueOf(String.valueOf(EPOper.get(tpID,
		// rowsNum)))- type_FILEBEGIN;
		String rows = netbankConf.getTYPE_TO();
		SysPub.appLog("INFO", rows);
		EPOper.put(tpID, rows.substring(1), fileList.size());// 组循环次数
		for (int j = 0; j < fileList.size(); j++) {
			for (int i1 = 0; i1 < typeContentsArr.length; i1++) {
				SysPub.appLog("INFO", "第[%s]行，内容为：[%s]", j, svrRes + type_ROOT + "[" + j + "]." + typeContentsArr[i1],
						fileList.get(j)[i1]);
				SysPub.appLog("INFO", "value:[%s]", fileList.get(j)[i1]);
				EPOper.put(tpID, svrRes + type_ROOT + "[" + j + "]." + typeContentsArr[i1], fileList.get(j)[i1]);
			}
		}

		EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
		SysPub.appLog("INFO", "报文组装完成");

		return 0;
	}

	/**
	 * 检查通过报文传送的值是否为空
	 * 
	 * @param tpID
	 * @param svrReq
	 * @param svrRes
	 * @return
	 * @throws Exception
	 */
	private static int chk_trans(String tpID, String svrReq, String svrRes) throws Exception {
		SysPub.appLog("INFO", "chk_trans	begin");
		for (NetbankConf netbankConf : trade) {
			String type_FROM = netbankConf.getTYPE_FROM();
			String type = netbankConf.getTYPE();
			SysPub.appLog("INFO", "netbankConf:[%s]", netbankConf.toString());
			// 选出从报文传过来的 req from非空
			if ("REQ".equals(type) && type_FROM != null && type_FROM.startsWith("$")) {
				String from = String.valueOf(EPOper.get(tpID, dealObj(type_FROM))).trim();
				SysPub.appLog("INFO", "from:[%s]", from);
				String explain = String.valueOf(netbankConf.getTYPE_EXPLAIN()).trim();
				String msg = explain + "不能为空";
				boolean isEerror = false;

				if ("0".equals(netbankConf.getTYPE_ISNULL())) {// 都检查
					isEerror = (from == null) || "".equals(from);
				} else if ("1".equals(netbankConf.getTYPE_ISNULL())) {// 检查null
					isEerror = (from == null);
				} else if ("2".equals(netbankConf.getTYPE_ISNULL())) {// 检查空
					isEerror = "".equals(from);
				} else if ("3".equals(netbankConf.getTYPE_ISNULL())) {// 不检查

				}

				if (isEerror) {
					EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
					EPOper.put(tpID, svrRes + "[0].hostErrorMessage", msg);
					SysPub.appLog("INFO", msg);
					return -1;
				}
			}
		}
		SysPub.appLog("INFO", "chk_trans	end");
		return 0;
	}

	/**
	 * 去核心对应交易
	 * 
	 * @param tpID
	 * @param ISO_8583
	 * @param svrReq
	 * @param svrRes
	 * @param pubData
	 *            公共参数,序列为： platDate, platSeq, brchNo, tellerNo, termNO, chNo,
	 *            estwSeq, txCode, txDate, txTime,reqNo
	 * @param reqFmt
	 *            请求报文（非公共部分），格式如下： key：./xmlForPubDel 的 FROM value：key对应得域中传送的值
	 * @return
	 * @throws Exception
	 */
	public static int callHost(String tpID, String cltReq, String cltRes, String svrReq, String svrRes, long startTime)
			throws Exception {
		SysPub.appLog("INFO", "STEP1 去核心");
		String hostCode = "";
		String check = "";
		String returnCodeObj = "";
		String returnMsgObj = "";

		// 组公共发送报文
		SysPub.appLog("INFO", "cltReq:" + cltReq + "[0].iso_8583_002");
		SysPub.appLog("INFO", "brchNo:" + sendPub.get("brchNo"));
		EPOper.put(tpID, "ISO_8583[0].iso_8583_002", sendPub.get("brchNo"));
		EPOper.put(tpID, "ISO_8583[0].iso_8583_003", sendPub.get("brchNo"));
		EPOper.put(tpID, "ISO_8583[0].iso_8583_005", sendPub.get("txDate"));
		EPOper.put(tpID, "ISO_8583[0].iso_8583_007", sendPub.get("tellerNo"));
		EPOper.put(tpID, "ISO_8583[0].iso_8583_010", sendPub.get("termNO"));

		// 组交易发送报文,并记流水
		for (NetbankConf netbankConf : trade) {
			// 获取成功的响应吗
			String type_CHECK = netbankConf.getTYPE_CHECK();
			if (type_CHECK != null && type_CHECK.startsWith("$")) {
				check = type_CHECK.replaceAll("\\$", "");
				returnCodeObj = dealObj(netbankConf.getTYPE_FROM());
			}
			if (type_CHECK != null && type_CHECK.startsWith("#")) {
				returnMsgObj = dealObj(netbankConf.getTYPE_FROM());
			}

			// 取seq为1 时的 desc，为call 的交易码
			if ("1".equals(netbankConf.getTYPE_SEQ())) {
				hostCode = netbankConf.getCODE_DESC().trim();
				SysPub.appLog("ERROR", "hostCode:[%s]", hostCode);
			}
			String type = netbankConf.getTYPE();
			String type_PURPOSE = netbankConf.getTYPE_PURPOSE();
			if ("REQ".equals(type) && "1".equals(type_PURPOSE)) {// 找到req ,
																	// 用途为“发报文”的配置内容
				String file = netbankConf.getTYPE_FILE();
				String from = netbankConf.getTYPE_FROM();
				if (from == null) {
					from = "";
				}
				String to = netbankConf.getTYPE_TO();
				String type_EXPR = netbankConf.getTYPE_EXPR();

				if (file != null && !"".equals(file.trim())) {// 为FILE时

				} else if (from.startsWith("$")) {// 为from且以报文形式获得数据
					Object dealExpr = DealExpr(String.valueOf(EPOper.get(tpID, dealObj(from))), type_EXPR);
					SysPub.appLog("INFO", "req to:[%s][%s]", to,dealObj(to));
					SysPub.appLog("INFO", "req from:[%s][%s]",from, dealExpr.toString());
					EPOper.put(tpID, dealObj(to), dealExpr);
				} else {// 为from且以固定值形式获得数据
					SysPub.appLog("INFO", "req to:[%s][%s]", to,dealObj(to));
					SysPub.appLog("INFO", "req from:[%s]",from);
					EPOper.put(tpID, dealObj(to), DealExpr(from, type_EXPR));

				}
			} else if ("REQ".equals(type) && "2".equals(type_PURPOSE)) {// 找到req
																		// ,
																		// 用途为“记流水”的配置内容
				recordSeqByConf(netbankConf, tpID);
			}
		}

		EPOper.copy(tpID, tpID, cltReq, "OBJ_ALA_abstarct_REQ[0].req");
		String nextto = trade.get(0).getNEXTTO();
		try {
			SysPub.appLog("ERROR", "hostCode:[%s]", hostCode);
			DtaTool.call(nextto, hostCode);
		} catch (Exception e) {
			EPOper.put(tpID, svrRes + "[0].hostReturnCode", "9999");
			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", "调用" + nextto + ":" + hostCode + "服务失败");
			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");
			jrnl.setRET_CODE("9999");
			jrnl.setRET_MSG("调用" + nextto + ":" + hostCode + "服务失败");
			long endTime=System.currentTimeMillis(); //获取结束时间

	        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
	        String szTmp = String.valueOf(endTime-startTime);
	        jrnl.setRMARK4(szTmp);
			JrnlDao.update(jrnl);
			SysPub.appLog("ERROR", "调用[%s]服务失败", nextto + ":" + hostCode);
			return -1;
		}
		SysPub.appLog("INFO", "cltRes:" + cltRes);
		// if(EPOper.isExistSDO(tpID, cltRes)){
		EPOper.delete(tpID, cltRes);
		// }
		EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res", cltRes);
		String retCode1 = (String) EPOper.get(tpID, returnCodeObj);// 响应代码
		String retMsg1 = null;
		if (returnMsgObj != null && !"".equals(returnMsgObj))
			retMsg1 = (String) EPOper.get(tpID, returnMsgObj);// 响应信息
		else {
			EhcacheUtil ecache = EhcacheUtil.getInstance();
			retMsg1 = (String) ecache.get("RetCache", nextto + "+" + hostCode + "+" + retCode1);
			if (retMsg1 == null && "".equals(retMsg1))
				retMsg1 = "交易失败";
		}
		SysPub.appLog("INFO", "returnC:" + returnCodeObj);
		SysPub.appLog("INFO", "retCode1:" + retCode1);
		SysPub.appLog("INFO", "returnM:" + returnMsgObj);
		if (!check.equals(retCode1)) {
			/* 报文头 */
			EPOper.copy(tpID, tpID, svrReq + "[0].tx_code", svrRes + "[0].tx_code");
			// /* 报文体 */
			EPOper.put(tpID, svrRes + "[0].hostReturnCode", retCode1);

			EPOper.put(tpID, svrRes + "[0].hostErrorMessage", retMsg1);

			EPOper.copy(tpID, tpID, svrRes, "OBJ_ALA_abstarct_RES[0].res");

			jrnl.setRET_CODE(retCode1);
			jrnl.setRET_MSG(retMsg1);
			long endTime=System.currentTimeMillis(); //获取结束时间

	        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");
	        String szTmp = String.valueOf(endTime-startTime);
	        jrnl.setRMARK4(szTmp);
			JrnlDao.update(jrnl);
			SysPub.appLog("INFO", "PubDel end RetCode=[%s] RetMsg[%s]", retCode1, retMsg1);
			return -1;
		}

		SysPub.appLog("INFO", "STEP1 去核心[%s]结束", hostCode);
		return 0;
	}

	/**
	 * 根据配置记流水
	 * 
	 * @param netbankConf
	 * @param tpID
	 * @throws ClassNotFoundException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void recordSeqByConf(NetbankConf netbankConf, String tpID) throws ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String type_TO = netbankConf.getTYPE_TO().toUpperCase();
		String type_DATATYPE = netbankConf.getTYPE_DATATYPE();
		String type_FROM = netbankConf.getTYPE_FROM();
		Object value;
		if (type_FROM.startsWith("$")) {
			value = EPOper.get(tpID, dealObj(type_FROM));
		} else {
			value = type_FROM;
		}
		Object dealExpr = DealExpr(value, netbankConf.getTYPE_EXPR());
		Class cls = Class.forName("com.adtec.ncps.busi.chnl.bean.Jrnl");
		Field field = cls.getDeclaredField(type_TO);// 对应属性
		field.setAccessible(true);// 设置允许访问
		field.set(jrnl, switchDataType(dealExpr, type_DATATYPE));
	}

	/**
	 * 基本类型转换
	 * 
	 * @param data
	 * @param type
	 * @return
	 */
	public static Object switchDataType(Object data, String type) {
		if ("String".equals(type)) {
			data = String.valueOf(data);
		} else if ("Integer".equals(type) || "int".equals(type)) {
			data = Integer.valueOf(String.valueOf(data));
		} else if ("Double".equals(type) || "double".equals(type)) {
			data = Double.valueOf(String.valueOf(data));
		} else if ("Long".equals(type)) {
			data = Long.valueOf(String.valueOf(data));
		}
		return data;
	}

	/**
	 * 将对象的$标识符去掉
	 * 
	 * @param str
	 * @return
	 */
	public static String dealObj(String str) {
		return str.replaceAll("\\$", "");
	}
	
	
	
	
	public static int tranOthDeal(String tpID,String svrReq) throws Exception{
		
			/* 理财持仓 */
	        //uLog(LOG_INFO,"在途=[%s]持仓=[%s]",lcOnRoad,lcOwned);
	//      sprintf(lcAmt,"%.2f",strtod(lcOnRoad,NULL) + strtod(lcOwned,NULL) );
	        //uLog(LOG_INFO,"理财总资产=[%s]",lcAmt);
	        
	//      在途止付不体现在理财资产里面——2016年7月25日16:52:50 @贾汶川
			
		
		/* 上核心查询 */
		SysPub.appLog("INFO", "STEP1 去核心[%s]检查付款账户", "740009");
		

		EPOper.put(tpID,   "ISO_8583[0].iso_8583_016","7400");//交易代码
		
		String accountNo =(String) EPOper.get(tpID, svrReq+".cd[0].accountNo");
		String password = (String) EPOper.get(tpID, svrReq+".cd[0].password");
		
		SysPub.appLog("INFO", "客户名="+accountNo);
		SysPub.appLog("INFO", "密码="+password);
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_079",password); //密码
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_030",accountNo); //客户号
		
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_010","EBNK"); //客户号
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_007","900015"); //柜员号
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_003","50001"); //交易机构代码
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_002","50001"); //开户机构代码
		
		EPOper.put(tpID,  "ISO_8583" + "[0].iso_8583_053","9"); //核心交易码
		EPOper.copy(tpID, tpID,  "ISO_8583", "OBJ_ALA_abstarct_REQ[0].req");
		try {
			DtaTool.call("HOST_CLI", "S740009");
		} catch (Exception e) {
			SysPub.appLog("ERROR", "调用核心服务失败");
			return -1;
		}
		EPOper.delete(tpID, "ISO_8583");
		EPOper.copy(tpID, tpID, "OBJ_ALA_abstarct_RES[0].res",  "ISO_8583");
		
		String retCode1 = (String) EPOper.get(tpID,   "ISO_8583[0].iso_8583_012");//响应代码
		if(!"0000".equals(retCode1)){
			/* 报文头 */
			SysPub.appLog("ERROR", "调用核心服务失败");
			return -1;
		}
		return 0;
	}
	
	
	
}
