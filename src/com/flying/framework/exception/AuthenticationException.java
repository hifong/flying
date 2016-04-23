package com.flying.framework.exception;

import com.flying.common.util.Codes;
import com.flying.framework.security.Principal;


@SuppressWarnings("serial")
public class AuthenticationException extends SecurityException {
	private String[] roles;
	private String[] permissions;
	private String serviceId;
	
	public AuthenticationException(Principal principal, String message, String moduleId, String serviceId) {
		super(principal, message, String.valueOf(Codes.AUTH_FAIL), moduleId);
		this.serviceId = serviceId;
	}
	
	public AuthenticationException(Principal principal, String message, String moduleId, String serviceId, String[] roles, String[] permissions) {
		super(principal, message, String.valueOf(Codes.AUTH_FAIL), moduleId);
		this.roles = roles;
		this.permissions = permissions;
		this.serviceId = serviceId;
	}

	public String[] getRoles() {
		return roles;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public String getServiceId () {
		return this.serviceId;
	}
}
