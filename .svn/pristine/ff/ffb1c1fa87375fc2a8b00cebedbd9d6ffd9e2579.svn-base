package com.adtec.ncps.busi.chnl.utils;

import java.util.ArrayList;
import java.util.List;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.adtec.ncps.busi.chnl.bean.NetbankConf;

public class EhcacheUtil3 {

	private CacheManager manager;

	private static EhcacheUtil3 ehCache;


	private EhcacheUtil3() {

		manager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("MapCache", CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, List.class, ResourcePoolsBuilder.heap(100)).build())
				.build(true);

		Cache<String, List> preConfigured = manager.getCache("MapCache", String.class, List.class);

//		Cache<String, List> myCache = manager.createCache("myCache", CacheConfigurationBuilder
//				.newCacheConfigurationBuilder(String.class, List.class, ResourcePoolsBuilder.heap(100)).build());


		//manager.close();

	}

	public static EhcacheUtil3 getInstance() {
		if (ehCache == null) {
			ehCache = new EhcacheUtil3();
		}
		return ehCache;
	}

	public void put(String cacheName, String key, Object value) {
		Cache cache = manager.getCache(cacheName,String.class,List.class);
		
		cache.put(key, value);
	}

	public Object get(String cacheName, String key) {
		Cache cache = manager.getCache(cacheName,String.class,List.class);
		Object element = cache.get(key);
		return element == null ? null : element;
	}

	public Cache get(String cacheName) {
		return manager.getCache(cacheName,String.class,List.class);
	}

	public void remove(String cacheName, String key) {
		Cache cache = manager.getCache(cacheName,String.class,List.class);
		cache.remove(key);
	}

	public static void main(String[] args) {
		ehCache.getInstance();
		
		List tt = new ArrayList();
		NetbankConf e = new NetbankConf();
		tt.add(e);
		ehCache.put("MapCache", "name", tt);
		//ehCache.put("MapCache", "name2", "wangxb2");
		
	

		List tt1 =  (List) ehCache.get("MapCache", "name");
		System.out.println(tt1.size());
	}

}
