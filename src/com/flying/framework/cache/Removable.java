package com.flying.framework.cache;

public interface Removable {
	<T> T remove(String key);
}
