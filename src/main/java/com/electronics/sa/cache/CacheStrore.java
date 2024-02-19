package com.electronics.sa.cache;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheStrore<T> {
	
	private Cache<String, T> cache;

	public CacheStrore(Duration expiry) {
		
		this.cache = CacheBuilder.newBuilder()
				.expireAfterWrite(expiry)
				.concurrencyLevel(Runtime.getRuntime().availableProcessors())
				.build();
	}
	
	public void add( String key , T value) {
		cache.put(key, value);
	}
	
	public T get(String key) {
		return cache.getIfPresent(key);
	}
	public String remove(String key) {
		 cache.invalidate(key);
		 return "SuccessFully Removed!!";
	}

}
