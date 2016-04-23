package com.flying.framework.cache;

public interface Cache {
	<T> T get(String key);
	<T> void put(String key, T value);
}
