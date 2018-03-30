package com.union.api;

import com.union.connect.UnionComm;
import com.union.connect.UnionStart;
import com.union.message.UnionMessage;
import com.union.message.UnionRequest;
import com.union.utils.UnionVerify;

public class UnionTKMS {
	/**
	 * 开始初始化
	 * 本地初始化：private static UnionStart api = new UnionStart();
	 * 读取环境变量初始化：private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	 */
	private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	
	/*
	 * T003 更新终端密钥
	 * @param termType 终端类型
	 * @param termID 终端号
	 * @param keyType 密钥类型
	 * @return ZMKName 主密钥名称
	 * @return keyName 密钥名称
	 * @return keyValue 密钥密文
	 * @return keyValue2 密钥密文2
	 * @return checkValue 校验值
	 */
	public UnionMessage servT003(String termType, String termID ,String keyType) {
	    if(UnionVerify.paramIsEmpty(termType)) {
	    	return UnionVerify.paramValueWrong("termType");
		}
		if(UnionVerify.paramIsEmpty(termID)) {
			return UnionVerify.paramValueWrong("termID");
		}
		if(UnionVerify.paramIsEmpty(keyType)) {
			return UnionVerify.paramValueWrong("keyType");
		}
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "T003");
		um.putBodyField("termType", termType);
		um.putBodyField("termID", termID);
		um.putBodyField("keyType", keyType);
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
		}
}

