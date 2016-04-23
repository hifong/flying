package com.flying.framework.security;

public class EscapeEntity {
	
	public final static int INT_TYPE = 0;
	public final static int STRING_TYPE = 1;
	
	private int type = 1;
	
	private String escapePattern;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getEscapePattern() {
		return escapePattern;
	}

	public void setEscapePattern(String escapePattern) {
		this.escapePattern = escapePattern;
	}
}
