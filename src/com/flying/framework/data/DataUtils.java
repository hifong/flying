package com.flying.framework.data;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 * 
 */
public class DataUtils {
	public static Data convert(LocalModule module, HttpServletRequest request) throws IOException {
		@SuppressWarnings("unchecked")
		DataConverter<HttpServletRequest> dc = module.getDataConverter(HttpServletRequest.class.getName());
		return dc.convert(request);
	}

	/**
	 * TODO Implements
	 * @param data
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static <T> T convert(Data data, Class<T> type) throws Exception {
		T t = type.newInstance();
		Map map = BeanUtils.describe(t);
		return null;
	}
}
