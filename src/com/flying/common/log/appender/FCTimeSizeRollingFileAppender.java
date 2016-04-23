package com.flying.common.log.appender;

import java.io.File;

/**
 * 使用配置的日志根目录
 * @author wanghaifeng
 *
 */
public class FCTimeSizeRollingFileAppender extends TimeSizeRollingFileAppender {

	public void setFile(String file) {
		this.fileName = System.getProperty("LOGROOT") + File.separator + file;
        int index = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));
		if (index > 0) {
			String sPath = fileName.substring(0, index);
			File path = new File(sPath);
			if (!path.exists()) {
				path.mkdirs();
			}
		}
	}

}
