package com.flying.framework.service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.commons.beanutils.MethodUtils;

import com.flying.framework.annotation.Param;

public class Main {
	public void test(@Param("id") String id){
		
	}
	public static void main(String[] args) throws Exception {
		Method m = MethodUtils.getAccessibleMethod(Main.class, "test", String.class);
		Parameter p = m.getParameters()[0];
		System.out.println(p);
		System.out.println(p.getDeclaredAnnotations().length);
		Param pa = p.getAnnotation(Param.class);
		System.out.println(pa.value());
	}
}
