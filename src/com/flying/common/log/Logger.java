package com.flying.common.log;

import org.apache.log4j.Priority;

import com.flying.framework.context.ThreadContext;
import com.flying.framework.module.LocalModule;

public class Logger {
	private static boolean OUTPUT_MODULE_INFO = true;
	
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
	
	public boolean isEnabledFor(Priority level) {
		return logger.isEnabledFor(level);
	}

	private final String moduleInfo() {
		if(!OUTPUT_MODULE_INFO) return "";
		LocalModule m = ThreadContext.getContext() == null? null:ThreadContext.getContext().getModule();
		return m == null?"":"["+m.getId()+"]";
	}
	
	private org.apache.log4j.Logger logger;
	
	public static Logger getLogger(String category) {
		return new Logger(org.apache.log4j.Logger.getLogger(category));
	}
	
	public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
		return new Logger(org.apache.log4j.Logger.getLogger(clazz));
	}

	private Logger(org.apache.log4j.Logger logger) {
		this.logger = logger;
	}
	
	public void debug(Object message, Throwable t) {
		logger.debug(moduleInfo() + message, t);
	}

	public void debug(Object message) {
		logger.debug(moduleInfo() + message);
	}

	public void error(Object message, Throwable t) {
		logger.error(moduleInfo() + message, t);
	}

	public void error(Object message) {
		logger.error(moduleInfo() + message);
	}

	public void fatal(Object message, Throwable t) {
		logger.fatal(moduleInfo() + message, t);
	}

	public void fatal(Object message) {
		logger.fatal(moduleInfo() + message);
	}

	public void info(Object message, Throwable t) {
		logger.info(moduleInfo() + message, t);
	}

	public void info(Object message) {
		logger.info(moduleInfo() + message);
	}

	public void log(Priority priority, Object message, Throwable t) {
		logger.log(priority, moduleInfo() + message, t);
	}

	public void log(Priority priority, Object message) {
		logger.log(priority, moduleInfo() + message);
	}

	public void log(String callerFQCN, Priority level, Object message,
			Throwable t) {
		logger.log(callerFQCN, level, moduleInfo() + message, t);
	}

	public void warn(Object message, Throwable t) {
		logger.warn(moduleInfo() + message, t);
	}

	public void warn(Object message) {
		logger.warn(moduleInfo() + message);
	}
}
