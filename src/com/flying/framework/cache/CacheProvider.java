package com.flying.framework.cache;

import com.flying.framework.module.LocalModule;

public interface CacheProvider {
	public Cache getCache(LocalModule module);
}
