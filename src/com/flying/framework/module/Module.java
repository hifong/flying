package com.flying.framework.module;

import com.flying.framework.data.Data;

public abstract class Module {
	protected final String id;
	protected final String path;
	protected final Data configs;
	
	public Module(String id, String path, Data configs) {
		this.id = id;
		this.path = path;
		this.configs = configs;
	}
	
	public final String getId() {
		return this.id;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public Data getConfigs() {
		return this.configs;
	}

	public ModuleLocation getLocation() {
		return ModuleLocation.LOCAL;
	}

	//加载时执行
	abstract public void onLoad();
	
	//模块卸载时执行
	abstract public void onUnload();
	
	public int compareTo(LocalModule o) {
		return this.id.compareTo(o.id);
	}
	
	public abstract Data invoke(String serviceId, Data request) throws Exception;
}
