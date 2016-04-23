package com.flying.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceConfig {
	  public abstract String name();
	  
	  public abstract String value();
	  
	  public abstract String description() default "";
}
