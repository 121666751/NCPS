package com.adtec.ncps.busi.qrps.dao;

import com.adtec.ncps.busi.qrps.bean.QrBook;
import com.adtec.ncps.busi.ncp.dao.BaseDao;

public class QrBookDao {
    /**
     * 表名
     */
    private static final String tableName = "t_qrp_book";

    /**
     * 唯一标示
     */
    private static final String primaryKey = "plat_date,seq_no";

    /**
     * 根据给定对象实例更新数据库
     * 
     * @param teller
     * @return int
     * @throws BaseException
     */
    public static int update(QrBook book) throws Exception {
	int num = BaseDao.updateObject(book, tableName, primaryKey);
	return num;
    }

    /**
     * 根据给定对象,从数据库删除相应的记录
     * 
     * @param teller
     * @return int
     * @throws Exception
     */
    public static int delete(QrBook book) throws Exception {
	int num = BaseDao.deleteObject(book, tableName, primaryKey);
	return num;
    }

    /**
     * 根据给定的柜员对象实例 ,对数据库进行插入操作
     * 
     * @param teller
     * @return int
     * @throws Exception
     */
    public static int insert(QrBook book) throws Exception {
	int num = BaseDao.insertObject(book, tableName);
	return num;
    }

}
