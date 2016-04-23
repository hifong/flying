package com.flying.framework.application;

import org.junit.Test;

import com.flying.framework.Tester;
import com.flying.framework.application.Application;

public class ApplicationTest extends Tester {
	
	@Test
	public void getModule() {
		logger.info("*************************************************************");
		logger.info(Application.getInstance().getModules().getModule("mc"));
		logger.info(Application.getInstance().getModules().getModule("uc"));
		logger.info(Application.getInstance().getModules().getModule("pas"));
	}
}
