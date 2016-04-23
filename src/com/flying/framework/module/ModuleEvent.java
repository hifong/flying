package com.flying.framework.module;


public class ModuleEvent {
	private final LocalModule module;
	private final String type;
	private final String serviceId;

	public ModuleEvent(LocalModule module, String serviceId, String type) {
		this.module = module;
		this.serviceId = serviceId;
		this.type = type;
	}
	
	public ModuleEvent(LocalModule module, String[] configs) {
		this(module, configs[1], configs[0]);
	}

	public LocalModule getModule() {
		return module;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getType() {
		return type;
	}
	
	public void fire() {
		module.invoke(serviceId, null);
	}
}
