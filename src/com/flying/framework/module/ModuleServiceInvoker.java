package com.flying.framework.module;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.ServiceException;
import com.flying.framework.exception.ServiceNotFoundException;

@SuppressWarnings("rawtypes")
class ModuleServiceInvoker {
	private final static Logger logger = Logger.getLogger(ModuleServiceInvoker.class);
	
	private final static Map<String, MethodInfo> methodInfos = Utils.newHashMap();

	private final static String[] 	EMPTY_STRING_ARRAY 	= new String[0];
	private final static long[] 	EMPTY_LONG_ARRAY 	= new long[0];
	private final static int[] 		EMPTY_INT_ARRAY 	= new int[0];
	private final static float[] 	EMPTY_FLOAT_ARRAY 	= new float[0];
	private final static double[] 	EMPTY_DOUBLE_ARRAY 	= new double[0];
	private final static boolean[] 	EMPTY_BOOLEAN_ARRAY = new boolean[0];
	private final static Date[] 	EMPTY_DATE_ARRAY 	= new Date[0];
	
	public static Data invoke(LocalModule module, Data request, String service) {
		String[] tmps = StringUtils.split(service, ":");
		if(tmps.length != 2) {
			throw new ServiceException("Service must be like 'serviceId:methodName'", "-1", module.getId());
		}
		return invoke(module, request, tmps[0], tmps[1]);
	}

	private static MethodInfo getMethodInfo(Object service, String moduleId, String serviceId, String methodName) throws Exception {
		final String key = moduleId+"."+serviceId+":"+methodName;
		if(methodInfos.containsKey(key)) 
			return methodInfos.get(key);

		final Method[] methods = service.getClass().getDeclaredMethods();
		Method method = null;
		for(Method m: methods){
			if(m.getName().equals(methodName)) {
				method = m;
				break;
			}
		}
		if(method == null) throw new ServiceNotFoundException(moduleId, serviceId+":"+methodName);
		
		//
		Class[] argTypes = method.getParameterTypes();
		Class superclass = service.getClass().getSuperclass();
		if(service.getClass().getName().indexOf("$$EnhancerByCGLIB$$") <0 ) superclass = null;
		@SuppressWarnings("unchecked")
		Method supermethod = superclass == null? null: superclass.getDeclaredMethod(methodName, argTypes);
		//
		final Parameter[] parameters = supermethod == null? method.getParameters(): supermethod.getParameters();
		final Param[] params = new Param[parameters.length];
		for(int i=0; i < parameters.length; i++) {
			params[i] = parameters[i].getAnnotation(Param.class);
		}
		
		MethodInfo methodInfo = new MethodInfo(method, parameters, params);
		methodInfos.put(key, methodInfo);
		//
		return methodInfo;
	}
	
	/**
	 * 调用本地服务
	 * @param module
	 * @param request
	 * @param serviceId
	 * @return
	 */
	public static Data invoke(LocalModule module, Data request, String serviceId, String methodName) {
		Throwable cause = null;
		if(StringUtils.isEmpty(serviceId)) return new Data();
		
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(module.getClassLoader());
		try {
			Object service = module.getService(serviceId);
			
			//
			MethodInfo methodInfo = getMethodInfo(service, module.getId(), serviceId, methodName);
			final Method method = methodInfo.method;
			final Parameter[] parameters = methodInfo.parameters;
			final Object[] args = new Object[parameters.length];
			for(int i=0; request != null && i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				Param param = methodInfo.params[i];
				
				Class type = parameter.getType();
				if(param == null && type == Data.class) {
					args[i] = request;
					continue;
				}
				//
				String key = param == null?parameter.getName():param.value();
				if(type == Integer.class || "int".equals(type.toString()))
					args[i] = request.getInt(key, 0);
				else if(type == Long.class || "long".equals(type.toString()))
					args[i] = request.getLong(key, 0);
				else if(type == Float.class || "float".equals(type.toString()))
					args[i] = request.getFloat(key, 0);
				else if(type == Double.class || "double".equals(type.toString()))
					args[i] = request.getDouble(key, 0);
				else if(type == Date.class)
					args[i] = request.getDate(key);
				else if(type == Boolean.class || "boolean".equals(type.toString())) 
					args[i] = request.getBoolean(key, false);
				else if(type == String.class)
					args[i] = request.getString(key);
				else if(type == EMPTY_STRING_ARRAY.getClass())
					args[i] = request.getStrings(key);
				else if(type == EMPTY_LONG_ARRAY.getClass())
					args[i] = request.getLongs(key, 0);
				else if(type == EMPTY_INT_ARRAY.getClass())
					args[i] = request.getInts(key, 0);
				else if(type == EMPTY_FLOAT_ARRAY.getClass())
					args[i] = request.getFloats(key, 0);
				else if(type == EMPTY_DOUBLE_ARRAY.getClass())
					args[i] = request.getDoubles(key, 0);
				else if(type == EMPTY_DATE_ARRAY.getClass())
					args[i] = request.getDates(key);
				else if(type == EMPTY_BOOLEAN_ARRAY.getClass())
					args[i] = request.getBooleans(key);
				else {
					args[i] = request.get(key);
				}
			}
			return (Data)method.invoke(service, args);
			
			//
		} catch (Exception e) {
			logger.error(serviceId+":"+methodName + " invoke error:"+e.getMessage() +";\ndetail:",e);
			cause = Utils.getBusinessCause(e);
			if(cause instanceof AppException) {
				throw (AppException)cause;
			} else if(e instanceof NoSuchMethodException) {
				throw new ServiceNotFoundException(module.getId(), serviceId);
			} else {
				throw new ServiceException(cause, module.getId(), serviceId);
			}
		}finally{
			Thread.currentThread().setContextClassLoader(threadClassLoader);
		}
	}
	
	static class MethodInfo {
		final Method method;
		final Parameter[] parameters;
		final Param[] params;
		
		MethodInfo(Method method, Parameter[] parameters, Param[] params) {
			this.method = method;
			this.parameters = parameters;
			this.params = params;
		}
	}
	
}
