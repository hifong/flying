package com.flying.framework.context;

import java.util.concurrent.atomic.AtomicLong;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

/**
 * @author wanghaifeng
 *
 */
public class ThreadContext {
	private final static ThreadLocal<ThreadContext> context = new ThreadLocal<ThreadContext>();
	private final static AtomicLong COUNTER = new AtomicLong(100000);
	private Principal principal;
	private LocalModule module;
	private Data request;
	private String serviceId;
	private final long transactionId = COUNTER.incrementAndGet();
	private InvokeType invokeType = InvokeType.Local;;
	
	private final long starttime;
	private final Data variables = new Data();
	
	public static ThreadContext getContext() {
		ThreadContext c = context.get();
		if(c == null) {
			c = new ThreadContext(null, null, null);
			context.set(c);
		}
		return c;
	}
	
	public static void setContext(ThreadContext ctx) {
		context.set(ctx);
	}
	
	public ThreadContext(LocalModule module, Data request, Principal principal) {
		this.module = module;
		this.request = request;
		this.principal = principal;
		this.starttime = System.currentTimeMillis();
	}

	public void reset(LocalModule module, String serviceId, Data request, Principal principal) {
		this.module = module;
		this.serviceId = serviceId;
		this.request = request;
		this.principal = principal;
	}
	
	public LocalModule getModule() {
		return module;
	}

	public long getStarttime() {
		return starttime;
	}

	public Data getRequest() {
		return request;
	}

	public Data getVariables() {
		return variables;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public InvokeType getInvokeType() {
		return invokeType;
	}

	public void setInvokeType(InvokeType invokeType) {
		this.invokeType = invokeType;
	}

	public enum InvokeType {
		Local, Remote
	}

	public long getTransactionId() {
		return transactionId;
	}
}
