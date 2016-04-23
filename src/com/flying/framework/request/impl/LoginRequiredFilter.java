package com.flying.framework.request.impl;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.flying.framework.config.ServiceConfig;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestFilter;
import com.flying.framework.request.RequestFilter;
import com.flying.framework.request.RequestFilterChain;

/**
 * @author liuyuan
 * 
 */
public class LoginRequiredFilter extends AbstractRequestFilter implements RequestFilter {

	private Logger logger = Logger.getLogger(LoginRequiredFilter.class);

	private Pattern pattern;
	private Object lock = new Object();

	public Pattern getPattern(ServiceConfig serviceConfig) {
		if (pattern == null) {
			synchronized (lock) {
				if (pattern == null) {
					String patternStr = serviceConfig.getConfig("pattern");
					if (StringUtils.isBlank(patternStr)) {
						pattern = Pattern.compile(".+(\\.page|\\.shtml|\\.widget)", Pattern.CASE_INSENSITIVE);
					} else {
						try {
							pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
						} catch (Exception e) {
							logger.error("LoginRequiredRequestInterceptor:getPattern", e);
							pattern = Pattern.compile(".+(\\.page|\\.shtml|\\.widget)", Pattern.CASE_INSENSITIVE);
						}
					}
				}
			}
		}
		return pattern;
	}

	@Override
	public void doFilter(LocalModule module, HttpServletRequest req, HttpServletResponse resp, RequestFilterChain chain) throws Exception {
		chain.doFilter(req, resp);
	}
}
