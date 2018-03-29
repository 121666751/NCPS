package com.union.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class UnionRequest implements UnionMessage {
	
	private Map<String, byte[]> head;
	private Map<String, byte[]> body;
	
	private final static String DEFAULT_ENCODING = "GBK";
	
	private static Logger logger = Logger.getLogger(UnionRequest.class);
	
	private static byte[] reqLen = { 0, 0 };
	private static byte[] req = "V0012".getBytes();
	private static byte[] reqHead = "04head".getBytes();
	private static byte[] reqBody = "04body".getBytes();
	
	public UnionRequest() {
		head = new HashMap<String, byte[]>();
		body = new HashMap<String, byte[]>();
	}
	
	public void putHeadField(String key, String value) {
		if(value == null || value.equals("")) {
			return;
		}
		try {
			head.put(key, value.getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			logger.error("不支持" + DEFAULT_ENCODING + "字符编码", e);
		}
	}
	
	public void putBodyField(String key, String value) {
		if(value == null || value.equals("")) {
			return;
		}
		try {
			body.put(key, value.getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			logger.error("不支持" + DEFAULT_ENCODING + "字符编码", e);
		}
	}
	
	public void putBodyField(String key, int value) {
		putBodyField(key, "" + value);
	}
	
	public String getHeadField(String key) {
		byte[] value = head.get(key);
		if(value != null) {
			try {
				return new String(value, DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				logger.error("不支持" + DEFAULT_ENCODING + "字符编码", e);
			}
		}
		return null;
	}

	public String getBodyField(String key) {
		byte[] value = body.get(key);
		if(value != null) {
			try {
				return new String(value, DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				logger.error("不支持" + DEFAULT_ENCODING + "字符编码", e);
			}
		}
		return null;
	}
	
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(reqLen);
			os.write(req);
			writeTwoLen(os, head.size());
			os.write(reqHead);
			writeTwoLen(os, body.size());
			os.write(reqBody);
			writeMap(os, head);
			writeMap(os, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}
	
	private void writeTwoLen(ByteArrayOutputStream os, int len) {
		os.write(len / 10 + 48);
		os.write(len % 10 + 48);
	}
	
	private void writeFourLen(ByteArrayOutputStream os, int len) {
		os.write(len / 1000 + 48);
		os.write(len % 1000 / 100 + 48);
		os.write(len % 100 / 10 + 48);
		os.write(len % 10 + 48);
	}
	
	private void writeMap(ByteArrayOutputStream os, Map<String, byte[]> map) throws IOException {
		Set<String> keys = map.keySet();
		byte[] value;
		for(String key : keys) {
			writeTwoLen(os, key.length());
			os.write(key.getBytes());
			value = map.get(key);
			writeFourLen(os, value.length);
			os.write(value);
		}
	}

}
