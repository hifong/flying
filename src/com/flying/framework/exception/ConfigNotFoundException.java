package com.flying.framework.exception;


@SuppressWarnings("serial")
public class ConfigNotFoundException  extends ObjectNotFoundException {

	public ConfigNotFoundException(String moduleId, String id) {
		super(moduleId, id);
	}
}
