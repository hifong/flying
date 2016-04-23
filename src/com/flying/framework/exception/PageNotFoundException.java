package com.flying.framework.exception;


@SuppressWarnings("serial")
public class PageNotFoundException extends ObjectNotFoundException {

	public PageNotFoundException(String moduleId, String id) {
		super(moduleId, id);
	}

}
