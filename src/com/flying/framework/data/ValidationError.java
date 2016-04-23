package com.flying.framework.data;

@SuppressWarnings("rawtypes")
public class ValidationError {
	private final String field;
	private final Class type;
	private Object realValue;
	private Object referenceValue;
	private final OP op;
	
	public ValidationError(String field, Class type, OP op) {
		this.field = field;
		this.type = type;
		this.op = op;
	}
	

	public ValidationError(String field, Class type, OP op, Object realValue, Object referenceValue) {
		this(field, type, op);
		this.realValue = realValue;
		this.referenceValue = referenceValue;
	}
	
	public String getField() {
		return field;
	}

	public Object getRealValue() {
		return realValue;
	}

	public OP getOp() {
		return op;
	}
	
	public Object getReferenceValue() {
		return this.referenceValue;
	}

	public Class getType() {
		return type;
	}

	public enum OP {
		max,
		min,
		maxlength,
		required,
		format,
		lt,
		gt,
		eq
	}
	
	public String toString() {
		if(this.op == OP.required) 
			return field + "[" + type +"] is required!";
		else if(this.op == OP.max)
			return field + "[" + type +"] max value is " + this.referenceValue;
		else if(this.op == OP.min)
			return field + "[" + type +"] min value is " + this.referenceValue;
		else if(this.op == OP.maxlength)
			return field + "[" + type +"] max length is " + this.referenceValue;
		else if(this.op == OP.format)
			return field + "[" + type +"] format is " + this.referenceValue;
		else if(this.op == OP.lt || this.op == OP.gt || this.op == OP.eq)
			return field + "[" + type +"] format is " + this.referenceValue;
		else
			return field + "[" + type +"] " + this.op + " " + this.referenceValue;
	}
}
