package com.flying.framework.config;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

import com.flying.common.util.Utils;
import com.flying.framework.data.Data;

/**
 * @author wanghaifeng
 *
 */
public class EventHandler implements Serializable {
	private static final long serialVersionUID = 847818937027917201L;
	private String moduleId;
	private String serviceId;
	private final Map<String, Pattern> conditionPatterns = Utils.newHashMap();
	private final Map<String, String> conditions = Utils.newHashMap();

	public EventHandler(String moduleId, String serviceId) {
		this.setModuleId(moduleId);
		this.setServiceId(serviceId);
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public void addCondition(String name, String value) {
		assert name != null && value != null: " condition name and value is not null!";
		this.conditionPatterns.put(name, Pattern.compile(value));
		this.conditions.put(name, value);
	}
	
	public Map<String, String> getConditions() {
		return conditions;
	}
	
	public boolean canHandle(Data data) {
		if(conditionPatterns.isEmpty()) return true;
		for(String key: conditionPatterns.keySet()) {
			String value = data.contains(key)?data.get(key).toString(): null;
			if(value == null) return false;
			Pattern p = conditionPatterns.get(key);
			if(!p.matcher(value).matches()) return false;
		}
		return true;
	}
	
	public String toString() {
		return serviceId + "@" + moduleId;
	}
}
