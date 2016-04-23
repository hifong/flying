package com.flying.framework.util;

import java.util.Map;

import org.lilystudio.smarty4j.Engine;

import com.flying.common.util.Utils;
import com.flying.framework.module.LocalModule;

public class SmartyUtils {
	private static Map<String,Engine> map = Utils.newHashMap();
	private static Object lock = new Object();
	public static Engine getEngine(LocalModule module){
		String moduleId = module.getId();
		Engine engine = map.get(moduleId);
		if(engine==null){
			synchronized (lock) {
				if(engine==null){
					engine = new Engine();
					engine.setTemplatePath(module.getModuleConfig().getTemplatePath());
					map.put(moduleId, engine);
				}
			}
		}
		return engine;
	}
	
	public synchronized static void addEngine(String moduleId,Engine engine){
		map.put(moduleId, engine);
	}
}
