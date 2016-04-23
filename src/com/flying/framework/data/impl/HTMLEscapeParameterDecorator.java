package com.flying.framework.data.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.flying.common.util.Utils;
import com.flying.framework.data.DataDecorator;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.EscapeEntity;

/**
 * @author wanghaifeng 将HTML参数中的特殊去掉
 */
public class HTMLEscapeParameterDecorator implements DataDecorator {
	private final static Logger logger = Logger.getLogger(HTMLEscapeParameterDecorator.class);

	public final static String defaultEscapePattern = "<|>|%3C|%3E|%29|%28|%3c|%3e|%3B|%3b|://|'|\"|,|;|\r|\n|\\(|\\)|\\{|\\}|\\[|\\]";

	private LocalModule module;

	public HTMLEscapeParameterDecorator(LocalModule module) {
		this.module = module;
	}

	@SuppressWarnings("unchecked")
	public String[] decorate(final String paramName, final String[] paramValues) {
		if (paramValues == null)
			return null;
		Map<String, EscapeEntity> escapeParamMap = null;
		final String[] values = paramValues;
		String escapePattern = null;
		int type = EscapeEntity.STRING_TYPE;
		try {
			escapeParamMap = (Map<String, EscapeEntity>) module.getSpringBeanFactory().getBean("escapeParamMap");
			if (escapeParamMap != null) {
				EscapeEntity escapeEntity = escapeParamMap.get(paramName);
				escapePattern = escapeEntity.getEscapePattern();
				type = escapeEntity.getType();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			switch (type) {
			case EscapeEntity.INT_TYPE:
				for (int i = 0; i < values.length; i++) {
					try {
						Long.parseLong(values[i]);
					} catch (Exception e) {
						values[i] = "0";
					}
				}
				break;
			case EscapeEntity.STRING_TYPE:
				if (StringUtils.isBlank(escapePattern)) {
					escapePattern = defaultEscapePattern;
				}

				for (int i = 0; i < values.length; i++) {
					values[i] = Utils.escapeSecurity(values[i], escapePattern);
				}
				break;
			default:
				break;
			}
			return values;
		} catch (Exception e) {
			logger.debug("RequestUtils:convert not found escapeParamMap and escapePattern", e);
			return paramValues;
		}
	}

	public String decorate(String paramName, String paramValue) {
		if (paramValue == null)
			return null;
		String[] values = decorate(paramName, new String[] { paramValue });
		if (values == null || values.length == 0) {
			return paramValue;
		}
		return values[0];
	}
}
