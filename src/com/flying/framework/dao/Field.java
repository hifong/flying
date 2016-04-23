package com.flying.framework.dao;

import java.lang.reflect.Type;

public class Field {
	private final String field;
	private final String desc;
	private final Type type;
	
	public Field(String field, String desc, Type type) {
		this.field = field;
		this.desc = desc;
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public String getDesc() {
		return desc;
	}

	public Type getType() {
		return type;
	}
}
