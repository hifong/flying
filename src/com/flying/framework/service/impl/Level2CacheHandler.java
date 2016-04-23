package com.flying.framework.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.flying.common.util.Utils;
import com.flying.framework.annotation.UseCache;
import com.flying.framework.data.Data;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class Level2CacheHandler implements ServiceHandler<UseCache> {
	private final static Logger log = Logger.getLogger(Level2CacheHandler.class);
	private final static ThreadLocal<Map<String, Data>> cache = new ThreadLocal<Map<String, Data>>();

	@Override
	public void handle(UseCache annotation, Data request, ServiceHandlerContext context) throws Exception {
		String[] keys = annotation.keys();
		StringBuffer cacheKey = new StringBuffer(annotation.tag());
		if (keys != null) {
			for (String k : keys) {
				if (cacheKey.length() != 0)
					cacheKey.append(".");
				cacheKey.append(request.getString(k, k));
			}
		}
		// read cache
		if (cache.get() != null && cache.get().containsKey(cacheKey.toString())) {
			context.setResult(cache.get().get(cacheKey.toString()));
			log.debug("Level2CacheHandler,Get data from cache, key[" + cacheKey + "]");
		} else {
			context.doChain(request);
			if (cache.get() == null) {
				final Map<String, Data> map = Utils.newHashMap();
				cache.set(map);
			}
			cache.get().put(cacheKey.toString(), context.getResult());
			log.debug("Level2CacheHandler,Set data to cache,key[" + cacheKey + "]");
		}
	}

}
