package com.flying.framework.service.impl;

import org.apache.log4j.Logger;

import com.flying.common.helper.SequenceHelper;
import com.flying.framework.annotation.RemoveCache;
import com.flying.framework.cache.Removable;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class RemoveCacheHandler implements ServiceHandler<RemoveCache>{
	private final static Logger log = Logger.getLogger(RemoveCacheHandler.class);

	@Override
	public void handle(RemoveCache annotation, Data request, ServiceHandlerContext context)
					throws Exception {
		final LocalModule module = context.getModule();
		try {
			context.doChain(request);
		} finally {
			if(module.getCache() == null) return;

			final String tag = annotation.tag().startsWith("$")? request.getString(annotation.tag().substring(1), annotation.tag()) : annotation.tag();
			final String[] keys = annotation.keys();
			
			StringBuffer cacheKey = new StringBuffer(tag);
			if(keys != null) {
				for(String k: keys) {
					if(!request.contains(k)) continue;
					if(cacheKey.length() != 0) cacheKey.append(".");
					cacheKey.append(request.getString(k, k));
				}
			}
			if(annotation.versionable()) {
				final String verKey = tag + ":VERSION$";
				final String oldVer = module.getCache().get(verKey);
				cacheKey.append(oldVer);
				//
				String newVer = String.valueOf(SequenceHelper.nextVal(verKey));
				module.getCache().put(verKey, newVer);
				log.debug("RemoveCache new cacheKey is:" + cacheKey);
			}
			if(module.getCache() instanceof Removable) {
				((Removable)module.getCache()).remove(cacheKey.toString());
				log.debug("RemoveCache remove key:" + cacheKey);
			}
		}
	}

}
