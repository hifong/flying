package com.flying.framework.exception;

import com.flying.common.util.Codes;
import com.flying.framework.security.Principal;


@SuppressWarnings("serial")
public class MethodAuthenticationException extends SecurityException {
	private final String serviceId;
	
	public MethodAuthenticationException(Principal principal, String message, String moduleId, String serviceId) {
		super(principal, message, String.valueOf(Codes.AUTH_FAIL), moduleId);
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return serviceId;
	}
}
