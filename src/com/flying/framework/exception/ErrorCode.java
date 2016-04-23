package com.flying.framework.exception;

public interface ErrorCode {
	public final static String INTERNAL_ERROR = "500";	//服务器内部错误
	public final static String CONFIG_ERROR = "501";	//配置错误
	public final static String INIT_ERROR = "502";		//初始化错误
	public final static String SERVICE_ERROR = "504";	//服务错误
	public final static String FILE_MAX_SIZE_ERROR = "505";	//服务错误

	public final static String OBJECT_NOT_FOUND = "404";	//服务器内部错误
	public final static String SECURITY_ERROR = "403";	//安全错误
	
}
