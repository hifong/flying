package com.flying.framework.exception;


@SuppressWarnings("serial")
public class BeanNotFoundException extends ObjectNotFoundException {

	public BeanNotFoundException(String moduleId, String id) {
		super(moduleId, id);
	}
}
