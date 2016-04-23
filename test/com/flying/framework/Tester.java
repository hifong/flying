package com.flying.framework;

import org.junit.Before;

import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.web.listener.InitListener;

public class Tester {
	protected Logger logger;
	protected Application app;
	
	@Before
    public void setUp() throws Exception {
		String configHome = "C:\\flying\\framework\\configs";
    	System.setProperty("LOGROOT", "C:\\Temp\\logs\\");
    	app = Application.initApplication(configHome);
    	
    	logger = Logger.getLogger(InitListener.class);
    	Thread.sleep(5000);
    }
}
