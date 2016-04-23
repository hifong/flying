package com.flying.framework.application;

import java.io.File;

import javax.servlet.ServletContext;

import com.flying.framework.data.Data;
import com.flying.framework.module.Modules;

public class Application {
	private final String home;
	private final ApplicationConfig applicationConfig;
	private ServletContext servletContext;
	private Modules modules;

	private static Application instance;
	
	public synchronized static Application initApplication(String applicationHome) {
		if(applicationHome == null) throw new RuntimeException("Config home is null, initialize fail!");
		File file = new File(applicationHome);
		if(!file.exists() || !file.isDirectory()) throw new RuntimeException("Config home["+applicationHome+"] is not exists or not a directory!");
		
		if(!applicationHome.endsWith(File.separator)) applicationHome = applicationHome + File.separator;
		instance = new Application(applicationHome);
		return instance;
	}

	public static Application getInstance() {
		return instance;
	}

	private Application(String home) {
		this.home = home;
		
		String filePath = home + "application.xml";
		final File file = new File(filePath);
		if(!file.exists() || !file.isFile()) throw new RuntimeException("config file [" + filePath +"] is not exists or not a file!");
		this.applicationConfig = new ApplicationConfig(file);
		new Thread(new Runnable(){
			
			@Override
			public void run() {
				Application.this.modules = new Modules(Application.this.applicationConfig.getValue("modules"));
			}
			
		}).start();

//		if(applicationConfig.getConfigs("thrift") != null) {
//			new Thread(new ThriftServiceInvokerSimpleServer()).start();
//		}
	}

	public String getHome() {
		return home;
	}

	public String getConfigFileInHome(String configFile) {
		return home + configFile;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Modules getModules() {
		return modules;
	}

	public ApplicationConfig getApplicationConfig() {
		return this.applicationConfig;
	}

	public String getEventProducer() {
		return this.applicationConfig.getString("event-producer");
	}

	public boolean isProductMode() {
		return "true".equals(this.applicationConfig.getString("product-mode"));
	}

	public String getContextRoot() {
		return this.applicationConfig.getString("context-path");
	}

	public String getDomainName() {
		return this.applicationConfig.getString("domain-name");
	}

	public Data getConfigs(String group) {
		return this.applicationConfig.getValue("group-configs").getValue(group);
	}

	public String getConfigValue(String group, String key) {
		Data configs = this.getConfigs(group);
		return configs == null ? null : configs.getString(key);
	}
}
