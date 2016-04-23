package com.flying.framework.exception;


@SuppressWarnings("serial")
public class ModuleNotFoundException extends ObjectNotFoundException {

	public ModuleNotFoundException(String moduleId) {
		super(moduleId, "");
	}

}
