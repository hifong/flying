package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.framework.service.impl.DaoQueryHandler")
public @interface DaoQuery {
	Position position() default Position.none_body;
	String entity() default "";
	boolean pageable() default false;
	boolean single() default false;		//result just one record
	String qsql() default "";		//select xx from x
	String osql() default "";		//order by
	String csql() default "";		//select count(1) from x
	
	enum Position{
		before_body, after_body, none_body
	}
}
