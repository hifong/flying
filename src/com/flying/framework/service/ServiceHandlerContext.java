package com.flying.framework.service;

import java.lang.annotation.Annotation;
import java.util.List;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceProxy.MethodParam;

@SuppressWarnings("rawtypes")
public class ServiceHandlerContext {
	
	private final LocalModule module;
	private final String serviceId;
	private final Object service;
	private final String methodName;
	private final List<ServiceHandler> annotationHandlers;
	private final List<Annotation> annotations;
	private final ServiceHandler serviceHandler;
	private final MethodParam[] methodParams;
	
	private int curHandlerIndex;
	private Data data;
	
	public ServiceHandlerContext(LocalModule module, Object service, String serviceId, String methodName, 
			List<Annotation> annotations, List<ServiceHandler> handlers, MethodParam[] params, ServiceHandler handler) {
		this.module = module;
		this.service = service;
		this.serviceId = serviceId;
		this.methodName = methodName;
		this.annotations = annotations;
		this.annotationHandlers = handlers;
		this.methodParams = params;
		this.serviceHandler = handler;
	}
	
	@SuppressWarnings("unchecked")
	public void doChain(Data request) throws Exception {
		curHandlerIndex ++;
		if(curHandlerIndex <= annotationHandlers.size() + 1) {
			ServiceHandler handler;
			if(curHandlerIndex <= annotationHandlers.size())
				handler = annotationHandlers.get(curHandlerIndex - 1);
			else
				handler = this.serviceHandler;
			handler.handle(curHandlerIndex -1 < annotations.size()?annotations.get(curHandlerIndex - 1) : null, request, this);
		}
	}

	public LocalModule getModule() {
		return module;
	}
	
	public void setResult(Data data) {
		this.data = data;
	}
	
	public Data getResult() {
		return data;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public Object getService() {
		return service;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getMethodName() {
		return methodName;
	}

	public MethodParam[] getMethodParams() {
		return methodParams;
	}
}
