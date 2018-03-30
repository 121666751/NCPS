package com.adtec.tcp;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class  FixLenPackKit {

	/**
	 * 将pojo按照requestPacketFormatArray三维数组定义的格式生成固定格式报文
	 * @param requestPacketFormatArray
	 * @param pojo
	 * @param rightCharStr
	 * @param leftCharStr
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */
	public static String FixedLenPack(String[][][] requestPacketFormatArray,Object pojo,char rightCharStr,char leftCharStr,String charsetName) throws Exception{
		String fieldName = new String();
		String fieldLenght = new String();
		String parameterClass = new String();
		StringBuffer requestStr = new StringBuffer();
		int requestPacketFormatArrayLength = requestPacketFormatArray.length;
		for(int i=0;i<requestPacketFormatArrayLength;i++){
			String[][] requestFieldFormatArray = requestPacketFormatArray[i];
			int requestFieldFormatArrayLength = requestFieldFormatArray.length;
			if(requestFieldFormatArrayLength!=3){
				throw new Exception("invalid field defined");
			}
			for(int j=0;j<requestFieldFormatArrayLength;j++){
				String[] requestPropertyFormatArray = requestFieldFormatArray[j];
				int requestPropertyFormatArrayLength = requestPropertyFormatArray.length;
				if(requestPropertyFormatArrayLength!=2){
					throw new Exception("invalid field property defined");
				}
				if(requestPropertyFormatArray[0].equals("name"))
					fieldName = requestPropertyFormatArray[1];
				if(requestPropertyFormatArray[0].equals("length"))
					fieldLenght = requestPropertyFormatArray[1];
				if(requestPropertyFormatArray[0].equals("parameterClass"))
					parameterClass = requestPropertyFormatArray[1];
			}
			Class c = pojo.getClass();
			Method m = c.getMethod("get"  + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
			String fieldValue = m.invoke(pojo)==null?"":m.invoke(pojo).toString();
			if(parameterClass.equals("java.lang.String"))
				requestStr.append(formatFieldByRightChar(fieldValue,Integer.parseInt(fieldLenght),rightCharStr,charsetName));
			else{
				requestStr.append(formatFieldByLeftChar(fieldValue,Integer.parseInt(fieldLenght),leftCharStr,charsetName));
			}
			
		}
		return requestStr.toString();
	}
	/**
	 * 将strByte字符串按照requestPacketFormatArray三维数组定义的格式生pojo
	 * @param requestPacketFormatArray
	 * @param pojo
	 * @param strByte
	 * @param charsetName
	 * @throws Exception
	 */
	public static void fixedLengthPacket(String[][][] requestPacketFormatArray,Object pojo,String str,String charsetName) throws Exception{
		String fieldName = new String();
		String fieldLenght = new String();
		String parameterClass = new String();
		StringBuffer requestStr = new StringBuffer();
		byte[] strByte = str.getBytes(charsetName);
		int index = 0;
		int length = strByte.length;
		int requestPacketFormatArrayLength = requestPacketFormatArray.length;
		for(int i=0;i<requestPacketFormatArrayLength;i++){
			String[][] requestFieldFormatArray = requestPacketFormatArray[i];
			int requestFieldFormatArrayLength = requestFieldFormatArray.length;
			if(requestFieldFormatArrayLength!=3){
				throw new Exception("invalid field defined");
			}
			for(int j=0;j<requestFieldFormatArrayLength;j++){
				String[] requestPropertyFormatArray = requestFieldFormatArray[j];
				int requestPropertyFormatArrayLength = requestPropertyFormatArray.length;
				if(requestPropertyFormatArrayLength!=2){
					throw new Exception("invalid field property defined");
				}
				if(requestPropertyFormatArray[0].equals("name"))
					fieldName = requestPropertyFormatArray[1];
				if(requestPropertyFormatArray[0].equals("length"))
					fieldLenght = requestPropertyFormatArray[1];
				if(requestPropertyFormatArray[0].equals("parameterClass"))
					parameterClass = requestPropertyFormatArray[1];
			}
			Class c = pojo.getClass();
			int tempLenght = Integer.parseInt(fieldLenght);
			byte[] byteTemp = new byte[tempLenght];
			for(int k=0;k<tempLenght;k++)
					byteTemp[k] = strByte[index+k];
			index = index+tempLenght;
			if(parameterClass.equals("java.lang.String")){
				Method m = c.getMethod("set"  + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),Class.forName(parameterClass));
				m.invoke(pojo,new String(byteTemp).trim());
			}
			else if(parameterClass.equals("int")){
				Method m = c.getMethod("set"  + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),int.class);;
				//Method mArray[] = c.getDeclaredMethods();
				//for(Method mTemp:mArray)
				//	if(mTemp.getName().equals("set"  + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1)))
				//		m=mTemp;
				m.invoke(pojo,Integer.valueOf(new String(byteTemp)));
			}
			else if(parameterClass.equals("long")){
				Method m = null;
				Method mArray[] = c.getDeclaredMethods();
				for(Method mTemp:mArray)
					if(mTemp.getName().equals("set"  + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1)))
						m=mTemp;
				m.invoke(pojo,Long.valueOf(new String(byteTemp)));
			}
			else if(parameterClass.equals("float")){
				Method m = null;
				Method mArray[] = c.getDeclaredMethods();
				for(Method mTemp:mArray)
					if(mTemp.getName().equals("set"  + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1)))
						m=mTemp;
				m.invoke(pojo,Float.valueOf(new String(byteTemp)));
			}
		}
	}
	/**
	 * str右补charStr参数指定的字符，格式化为指定byte长度为size的字符串
	 * @param str 原字符串大小
	 * @param size byte长度
	 * @param charStr 补充字符
	 * *@param  charsetName
     * the name of a supported
     * {@link java.nio.charset.Charset </code>charset<code>}
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String formatFieldByRightChar(String str,int size,char charStr,String charsetName) throws UnsupportedEncodingException
	{
		byte[] bufferByte = new byte[size];
		for (int i = 0; i < bufferByte.length; i++)
		{
			bufferByte[i] = (byte) charStr;
		}
		byte[] strByte    = str.getBytes(charsetName); // 得到字符的字节数
		int strLength     = strByte.length;
		System.arraycopy(strByte, 0, bufferByte, 0, size>strLength?strLength:size);
		return new String(bufferByte);
	}
	public static String formatFieldByRightChar(String str,int size,String charsetName) throws UnsupportedEncodingException
	{
		return formatFieldByRightChar(charsetName, size, ' ', charsetName);
	}
	/**
	 * str左补charStr参数指定的字符，格式化为指定byte长度为size的字符串
	 * @param str 原字符串大小
	 * @param size byte长度
	 * @param charStr 补充字符
	 * @param  charsetName
     * the name of a supported
     * {@link java.nio.charset.Charset </code>charset<code>}
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String formatFieldByLeftChar(String str,int size,char charStr,String charsetName) throws UnsupportedEncodingException
	{
		byte[] bufferByte = new byte[size];
		for (int i = 0; i < bufferByte.length; i++)
		{
			bufferByte[i] = (byte) charStr;
		}
		byte[] strByte    = str.getBytes(charsetName);
		int strLength     = strByte.length;
		System.arraycopy(strByte, 0, bufferByte, (size-strLength)>0?(size-strLength):0, (size-strLength)>0?strLength:size);
		return new String(bufferByte);
	}
	/**
	 * @param str
	 * @param size
	 * @param charsetName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String formatFieldByLeftChar(String str,int size,String charsetName) throws UnsupportedEncodingException
	{
		return formatFieldByLeftChar(str,size,'0',charsetName);
	}
	
	public static void main(String args[]){
		class TestInvode{
			String cardno;
			String name;
			int num;
			float amount;
			public String getCardno() {
				return cardno;
			}
			public void setCardno(String cardno) {
				this.cardno = cardno;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public int getNum() {
				return num;
			}
			public void setNum(int num) {
				this.num = num;
			}
			public float getAmount() {
				return amount;
			}
			public void setAmount(float amount) {
				this.amount = amount;
			}
		}
		String requestFormat[][][] = 
		 {
					{
						{"name","cardno"},{"length","16"},{"parameterClass","java.lang.String"}
					},
					{
						{"name","name"},{"length","10"},{"parameterClass","java.lang.String"}
					}
					,
					{
						{"name","num"},{"length","10"},{"parameterClass","int"}
					},
					{
						{"name","amount"},{"length","10"},{"parameterClass","float"}
					}
		 };
		TestInvode ti = new TestInvode();
		ti.cardno = "4512";
		ti.name="不告诉你";
		ti.num = 95;
		ti.amount=45454.44f;
		try {
			System.out.println("######"+FixedLenPack(requestFormat, ti, ' ', '0', "gb2312"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String str = "4512            不告诉你  00000000950045454.44";
		TestInvode tii = new TestInvode();
		try {
			fixedLengthPacket(requestFormat, tii, str,"gb2312");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("######tii.cardno:"+tii.cardno);
		System.out.println("######tii.name:"+tii.name);
		System.out.println("######tii.num:"+tii.num);
		String s = "人";
		try {
			System.out.println(s.getBytes("utf-8").length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getContextClassLoader());
		System.out.println(tii.getClass().getClassLoader());
		System.out.println(ClassLoader.getSystemClassLoader());
	}
}
