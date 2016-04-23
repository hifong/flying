package com.flying.framework.remote;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.flying.common.util.Utils;
import com.flying.framework.application.Application;

public abstract class RemoteServiceInvokerFactory {
	private static final Map<String, RemoteServiceInvoker> invokers = Utils.newHashMap();
	
	public static RemoteServiceInvoker getRemoteServiceInvoker(String type) throws Exception {
		if(invokers.containsKey(type))
			return invokers.get(type);
		
		String className = Application.getInstance().getConfigValue("remoting", type);
		if(StringUtils.isEmpty(className))
			className = Application.getInstance().getConfigValue("remoting", "hessian");
		return (RemoteServiceInvoker)Class.forName(className).newInstance();
	}
}
