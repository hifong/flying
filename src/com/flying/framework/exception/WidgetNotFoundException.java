package com.flying.framework.exception;


@SuppressWarnings("serial")
public class WidgetNotFoundException extends ObjectNotFoundException {

	public WidgetNotFoundException(String moduleId, String id) {
		super(moduleId, id);
	}

}
