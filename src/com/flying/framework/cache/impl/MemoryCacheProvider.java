package com.flying.framework.cache.impl;

import com.flying.framework.cache.Cache;
import com.flying.framework.cache.CacheProvider;
import com.flying.framework.module.LocalModule;

public class MemoryCacheProvider implements CacheProvider {

	@Override
	public synchronized Cache getCache(LocalModule module) {
		return new MemoryCache(module);
	}

}
