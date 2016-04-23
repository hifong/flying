package com.flying.framework.service;

import java.lang.annotation.Annotation;

import com.flying.framework.data.Data;

/**
 * 业务方法执行拦截
 * @author king
 *
 */
public interface ServiceHandler<T extends Annotation> {
	/**
	 * @param annotation
	 * @param module	正在处理的业务模块
	 * @param serviceId	正在处理的serviceId
	 * @param methodName	正在调用的MethodName
	 * @param request
	 * @param chain
	 * @throws Exception
	 */
	void handle(T annotation,Data request, ServiceHandlerContext context) throws Exception;
}
