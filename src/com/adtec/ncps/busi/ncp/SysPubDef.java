package com.adtec.ncps.busi.ncp;

import com.adtec.starring.global.SysDef;

/**
 * 系统全局变量定义
 * @author dingjunbo
 *
 */
public class SysPubDef {
	//发卡行机构标识，对应银联报文头域名IssrId
	public static final String BRANKNO = "05519200";
	//返回银联响应码和响应信息--成功
	public static final String CUP_SUC_RET = "00000000";
	public static final String CUP_SUC_MSG = "交易成功";
	//返回银联响应码和响应信息--系统错误
	public static final String CUP_ERR_RET = "PB00100";
	public static final String CUP_ERR_MSG = "系统错误";
	//返回银联响应码和响应信息--日终错误
	public static final String CUP_ENDDAY_RET = "PB090000";
	public static final String CUP_ENDDAY_MSG = "正在日终处理";
	//返回银联响应码和响应信息--超时
	public static final String CUP_TIME_RET = "PB068000";
	public static final String CUP_TIME_MSG = "系统超时";
	//返回管理端响应码
	public static final String ERR_RET = "000001";
	public static final String ERR_MSG = "系统错误";
	//返回管理端响应码和响应信息--成功
	public static final String SUC_RET = "00000";
	public static final String SUC_MSG = "交易成功";
	//默认查询记录数
	public static final int MNG_QRY_COUNT = 10;
	//报文方向
	public static final String DRCTN = "12";
	
	//短信失效时间-秒
	public static final int iInvlSec = 120;// TODO 测试  根据实际情况设置
	
	//对账设置固定目录
	public static final String NCP_CHK_DIR=SysDef.WORK_DIR+"/share/ncpchk/";
	public static final String HOST_CHK_DIR=SysDef.WORK_DIR+"/share/host/";
	public static final String SUM_CLR_DIR=SysDef.WORK_DIR+"/share/ncpsum/";
	//管理端文件存放目录
	public static final String MNG_FILE_DIR=SysDef.WORK_DIR+"/share/print/uncps/";
}
