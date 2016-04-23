package com.flying.common.exception;

public class RestrictionException extends Exception {
	private String className;
	private String fieldName;
	public RestrictionException(String className, String fieldName,
			String message) {
		super(message);
		this.className = className;
		this.fieldName = fieldName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	@Override
	public String toString() {
		return "RestrictionException [className=" + className + ", fieldName="
				+ fieldName + "," + getLocalizedMessage() + "]";
	}

}
