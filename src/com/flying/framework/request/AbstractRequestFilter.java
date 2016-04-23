package com.flying.framework.request;

import java.util.regex.Pattern;

import com.flying.framework.config.ServiceConfig;
import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 *
 */
public abstract class AbstractRequestFilter implements RequestFilter {
	protected LocalModule module;
	protected ServiceConfig serviceConfig;
	protected Pattern pattern;
	
	private synchronized Pattern getPattern() {
		if(this.pattern != null) return this.pattern;
		String mapping = (serviceConfig == null?null:serviceConfig.getConfig("mapping"));
		this.pattern = (mapping == null?null:Pattern.compile(mapping));
		return this.pattern;
	}
	
	public boolean isMapping(String uri) {
		if(this.getPattern() == null)
			return true;
		else
			return this.pattern.matcher(uri).find();
	}
	
	public void setModule(LocalModule module) {
		this.module = module;
	}
	
	public void setServiceConfig(ServiceConfig config) {
		this.serviceConfig = config;
	}
	
	public ServiceConfig getServiceConfig() {
		return this.serviceConfig;
	}
}
