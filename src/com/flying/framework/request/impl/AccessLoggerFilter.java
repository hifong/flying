package com.flying.framework.request.impl;

import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.common.log.Logger;
import com.flying.common.log.util.LoggerStringBuffer;
import com.flying.common.util.DateUtils;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestFilter;
import com.flying.framework.request.RequestFilter;
import com.flying.framework.request.RequestFilterChain;
import com.flying.framework.security.Principal;

/**
 * 登记请求的访问日志
 * @author 海峰
 *
 */
public class AccessLoggerFilter extends AbstractRequestFilter implements RequestFilter {
	private final static Logger accessLogger = Logger.getLogger("access");
	private Pattern accessPattern = null;
	
	@Override
	public void doFilter(LocalModule module, HttpServletRequest req, HttpServletResponse resp, RequestFilterChain chain) throws Exception {
		if (accessPattern == null) {
			synchronized (this) {
				if (accessPattern == null) {
					String pns = this.serviceConfig.getConfig("accessLoggerPattern");
					if (pns == null)
						pns = ".+(\\.page|\\.shtml|\\.widget|\\.action|\\.api|\\.service).*";
					accessPattern = Pattern.compile(pns, Pattern.CASE_INSENSITIVE);
				}
			}
		}
		//
		long startTime = System.currentTimeMillis();
		try {
			chain.doFilter(req, resp);
		} finally {
			final String requestURI = req.getRequestURI() + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
			if (accessPattern.matcher(requestURI).matches()) {
				Data request = DataUtils.convert(module, req);
				Principal principal = request.get("Principal");
				LoggerStringBuffer ls = new LoggerStringBuffer();
				ls.append(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
				String id = principal == null ? "" : principal.getId();
				String name = principal == null ? "" : principal.getName();
				ls.append(id).append(name);
				ls.append(requestURI).append(String.valueOf((System.currentTimeMillis() - ThreadContext.getContext().getStarttime())));
				ls.append(req.getRemoteAddr()).append(String.valueOf(req.getRemotePort()));
				ls.append(String.valueOf(System.currentTimeMillis() - startTime));
				accessLogger.info(ls.toString());
			}
		}
	}

}
