package com.flying.framework.dao;

import java.util.List;

public class Entity {
	private final String table;
	private final List<Field> fields;
	
	public Entity(String table, List<Field> fields) {
		this.table = table;
		this.fields = fields;
	}

	public String getTable() {
		return table;
	}

	public List<Field> getFields() {
		return fields;
	}
}
