package com.union.connect;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.ObjectPool;

import com.union.interfaces.UnionServer;

/**
 * 连接池组
 * 
 * @author wu
 * @date 2016-04-27
 * @version 1.0
 */
public class UnionPoolGroup {
	
	private final static Map<UnionServer, ObjectPool<UnionSocket>> poolGroup;
	
	static {
		poolGroup = new HashMap<UnionServer, ObjectPool<UnionSocket>>();
	}
	
	
	public static ObjectPool<UnionSocket> getPool(UnionServer key) {
		return poolGroup.get(key);
	}
	
	
	public static ObjectPool<UnionSocket> addPool(UnionServer key, ObjectPool<UnionSocket> value) {
		removePool(key);
		return poolGroup.put(key, value);
	}
	
	
	public static void removePool(UnionServer key) {
		ObjectPool<UnionSocket> op = poolGroup.get(key);
		if(op != null) {
			try {
				op.close();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
