package com.flying.framework.exception;


/**
 * @author wanghaifeng
 *
 */
@SuppressWarnings("serial")
public class ObjectNotFoundException extends AppException {
	protected String id;
	
	public ObjectNotFoundException(String moduleId, String id) {
		super(ErrorCode.OBJECT_NOT_FOUND, moduleId);
		this.id = id;
	}
	
	public ObjectNotFoundException(Throwable t, String moduleId, String id) {
		super(t, moduleId, id);
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "Object id[" + id +"] not found in module[" + moduleId + "]";
	}

	@Override
	public String getLocalizedMessage() {
		return "Object id[" + id +"] not found in module[" + moduleId + "]";
	}
	
}
