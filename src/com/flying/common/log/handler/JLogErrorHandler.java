package com.flying.common.log.handler;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

public class JLogErrorHandler implements ErrorHandler, ErrorCode {

	public JLogErrorHandler() {

	}

	public void setLogger(Logger logger) {

	}

	public void activateOptions() {

	}

	public void error(String message, Exception e, int errorCode,
			LoggingEvent event) {

		error(message, e, errorCode, null);
	}

	public void error(String message) {

	}

	public void setAppender(Appender primary) {

	}

	public void setBackupAppender(Appender backup) {

	}

	public void error(String message, Exception e, int errorCode) {

		System.err.println(message);
		if (e != null) {
			e.printStackTrace();
		}
	}

}
