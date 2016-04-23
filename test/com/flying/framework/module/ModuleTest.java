package com.flying.framework.module;

import org.junit.Test;

import com.flying.framework.Tester;

public class ModuleTest extends Tester{
	@Test
	public void getModules() {
		logger.debug(app.getModules().getModule("pas"));
	}
}
