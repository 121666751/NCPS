package com.adtec.ncps.busi.ncp.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.adtec.ncps.busi.ncp.SysPub;

public class BaseDaoTool {

	/**
	 * 根据给定的类 clazz 取其属性名称 
	 * @param clazz
	 * @return 返回对象的所有属性名称数组
	 */
	public static String[] getPropertyNames(Class<? extends Object> clazz){
		String[] propertys = null;
		Field []fields = clazz.getDeclaredFields();
		if( fields != null && fields.length > 0 )
		{
			int propertysNum =  fields.length;
			propertys = new String[propertysNum];
			for( int i = 0; i < propertysNum; i++)
			{
				propertys[i] = fields[i].getName();
				//System.out.println("propertys:" + propertys[i]);
			}
		}
		return propertys;
	}

	/**
	 * 根据给定的类 clazz和属性取其方法
	 * @param clazz
	 * @return 返回对象的get方法
	 * @throws Exception 
	 */
	public static Method getPropertyGETMethod(Class<? extends Object>clazz,String propertyName) throws Exception
	{
		String  methodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
		Method method = null;
		try {
			method = clazz.getMethod(methodName, null);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			String message = "方法" + methodName + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			String message = "方法" + methodName + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		}
		return method;
	}
	
	/**
	 * 根据给定的类 clazz和属性取其set方法
	 * @param clazz
	 * @return 返回对象的set方法
	 * @throws Exception 
	 */
	public static Method getPropertySETMethod(Class<? extends Object>clazz,String propertyName) throws Exception
	{
		String  methodName = "set" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
		Method method = null;
		try {
			Field field = clazz.getDeclaredField(propertyName);
			method = clazz.getMethod(methodName,field.getType());
			SysPub.appLog("TRACE","method:%s",method.getName());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			String message = "方法" + methodName + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			String message = "方法" + methodName + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		}
		return method;
	}
	/**
	 * 根据给定的对象，调用get方法取值
	 * @param clazz
	 * @return 执行object对象的get方法返回的值
	 * @throws Exception 
	 */
	public static Object invokeGETMethod(Object object,Method method) throws Exception{
		Object value = null;
		try {
			value = method.invoke(object, null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			String message = "方法" + method.getName() + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			String message = "方法" + method.getName() + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			String message = "方法" + method.getName() + "找不到!";
			SysPub.appLog("ERROR",message);
			e.printStackTrace();
		}
		return value;
	}
	/**
	 * 根据给定的对象，取其对象所有值 
	 * @param clazz
	 * @return 调用对象的get方法，返回对象的所有属性值
	 * @throws Exception 
	 */
	public static Object[] getPropertyValues(Object object) throws Exception{
		if( object == null)
			return null;
		String[] propertys = getPropertyNames(object.getClass());
		if( propertys ==  null )
			return null;
		int propertyNum = propertys.length;
		Method method = null;
		Object []propertysValue = new Object[propertyNum];
		for(int i = 0; i<propertyNum; i++)
		{
			method = getPropertyGETMethod(object.getClass(),propertys[i]);
			propertysValue[i] = invokeGETMethod(object, method);
			//System.out.println(propertysValue[i]);
		}
		return propertysValue;	
	}
	/**
	 * 根据给定的对象实例object,取其属性名称为propertyName的值,必须有相应的propertyName get方法
	 * @param object 对象实例
	 * @param propertyName 属性名称
	 * @return 调用对象的get方法，返回对象的属性值
	 * @throws Exception 
	 */
	public static Object getPropertyValue(Object object,String propertyName) throws Exception{
		Class<? extends Object> clazz = object.getClass();
		Method method = BaseDaoTool.getPropertyGETMethod(clazz, propertyName);
		Object propertyValue = invokeGETMethod(object,method);
		return propertyValue;
	}
	/**
	 *	把字符串,根据","解成字符串数组,放进集合Set
	 * @param primaryKey 
	 * @return 字符串集合
	 */
	public static Set<String> primaryKeyToPropertyNames(String primaryKey){
		if(primaryKey!=null){
			Set<String> primaryKeys = new HashSet<String>();
			primaryKey = primaryKey.replaceAll(" ", "");
			String[] s = primaryKey.split(",", 0);
			int length = s.length;
			for(int i=0;i<length;i++){
				if(!s[i].equals("")){
					primaryKeys.add(s[i]);
				}
			}
			return primaryKeys;
		}
		return null;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
