package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.framework.service.impl.UseCacheHandler")
public @interface UseCache {
	String tag();		//all object of one category,so you can remove or improve it's version
	String[] keys() default {};		//other keys
	boolean versionable() default true;	//if use version,you can improve ver to disable unusable object;
}
