package com.adtec.ncps.busi.chnl.dao;

import com.adtec.ncps.busi.chnl.bean.Jrnl;
import com.adtec.ncps.busi.ncp.bean.Book;
import com.adtec.ncps.busi.ncp.dao.BaseDao;

public class JrnlDao {
	/**
	 * 表名
	 */
	private static final String tableName = "t_jrnl";
	
	/**
	 * 唯一标示
	 */
	private static final String primaryKey = "PLAT_DATE,SEQ_NO";
	/**
	 * 根据给定对象实例更新数据库
	 * @param teller 
	 * @return int
	 * @throws BaseException
	 */
	public static int update(Jrnl jrnl) throws Exception{
		int num = BaseDao.updateObject(jrnl, tableName, primaryKey);
		return num;
	}
	
	/**
	 * 根据给定对象,从数据库删除相应的记录
	 * @param teller
	 * @return int
	 * @throws Exception
	 */
	public static int delete(Jrnl jrnl) throws Exception{
		int num = BaseDao.deleteObject(jrnl, tableName, primaryKey);
		return num;
	}
	
	/**
	 * 根据给定的柜员对象实例 ,对数据库进行插入操作
	 * @param teller
	 * @return int
	 * @throws Exception
	 */
	public  static int insert(Jrnl jrnl) throws Exception{
		int num = BaseDao.insertObject(jrnl, tableName);
		
		return num;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
