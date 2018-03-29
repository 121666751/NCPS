package com.union.message;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.union.error.UnionError;

public class UnionResponse implements UnionMessage {
	
	private Map<String, String> head;
	private Map<String, String> body;
	
	private final static String DEFAULT_ENCODING = "GBK";
	
	public UnionResponse() {
		head = new HashMap<String, String>();
		body = new HashMap<String, String>();
	}
	
	public void putHeadField(String key, String value) {
		head.put(key, value);
	}
	
	public void putBodyField(String key, String value) {
		body.put(key, value);
	}
	
	public void putBodyField(String key, int value) {
		putBodyField(key, "" + value);
	}
	
	public String getHeadField(String key) {
		return head.get(key.toLowerCase());
	}
	
	public String getBodyField(String key) {
		return body.get(key.toLowerCase());
	}
	
	public static UnionResponse parse(byte[] data) throws UnsupportedEncodingException {
		UnionResponse resp = new UnionResponse();
		
		int nodeCounts = data[4] - 48;
		int headNodeCounts = (data[5] - 48) * 10 + (data[6] - 48);
		
		int offset = 0;
		if(nodeCounts == 1) {
			offset = 13;
		} else {
			offset = 21;
		}
		
		String key, value;
		int keyLen, valueLen;
		for(int i = 0; i < headNodeCounts; i++) {
			keyLen = (data[offset++] - 48) * 10 
				   + (data[offset++] - 48);
			key = new String(data, offset, keyLen);
			offset += keyLen;
			valueLen = (data[offset++] - 48) * 1000
					 + (data[offset++] - 48) * 100
					 + (data[offset++] - 48) * 10
					 + (data[offset++] - 48);
			value = new String(data, offset, valueLen, DEFAULT_ENCODING);
			offset += valueLen;
			resp.putHeadField(key.toLowerCase(), value);
		}
		if(nodeCounts == 2) {
			int bodyNodeCounts = Integer.parseInt(new String(data, 13, 2));  // body的子节点个数
			for(int i = 0; i < bodyNodeCounts; i++) {
				keyLen = (data[offset++] - 48) * 10 + (data[offset++] - 48);
				key = new String(data, offset, keyLen);
				offset += keyLen;
				valueLen = (data[offset++] - 48) * 1000
						 + (data[offset++] - 48) * 100
						 + (data[offset++] - 48) * 10
						 + (data[offset++] - 48);
				value = new String(data, offset, valueLen, DEFAULT_ENCODING);
				offset += valueLen;
				resp.putBodyField(key.toLowerCase(), value);
			}
		}
		return resp;
	}
	
	public byte[] toByteArray() {
		return null;
	}
	
	public static UnionResponse newFailInstance(UnionError err) {
		return newFailInstance(err.getCode(), err.getMessage());	
	}
	
	public static UnionResponse newFailInstance(String errorCode, String errorMessage) {
		UnionResponse resp = new UnionResponse();
		resp.putHeadField("responsecode", errorCode);
		resp.putHeadField("responseremark", errorMessage);
		return resp;
	}

}
