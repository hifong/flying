/*------------------------------------------------------------------------------
 * COPYRIGHT Aspire 2011
 *
 * The copyright to the computer program(s) herein is the property of
 * Aspire Inc. The programs may be used and/or copied only with written
 * permission from Aspire Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.flying.framework.web.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import com.flying.framework.application.Application;

public class InitListener implements ServletContextListener {
	
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    public void contextInitialized(ServletContextEvent event) {
    	String configHome = System.getenv("configHome");
    	if(StringUtils.isBlank(configHome)){
    		configHome = System.getProperty("configHome");
    	}
    	
    	if(StringUtils.isBlank(configHome)){
    		configHome = event.getServletContext().getInitParameter("configHome");
    	}
    	String logRoot = event.getServletContext().getInitParameter("logRoot");
    	System.setProperty("LOGROOT", logRoot);
    	//
		String configFile = configHome + File.separator + "log4j.xml";
		if(new File(configFile).exists()){
			DOMConfigurator.configureAndWatch(configFile);
		}
    	//
    	Application app = Application.initApplication(configHome);
    	app.setServletContext(event.getServletContext());
    }

}
