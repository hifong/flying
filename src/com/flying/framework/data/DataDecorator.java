package com.flying.framework.data;

/**
 * @author wanghaifeng
 * 参数修饰器，负责将参数转换后输出，一般用于处理参数的合法性、安全问题等参数修饰
 */
public interface DataDecorator {
	public String[] decorate(final String paramName, final String[] paramValues);
	
	public String decorate(final String paramName, final String paramValue);
}
