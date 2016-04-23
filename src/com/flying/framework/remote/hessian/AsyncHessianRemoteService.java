package com.flying.framework.remote.hessian;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.caucho.hessian.server.HessianServlet;
import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

@SuppressWarnings("serial")
@WebServlet(value="/asyncRemoting", asyncSupported=true, initParams={@WebInitParam(name="timeout", value="10000")})
public class AsyncHessianRemoteService extends HessianServlet implements RemoteService{
	private final static Logger logger = Logger.getLogger(AsyncHessianRemoteService.class);
	
	private long timeout = 10000L;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		timeout = Long.parseLong(servletConfig.getInitParameter("timeout"));
	}
	
	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)servletRequest;
		long start = System.currentTimeMillis();
		final String requestURI = req.getRequestURI() + (req.getQueryString() == null?"":"?" + req.getQueryString());
		
		final AsyncContext asyncContext = req.startAsync();
		asyncContext.addListener(new AsyncHessianRemoteServiceListener());
		asyncContext.setTimeout(timeout);  
		asyncContext.start(new Runnable() {
			@Override
			public void run() {
				try {
					AsyncHessianRemoteService.this.service(servletRequest, servletResponse);
					asyncContext.complete();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					logger.info("Access(" + (System.currentTimeMillis() - start) + ")\tURI:" + requestURI);
				}
			}

		});
	}

	public RemoteValue invoke(Principal principal, String moduleId, String serviceId, RemoteValue remoteData) throws Exception {
		long start = System.currentTimeMillis();
		try {
			Data req = remoteData.getValue();
			LocalModule module = Application.getInstance().getModules().getLocalModule(moduleId);
			ThreadContext.getContext().reset(module, serviceId, req, principal);
			ThreadContext.getContext().setInvokeType(ThreadContext.InvokeType.Remote);
			
			Data result = module.invoke(serviceId, req);
			remoteData.setValue(result);
		} catch (Exception e){
			remoteData.setException(e);
		} finally {
			logger.info("RemoteInvoker(" + (System.currentTimeMillis() - start) + ")\tModuleId:" + moduleId+";ServiceId:" + serviceId);
		}
		return remoteData;
	}
	
	private class AsyncHessianRemoteServiceListener implements AsyncListener {

		@Override
		public void onComplete(AsyncEvent event) throws IOException {
			System.out.println("异步调用完成……");
		}

		@Override
		public void onError(AsyncEvent event) throws IOException {
			System.out.println("异步调用出错……");
		}

		@Override
		public void onStartAsync(AsyncEvent event) throws IOException {
			System.out.println("异步调用开始……");
		}

		@Override
		public void onTimeout(AsyncEvent event) throws IOException {
			System.out.println("异步调用超时……");
		}

	}
	
}
