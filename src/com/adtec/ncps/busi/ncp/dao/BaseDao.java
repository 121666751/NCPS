package com.adtec.ncps.busi.ncp.dao;

import java.util.Iterator;
import java.util.Set;
import com.adtec.ncps.busi.ncp.DataBaseUtils;


public class BaseDao {

	/**
	 * @param object 对象实例
	 * @param tableName 表名
	 * @return 新增的记录数
	 * @throws Exception
	 */
	public static int insertObject(Object object,String tableName) throws Exception{
		int result = 0; 
		StringBuilder sbdSql = new StringBuilder();
		sbdSql.append( "INSERT INTO " ).append( tableName ).append( " ");
		String[] propertys = BaseDaoTool.getPropertyNames(object.getClass());
		Object[] propertyValues = BaseDaoTool.getPropertyValues(object);
		StringBuilder sbdColumns = new StringBuilder("(");
		StringBuilder sbdValues = new StringBuilder("(");
		if(propertys!=null||propertys.length>0){
			int length = propertys.length;
			if(length>1){
				sbdValues.insert(0, "VALUES");
			}else{
				sbdValues.insert(0, "VALUE");
			}
			for(int i=0;i<length;i++){
				if(i!=(length-1)){
					sbdColumns.append( propertys[i] ).append( ",");
					sbdValues.append( "?,");
				}else{
					sbdColumns.append( propertys[i] ).append( "");
					sbdValues.append( "?");
				}
			}
		}else{
			sbdValues.insert(0, "VALUES");
		}
		sbdColumns.append(  ")");
		sbdValues.append( ")");
		sbdSql.append( sbdColumns ).append( " " ).append( sbdValues);
		result = DataBaseUtils.execute(sbdSql.toString(),propertyValues);
		return result;
	}
	/**
	 * 根据给定的对象实例 表名 唯一标示(多个的用逗号隔开)
	 * @param object 对象实例
	 * @param tableName 表名
	 * @param primaryKey 唯一标示(多个的用逗号隔开)
	 * @return 更新记录数
	 * @throws Exception 
	 */
	public static int updateObject(Object object,String tableName ,String primaryKey) throws Exception
	{
		int result = 0; 
		if(object!=null&&primaryKey!=null&&!primaryKey.equals("")){
			Class<? extends Object> clazz = object.getClass();
			String[] propertys = BaseDaoTool.getPropertyNames(clazz);
			Object[] propertyValues = BaseDaoTool.getPropertyValues(object);
			StringBuilder sbdSql = new StringBuilder();
			sbdSql.append( "UPDATE " ).append( tableName ).append( " SET ");
			Object[] values = null;
			StringBuilder sbd_sql = new StringBuilder();
			StringBuilder sbdWhere = new StringBuilder();
			Set<?> set = BaseDaoTool.primaryKeyToPropertyNames(primaryKey);
			if(propertys!=null&&propertys.length>0){
				int length = propertys.length;
				values = new Object[length];
				int values_index = 0;
				int primaryKeyValues_index = length-set.size();
				for(int i=0;i<length;i++){
					String propertyName = propertys[i];
					if(!set.contains(propertyName)){
						values[values_index] = BaseDaoTool.getPropertyValue(object, propertyName);
						values_index = values_index + 1;
						if(sbd_sql.toString().equals("")){
							sbd_sql.append( "" ).append( propertys[i] ).append( " = ?");
						}else{
							sbd_sql.append( "," ).append( propertys[i] ).append( " = ?");
						}
					}else{
						values[primaryKeyValues_index] = propertyValues[i];						
						primaryKeyValues_index = primaryKeyValues_index + 1;
						if(sbdWhere.toString().equals("")){
							sbdWhere.append( " WHERE " ).append( propertys[i] ).append( " = ?");							
						}else{
							sbdWhere.append( " AND " ).append( propertys[i] ).append( " = ?");
						}
					}
				}
			}
			//if(sbdWhere==null||sbdWhere.toString().trim().equals("")){
			//	DataPoolTool.throwErr("数据库操作失败,你没有定义正确的唯一标示![" + object.getClass().getName() + ":" + primaryKey + "]");
			//}
			sbdSql.append( sbd_sql ).append( sbdWhere);
			result = DataBaseUtils.execute(sbdSql.toString(),values);
		}
		return result;
	}
	/**
	 * 根据给定的对象实例 表名 唯一标示(有多个的用逗号隔开)
	 * @param object 对象实例
	 * @param tableName 表名
	 * @param primaryKey 唯一标示(有多个的用逗号隔开)
	 * @return 删除的记录数
	 * @throws Exception
	 */
	public static int deleteObject(Object object,String tableName,String primaryKey) throws Exception{
		int result = 0;
		Set<String> primaryKeys = BaseDaoTool.primaryKeyToPropertyNames(primaryKey);
		int size = primaryKeys.size();
		if(size>0){
			StringBuilder sbdSql = new StringBuilder();
			sbdSql.append( "DELETE FROM " ).append( tableName) ;
			StringBuilder sbdWhere = new StringBuilder();
			Object[] values = new Object[size];
			int index = 0;
			for(Iterator<?> iter = primaryKeys.iterator();iter.hasNext();){
				String propertyName = (String)iter.next();
				values[index] = BaseDaoTool.getPropertyValue(object, propertyName);
				index = index + 1;
				if(sbdWhere.toString().equals("")){
					sbdWhere.append(" WHERE " ).append( propertyName ).append( " = ? ");
				}else{
					sbdWhere.append( " AND " ).append( propertyName ).append( " = ? ");
				}
			}
			sbdSql.append( sbdWhere.toString());
			result = DataBaseUtils.execute(sbdSql.toString(), values);
		}
		primaryKeys = null;
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
