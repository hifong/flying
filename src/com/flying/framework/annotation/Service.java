package com.flying.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@Component  
public @interface Service {  
    String value();
    String desc() default "";
    boolean loadOnStartup() default false;
    boolean isSingleInstance() default true;
    ServiceConfig[] configs() default {};
}