package com.flying.framework.exception;


/**
 * @author wanghaifeng
 *
 */
@SuppressWarnings("serial")
public class AppException extends RuntimeException {
	protected String errorCode = ErrorCode.INTERNAL_ERROR;
	protected String moduleId;
	protected String serviceId;
	
	public AppException(Throwable t, String message, String errorCode, String moduleId, String serviceId) {
		super(message, t);
		this.errorCode = errorCode;
		this.moduleId = moduleId;
		this.serviceId = serviceId;
	}
	
	public AppException(Throwable t, String message, String errorCode, String moduleId) {
		super(message, t);
		this.errorCode = errorCode;
		this.moduleId = moduleId;
	}
	
	public AppException(String message, Throwable t) {
		super(message, t);
	}
	
	public AppException(Throwable t, String moduleId) {
		super(t);
		this.moduleId = moduleId;
	}
	
	public AppException(Throwable t, String moduleId, String serviceId) {
		super(t);
		this.moduleId = moduleId;
	}
	
	public AppException(String message, String errorCode, String moduleId) {
		super(message);
		this.errorCode = errorCode;
		this.moduleId = moduleId;
	}
	
	public AppException(String errorCode, String moduleId) {
		super();
		this.errorCode = errorCode;
		this.moduleId = moduleId;
	}
	
	public String getMessage() {
		return "Error(" + errorCode + ") in " + moduleId + ", Message:" + super.getMessage();
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getServiceId() {
		return serviceId;
	}
}
