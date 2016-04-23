package com.flying.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.Constants;
import com.flying.framework.application.Application;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.ObjectNotFoundException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.RequestFilterChain;
import com.flying.framework.security.Principal;
import com.flying.framework.security.SimpleUserPrincipal;

/**
 * @author king
 *
 */
public class ModuleEntryFilter  implements Filter {
	private final static Logger logger = Logger.getLogger(ModuleEntryFilter.class);
	
	private ServletContext servletContext;
	private String welcomePage;

	public void init(FilterConfig config) throws ServletException {
		this.servletContext = config.getServletContext();
		//
		this.welcomePage = config.getInitParameter("welcomePage");
		
	}
	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest sreq, ServletResponse sresp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)sreq;
		HttpServletResponse resp = (HttpServletResponse)sresp;
		req.setAttribute(Constants.SERVLET_CONTEXT, servletContext);
		//
		String path = req.getRequestURI();
		if((path == null || path.length() <= 1) && !StringUtils.isEmpty(welcomePage)) 
			path = this.welcomePage;
		//
		LocalModule module = null;
		String[] uris = null;
		String reqType = null;
		if(path.indexOf(".") > 0) {
			path = path.replaceAll("/+", "/").substring(req.getContextPath().length());
			reqType = path.substring(path.lastIndexOf(".") + 1);
			final String pathEff = path.substring(0, path.lastIndexOf("."));
			
			uris = StringUtils.split(pathEff, "/");
			
			final String moduleId = uris[0];
			module = Application.getInstance().getModules().exists(moduleId)?Application.getInstance().getModules().getLocalModule(moduleId): null;
			Principal principal = (Principal)req.getSession().getAttribute(Principal.PRINCIPAL);
			if(principal == null && req.getUserPrincipal() != null) {
				principal = new SimpleUserPrincipal(req.getUserPrincipal().getName(), req.getUserPrincipal().getName(), req.getUserPrincipal());
			}
			if(module != null) {
				req.setAttribute(Constants.MODULE, module);
				Data request = DataUtils.convert(module, req);
				ThreadContext ctx = new ThreadContext(module, request, principal);
				ThreadContext.setContext(ctx);
			}
		}
		//
		if(module != null && !StringUtils.isEmpty(reqType) && module.canHandleRequest(reqType)) {
			final String[] pathItems = new String[uris.length - 1];
			System.arraycopy(uris, 1, pathItems, 0, uris.length - 1);
			processRequestInChain(module, reqType, req, resp, pathItems);
		} else {
			chain.doFilter(sreq, sresp);
		}
	}
	
	private void processRequestInChain(LocalModule module, String requestType, HttpServletRequest req, HttpServletResponse resp, String[] pathItems)
			throws ServletException, IOException {
		try {
			req.setAttribute("$URIS", pathItems);
			req.setAttribute("$REQUEST_TYPE", requestType);
			RequestFilterChain chain = new RequestFilterChain(module, req.getRequestURI());
			chain.doFilter(req, resp);
		} catch (AppException e) {
			logger.error("ModuleEntry error!", e);
			Throwable t = e.getCause();
			if(e instanceof ObjectNotFoundException) {
				resp.sendError(404);
			} else if(t instanceof NoSuchMethodException) {
				resp.sendError(404);
			}else {
				resp.sendError(500);
			}
		} catch (IOException e) {
			logger.error("ModuleEntry error!", e);
			resp.sendError(404);
		} catch (Exception e){
			logger.error("ModuleEntry error!", e);
			resp.sendError(500);
		}
	}
	
}