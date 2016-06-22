package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Param {
	String value();
	String desc() default "";
	
	//validation
	String max() default "";
	String min() default "";
	int maxlength() default 0;
	boolean required() default false;
	String format() default "";
	String lt() default "";
	String gt() default "";
	String eq() default "";
	String validator() default "";
	//
	String tag() default "";
	
}
