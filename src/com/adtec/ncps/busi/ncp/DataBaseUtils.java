package com.adtec.ncps.busi.ncp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.adtec.ncps.busi.ncp.dao.BaseDaoTool;
import com.adtec.starring.data.ibatis.IBaseDos;
import com.adtec.starring.datapool.EPOper;
import com.adtec.starring.exception.BaseException;
import com.adtec.starring.exception.SysErr;
import com.adtec.starring.log.DBExecuter;
import com.adtec.starring.log.TrcLog;
import com.adtec.starring.respool.PoolOperate;
import com.adtec.starring.respool.RuntimePool;
import com.adtec.starring.struct.admin.ESAdmin;
import com.adtec.starring.struct.admin.MchParm;
import com.adtec.starring.struct.dta.DtaInfo;
import com.adtec.starring.struct.res.LocalInfo;
import com.adtec.starring.util.SpringUtil;
import com.adtec.starring.util.StringTool;

public class DataBaseUtils {
	/*
	 * @author dingjunbo
	 * 
	 * @createAt 2017年5月17日
	 * 
	 * @version 1.0 获取数据源
	 */
	public static DataSource getDatasource() {
		// TODO Auto-generated method stub
		DtaInfo dtaInfo = DtaInfo.getInstance();
		ESAdmin esAdmin = PoolOperate.getParmPoolByVersion(dtaInfo.getParVersion()).getEsadmin();
		String dbDomain = esAdmin.getDtaParm(dtaInfo.getDtaName()).getDbDomain();
		if (StringTool.isNullOrEmpty(dbDomain)) {
			LocalInfo localInfo = RuntimePool.getInstance().getPlatPubInfo().getLocalInfo();
			String machName = localInfo.getLocMchName();
			MchParm machParm = esAdmin.getMachParm(machName);
			dbDomain = machParm.getDbDomain();
			if (StringTool.isNullOrEmpty(dbDomain)) {
				dbDomain = esAdmin.getDtaParm(dtaInfo.getDtaName()).getDbDomain();
				if (StringTool.isNullOrEmpty(dbDomain)) {
					throw new BaseException(SysErr.E_BPM_DSNAMENULL);
				}
			}
		}

		String ds = esAdmin.getDbDomainMap().get(dbDomain).getDataSoucreList().get(0);

		DataSource dataSource;

		dataSource = (DataSource) SpringUtil.getBean(ds);

		return dataSource;
	}

	/**
	 * 取数据源
	 * 
	 * @return IBaseDos
	 */
	public static IBaseDos getiBatsDatasource() {
		// TODO Auto-generated method stub
		DtaInfo dtaInfo = DtaInfo.getInstance();
		ESAdmin esAdmin = PoolOperate.getParmPoolByVersion(dtaInfo.getParVersion()).getEsadmin();
		String ds = esAdmin.getDtaParm(dtaInfo.getDtaName()).getOperDB();
		if (StringTool.isNullOrEmpty(ds)) {
			LocalInfo localInfo = RuntimePool.getInstance().getPlatPubInfo().getLocalInfo();
			String machName = localInfo.getLocMchName();
			MchParm machParm = esAdmin.getMachParm(machName);
			ds = machParm.getOperDB();
			if (StringTool.isNullOrEmpty(ds)) {
				ds = esAdmin.getOperDB();
				if (StringTool.isNullOrEmpty(ds)) {
					throw new BaseException(SysErr.E_BPM_DSNAMENULL);
				}
			}
		}
		IBaseDos dataSource = (IBaseDos) SpringUtil.getBean(ds + "Dos");
		return dataSource;
	}

	/**
	 * 执行更新、插入、删除语句
	 * 
	 * @param sql
	 * @return 执行成功的条数
	 * @version 更新数据-自动提交事务
	 * @throws Exception
	 */
	public static Integer execute(String sql, Object[] value) throws Exception {
		DataSource ds = DataBaseUtils.getDatasource();
		PreparedStatement pstmt = null;
		Integer rtn = -1;
		DBExecuter executer = new DBExecuter(ds, "", true);
		try {
			SysPub.appLog("DEBUG", "start execute update or insert sql:" + sql);
			pstmt = (PreparedStatement) executer.bind(sql);
			// 20170611增加动态查询
			if (value != null && value.length > 0) {
				int ilen = value.length;
				for (int i = 1; i <= ilen; i++) {
					SysPub.appLog("TRACE", "value:%s", value[i - 1]);
					if (value[i - 1] == null) {
						pstmt.setObject(i, "");
					} else {
						pstmt.setObject(i, value[i - 1]);
					}
				}
			}
			rtn = pstmt.executeUpdate();
		} catch (Exception e) {
			SysPub.appLog("ERROR", "execute error! sql:" + sql);
			dbLog(e, sql);
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (executer != null)
					executer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rtn;
	}

	/**
	 * @param sqlStr
	 *            查询语句
	 * @param elemName
	 *            数据对象名称
	 * @version 1.0 查询数据保存到数据对象
	 */
	public static int queryToElem(String sqlStr, String elemName, Object[] value) throws Exception {
		DtaInfo dtaInfo = DtaInfo.getInstance();
		String tpID = dtaInfo.getTpId();
		DataSource ds = DataBaseUtils.getDatasource();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBExecuter executer = new DBExecuter(ds, "", true);
		int j = 0;
		try {
			SysPub.appLog("DEBUG", "sql:" + sqlStr);
			pstmt = (PreparedStatement) executer.bind(sqlStr);
			// 20170611增加动态查询
			if (value != null && value.length > 0) {
				int ilen = value.length;
				for (int i = 1; i <= ilen; i++) {
					SysPub.appLog("DEBUG", "value[i-1]:" + value[i - 1]);
					pstmt.setObject(i, value[i - 1]);
				}
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNames = new String[count];
			for (int i = 1; i <= count; i++) {
				colNames[i - 1] = rsmd.getColumnName(i);
			}
			while (rs.next()) {
				for (int i = 0; i < colNames.length; i++) {
					String colName = colNames[i];
					String tmp = elemName + "[" + j + "]." + colName;
					// SysPub.appLog("DEBUG", "数据元素名称为：%s,值：%s", tmp,
					// rs.getObject(colName));
					EPOper.put(tpID, tmp, rs.getObject(colName));
				}
				j++;
			}
		} catch (Exception e) {
			// SysPub.appLog("ERROR", "数据库操作失败!!!");
			// e.printStackTrace();
			dbLog(e, sqlStr);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (executer != null)
					executer.close();

			} catch (SQLException e) {
				SysPub.appLog("ERROR", "数据库操作失败!");
				e.printStackTrace();
			}
		}
		return j;
	}

	/**
	 * @param sqlStr
*            查询一张表记录数
	 * @param elemName
	 *            数据对象名称
	 * @version 1.0 查询数据保存到数据对象
	 */
	public static int queryToCount(String sqlStr, Object[] value) throws Exception {
		DataSource ds = DataBaseUtils.getDatasource();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBExecuter executer = new DBExecuter(ds, "", true);
		int j = 0;
		try {
			SysPub.appLog("DEBUG", "sql:" + sqlStr);
			pstmt = (PreparedStatement) executer.bind(sqlStr);
			// 20170611增加动态查询
			if (value != null && value.length > 0) {
				int ilen = value.length;
				for (int i = 1; i <= ilen; i++) {
					SysPub.appLog("DEBUG", "value[i-1]:" + value[i - 1]);
					pstmt.setObject(i, value[i - 1]);
				}
			}
			rs = pstmt.executeQuery();
			if (rs == null) {
				SysPub.appLog("DEBUG", "rs:" + rs);
				return 0;
			}
			while (rs.next()) {
				j++;
				SysPub.appLog("DEBUG", "j:"+j);
			}
			//rs.last();
			//j=rs.getRow();
		} catch (Exception e) {
			// SysPub.appLog("ERROR", "数据库操作失败!!!");
			// e.printStackTrace();
			dbLog(e, sqlStr);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (executer != null)
					executer.close();

			} catch (SQLException e) {
				SysPub.appLog("ERROR", "数据库操作失败!");
				e.printStackTrace();
			}
		}
		return j;
	}
	
	/**
	 * @param sqlStr
	 *            查询语句
	 * @param clazz
	 * @version 1.0 查询数据保存到类对象
	 * @throws Exception
	 */
	public static List<?> queryList(String sqlStr, Object[] value, Class<?> clazz) throws Exception {
		DataSource ds = DataBaseUtils.getDatasource();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Object> list = new ArrayList();
		DBExecuter executer = new DBExecuter(ds, "", true);
		String szTmp = "";
		try {
			SysPub.appLog("DEBUG", "sql:" + sqlStr);
			pstmt = (PreparedStatement) executer.bind(sqlStr);
			int ilength = value.length;
			for (int i = 1; i <= ilength; i++) {
				SysPub.appLog("DEBUG", "value[i-1]:" + value[i - 1]);
				pstmt.setObject(i, value[i - 1]);
			}
			rs = pstmt.executeQuery();
			String[] propertys = BaseDaoTool.getPropertyNames(clazz);
			int propertyNum = propertys.length;
			Object obj = null;
			while (rs.next()) {
				obj = clazz.newInstance();
				for (int i = 0; i < propertyNum; i++) {
					Method method = BaseDaoTool.getPropertySETMethod(clazz, propertys[i]);
					Field field = clazz.getDeclaredField(propertys[i]);
					String methodName = method.getName();
					String szFieldType = field.toGenericString();
					szTmp = "方法名:" + methodName + "参数:" + szFieldType;
					if (szFieldType.indexOf("java.lang.Byte ") >= 0)
						method.invoke(obj, rs.getByte(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Integer ") >= 0)
						method.invoke(obj, rs.getInt(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Short ") >= 0)
						method.invoke(obj, rs.getShort(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Long ") >= 0)
						method.invoke(obj, rs.getLong(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Boolean ") >= 0)
						method.invoke(obj, rs.getBoolean(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Character ") >= 0)
						method.invoke(obj, rs.getCharacterStream(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Float ") >= 0)
						method.invoke(obj, rs.getFloat(propertys[i]));
					else if (szFieldType.indexOf("java.lang.Double ") >= 0)
						method.invoke(obj, rs.getDouble(propertys[i]));
					else if (szFieldType.indexOf("java.lang.String ") > 0)
						method.invoke(obj, rs.getString(propertys[i]));
					else
						method.invoke(obj, rs.getObject(propertys[i]));
				}
				list.add(obj);
			}
		} catch (IllegalAccessException e) {
			SysPub.appLog("ERROR", "%s%s", szTmp, "执行失败!!");
			throw e;
		} catch (IllegalArgumentException e) {
			SysPub.appLog("ERROR", "%s%s", szTmp, "执行失败!!");
			throw e;
		} catch (InvocationTargetException e) {
			SysPub.appLog("ERROR", "%s%s", szTmp, "执行失败!!");
			throw e;
		} catch (NullPointerException e) {
			SysPub.appLog("ERROR", "%s%s", szTmp, "执行失败!!");
			throw e;
		} catch (ExceptionInInitializerError e) {
			SysPub.appLog("ERROR", "%s%s", szTmp, "执行失败!!");
			throw e;
		} catch (Exception e) {
			SysPub.appLog("ERROR", "数据库操作失败!!!");
			dbLog(e, sqlStr);
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (executer != null)
				executer.close();
		}
		return list;
	}

	/**
	 * 连接数据库连接池
	 * 
	 * @param sql
	 * @return 0
	 * @version 回滚事务
	 * @throws Exception
	 */
	public static DBExecuter conn() throws Exception {
		DataSource ds = DataBaseUtils.getDatasource();
		DBExecuter executer = new DBExecuter(ds, "", false);
		try {
			SysPub.appLog("DEBUG", "连接数据库");
			executer.rollback();
		} catch (Exception e) {
			SysPub.appLog("ERROR", "连接数据库失败");
			e.printStackTrace();
			throw e;
		}
		return executer;
	}

	/**
	 * 执行更新、插入、删除语句
	 * 
	 * @param sql
	 * @return 执行成功的条数
	 * @version 更新数据--不提交事务
	 * @throws Exception
	 */
	public static Integer executenotr(DBExecuter _executer, String sql, Object[] value){
		PreparedStatement pstmt = null;
		Integer rtn = -1;
		try {
			SysPub.appLog("DEBUG", "start execute update or insert sql:" + sql);
			pstmt = (PreparedStatement) _executer.bind(sql);
			// 20170611增加动态查询
			if (value != null && value.length > 0) {
				int ilen = value.length;
				for (int i = 1; i <= ilen; i++) {
					SysPub.appLog("DEBUG", "value:%s", value[i - 1]);
					if (value[i - 1] == null) {
						pstmt.setObject(i, "");
					} else {
						pstmt.setObject(i, value[i - 1]);
					}
				}
			}
			rtn = pstmt.executeUpdate();
		} catch (Exception e) {
			// SysPub.appLog("ERROR", "execute error! sql:" + sql);
			// e.printStackTrace();
			try {
				dbLog(e, sql);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rtn;
	}
	
	/**
	 * 执行更新、插入、删除语句
	 * 
	 * @param sql
	 * @return 执行成功的条数
	 * @version 更新数据--自动提交事务
	 * @throws Exception
	 */
	public static Integer executenotr(String sql, Object[] value) throws Exception {
		// 取数据源
        DataSource dataSource = getDatasource();
        Connection connection = null;
        PreparedStatement pstmt = null;
		Integer rtn = -1;
		try {
			SysPub.appLog("DEBUG", "start execute update or insert sql:" + sql);
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sql);
			// 20170611增加动态查询
			if (value != null && value.length > 0) {
				int ilen = value.length;
				for (int i = 1; i <= ilen; i++) {
					SysPub.appLog("DEBUG", "value:%s", value[i - 1]);
					if (value[i - 1] == null) {
						pstmt.setObject(i, "");
					} else {
						pstmt.setObject(i, value[i - 1]);
					}
				}
			}
			rtn = pstmt.executeUpdate();
		} catch (Exception e) {
			// SysPub.appLog("ERROR", "execute error! sql:" + sql);
			// e.printStackTrace();
			dbLog(e, sql);
			throw e;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rtn;
	}

	/**
	 * 回滚事务
	 * 
	 * @param sql
	 * @return 0
	 * @version 回滚事务
	 * @throws Exception
	 */
	public static Integer rollback(DBExecuter _executer) throws Exception {
		try {
			SysPub.appLog("DEBUG", "回滚事务");
			_executer.rollback();
		} catch (Exception e) {
			SysPub.appLog("ERROR", "回滚事务失败");
			e.printStackTrace();
			throw e;
		} finally {
			if (_executer != null)
				_executer.close();
		}
		return 0;
	}

	/**
	 * 提交事务
	 * 
	 * @param sql
	 * @return 0
	 * @version 提交事务
	 * @throws Exception
	 */
	public static Integer commit(DBExecuter _executer) throws Exception {
		try {
			SysPub.appLog("DEBUG", "提交事务");
			_executer.commit();
		} catch (Exception e) {
			SysPub.appLog("ERROR", "提交事务失败");
			e.printStackTrace();
			throw e;
		} finally {
			if (_executer != null)
				_executer.close();
		}
		return 0;
	}

	/**
	 * 数据库日志
	 * 
	 * @param e
	 * @param sql
	 * @param values
	 * @throws Exception
	 */
	private static void dbLog(Exception e, String sql) throws Exception {
		// SysPub.appLog("ERROR", "数据库访问错误,执行sql:%s;", sql);
		TrcLog.error("db.log", "数据库访问错误,执行sql:%s;", sql);
		if (e != null) {
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			if (stackTraceElements != null && stackTraceElements.length > 0) {
				int _length = stackTraceElements.length;
				for (int i = 0; i < _length; i++) {
					// SysPub.appLog("ERROR", "%s",
					// stackTraceElements[i].toString());
					TrcLog.error("db.log", "%s", stackTraceElements[i].toString());
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	}
}