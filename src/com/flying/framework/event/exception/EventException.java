package com.flying.framework.event.exception;

import com.flying.framework.exception.AppException;

/**
 * @author wanghaifeng
 *
 */
public class EventException extends AppException {
	private static final long serialVersionUID = -4637031592601941595L;
	public EventException(Throwable t, String moduleId, String serviceId) {
		super(t, moduleId, serviceId);
	}
}
