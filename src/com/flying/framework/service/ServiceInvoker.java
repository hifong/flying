package com.flying.framework.service;

import com.flying.framework.application.Application;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;

public abstract class ServiceInvoker {
	public static Data invoke(String serviceModuleId, String serviceId, Data request) {
		if(serviceModuleId == null) serviceModuleId = ThreadContext.getContext().getModule().getId();
		return Application.getInstance().getModules().getLocalModule(serviceModuleId).invoke(serviceId, request);
	}

	public static Data invoke(String moduleId, String serviceModuleId, String serviceId, Data request) {
		if(serviceModuleId == null) serviceModuleId = moduleId;
		if(serviceModuleId == null) serviceModuleId = ThreadContext.getContext().getModule().getId();
		return Application.getInstance().getModules().getLocalModule(serviceModuleId).invoke(serviceId, request);
	}
}
