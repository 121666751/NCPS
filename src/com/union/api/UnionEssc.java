package com.union.api;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.union.connect.UnionComm;
import com.union.connect.UnionStart;
import com.union.message.UnionMessage;
import com.union.message.UnionRequest;
import com.union.utils.Base64;
import com.union.utils.UnionStr;
import com.union.utils.UnionVerify;

public class UnionEssc {

	/**
	 * 开始初始化
	 * 本地初始化：private static UnionStart api = new UnionStart();
	 * 读取环境变量初始化：private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	 * 绝对路径初始化：private static UnionStart api = new UnionStart("E:/mybranchWorkSpace/UnionAPI3.0.0/src/serverList.conf");
	 */
	private static UnionStart api = new UnionStart("/home/zhqz/src/serverList.conf");
	
	
	/**
	 * E119 启用、挂起对称密钥
	 * @param keyName 	密钥名称
	 * @param mode 			模式  1：启用密钥  2：挂起密钥
	 * @return
	 * 					返回 {@link UnionMessage} 对象，获取数据方法在该对象注释中已经说明。
	 */
	public UnionMessage servE119(String keyName,int mode ){
	    if(UnionVerify.paramIsEmpty(keyName)) {
	    	return UnionVerify.paramValueWrong("keyName");
		}
		if(mode<0||mode>2) {
			return UnionVerify.paramValueWrong("mode");
		}
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "E119");
		um.putBodyField("keyName", keyName);
		um.putBodyField("mode", mode);
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/**
	 * E150 生成MAC		//2017.06.09 linxl 根据文档添加了参数keyVersion
	 * @param keyVersion			密钥版本
	 * @param mode					模式
	 * @param keyName			密钥名称
	 * @param keyValue				密钥密文
	 * @param algorithmID		算法标识
	 * @param fillMode				自动填充方式	
	 * @param dataType			数据类型
	 * @param data					数据
	 * @return 
	 * 					返回 {@link UnionMessage} 对象，获取数据方法在该对象注释中已经说明。
	 * 					获取响应信息：
	 * 				 	{@code string = mac} 	：MAC
	 */
	public UnionMessage servE150(String keyVersion, String mode,String keyName, String keyValue, String algorithmID,
			String fillMode, String dataType, String data){
		
		if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
	    }
		
		//组装服务代码
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "E150");
		
		//组装报文体
		um.putBodyField("data", data);
		
		//判断可选
		if(!UnionVerify.paramIsEmpty(keyVersion)) {
			um.putBodyField("keyVersion", keyVersion);
		}
		if(!UnionVerify.paramIsEmpty(mode)) {
			um.putBodyField("mode", mode);
		}
		if(!UnionVerify.paramIsEmpty(keyName)) {
			um.putBodyField("keyName", keyName);
		}
		if(!UnionVerify.paramIsEmpty(keyValue)) {
			um.putBodyField("keyValue", keyValue);
		}
		if(!UnionVerify.paramIsEmpty(algorithmID)) {
			um.putBodyField("algorithmID", algorithmID);
		}
		if(!UnionVerify.paramIsEmpty(fillMode)) {
			um.putBodyField("fillMode", fillMode);
		}
		if(!UnionVerify.paramIsEmpty(dataType)) {
			um.putBodyField("dataType", dataType);
		}
		
		return UnionComm.sendAndRecvMsg(um, api.getMaxSendTimes());
	}
	
	
	/**
	 * EA30 数字签名
	 * @param keyName 	密钥名称
	 * @param userID			用户标识
	 * @param data			签名数据
	 * @return sign				签名结果
	 */
	public UnionMessage servEA30(String keyName, String userID, String data){
		//判断必填项
		if(UnionVerify.paramIsEmpty(keyName)) {
			return UnionVerify.paramValueWrong("keyName");
		}
		if(UnionVerify.paramIsEmpty(data)) {
			return UnionVerify.paramValueWrong("data");
		}
		
		UnionMessage um = new UnionRequest();
		//组装服务码
		um.putHeadField("serviceCode", "EA30");
		
		//组装报文体
		um.putBodyField("keyName", keyName);
		um.putBodyField("data", data);
		
		//判断可选项，存在则组装进报文体
		if(!UnionVerify.paramIsEmpty(userID)) {
			um.putBodyField("userID", userID);
		}
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/**
	 * EA31 数字验签
	 * @param keyName	密钥名称
	 * @param userID			用户标识
	 * @param data			签名数据
	 * @param sign			签名结果
	 * @return
	 */
	public UnionMessage servEA31(String keyName, String userID, String data, String sign){
		//判断必填项
		if(UnionVerify.paramIsEmpty(keyName)) {
			return UnionVerify.paramValueWrong("keyName");
		}
		if(UnionVerify.paramIsEmpty(data)) {
			return UnionVerify.paramValueWrong("data");
		}
		if(UnionVerify.paramIsEmpty(sign)) {
			return UnionVerify.paramValueWrong("sign");
		}
		
		UnionMessage um = new UnionRequest();
		//组装服务码
		um.putHeadField("serviceCode", "EA31");
		
		//组装报文体
		um.putBodyField("keyName", keyName);
		um.putBodyField("data", data);
		um.putBodyField("sign", sign);
		
		//判断可选项，存在则组装进报文体
		if(!UnionVerify.paramIsEmpty(userID)) {
			um.putBodyField("userID", userID);
		}
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/**
	 * EA32 敏感信息加密
	 * @param keyName	密钥名称
	 * @param sensInfo		敏感信息
	 * @return encKey		公钥加密的保护密钥
	 * @return encInfo 		保护密钥加密的敏感信息
	 */
	public UnionMessage servEA32(String keyName, String sensInfo){
		//判断必填项
		if(UnionVerify.paramIsEmpty(keyName)) {
			return UnionVerify.paramValueWrong("keyName");
		}
		if(UnionVerify.paramIsEmpty(sensInfo)) {
			return UnionVerify.paramValueWrong("sensInfo");
		}
		
		UnionMessage um = new UnionRequest();
		//组装服务码
		um.putHeadField("serviceCode", "EA32");
		
		//组装报文体
		um.putBodyField("keyName", keyName);
		um.putBodyField("sensInfo", sensInfo);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/**
	 * EA33 敏感信息解密
	 * @param keyName	密钥名称
	 * @param encKey		公钥加密的保护密钥
	 * @param encInfo		保护密钥加密的敏感信息
	 * @return sensInfo		敏感信息明文
	 */
	public UnionMessage servEA33(String keyName, String encKey, String encInfo){
		//判断必填项
		if(UnionVerify.paramIsEmpty(keyName)) {
			return UnionVerify.paramValueWrong("keyName");
		}
		if(UnionVerify.paramIsEmpty(encKey)) {
			return UnionVerify.paramValueWrong("encKey");
		}
		if(UnionVerify.paramIsEmpty(encInfo)) {
			return UnionVerify.paramValueWrong("encInfo");
		}
		
		UnionMessage um = new UnionRequest();
		//组装服务码
		um.putHeadField("serviceCode", "EA33");
		
		//组装报文体
		um.putBodyField("keyName", keyName);
		um.putBodyField("encKey", encKey);
		um.putBodyField("encInfo", encInfo);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA10	获取证书算法标识和公钥值
	 * serialNum		证书序列号
	 */
	public UnionMessage servEA10(String serialNum){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
			
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA10");
		um.putBodyField("serialNum", serialNum);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA11	数字签名加签
	 * serialNum		证书序列号
	 * data				待签名数据
	 * dataType			待签名数据的类型
	 */
	public UnionMessage servEA11(String serialNum, String data, int dataType){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
	    
	    if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
		}
	    
	    if (dataType != 0 && dataType != 1)
	    	return UnionVerify.paramValueWrong("dataType");
	
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA11");
		um.putBodyField("serialNum", serialNum);
		um.putBodyField("data", data);
		um.putBodyField("dataType", dataType);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA12	数字签名核签
	 * serialNum		证书序列号
	 * data				待验签数据
	 * dataType			待验签数据的类型
	 * sign				待验证的签名结果
	 */
	public UnionMessage servEA12(String serialNum, String data, int dataType, String sign){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
	    
	    if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
		}
	    
	    if(UnionVerify.paramIsEmpty(sign)) {
	    	return UnionVerify.paramValueWrong("sign");
		}
	    
	    if (dataType != 0 && dataType != 1)
	    	return UnionVerify.paramValueWrong("dataType");
	
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA12");
		um.putBodyField("serialNum", serialNum);
		um.putBodyField("data", data);
		um.putBodyField("dataType", dataType);
		um.putBodyField("sign", sign);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA13	公钥加密对称密钥
	 * mode				密钥模式
	 * keyValue			密钥值
	 * keyType			密钥算法类型
	 * serialNum		证书序列号
	 */
	public UnionMessage servEA13(int mode, String keyValue, String keyType, String serialNum){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
	    if(UnionVerify.paramIsEmpty(keyType)) {
	    	return UnionVerify.paramValueWrong("keyType");
		}
	    if (mode != 0 && mode != 1)
	    	return UnionVerify.paramValueWrong("mode");
	    if (mode == 1 && UnionVerify.paramIsEmpty(keyValue))
	    	return UnionVerify.paramValueWrong("keyValue");
	
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA13");
		um.putBodyField("serialNum", serialNum);
		um.putBodyField("mode", mode);
		if (mode == 1)
			um.putBodyField("keyValue", keyValue);
		um.putBodyField("keyType", keyType);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA14	公钥加密的对称密钥转lmk加密
	 * serialNum		证书序列号
	 * keyByPK			公钥加密的密文
	 */
	public UnionMessage servEA14(String serialNum, String keyByPK){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
	    if(UnionVerify.paramIsEmpty(keyByPK)) {
	    	return UnionVerify.paramValueWrong("keyByPK");
		}
	
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA14");
		um.putBodyField("serialNum", serialNum);
		um.putBodyField("keyByPK", keyByPK);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA15	数据加密
	 * keyValue		密钥值
	 * keyType		密钥类型
	 * paddingFlag	填充标识
	 * data			待加密的数据
	 * dataType		data数据类型
	 * algorithmID	算法标识
	 * iv			iv向量
	 */
	public UnionMessage servEA15(String keyValue, String keyType, int paddingFlag,
			String data, int dataType, int algorithmID, String iv){
	    if(UnionVerify.paramIsEmpty(keyValue)) {
	    	return UnionVerify.paramValueWrong("keyValue");
		}
	    if(UnionVerify.paramIsEmpty(keyType)) {
	    	return UnionVerify.paramValueWrong("keyType");
		}
	    if (paddingFlag != 0 && paddingFlag != 1)
	    	return UnionVerify.paramValueWrong("paddingFlag");
	    if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
		}
	    if (dataType != 0 && dataType != 1)
	    	return UnionVerify.paramValueWrong("dataType");
	    if (algorithmID != 0 && algorithmID != 1)
	    	return UnionVerify.paramValueWrong("algorithmID");
	  
	
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA15");
		um.putBodyField("keyValue", keyValue);
		um.putBodyField("keyType", keyType);
		um.putBodyField("paddingFlag", paddingFlag);
		um.putBodyField("data", data);
		um.putBodyField("dataType", dataType);
		um.putBodyField("algorithmID", algorithmID);
		if (algorithmID == 1)
			um.putBodyField("iv", iv);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	/*
	 * EA16	数据解密
	 * keyValue		密钥值
	 * keyType		密钥类型
	 * paddingFlag	填充标识
	 * data			待加密的数据
	 * exportFlag	输出数据类型
	 * algorithmID	算法标识
	 * iv			iv向量
	 */
	public UnionMessage servEA16(String keyValue, String keyType, int paddingFlag,
			String data, int exportFlag, int algorithmID, String iv){
	    if(UnionVerify.paramIsEmpty(keyValue)) {
	    	return UnionVerify.paramValueWrong("keyValue");
		}
	    if(UnionVerify.paramIsEmpty(keyType)) {
	    	return UnionVerify.paramValueWrong("keyType");
		}
	    if (paddingFlag != 0 && paddingFlag != 1)
	    	return UnionVerify.paramValueWrong("paddingFlag");
	    if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
		}
	    if (exportFlag != 0 && exportFlag != 1)
	    	return UnionVerify.paramValueWrong("exportFlag");
	    if (algorithmID != 0 && algorithmID != 1)
	    	return UnionVerify.paramValueWrong("algorithmID");

	
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA16");
		um.putBodyField("keyValue", keyValue);
		um.putBodyField("keyType", keyType);
		um.putBodyField("paddingFlag", paddingFlag);
		um.putBodyField("data", data);
		um.putBodyField("exportFlag", exportFlag);
		um.putBodyField("algorithmID", algorithmID);
		if (algorithmID == 1)
			um.putBodyField("iv", iv);
		
		return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
	}
	
	
	/**
	 * EA17 网联接入数字信封加密
	 * @param serialNum	证书序列号钥值
	 * @param mode			密钥模式
	 * @param keyType		密钥类型
	 * @param keyValue		密钥密文
	 * @param data			明文数据
	 * @param dataLen		数据长度
	 * @param dataType		明文data数据类型
	 * @param separator	分隔符
	 * @param paddingFlag	填充标识
	 * @param exportFlag	输出密文数据标识
	 * @param keyByPKFormat	输出公钥加密的密钥密文编码格式
	 * @param algorithmID	算法标识
	 * @param iv			iv向量
	 */
	public UnionMessage servEA17(String serialNum, int mode, String keyType,
			 String keyValue, String data, int dataLen,int dataType,
			 String separator, int paddingFlag, int exportFlag,
			 int keyByPKFormat, int algorithmID, String iv){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
	    
	    if( mode != 0 && mode != 1)
	    	return UnionVerify.paramValueWrong("mode");

	    if(UnionVerify.paramIsEmpty(keyType)) {
	    	return UnionVerify.paramValueWrong("keyType");
		}
	    
	    
	    if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
		}
	    
	    if (dataType != 0 && dataType != 1 && dataType != 3)
	    	return UnionVerify.paramValueWrong("dataType");
	    
	    if (paddingFlag != 0 && paddingFlag != 1)
	    	return UnionVerify.paramValueWrong("paddingFlag");
	    
	    if (exportFlag != 1 && exportFlag != 3)
	    	return UnionVerify.paramValueWrong("exportFlag");
	    
	    if (keyByPKFormat != 1 && keyByPKFormat != 3)
	    	return UnionVerify.paramValueWrong("keyByPKFormat");
	    
	    if (algorithmID != 0 && algorithmID != 1)
	    	return UnionVerify.paramValueWrong("algorithmID");
    
	    UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA17");
		um.putBodyField("serialNum", serialNum);
		um.putBodyField("mode", mode);
		um.putBodyField("keyType", keyType);
		um.putBodyField("data", data);
		um.putBodyField("dataLen", dataLen);
		um.putBodyField("dataType", dataType);
		um.putBodyField("separator", separator);
		um.putBodyField("paddingFlag", paddingFlag);
		um.putBodyField("exportFlag", exportFlag);
		um.putBodyField("keyByPKFormat", keyByPKFormat);
		um.putBodyField("algorithmID", algorithmID);
		if (mode == 1)
			um.putBodyField("keyValue", keyValue);
		if (!UnionVerify.paramIsEmpty(iv))
			um.putBodyField("iv", iv);
		
		return UnionComm.sendAndRecvMsg(um, api.getMaxSendTimes());
	}
	
	
	/**
	 * EA18 网联接入数字信封解密
	 * @param serialNum	证书序列号钥值
	 * @param keyByPK		公钥加密的密钥密文
	 * @param keyByPKFormat	公钥加密的密钥密文数据编码格式
	 * @param data			明文数据
	 * @param dataLen		数据长度
	 * @param dataType		明文data数据类型
	 * @param separator	分隔符
	 * @param paddingFlag	填充标识
	 * @param exportFlag	输出密文数据标识
	 * @param algorithmID	算法标识
	 * @param iv			iv向量
	 */
	public UnionMessage servEA18(String serialNum, String keyByPK, int keyByPKFormat,
			 String data, int dataLen,int dataType,
			 String separator, int paddingFlag, int exportFlag,
			 int algorithmID, String iv){
	    if(UnionVerify.paramIsEmpty(serialNum)) {
	    	return UnionVerify.paramValueWrong("serialNum");
		}
	    
	    if(UnionVerify.paramIsEmpty(keyByPK)) {
	    	return UnionVerify.paramValueWrong("keyByPK");
		}   
	    
	    if(UnionVerify.paramIsEmpty(data)) {
	    	return UnionVerify.paramValueWrong("data");
		}
	    
	    if (dataType != 1 && dataType != 3)
	    	return UnionVerify.paramValueWrong("dataType");
	    
	    if (paddingFlag != 0 && paddingFlag != 1)
	    	return UnionVerify.paramValueWrong("paddingFlag");
	    
	    if (exportFlag != 0 && exportFlag != 1 && exportFlag != 3)
	    	return UnionVerify.paramValueWrong("exportFlag");

	    if (algorithmID != 0 && algorithmID != 1)
	    	return UnionVerify.paramValueWrong("algorithmID");
	   
	    UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "EA18");
		um.putBodyField("serialNum", serialNum);
		um.putBodyField("keyByPK", keyByPK);
		um.putBodyField("keyByPKFormat", keyByPKFormat);
		um.putBodyField("data", data);
		um.putBodyField("dataLen", dataLen);
		um.putBodyField("dataType", dataType);
		um.putBodyField("separator", separator);
		um.putBodyField("paddingFlag", paddingFlag);
		um.putBodyField("exportFlag", exportFlag);
		um.putBodyField("algorithmID", algorithmID);
		if (!UnionVerify.paramIsEmpty(iv))
			um.putBodyField("iv", iv);
		
		return UnionComm.sendAndRecvMsg(um, api.getMaxSendTimes());
	}
	
	/*
	 * 	文件解密,算法ECB,填充方式PKCS5
	 * plainFile	明文文件名
	 * cipherFile	密文文件名
	 * keyValue		密钥值
	 * keyType		密钥类型
	 * mode			模式，0加密，1解密
	 */
	private UnionMessage unionEncryptAndDecryptFile(String plainFile, String cipherFile,
			String keyValue, String keyType, int mode){
	    if(UnionVerify.paramIsEmpty(plainFile)) {
	    	return UnionVerify.paramValueWrong("plainFile");
		}
	    if(UnionVerify.paramIsEmpty(cipherFile)) {
	    	return UnionVerify.paramValueWrong("cipherFile");
		}
	    if(UnionVerify.paramIsEmpty(keyValue)) {
	    	return UnionVerify.paramValueWrong("keyValue");
		}
	    if(UnionVerify.paramIsEmpty(keyType)) {
	    	return UnionVerify.paramValueWrong("keyType");
		}
	
	    // 读取文件到buffer中
	    FileInputStream in = null;
	    FileOutputStream out = null;
	    UnionMessage um = null;
	    
	    try {
	    	if (mode == 1)
	    	{
	    		in = new FileInputStream(cipherFile);
	    		out = new FileOutputStream(plainFile);
	    	}
	    	else
	    	{
	    		in = new FileInputStream(plainFile);
	    		out = new FileOutputStream(cipherFile);
	    	}
	    	int peerLen = 512;
	    	byte[] inBuffer = new byte[peerLen];
	    	int totalLen = 0;
	    	totalLen = in.available();
	    	int num = 0;
	    	int curlen = 2048;
	    	int	paddingFlag = 0;
	    	if (totalLen % peerLen != 0)
	    		num = totalLen/peerLen+1;
	    	else
	    		num = totalLen/peerLen;
	    	for (int i = 0; i < num; i++)
	    	{
	    		if (i == num-1)
	    		{
	    			paddingFlag = 1;
	    			if (totalLen % peerLen != 0)
	    				curlen = totalLen % peerLen;
	    		}
	    		final byte[] buffer = curlen < peerLen ? new byte[curlen]:inBuffer;
	    		in.read(buffer);
	    		String data = UnionStr.bcdhex_to_aschex(buffer);
	    		if (mode == 1)
	    			um = servEA16(keyValue, keyType, paddingFlag, data, 1, 0, null);
	    		else
	    			um = servEA15(keyValue, keyType, paddingFlag, data, 1, 0, null);
	    		if (!um.getHeadField("responseCode").equals("000000"))
	    			return um;
	    		String odata = um.getBodyField("data");
	    		final byte[] outBuffer = UnionStr.aschex_to_bcdhex(odata);
	    		out.write(outBuffer);
	    	}
	    } catch (Exception e){
	    	return UnionVerify.paramValueWrong(e.getMessage());
	    } finally {
	    	try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		return um;
	}
	
	/*
	 * 	文件解密,算法ECB,填充方式PKCS5
	 * plainFile	明文文件名
	 * cipherFile	密文文件名
	 * keyValue		密钥值
	 * keyType		密钥类型
	 */
	public UnionMessage unionDecryptFile(String plainFile, String cipherFile,
			String keyValue, String keyType)
	{
		return unionEncryptAndDecryptFile(plainFile, cipherFile, keyValue, keyType, 1);
	}
	
	/*
	 * 	文件加密,算法ECB,填充方式PKCS5
	 * plainFile	明文文件名
	 * cipherFile	密文文件名
	 * keyValue		密钥值
	 * keyType		密钥类型
	 */
	public UnionMessage unionEncryptFile(String plainFile, String cipherFile,
			String keyValue, String keyType)
	{
		return unionEncryptAndDecryptFile(plainFile, cipherFile, keyValue, keyType, 0);
	}
	
	/*
	 * 	文件解密,算法ECB,填充方式PKCS5
	 * plainFile	明文文件名，如"D:/files/aa.txt"
	 * cipherFile	密文文件名，如"D:/files/aa.enc"
	 * serialNum	加密证书序列号
	 * keyByPK		公钥加密的密钥密文，HEX数据
	 * keyByPKFormat	公钥加密的密钥密文类型，1：hex数据，3：base64编码数据
	 */
	public UnionMessage unionDecryptFileWithKeyByPK(String plainFile, String cipherFile,
			String serialNum, String keyByPK, int keyByPKFormat)
	{
		UnionMessage um = null;
		String keyByPKHex = null;
		if (keyByPKFormat != 1 && keyByPKFormat != 3)
		{
			return UnionVerify.paramValueWrong("keyByPKFormat");
		}
		if (keyByPKFormat == 3)
		{
			try {
				final byte[] bcdK = Base64.getDecoder().decode(keyByPK);
				keyByPKHex = UnionStr.bcdhex_to_aschex(bcdK);
			} catch (Exception e) {
				return UnionVerify.paramValueWrong(e.getMessage());
			}
		}
		else
			keyByPKHex = keyByPK;
		um = servEA14(serialNum, keyByPKHex);
		if (!um.getHeadField("responseCode").equals("000000"))
			return um;
		String keyValue = um.getBodyField("keyValue");
		String keyType = um.getBodyField("keyType");
		
		um = unionEncryptAndDecryptFile(plainFile, cipherFile, keyValue, keyType, 1);
		return um;
	}

}

