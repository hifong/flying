package com.flying.common.log.util;

public class LoggerStringBuffer {
	private final StringBuffer sb;

	public LoggerStringBuffer(){
		sb = new StringBuffer();
	}
	
	public LoggerStringBuffer(int capacity) {
		sb = new StringBuffer(capacity);
	}

	public LoggerStringBuffer append(String msg) {
		if(sb.length() > 0)
			sb.append('|');
		sb.append(msg == null?"":msg);
		return this;
	}

	public String toString() {
		return sb.toString();
	}
}
