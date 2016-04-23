package com.flying.common.util;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

public interface Constants {
	public final static String SUFFIX_PAGE = ".page";
	public final static String SUFFIX_SHTML = ".shtml";
	public final static String SUFFIX_WIDGET = ".widget";
	
	public final static String NO_RIGHT = "NO_RIGHT";
	public final static String TEMPLATE_DEFAULT_VERSION = "DEFAULT";
	
	public final static String INTERCEPTOR_POINTCUT_BEFORE = "before";
	public final static String INTERCEPTOR_POINTCUT_AFTER = "after";
	
	public final static String PRINCIPAL = "principal";
	
	public final static String MODULE_TYPE = "module.type";
	
	public final static String PORTAL_MODULE_TYPE = "portal";
	
	public final static String ADMIN_MODULE_TYPE = "admin";
	/**
	 * PageRequestService中产生的数据以此key写入Request.Attribute
	 */
	public final static String PAGE_DATA = "PAGE_DATA";
	
	public final static String MODULE_UID = LocalModule.class.getName() + ".MODULE_UID";
	//写入Context变量
	public final static String REQUEST_UID = Data.class.getName() + ".REQUEST_UID";
	public final static String MODULE = "Module";
	public final static String MODULE_CONFIG = "ModuleConfig";
	public final static String REQUEST = "Request";
	public final static String PAGE_CONFIG = "PageConfig";
	public final static String WIDGETIDS = "WIDGETIDS";
	public final static String WIDGETID = "WIDGETID";
	public final static String SERVICEID = "SERVICEID";
	public final static String WIDGET_PARAMS = "$WIDGET_PARAMS";
	public final static String TEMPLATE_URI = "TEMPLATE_URI";
	public final static String RESPONSE_HEADERS = "RESPONSE_HEADERS";
	//
	public final static String SERVLET_CONTEXT = "SERVLET_CONTEXT";
	// 
	public final static String PAGE_ERROR = "PageError";
	
	public final static String REMOTE_ERROR = "REMOTE_ERROR";
	public final static String REMOTE_RESULT = "REMOTE_RESULT";
	
	public final static String LOGIN_CHECKIMG = "LOGINCHECKIMG";
	
	public final static String RESUBMIT_TOKEN = "RESUBMIT_TOKEN";	//重复提交避免令牌
	public final static String NEED_SUBMIT_TOKEN = "NEED_SUBMIT_TOKEN";
	
	public final static String REDIRECT_URL = "redirectUrl";
	
	public final static String ACTION_ENTITY = "_ENTITY_";
	public final static String ACTION_TYPE = "ACTION_TYPE"; //action,page,shtml
	public final static String ACTION_TYPE_ACTION = "ACTION";
	
	public final static String CRSF_TOKEN = "CRSF_TOKEN";	//避免跨站攻击令牌
	
	public final static String REMOVE_CONFIG_CACHE = "REMOVE_CONFIG_CACHE";	//避免跨站攻击令牌
	
	public final static String RETRIEVE_SESSION_ID = "USEMY";	//避免跨站攻击令牌
	
	public final static String CHAIN_RESULTS = "chain_results";	//避免跨站攻击令牌

	public final static int PAGE_SIZE = 30;
	
	public static final String AXIS_PATH_MAP = "axis.path.map";
	
	public static final String AXIS_SESSION_MARKER = "axis.isAxisSession";
	
	// Location of the services as defined by the servlet-mapping in web.xml
    public static final String INIT_PROPERTY_SERVICES_PATH = "axis.servicesPath";
    
    public static final String AXIS_PATH = "axis.path";
    
    public static final String AXIS_REQUEST_TYPE = "axis.requestType";
    
    public static final String AXIS_SERVLET = "axis.servlet";
    
    public final static String STRING_CLASS_NAME = String.class.getName();
	public final static String INTEGER_CLASS_NAME = Integer.class.getName();
	public final static String INTEGER_TYPE_NAME = Integer.TYPE.getName();
}
