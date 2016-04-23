package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 参数描述，将参数映射到一个POJO类
 *
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Mapping {
	String value() default "";
}
