package com.union.message;

/**
 * 获取具体数据之前请调用 {@link UnionMessage#getHeadField(String)} 方法获取状态信息：
 * 		通过 {@code string = responseCode} 获取响应码信息，
 * 		通过 {@code string = responseRemark}。
 * 
 *	调用 {@link UnionMessage#getBodyField(String)} 方法获取具体的响应信息，
 *	其中 {@code string} 在各服务注释中说明，若没有则说该服务只返回状态信息，没有返回响应信息。
 *
 * @author admi
 */
public interface UnionMessage {
	
	public void putHeadField(String key, String value);
	
	public void putBodyField(String key, String value);
	
	public void putBodyField(String key, int value);
	
	public String getHeadField(String key);
	
	public String getBodyField(String key);
	
	public byte[] toByteArray();

}
