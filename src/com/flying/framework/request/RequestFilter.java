package com.flying.framework.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.framework.config.ServiceConfig;
import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 * 
 */
public interface RequestFilter {
	public ServiceConfig getServiceConfig();

	public boolean isMapping(String uri);

	public void doFilter(LocalModule module, HttpServletRequest req, HttpServletResponse resp, RequestFilterChain chain) throws Exception;
}
