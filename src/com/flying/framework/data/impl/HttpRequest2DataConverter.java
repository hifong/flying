package com.flying.framework.data.impl;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.flying.common.util.Constants;
import com.flying.common.util.StringUtils;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataConverter;
import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 * 
 */
public class HttpRequest2DataConverter implements DataConverter<HttpServletRequest> {
	protected LocalModule module;
	protected ServiceConfig serviceConfig;
	
	public void setModule(LocalModule module) {
		this.module = module;
	}
	
	public void setServiceConfig(ServiceConfig config) {
		this.serviceConfig = config;
	}
	
	@Override
	public Data convert(HttpServletRequest request) {
		Data req = (Data)request.getAttribute(Constants.REQUEST_UID);
		if(req != null) {
			return req;
		} else {
			req = new Data();
			request.setAttribute(Constants.REQUEST_UID, req);
		}
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			String[] values = request.getParameterValues(name);
			if(values != null && values.length == 1 && StringUtils.isEmpty(values[0])) continue;
			if(name.endsWith("[]")) name = name.substring(0, name.length() - 2);
			
			if(values != null && values.length == 1) {
				req.put(name, values[0]);
			} else {
				req.put(name, values);
			}
		}
		
//		req.put("$.ContextPath", request.getContextPath());
//		req.put("$.ContentType",request.getContentType());
//		req.put("$.RemoteAddr",request.getRemoteAddr());
//		req.put("$.RemoteHost",request.getRemoteHost());
//		req.put("$.RemotePort",request.getRemotePort());
//		req.put("$.Method",request.getMethod());
//		req.put("$.SessionId",request.getSession().getId());
//		req.put("$.ContentLength",request.getContentLength());
//		String uri = request.getRequestURI();
//		if(uri.startsWith("//")){
//			uri = uri.substring(1);
//		}
//		req.put("RequestURI",uri);
//		req.put("RequestURL",request.getRequestURL().toString());
		
//		int start = uri.lastIndexOf("/") + 1;
//		int end = uri.lastIndexOf("?");
//		if(end<=0){
//			end = uri.length();
//		}
//		String filename = uri.substring(start, end);
//		req.put("Filename", filename);
		//
//		final Data headers = new Data();
//		names = request.getHeaderNames();
//		while(names.hasMoreElements()) {
//			String name = names.nextElement();
//			String values = request.getHeader(name);
//			if(name.startsWith("$"))
//				headers.put(name, values);
//		}
//		req.put("$.headers", headers);
		
		return req;
	}
	
}
