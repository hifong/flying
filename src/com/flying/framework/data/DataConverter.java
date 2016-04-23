package com.flying.framework.data;

/**
 * 将其他数据转换成Data
 * 
 * @author king
 *
 * @param <T>
 */
public interface DataConverter<T> {
	public Data convert(T input);
}
