package com.flying.framework.service;


/**
 * @author wanghaifeng
 *
 */
public interface SpringBeanFactory {
	public <T> T getBean(String id);
	
	public Object getSpringContext();
}
