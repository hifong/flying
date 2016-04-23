package com.flying.framework.exception;

import com.flying.common.util.Codes;
import com.flying.framework.security.Principal;


@SuppressWarnings("serial")
public class URIAuthenticationException extends SecurityException {
	private final String uri;
	
	public URIAuthenticationException(Principal principal, String message, String moduleId, String uri) {
		super(principal, message, String.valueOf(Codes.AUTH_FAIL), moduleId);
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}
