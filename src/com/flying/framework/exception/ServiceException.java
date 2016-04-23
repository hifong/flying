package com.flying.framework.exception;

@SuppressWarnings("serial")
public class ServiceException extends AppException {

	public ServiceException(Throwable t, String moduleId, String serviceId) {
		super(t, moduleId, serviceId);
	}

	public ServiceException(String message, String errorCode, String moduleId) {
		super(message, errorCode, moduleId);
	}

}
