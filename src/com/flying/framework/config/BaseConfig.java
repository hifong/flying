package com.flying.framework.config;

import java.io.Serializable;
import java.util.Map;

import com.flying.framework.data.Data;

@SuppressWarnings("serial")
public class BaseConfig implements Serializable{
	protected final String id;
	protected final Data configs = new Data();

	public BaseConfig(String id) {
		this.id = id;
	}
	
	public BaseConfig(String id, Map<String, String> configs){
		this.id = id;
		this.configs.putAll(configs);
	}
	
	public final String getId() {
		return id;
	}

	public final Data getConfigs() {
		return this.configs;
	}
	
	public final String getConfig(String key) {
		return configs.getString(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseConfig other = (BaseConfig) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
