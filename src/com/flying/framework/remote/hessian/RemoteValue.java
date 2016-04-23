package com.flying.framework.remote.hessian;

import java.io.Serializable;
import java.util.UUID;

import com.flying.framework.data.Data;

/**
 * @author wanghaifeng
 * 
 */
public class RemoteValue implements Serializable {
	private static final long serialVersionUID = -4319119127602853305L;
	private Throwable exception;
	private String result;
	final private String tid;
	private Data value;

	public RemoteValue(Data value) {
		this.tid = UUID.randomUUID().toString();
		this.value = value;
	}
	
	public Data getValue() {
		return value;
	}
	
	public void setValue(Data value) {
		this.value = value;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTid() {
		return tid;
	}
}
