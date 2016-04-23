package com.flying.common.cache;

import java.util.Collection;
import java.util.Map;

/**
 * @author wanghaifeng
 * @date Aug 6, 2009 10:32:37 AM
 * @param <T>
 */
public interface Cache<T> {
    public Map<String, T> getCache();

    public String getName();

    public void flush();

    public void clear();

    public boolean containsKey(String key);

    public boolean containsValue(T value);

    public boolean isEmpty();

    public T put(String key, T value);

    public void putAll(Map<String, T> map);

    public T get(String key);
    
    public T get(String key,long timeout);

    public T remove(Object key);

    public int size();

    public Collection<String> keys();

    public Collection<T> values();
}
