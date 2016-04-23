package com.flying.framework.service.impl;

import org.apache.log4j.Logger;

import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

public class ModuleEvent extends AbstractService{
	private final static Logger log = Logger.getLogger(ModuleEvent.class);

	public Data onLoad(Data request) {
		log.info("ModuleEvent.onLoad : module[" + module.getId() + "] config:" + serviceConfig.getConfig("onLoad"));
		return request;
	}
	
	public Data onUnload(Data request) {
		log.info("ModuleEvent.onUnload : module[" + module.getId() + "]config:" + serviceConfig.getConfig("onUnload"));
		return request;
	}
}
