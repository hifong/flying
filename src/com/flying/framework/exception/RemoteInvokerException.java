package com.flying.framework.exception;

import com.flying.framework.data.Data;

public class RemoteInvokerException extends AppException {
	private static final long serialVersionUID = -1571159222123056065L;
	private final String stackMessage;
	private final Data request;

	public RemoteInvokerException(final String moduleId,
			final String serviceId, final String msg,
			final String stackMessage, final Data request) {
		super(msg, ErrorCode.INTERNAL_ERROR, moduleId);
		this.serviceId = serviceId;
		this.stackMessage = stackMessage;
		this.request = request;
	}

	public String getStackMessage() {
		return stackMessage;
	}

	public Data getRequest() {
		return request;
	}

}
