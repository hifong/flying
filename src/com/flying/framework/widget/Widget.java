package com.flying.framework.widget;

import java.io.Writer;

import com.flying.framework.data.Data;

/**
 * @author wanghaifeng
 *
 */
public interface Widget {
	void output(Data request, Writer out) throws Exception;
}
