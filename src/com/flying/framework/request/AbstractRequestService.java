package com.flying.framework.request;

import com.flying.framework.config.ServiceConfig;
import com.flying.framework.module.LocalModule;

public abstract class AbstractRequestService implements RequestService {
	protected LocalModule module;
	protected ServiceConfig serviceConfig;

	public LocalModule getModule() {
		return module;
	}

	public void setModule(LocalModule module) {
		this.module = module;
	}

	public ServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	public void setServiceConfig(ServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}
}
