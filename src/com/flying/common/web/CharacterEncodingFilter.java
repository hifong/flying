package com.flying.common.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CharacterEncodingFilter implements Filter {
	protected FilterConfig filterConfig = null;
	private String encoding = "UTF-8";

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		req.setCharacterEncoding(encoding);
		resp.setCharacterEncoding(encoding);
		
		resp.setHeader("Cache-Control","no-cache");
		resp.setHeader("Pragrma","no-cache");
		resp.setDateHeader("Expires",0);

		chain.doFilter(req, resp);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		String encoding = filterConfig.getInitParameter("encoding");
		if(encoding != null) {
			this.encoding = encoding;
		}
	}

}
