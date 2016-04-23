package com.flying.framework.cache.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.flying.framework.cache.Cache;
import com.flying.framework.cache.Clearable;
import com.flying.framework.cache.Removable;
import com.flying.framework.data.Data;
import com.flying.framework.job.JobWorker;
import com.flying.framework.module.LocalModule;

public class MemoryCache implements Cache, Clearable, Removable{
	private final static long idleTimeout = 1000*60*5;
	private final Map<String, CacheEntry> map = new ConcurrentHashMap<String, CacheEntry>(100);
	private final LocalModule module;
	
	public MemoryCache(LocalModule module) {
		this.module = module;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		if(map.containsKey(key))
			return (T)map.get(key).cachedObject;
		else
			return null;
	}

	@Override
	public <T> void put(String key, T value) {
		map.put(key, new CacheEntry(value));
	}

	@Override
	public void clear() {
		map.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(String key) {
		if(!map.containsKey(key))
			return null;
		return (T)((CacheEntry)map.remove(key)).cachedObject;
	}

	public LocalModule getModule() {
		return module;
	}

	class CacheEntry {
		private final long creation = System.currentTimeMillis();
		private long reading = System.currentTimeMillis();
		private final AtomicLong count = new AtomicLong(0);
		
		private final Object cachedObject;
		CacheEntry(Object obj) {
			cachedObject = obj;
		}
		
		public long getCreation() {
			return this.creation;
		}
		
		public long getReading() {
			return this.reading;
		}
		
		public boolean isExpired() {
			return reading + idleTimeout < System.currentTimeMillis();
		}
		
		public Object read() {
			count.incrementAndGet();
			reading = System.currentTimeMillis();
			return cachedObject;
		}
	}
	
	class CacheExpiredJobWorker implements JobWorker {
		
		@Override
		public void work(LocalModule module, Data data) {
			for(Iterator<CacheEntry> it = map.values().iterator(); it.hasNext(); )
				if(it.next().isExpired()) it.remove();
		}

		@Override
		public int getInterval() {
			return 100 * 60;
		}
		
	}
}
