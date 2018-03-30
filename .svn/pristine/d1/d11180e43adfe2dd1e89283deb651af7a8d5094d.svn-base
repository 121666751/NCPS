package com.union.api;

import com.union.connect.UnionComm;
import com.union.connect.UnionStart;
import com.union.message.UnionMessage;
import com.union.message.UnionRequest;
import com.union.utils.UnionVerify;

public class UnionKMS {
	
	/**
	 * 开始初始化
	 * 本地初始化：private static UnionStart api = new UnionStart();
	 * 读取环境变量初始化：private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	 */
	private static UnionStart api = new UnionStart(UnionStart.systemEnv);
	
	/*
	 * K999 申请一张IC卡的安全数据
	 */
	public UnionMessage servK999(String icStandard, String protectKeyName,String protectKeyVersion,String cardBin,int lengthOfRSA, String EF04, String EF06, String EF08, String EF10, String recordNo, String iccPAN, int iccAppNum,String signData01, String ICCExpires01, String dataVerificationCode01, String signData02,String ICCExpires02, String dataVerificationCode02,String signData03,
			String ICCExpires03,String dataVerificationCode03,String appExtension){
		//参数检验	
	    if(UnionVerify.paramIsEmpty(icStandard)) {
	    	return UnionVerify.paramValueWrong("icStandard");
		}
		if(UnionVerify.paramIsEmpty(protectKeyName)) {
			return UnionVerify.paramValueWrong("protectKeyName");
		}
		if(UnionVerify.paramIsEmpty(protectKeyVersion)) {
			return UnionVerify.paramValueWrong("protectKeyVersion");
		}
		if(UnionVerify.paramIsEmpty(cardBin)) {
			return UnionVerify.paramValueWrong("cardBin");
		}
		if(lengthOfRSA<0) {
			return UnionVerify.paramValueWrong("lengthOfRSA");
		}
		if(UnionVerify.paramIsEmpty(EF04)) {
			return UnionVerify.paramValueWrong("EF04");
		}
		if(UnionVerify.paramIsEmpty(EF06)) {
			return UnionVerify.paramValueWrong("EF06");
		}
		if(UnionVerify.paramIsEmpty(EF08)) {
			return UnionVerify.paramValueWrong("EF08");
		}
		if(UnionVerify.paramIsEmpty(EF10)) {
			return UnionVerify.paramValueWrong("EF10");
		}
		if(UnionVerify.paramIsEmpty(recordNo)) {
			return UnionVerify.paramValueWrong("recordNo");
		}
		if(UnionVerify.paramIsEmpty(iccPAN)) {
			return UnionVerify.paramValueWrong("iccPAN");
		}
		if(iccAppNum<0) {
			return UnionVerify.paramValueWrong("iccAppNum");
		}
		if(UnionVerify.paramIsEmpty(signData01)) {
			return UnionVerify.paramValueWrong("signData01");
		}
		if(UnionVerify.paramIsEmpty(ICCExpires01)) {
			return UnionVerify.paramValueWrong("ICCExpires01");
		}
		if(UnionVerify.paramIsEmpty(dataVerificationCode01)) {
			return UnionVerify.paramValueWrong("dataVerificationCode01");
		}
		if(UnionVerify.paramIsEmpty(signData02)) {
			return UnionVerify.paramValueWrong("signData02");
		}
		if(UnionVerify.paramIsEmpty(ICCExpires02)) {
			return UnionVerify.paramValueWrong("ICCExpires02");
		}
		if(UnionVerify.paramIsEmpty(dataVerificationCode02)) {
			return UnionVerify.paramValueWrong("dataVerificationCode02");
		}
		if(UnionVerify.paramIsEmpty(signData03)) {
			return UnionVerify.paramValueWrong("signData03");
		}
		if(UnionVerify.paramIsEmpty(ICCExpires03)) {
			return UnionVerify.paramValueWrong("ICCExpires03");
		}
		if(UnionVerify.paramIsEmpty(dataVerificationCode03)) {
			return UnionVerify.paramValueWrong("dataVerificationCode03");
		}
		if(UnionVerify.paramIsEmpty(appExtension)) {
			return UnionVerify.paramValueWrong("appExtension");
		}
		UnionMessage um = new UnionRequest();
		um.putHeadField("serviceCode", "K999");
		 um.putBodyField("icStandard",icStandard);
         um.putBodyField("protectKeyName",protectKeyName);
         um.putBodyField("protectKeyVersion",protectKeyVersion);
         um.putBodyField("cardBin",cardBin);
         um.putBodyField("lengthOfRSA",lengthOfRSA);
         um.putBodyField("EF04",EF04);
         um.putBodyField("EF06",EF06);
         um.putBodyField("EF08",EF08);
         um.putBodyField("EF10",EF10);
         um.putBodyField("recordNo",recordNo);
         um.putBodyField("iccPAN",iccPAN);
         um.putBodyField("iccAppNum",iccAppNum);
         um.putBodyField("signData01",signData01);
         um.putBodyField("ICCExpires01",ICCExpires01);
         um.putBodyField("dataVerificationCode01",dataVerificationCode01);
         um.putBodyField("signData02",signData02);
         um.putBodyField("ICCExpires02",ICCExpires02);
         um.putBodyField("dataVerificationCode02",dataVerificationCode02);
         um.putBodyField("signData03",signData03);
         um.putBodyField("ICCExpires03",ICCExpires03);
         um.putBodyField("dataVerificationCode03",dataVerificationCode03);
         um.putBodyField("appExtension",appExtension);
//         um.getBodyField("securityFileData",securityFileData);
		 return UnionComm.sendAndRecvMsg(um,  api.getMaxSendTimes());
		}
}
