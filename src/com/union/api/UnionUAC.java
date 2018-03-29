package com.union.api;

import com.union.connect.UnionComm;
import com.union.connect.UnionStart;
import com.union.message.UnionMessage;
import com.union.message.UnionRequest;
import com.union.utils.UnionVerify;

public class UnionUAC {

	/**
	 * 开始初始化
	 * 本地初始化：private static UnionStart api = new UnionStart();
	 * 读取环境变量初始化：private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	 */
	private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	
	/**
	 * E416 加密文件
	 * @param pkName 公钥名称
	 * @param vkName  私钥名称
	 * @param fileName 文件名
	 * @param version  加密数据版本号
	 * @param bankID    机构编号
	 * @param serverInfo  服务器信息
	 * @return
	 */
	public UnionMessage servE416(String pkName, String vkName ,String fileName,
		     String  version ,String bankID ,String serverInfo) {
		
		
		//参数检验
		if(UnionVerify.paramIsEmpty(pkName)){
			return UnionVerify.paramValueWrong("pkName");
		}
		
		if(UnionVerify.paramIsEmpty(vkName)){
			return UnionVerify.paramValueWrong("vkName");
		}

		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "E416");
		
		um.putBodyField("pkName", pkName);
		um.putBodyField("vkName", vkName);
		um.putBodyField("fileName", fileName);
		um.putBodyField("version", version);
		um.putBodyField("bankID", bankID);
		um.putBodyField("serverInfo", serverInfo);

		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/**
	 * E417 解密文件
	 * @param pkName 公钥名称
	 * @param vkName 私钥名称
	 * @param fileName  文件名
	 * @return
	 */
	public UnionMessage servE417(String pkName, String vkName ,String fileName) {
		
		//参数检验
		if(UnionVerify.paramIsEmpty(pkName)){
			return UnionVerify.paramValueWrong("pkName");
		}
		
		if(UnionVerify.paramIsEmpty(vkName)){
			return UnionVerify.paramValueWrong("vkName");
		}
		
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "E417");
		
		um.putBodyField("pkName", pkName);
		um.putBodyField("vkName", vkName);
		um.putBodyField("fileName", fileName);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	
	
}
