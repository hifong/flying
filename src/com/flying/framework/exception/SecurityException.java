package com.flying.framework.exception;

import com.flying.framework.security.Principal;

public class SecurityException extends AppException {

	private static final long serialVersionUID = 1875385353212211630L;
	
	private final Principal principal;

	public SecurityException(Principal principal, String message, String errorCode, String moduleId) {
		super(message, errorCode, moduleId);
		this.principal = principal;
	}

	public Principal getPrincipal() {
		return principal;
	}
}
