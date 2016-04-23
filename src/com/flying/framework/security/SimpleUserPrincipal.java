package com.flying.framework.security;

public class SimpleUserPrincipal implements Principal {
	
	private static final long serialVersionUID = 5555491514306968767L;
	private final String id;
	private final String name;
	private final Object po;

	public SimpleUserPrincipal(String id, String name, Object po) {
		this.id = id;
		this.name = name;
		this.po = po;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public Object getPrincipalRef() {
		return po;
	}

	@Override
	public boolean isRole(String[] roles) {
		return false;
	}

	@Override
	public boolean hasPermission(String[] permissions) {
		return false;
	}

}
