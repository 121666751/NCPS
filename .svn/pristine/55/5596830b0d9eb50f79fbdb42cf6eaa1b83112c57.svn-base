package com.adtec.ncps.busi.chnl.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.MemoryUnit;

public class EhcacheUtil {

	private CacheManager manager;

	private static EhcacheUtil ehCache;


	private EhcacheUtil() {

		Configuration con = new Configuration();

		CacheConfiguration cache = new CacheConfiguration().name("MapCache");// 创建一个叫MapCache的缓存
		//cache.maxElementsInMemory(200);
		//cache.setMaxElementsInMemory(1000);
		cache.maxBytesLocalHeap(100, MemoryUnit.MEGABYTES);
		//cache.setMaxElementsInMemory(150);
        //cache.setTimeToLiveSeconds(0);   //永久有效
        //cache.setTimeToIdleSeconds(0);   //永久有效
        cache.setOverflowToDisk(false);
        cache.setEternal(true);          //永久有效
        DiskStoreConfiguration diskStore = new DiskStoreConfiguration();
        diskStore.setPath("java.io.tmpdir");
        
        con.addDiskStore(diskStore);
		con.addCache(cache);
		CacheConfiguration cache1 = new CacheConfiguration().name("RetCache");// 创建一个叫MapCache的缓存
		//cache.maxElementsInMemory(200);
		//cache.setMaxElementsInMemory(1000);
		cache1.maxBytesLocalHeap(100, MemoryUnit.MEGABYTES);
		//cache.setMaxElementsInMemory(150);
        //cache.setTimeToLiveSeconds(0);   //永久有效
        //cache.setTimeToIdleSeconds(0);   //永久有效
		cache1.setOverflowToDisk(false);
		cache1.setEternal(true);          //永久有效
		con.addCache(cache1);

		manager = new CacheManager(con);

	}

	public static EhcacheUtil getInstance() {
		if (ehCache == null) {
			ehCache = new EhcacheUtil();
		}
		return ehCache;
	}

	public void put(String cacheName, String key, Object value) {
		Cache cache = manager.getCache(cacheName);
		Element element = new Element(key, value);
		cache.put(element);
	}

	public Object get(String cacheName, String key) {
		Cache cache = manager.getCache(cacheName);
		Element element = cache.get(key);
		return element == null ? null : element.getObjectValue();
	}

	public Cache get(String cacheName) {
		return manager.getCache(cacheName);
	}

	public void remove(String cacheName, String key) {
		Cache cache = manager.getCache(cacheName);
		cache.remove(key);
	}

	public static void main(String[] args) {
		ehCache.getInstance();
		ehCache.put("MapCache", "name", "wangxb");
		ehCache.put("MapCache", "name2", "wangxb2");
		
	

		String szName = (String) ehCache.get("MapCache", "name");
		String szName2 = (String) ehCache.get("MapCache", "name2");
		System.out.println(szName+"["+szName2);
	}

}
