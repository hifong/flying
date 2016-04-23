package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Validation {
	String max() default "";
	String min() default "";
	boolean required() default false;
	String format() default "";
	String lt() default "";
	String gt() default "";
	String let() default "";
	String get() default "";
	String eq() default "";
}
