package com.flying.common.util;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Priority;

import com.flying.common.log.Logger;
import com.flying.common.log.util.LoggerStringBuffer;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

public class LogUtils {
	
	private static Map<String,Logger> logMap = Utils.newHashMap();
	
	public static void init(){
		Set<String> set = Application.getInstance().getModules().getLocalModules().keySet();
		for (String key : set) {
			Logger logger = Logger.getLogger(key);
			logMap.put(key, logger);
		}
	}
	
	public static boolean isEnabledFor(LocalModule module,Priority level){
		Logger logger = logMap.get(module.getId());
		return logger.isEnabledFor(level);
	}
	
	public static void log(LocalModule module,Data request,String requestString, String responseString, String function) {
		Principal p = null;
		String requestURI = null;
		if(request!=null){
			p = request.get("Principal");
			requestURI = request.getString("RequestURI");
		}
		log(module,p,requestURI,requestString,responseString,function);
	}
	
	public static void log(LocalModule module,Principal p,String requestURI,String requestString, String responseString, String function) {
		Logger logger = logMap.get(module.getId());
		if(logger==null||!logger.isDebugEnabled()){
			return;
		}
		LoggerStringBuffer ls = new LoggerStringBuffer();
		String id = p == null ? "" : p.getId();
		String name = p == null ? "" : p.getName();
		ls.append("*****").append(function).append("*****\r\n");
		ls.append("|id:").append(id).append("|name:").append(name).append("|url:").append(requestURI).append("\r\n");
		ls.append("|time:").append(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss SSS")).append("\r\n");
		ls.append("|send:").append(requestString).append("\r\n");
		ls.append("|response:").append(responseString).append("\r\n");
		logger.debug(ls.toString() + "\n-------------------------------------------------------------------------");
	}
}
