package com.flying.framework.service;

import com.flying.framework.config.ServiceConfig;
import com.flying.framework.module.LocalModule;

public abstract class AbstractService {
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

	public void setServiceConfig(ServiceConfig config) {
		this.serviceConfig = config;
	}
	
}
