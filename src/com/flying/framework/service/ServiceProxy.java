package com.flying.framework.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.ClassUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.KeyValuePair;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Handler;
import com.flying.framework.annotation.Param;
import com.flying.framework.config.EventConfig;
import com.flying.framework.config.EventHandler;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.data.Data;
import com.flying.framework.data.ValidationError;
import com.flying.framework.event.EventPublisher;
import com.flying.framework.exception.ValidationException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.util.ValidationUtils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

@SuppressWarnings("rawtypes")
public class ServiceProxy implements MethodInterceptor {
	private final static Logger logger = Logger.getLogger(ServiceProxy.class);

	private final LocalModule module;
	private final ServiceConfig serviceConfig;

	private final Enhancer enhancer = new Enhancer();
	private final Object service;

	private final Map<String, KeyValuePair<List<Annotation>, List<ServiceHandler>>> annotationHandlers = Utils.newHashMap();
	private final Map<Method, MethodParam[]> methodParams = Utils.newHashMap();

	public ServiceProxy(LocalModule module, ServiceConfig serviceConfig, Class<?> cls) {
		this.module = module;
		this.serviceConfig = serviceConfig;

		enhancer.setClassLoader(module.getClassLoader());
		enhancer.setSuperclass(cls);
		enhancer.setCallback(this);
		service = enhancer.create();
		if (PropertyUtils.isWriteable(service, "module"))
			try {
				BeanUtils.setProperty(service, "module", module);
			} catch (Exception e) {
				logger.warn("ServiceProxy.createInstance setModule fail!", e);
			}
		if (PropertyUtils.isWriteable(service, "serviceConfig"))
			try {
				BeanUtils.setProperty(service, "serviceConfig", serviceConfig);
			} catch (Exception e) {
				logger.warn("ServiceProxy.createInstance setServiceConfig fail!", e);
			}
	}

	public Object getInstance() {
		return service;
	}

	@Override
	public Object intercept(Object service, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(module.getClassLoader());
		try {
			if (Data.class == method.getReturnType() ) {
				MethodParam[] params = this.getMethodParams(service, method);
				// Handle InteceptorChain
				//orginze input
				Data request = new Data();
				for(int i=0; i < params.length; i++) {
					Parameter p = params[i].getParameter();
					Param pa = params[i].getParam();
					Type t = p.getType();
					if(pa == null && t == Data.class) {
						if(args[i] != null)
							request = ((Data)args[i]).merge(request, true);
					} else {
						String key = pa == null?p.getName():pa.value();
						request.put(key, args[i]);
					}
				}
				
				// validation check
				Map<String, List<ValidationError>> errors = Utils.newHashMap();
				for(int i=0; i < params.length; i++) {
					if(params[i].getParam() == null) continue;
					final String field = params[i].getParam().value();
					Class type = params[i].getParameter().getType();
					List<ValidationError> ers = ValidationUtils.checkValidation(field, type, params[i].getParam(), request);
					if(!ers.isEmpty()) errors.put(field, ers);
				}
				if(!errors.isEmpty())
					throw new ValidationException(this.module.getId(), this.serviceConfig.getId(), method.getName(), errors);
				//
				ServiceHandlerContext context = this.createServiceContext(method, service, methodProxy, args, params);
				context.doChain(request);
				//
				return context.getResult();
			} else {
				return methodProxy.invokeSuper(service, args);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(threadClassLoader);
		}
	}

	private ServiceHandlerContext createServiceContext(final Method method, final Object object, final MethodProxy mp, final Object[] args, MethodParam[] params) {
		final String serviceId = this.serviceConfig.getId();
		final String methodName = method.getName();
		//
		KeyValuePair<List<Annotation>, List<ServiceHandler>> annos = this.getHandlers(method);
		//
		final ServiceHandlerContext chain = new ServiceHandlerContext(module, service, serviceId, methodName, annos.getKey(), annos.getValue(), params, 
						new MethodInvokerHandler(object, mp, args));
		return chain;
	}

	/**
	 * 找到Method对应的声明和处理函数
	 * @param method
	 * @return
	 */
	private KeyValuePair<List<Annotation>, List<ServiceHandler>> getHandlers(Method method) {
		if (annotationHandlers.containsKey(method.getName()))
			return annotationHandlers.get(method.getName());
		final Annotation[] allAnnotations = method.getAnnotations();
		final List<ServiceHandler> handlers = Utils.newArrayList();
		final List<Annotation> annotations = Utils.newArrayList();
		for (Annotation annotation : allAnnotations) {
			String handlerInAnnotation = annotation.annotationType().getAnnotation(Handler.class) == null?null:annotation.annotationType().getAnnotation(Handler.class).handler();
			if(handlerInAnnotation != null) logger.debug("HandlerInAnnotation:" + annotation + " -> " + handlerInAnnotation);
			ServiceHandler handler = (handlerInAnnotation == null?module.getAnnotationHandler(annotation.annotationType().getName()) : (ServiceHandler)module.getService(handlerInAnnotation));
			if (handler != null) {
				handlers.add(handler);
				annotations.add(annotation);
			}
		}
		// 加入默认的
		ServiceHandler defaultHandler = module.getAnnotationHandler("DEFAULT");
		if (defaultHandler != null) {
			annotations.add(0, null);
			handlers.add(0, defaultHandler);
		}
		KeyValuePair<List<Annotation>, List<ServiceHandler>> result = new KeyValuePair<List<Annotation>, List<ServiceHandler>>(annotations, handlers);
		annotationHandlers.put(method.getName(), result);
		logger.debug("ServiceInvokerProxy build Handlers success for " + service + ":" + method.getName());
		return result;
	}
	
	private MethodParam[] getMethodParams(Object service, Method method) throws NoSuchMethodException, SecurityException {
		if(methodParams.containsKey(method)) return methodParams.get(method);
		
		Class[] argTypes = method.getParameterTypes();
		Class superclass = service.getClass().getSuperclass();
		if(service.getClass().getName().indexOf("$$EnhancerByCGLIB$$") <0 ) superclass = null;
		Method supermethod = superclass == null? null: ClassUtils.getMethodIfAvailable(superclass, method.getName(), argTypes);
		final Parameter[] params = supermethod == null? method.getParameters(): supermethod.getParameters();
		final MethodParam[] mps = new MethodParam[params.length];
		for(int i=0; i< params.length; i++) {
			mps[i] = new MethodParam(params[i], params[i].getAnnotation(Param.class));
		}
		methodParams.put(method, mps);
		
		return mps;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.annotationHandlers.clear();
	}
	
	public class MethodParam {
		private final Parameter parameter;
		private final Param param;
		
		public MethodParam(Parameter parameter, Param param) {
			this.parameter = parameter;
			this.param = param;
		}

		public Parameter getParameter() {
			return parameter;
		}

		public Param getParam() {
			return param;
		}
	}

	class MethodInvokerHandler implements ServiceHandler {
		private final MethodProxy methodProxy;
		private final Object[] args;
		private final Object object;

		public MethodInvokerHandler(Object object, MethodProxy mp, Object[] args) {
			this.object = object;
			this.methodProxy = mp;
			this.args = args;
		}

		@Override
		public void handle(Annotation annotation, Data request, ServiceHandlerContext context) throws Exception {
			try {
				// 执行方法体
				Object result = methodProxy.invokeSuper(object, args);
				if (result instanceof Data) {
					Data data = (Data) result;
					// 驱动定义的事件
					List<EventConfig> eventConfigs = serviceConfig.getEventConfigsBySender(context.getServiceId());
					for (EventConfig eventConfig : eventConfigs)
						for (EventHandler eventHandler : eventConfig.getHandlers()) {
							if (eventHandler.canHandle(data))
								EventPublisher.publish(eventHandler.getModuleId(), eventHandler.getServiceId(), data);
						}
					context.setResult(data);
				}
			} catch (Throwable t) {
				if (t instanceof Exception)
					throw (Exception) t;
				else
					throw new RuntimeException(t);
			}
		}

	}
}
