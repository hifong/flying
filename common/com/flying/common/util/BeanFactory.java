package com.flying.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.flying.common.config.ConfigUtils;
import com.flying.common.log.Logger;
import com.flying.framework.annotation.LoadOnStartup;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.SpringBeanFactory;

/**
 * @author wanghaifeng
 *
 */
public class BeanFactory implements SpringBeanFactory {
	private static final Logger logger = Logger.getLogger(BeanFactory.class);
	private ApplicationContext context;
	private LocalModule module;
	
	public BeanFactory(LocalModule module) {
		this.module = module;
	}
	
	@LoadOnStartup
	public void loadOnStartup() {
		Thread.currentThread().setContextClassLoader(module.getClassLoader());
		logger.debug(module.getName() + " init!");
		long start = System.currentTimeMillis();
		String path=module.getModuleConfig().getClassesPath();
		context = new FileSystemXmlApplicationContext(ConfigUtils.fileSeparator+path + "application-context.xml");
		logger.debug("BeanFactory loadOnStartup------------"+module.getName() + " finish! time : "+ (System.currentTimeMillis() - start));
	}
	

	@SuppressWarnings("unchecked")
	public <T> T getBean(String id) {
		Thread.currentThread().setContextClassLoader(module.getClassLoader());
		return (T)context.getBean(id);
	}

	public Object getSpringContext() {
		return context;
	}

}
