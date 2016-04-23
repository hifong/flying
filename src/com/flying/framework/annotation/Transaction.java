package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.framework.service.impl.TransactionHandler")
public @interface Transaction {
	
	public static final int PROPAGATION_REQUIRED = 0;
	public static final int PROPAGATION_SUPPORTS = 1;
	public static final int PROPAGATION_MANDATORY = 2;
	public static final int PROPAGATION_REQUIRES_NEW = 3;
	public static final int PROPAGATION_NOT_SUPPORTED = 4;
	public static final int PROPAGATION_NEVER = 5;
	public static final int PROPAGATION_NESTED = 6;
	public static final int ISOLATION_DEFAULT = -1;
	public static final int ISOLATION_READ_UNCOMMITTED = 1;
	public static final int ISOLATION_READ_COMMITTED = 2;
	public static final int ISOLATION_REPEATABLE_READ = 4;
	public static final int ISOLATION_SERIALIZABLE = 8;
	
	public int propagationBehavior() default PROPAGATION_REQUIRED;
	public int isolationLevel() default ISOLATION_DEFAULT;
}
