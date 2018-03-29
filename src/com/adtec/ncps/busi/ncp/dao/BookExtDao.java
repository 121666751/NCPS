package com.adtec.ncps.busi.ncp.dao;

import com.adtec.ncps.busi.ncp.bean.Book_ext;

public class BookExtDao {
	/**
	 * 表名
	 */
	private static final String tableName = "t_ncp_book_ext";
	
	/**
	 * 唯一标示
	 */
	private static final String primaryKey = "plat_date,seq_no";
	/**
	 * 根据给定对象实例更新数据库
	 * @param teller 
	 * @return int
	 * @throws Exception
	 */
	public static int update(Book_ext book_ext) throws Exception{
		int num = BaseDao.updateObject(book_ext, tableName, primaryKey);
		return num;
	}
	
	/**
	 * 根据给定对象,从数据库删除相应的记录
	 * @param book_ext
	 * @return int
	 * @throws Exception
	 */
	public static int delete(Book_ext book_ext) throws Exception{
		int num = BaseDao.deleteObject(book_ext, tableName, primaryKey);
		return num;
	}
	
	/**
	 * 根据给定的柜员对象实例 ,对数据库进行插入操作
	 * @param book_ext
	 * @return int
	 * @throws Exception
	 */
	public  static int insert(Book_ext book_ext) throws Exception{
		int num = BaseDao.insertObject(book_ext, tableName);
		return num;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
