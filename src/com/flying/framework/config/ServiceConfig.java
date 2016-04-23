package com.flying.framework.config;

import java.util.List;
import java.util.Map;

import com.flying.common.util.Utils;
import com.flying.framework.annotation.Service;

public class ServiceConfig extends BaseConfig{
	private final static Map<String, String> EMPTY_MAP = Utils.newHashMap();
	
	private static final long serialVersionUID = 6798523239770293085L;
	private final String target;
	private final String desc;
	private final Type type;
	private final boolean isSingleInstance;
	private final List<EventConfig> eventConfigs = Utils.newArrayList();
	private final boolean loadOnStartup;

	public ServiceConfig(String id, String target, String desc, String type, Map<String, String>configs, List<EventConfig> eventConfigs, boolean loadOnStartup, boolean isSingleInstance) {
		super(id, configs);
		this.target = target;
		this.desc = desc;
		this.type = Type.toType(type);
		this.eventConfigs.addAll(eventConfigs);
		this.loadOnStartup = loadOnStartup;
		this.isSingleInstance = isSingleInstance;
	}
	
	public ServiceConfig(Service service, Class<?> serviceClass) {
		super(service.value(), getConfigsFromService(service));
		this.desc = service.desc();
		this.target = serviceClass.getName();
		this.type = Type.Class;
		this.loadOnStartup = service.loadOnStartup();
		this.isSingleInstance = service.isSingleInstance();
	}
	
	private static Map<String, String> getConfigsFromService(Service service) {
		if(service.configs() == null || service.configs().length == 0) return EMPTY_MAP;
		
		Map<String, String> res = Utils.newHashMap();
		for(com.flying.framework.annotation.ServiceConfig sc: service.configs()) {
			res.put(sc.name(), sc.value());
		}
		return res;
	}

	public String getTarget() {
		return target;
	}

	public String getDesc() {
		return desc;
	}

	public Type getType() {
		return type;
	}

	public List<EventConfig> getEventConfigs() {
		return eventConfigs;
	}

	public enum Type {
		Class, Spring;
		public static Type toType(String type) {
			if("Spring".equalsIgnoreCase(type)) {
				return Spring;
			} else {
				return Class;
			}
		}
	}
	
	public List<EventConfig> getEventConfigsBySender(String sender) {
		List<EventConfig> result = Utils.newArrayList();
		for(EventConfig e: this.eventConfigs) {
			if(e.getSender().equals(sender)) {
				result.add(e);
			}
		}
		return result;
	}
	
	public boolean isLoadOnStartup() {
		return this.loadOnStartup;
	}

	public boolean isSingleInstance() {
		return isSingleInstance;
	}
}
