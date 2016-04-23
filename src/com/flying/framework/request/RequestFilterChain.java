package com.flying.framework.request;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.common.util.Utils;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.impl.RequestHandlerFilter;

/**
 * @author wanghaifeng
 *
 */
public class RequestFilterChain {
	private final LocalModule module;
	private final List<RequestFilter> filters = Utils.newArrayList();
	
	private int currentFilter = 0;
	
	public RequestFilterChain(LocalModule module, String uri) {
		this.module = module;
		List<RequestFilter> fs = module.getFilters(uri);
		if(fs != null && !fs.isEmpty())
			this.filters.addAll(fs);
		this.filters.add(new RequestHandlerFilter());
	}
	
	public void doFilter(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		this.currentFilter ++;
		if(currentFilter <= filters.size()) {
			RequestFilter filter = filters.get(currentFilter - 1);
			filter.doFilter(module, req, resp, this);
		}
	}
}
