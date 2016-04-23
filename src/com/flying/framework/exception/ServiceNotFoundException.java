package com.flying.framework.exception;


@SuppressWarnings("serial")
public class ServiceNotFoundException extends ObjectNotFoundException {

	public ServiceNotFoundException(Throwable t, String moduleId, String id) {
		super(t, moduleId, id);
	}

	public ServiceNotFoundException(String moduleId, String id) {
		super(moduleId, id);
	}
	
}
