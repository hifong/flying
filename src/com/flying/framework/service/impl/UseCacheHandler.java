package com.flying.framework.service.impl;

import org.apache.log4j.Logger;

import com.flying.common.helper.SequenceHelper;
import com.flying.framework.annotation.UseCache;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class UseCacheHandler implements ServiceHandler<UseCache> {
	private final static Logger log = Logger.getLogger(UseCacheHandler.class);

	@Override
	public void handle(UseCache annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		
		final String tag = annotation.tag().startsWith("$")? request.getString(annotation.tag().substring(1), annotation.tag()) : annotation.tag();
		final String[] keys = annotation.keys();
		StringBuffer cacheKey = new StringBuffer(tag).append("-").append(context.getServiceId()).append("-").append(context.getMethodName()).append("-");
		if (keys != null) {
			for (String k : keys) {
				if(!request.contains(k)) continue;
				if (cacheKey.length() != 0) cacheKey.append(".");
				cacheKey.append(request.getString(k, k));
			}
		}
		if (module.getCache() != null && annotation.versionable()) {	//get version according tag
			final String verKey = tag + ":VERSION$";
			String ver = (String) module.getCache().get(verKey);
			if (ver == null) {
				ver = String.valueOf(SequenceHelper.nextVal(verKey));
				module.getCache().put(verKey, ver);
			}
			cacheKey.append(":").append(ver);
		}
		// read cache
		if (module.getCache() != null) {
			Object value = module.getCache().get(cacheKey.toString());
			if (value != null) {
				context.setResult((Data) value);
				log.debug(context.getServiceId() + "." + context.getMethodName() + " Get data from cache, key[" + cacheKey + "]");
				return;
			}
		}
		// cache no data,continue
		context.doChain(request);
		// write cache
		if (module.getCache() != null) {
			module.getCache().put(cacheKey.toString(), context.getResult());
			log.debug(context.getServiceId() + "." + context.getMethodName() + " Set data to cache,key[" + cacheKey + "]");
		}
	}

}
