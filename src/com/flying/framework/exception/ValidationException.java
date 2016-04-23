package com.flying.framework.exception;

import java.util.List;
import java.util.Map;

import com.flying.common.util.Codes;
import com.flying.framework.data.ValidationError;

public class ValidationException extends AppException {
	private static final long serialVersionUID = -2546656607293817934L;
	
	private final String method;
	private final Map<String, List<ValidationError>> errors;
	
	public ValidationException(String moduleId, String serviceId, String method, Map<String, List<ValidationError>> errors) {
		super("validation error!", Codes.INVALID_PARAM + "", moduleId);
		this.serviceId = serviceId;
		this.method = method;
		this.errors = errors;
		
	}

	public String getMethod() {
		return method;
	}

	public Map<String, List<ValidationError>> getErrors() {
		return errors;
	}
	
	public String toString() {
		return moduleId +" " + serviceId + ":" + method +"\n" + errors;
	}
}
