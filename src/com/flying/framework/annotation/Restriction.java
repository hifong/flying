package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限定bean字段value长度
 * @author liuyuan
 *
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Restriction {
	boolean nillable() default true;
	int minLength() default 0;
	int maxLength() default Integer.MAX_VALUE;
	
	int minIntValue() default 0;
	int maxIntValue() default Integer.MAX_VALUE;
	
	//字段取枚举值判定
	String[] enumStrValues() default {};
	
	//字段取枚举值判定
	int[] enumIntValues() default {};
	//字段在数据库中的判定
	String validationSQL() default ""; 
}
