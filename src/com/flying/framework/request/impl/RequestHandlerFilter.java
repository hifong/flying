package com.flying.framework.request.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestFilter;
import com.flying.framework.request.RequestFilterChain;

/**
 * @author wanghaifeng
 *
 */
public class RequestHandlerFilter extends AbstractRequestFilter {

	public void doFilter(LocalModule module, HttpServletRequest req, HttpServletResponse resp, 
			RequestFilterChain chain) throws Exception {
		String[] uris = (String[])req.getAttribute("$URIS");
		String requestType = (String)req.getAttribute("$REQUEST_TYPE");
		
		module.getRequestService(requestType).service(module, uris, req, resp);
		
	}

}
