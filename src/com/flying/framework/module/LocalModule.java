package com.flying.framework.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.flying.common.config.ConfigUtils;
import com.flying.common.log.Logger;
import com.flying.common.util.ClassUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.LoadOnStartup;
import com.flying.framework.cache.Cache;
import com.flying.framework.cache.CacheProvider;
import com.flying.framework.cache.Clearable;
import com.flying.framework.config.ModuleConfig;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataConverter;
import com.flying.framework.data.DataDecorator;
import com.flying.framework.exception.ModuleInitializeException;
import com.flying.framework.exception.ServiceNotFoundException;
import com.flying.framework.metadata.MetadataRepository;
import com.flying.framework.module.LocalModuleClassPathScanner.ServiceClass;
import com.flying.framework.request.RequestFilter;
import com.flying.framework.request.RequestService;
import com.flying.framework.security.Principal;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceLoader;
import com.flying.framework.service.SpringBeanFactory;

import java.util.Set;

/**
 * LocalModule
 * @author wanghaifeng
 *
 */
public class LocalModule extends Module {
	private final static Logger logger = Logger.getLogger(LocalModule.class);

	private ModuleConfig moduleConfig;
	
	private ClassLoader classLoader;
	private ServiceLoader serviceLoader;
	
	private final Map<String, Object> beans = Utils.newHashMap();
	private final Map<String, Object> services = Utils.newHashMap();
	private final Map<String, RequestService> requestServices = Utils.newHashMap();
	@SuppressWarnings("rawtypes")
	private final Map<String, DataConverter> dataConverters = Utils.newHashMap();
	@SuppressWarnings("rawtypes")
	private final Map<String, ServiceHandler> annotationHandlers = Utils.newHashMap();
	private final List<ModuleEvent> events = Utils.newArrayList();
	
	private final Map<String, List<RequestFilter>> filters = Utils.newHashMap();
	
	private final MetadataRepository metadataRepository;
	//
	private Cache cache;
	//
	public static LocalModule newInstance(String id, String path, Data configs) {
		LocalModule m = new LocalModule(id, path, configs) ;
		return m;
	}
	
	private LocalModule(String id, String path, Data configs) {
		super(id, path, configs);
		long start = System.currentTimeMillis();
		
		try {
			this.moduleConfig = new ModuleConfig(path);
			this.classLoader = new LocalModuleClassLoader(this.moduleConfig, ServiceLoader.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(this.classLoader);
			
			this.serviceLoader = new ServiceLoader(this);
			this.loadServiceClasses();
			
			this.initBeans();
			this.initRequestServices();
			this.initDataConverters();
			this.initMethodAnnotationHandlers();
			this.initEvents();
			//
			this.metadataRepository = new MetadataRepository(this);
			//
			logger.info("Module load success, module id[" + id + "], path["+path+"], time:[" + (System.currentTimeMillis() - start)+"]");
		} catch (Exception e) {
			logger.error("Init bean(ID:"+id+"; Path:"+path+") fail!", e);
			if(e instanceof ModuleInitializeException) {
				throw (ModuleInitializeException)e;
			} else {
				throw new ModuleInitializeException(e, this.getId(), path);
			}
		}
	}
	
	private void loadServiceClasses() {
		LocalModuleClassPathScanner cp = new LocalModuleClassPathScanner(this);
		Set<ServiceClass> serviceClasses = cp.scan();
		for(ServiceClass sc: serviceClasses) {
			if(!this.moduleConfig.getServiceConfigs().containsKey(sc.getService().value())) {
				final ServiceConfig serviceConfig = new ServiceConfig(sc.getService(), sc.getServiceClass());
				this.moduleConfig.getServiceConfigs().put(sc.getService().value(), serviceConfig);
				this.moduleConfig.getServiceConfigs().put(sc.getServiceClass().getName(), serviceConfig);
			}
		}
	}

	private void initBeans() {
		logger.debug("LocalModule initBeans");
		Map<String, String> beanConfigs = this.moduleConfig.getBeans();
		for(Entry<String, String> e: beanConfigs.entrySet()) {
			final String beanId = e.getKey();
			final String beanClass = e.getValue();
			try {
				Class<?> clazz = newClass(beanClass);
				Constructor<?> c = ClassUtils.getConstructorIfAvailable(clazz, LocalModule.class);
				Object bean = null;
				if(c != null) {
					bean = c.newInstance(this);
				} else {
					c = ClassUtils.getConstructorIfAvailable(clazz);
					if(c != null )
						bean = c.newInstance();
				}
				
				//
				Method[] methods = clazz.getMethods();
				for(Method m: methods) {
					if(m.getAnnotation(LoadOnStartup.class) != null) {
						Class<?>[] pts = m.getParameterTypes();
						if(pts == null || pts.length == 0) 
							m.invoke(bean, new Object[]{});
					}
				}
				
				//
				beans.put(e.getKey(), bean);
				logger.debug("LocalModule initBeans " + e.getKey() + ":" + bean);
			}catch (Exception e2) {
				logger.error("LocalModule["+this.id+"] initBeans fail when init bean(id["+beanId+"],class["+beanClass+"]), because of ", e2);
				throw new ModuleInitializeException(e2, this.id, this.path);
			}
			if(beans.containsKey(CacheProvider.class.getName())) {
				CacheProvider cp = (CacheProvider)beans.get(CacheProvider.class.getName());
				this.cache = cp.getCache(this);
			}
		}
	}
	
	private void initRequestServices() {
		logger.debug("Module initRequestServices");
		for(Entry<String, String> entry: moduleConfig.getRequests().entrySet()) {
			String type = entry.getKey();
			String service = entry.getValue();
			this.requestServices.put(type, (RequestService)getService(service));
		}
	}
	
	private void initDataConverters() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.debug("Module initDataConverters");
		for(Entry<String, String> entry: moduleConfig.getConverters().entrySet()) {
			String type = entry.getKey();
			String service = entry.getValue();
			this.dataConverters.put(type, (DataConverter<?>)getService(service));
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void initMethodAnnotationHandlers() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.debug("Module initMethodAnnotationHandlers");
		for(Entry<String, String> entry: moduleConfig.getAnnotations().entrySet()) {
			String type = entry.getKey();
			String service = entry.getValue();
			this.annotationHandlers.put(type, (ServiceHandler)getService(service));
		}
	}
	
	private void initEvents() {
		logger.debug("Module initEvents");
		List<String[]> eventConfigs = this.moduleConfig.getEvents();
		for(String[] ss: eventConfigs) {
			this.events.add(new ModuleEvent(this, ss));
		}
	}
	
	public void onLoad() {
		for(ModuleEvent me: this.events) {
			if("load".equalsIgnoreCase(me.getType()))
				me.fire();
		}
	}

	public void onUnload() {
		for(ModuleEvent me: this.events) {
			if("unload".equalsIgnoreCase(me.getType()))
				me.fire();
		}
		if(this.cache != null && this.cache instanceof Clearable) ((Clearable)this.cache).clear();
	}

	public String getPath() {
		return path.endsWith(ConfigUtils.fileSeparator)? path : path + ConfigUtils.fileSeparator;
	}

	public String getName() {
		return this.moduleConfig.getName();
	}

	public String getDesc() {
		return this.moduleConfig.getDesc();
	}
	
	public ModuleConfig getModuleConfig() {
		return this.moduleConfig;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public Map<String, Object> getBeans() {
		return this.beans;
	}
	
	public Map<String, RequestService> getRequestServices() {
		return this.requestServices;
	}
	
	public boolean canHandleRequest(String requestType) {
		return this.requestServices.containsKey(requestType);
	}
	
	public RequestService getRequestService(String requestType) {
		if(!this.requestServices.containsKey(requestType)) throw new ServiceNotFoundException(this.id, requestType);
		return this.requestServices.get(requestType);
	}
	
	@SuppressWarnings("rawtypes")
	public DataConverter getDataConverter(String requestType) {
		if(!this.dataConverters.containsKey(requestType)) throw new ServiceNotFoundException(this.id, requestType);
		return this.dataConverters.get(requestType);
	}

	@SuppressWarnings("rawtypes")
	public ServiceHandler getAnnotationHandler(String annotationClassName) {
		if(!this.annotationHandlers.containsKey(annotationClassName)) return null;
		return this.annotationHandlers.get(annotationClassName);
	}
	
	public List<RequestFilter> getFilters(String uri) {
		if(this.filters.containsKey(uri)) return filters.get(uri);
		synchronized(this) {
			if(this.filters.containsKey(uri)) return filters.get(uri);
			List<RequestFilter> filters = Utils.newArrayList();
			List<String> configList = this.moduleConfig.getFilters();
			if(configList != null)
			for(String item: configList) {
				ServiceConfig serviceConfig = this.moduleConfig.getServiceConfig(item);
				if(serviceConfig == null) throw new ServiceNotFoundException(this.getId(), item);
				try {
					RequestFilter filter = (RequestFilter)getService(item);
					if(filter.isMapping(uri))
						filters.add(filter);
				} catch (Exception e) {
					logger.warn("Initialize filter " + item + " fail, ignored!, reason is :", e);
				}
			}
			this.filters.put(uri, filters);
			return filters;
		}
	}

	public Object getBean(String id) {
		return this.beans.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getService(String serviceId) {
		if(this.services.containsKey(serviceId)) {
			return (T)this.services.get(serviceId);
		} else {
			Object service = this.serviceLoader.loadService(serviceId);
			ServiceConfig config = this.moduleConfig.getServiceConfig(serviceId);
			if(config.isSingleInstance()) {
				this.services.put(serviceId, service);
				if(config.getType() == ServiceConfig.Type.Class) {
					String className = config.getTarget();
					this.services.put(className, service);
				}
			}
			return (T)service;
		}
	}
	
	public <T> T getService(Class<?> cls) {
		return getService(cls.getName());
	}

	public DataDecorator getParameterDecorator() {
		return newBeanInstance(DataDecorator.class);
	}
	
	public SpringBeanFactory getSpringBeanFactory() {
		return newBeanInstance(SpringBeanFactory.class);
	}
	
	public MetadataRepository getMetadataRepository() {
		return metadataRepository;
	}

	public RequestService newRequestService(final String type) {
		return (RequestService)this.getBean("service.request." + type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T newBeanInstance(Class<T> t) {
		try {
			return (T)getBean(t.getName());
		} catch (Exception e) {
			logger.error("newBeanInstance fail!", e);
			return null;
		}
	}

	private Map<String, Class<?>> classes = Utils.newHashMap();
	public Class<?> newClass(String className, boolean initialize) throws ClassNotFoundException {
		if(classes.containsKey(className))
			return classes.get(className);
		else {
			Class<?> c = Class.forName(className, initialize, classLoader);
			classes.put(className, c);
			return c;
		}
	}
	
	public Class<?> newClass(String className) throws ClassNotFoundException {
		return newClass(className, true);
	}
	
	public Cache getCache() {
		return this.cache;
	}
	
	public void startThread(Runnable r) {
		Thread t = new Thread(r);
		t.start();
	}
	
	@Override
	public Data invoke(String serviceId, Data request) {
		
		LocalModule currentModule = ThreadContext.getContext().getModule();
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		String sid = ThreadContext.getContext().getServiceId();
		Data req = ThreadContext.getContext().getRequest();
		Principal p = ThreadContext.getContext().getPrincipal();

		Data response = null;
		try {
			Thread.currentThread().setContextClassLoader(this.getClassLoader());
			ThreadContext.getContext().reset(this, serviceId, request, p);
			
			response = ModuleServiceInvoker.invoke(this, request, serviceId);
			
			return response;
		} finally {
			Thread.currentThread().setContextClassLoader(threadClassLoader);
			ThreadContext.getContext().reset(currentModule, sid, req, p);
		}
	}
	
	public String toString() {
		return "Module " + this.getId() + " @ " + this.path;
	}
	
}
