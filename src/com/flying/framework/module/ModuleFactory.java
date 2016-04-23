package com.flying.framework.module;

import com.flying.framework.data.Data;

public abstract class ModuleFactory {
	public static Module createModule(String id, String locate, String path, Data configs) {
		if("remote".equals(locate)) {
			return RemoteModule.newInstance(id, path, configs);
		} else {
			return LocalModule.newInstance(id, path, configs);
		}
	}
}
