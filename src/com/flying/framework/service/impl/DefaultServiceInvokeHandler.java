package com.flying.framework.service.impl;

import com.flying.framework.annotation.DefaultServiceInvokeAnnotation;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;
import com.flying.framework.util.LogUtil;

public class DefaultServiceInvokeHandler implements ServiceHandler<DefaultServiceInvokeAnnotation> {

	@Override
	public void handle(DefaultServiceInvokeAnnotation annotation, Data request, ServiceHandlerContext context) throws Exception {
		LocalModule currentModule = ThreadContext.getContext().getModule();
		Principal p = ThreadContext.getContext().getPrincipal();
		long start = System.currentTimeMillis();
		Exception ex = null;
		try {
			context.doChain(request);
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			long end = System.currentTimeMillis();
			LogUtil.invokeLog(p, "Local", currentModule == null ? "" : currentModule.getId(), context.getModule().getId(), context.getServiceId(), context.getMethodName(),
							(end - start), request, context.getResult(), ex);
		}
	}

}
