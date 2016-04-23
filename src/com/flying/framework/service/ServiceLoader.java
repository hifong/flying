package com.flying.framework.service;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.flying.common.log.Logger;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.data.DataConverter;
import com.flying.framework.exception.ServiceNotFoundException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.RequestFilter;
import com.flying.framework.request.RequestService;

/**
 * @author wanghaifeng
 *
 */
@SuppressWarnings("rawtypes")
public class ServiceLoader {
	private Logger logger = Logger.getLogger(ServiceLoader.class);
	private final static Class[] NON_PROXY_SERVICES = {RequestService.class, RequestFilter.class, ServiceHandler.class, DataConverter.class};
	private final LocalModule module;

	public ServiceLoader(LocalModule module) {
		this.module = module;
	}

	public Object loadService(String serviceId) {
		ServiceConfig config = module.getModuleConfig().getServiceConfig(serviceId);
		if (config.getType() == ServiceConfig.Type.Class) {
			synchronized (serviceId) {
				String className = config.getTarget();
				Object service ;
				try {
					Class clazz = module.newClass(className);
					if(isNonProxyService(clazz)) {
						service = createNonProxyService(module, config, clazz);
					} else {
						service = new ServiceProxy(module, config, clazz).getInstance();
					}
					logger.debug("ServiceLoader.loadService: module[" + module.getId() + "], service[" + serviceId + "] with [" + service + "] success!");
					return service;
				} catch (Exception e) {
					throw new ServiceNotFoundException(e, module.getId(), serviceId);
				}
			}
		} else if (config.getType() == ServiceConfig.Type.Spring) {
			return module.getSpringBeanFactory().getBean(config.getTarget());
		}
		throw new ServiceNotFoundException(module.getId(), serviceId);
	}
	
	@SuppressWarnings("unchecked")
	private boolean isNonProxyService(Class clazz) {
		for(Class c: NON_PROXY_SERVICES) {
			if(c.isAssignableFrom(clazz))
				return true;
		}
		return false;
	}
	
	private Object createNonProxyService(LocalModule module, ServiceConfig config, Class clazz) throws Exception {
		Object service = clazz.newInstance();
		if (PropertyUtils.isWriteable(service, "module"))
			try {
				BeanUtils.setProperty(service, "module", module);
			} catch (Exception e) {
				logger.warn("ServiceProxy.createInstance setModule fail!", e);
			}
		if (PropertyUtils.isWriteable(service, "serviceConfig"))
			try {
				BeanUtils.setProperty(service, "serviceConfig", config);
			} catch (Exception e) {
				logger.warn("ServiceProxy.createInstance setServiceConfig fail!", e);
			}
		return service;
	}
}
