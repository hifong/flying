package com.flying.common.config;

import java.io.File;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.flying.common.util.Utils;
import com.flying.framework.exception.AppException;

public class ConfigUtils {
	public final static String fileSeparator = System.getProperty("file.separator");
    public final static String LOG_BACKUP_SUFFIX = ".bak";
    
    private static boolean hadInitialized = false;
	
    public synchronized static void setConfigHome(String home) {
    	assert home != null: "Config home cann't be null!";
    	assert new File(home).isDirectory() && new File(home).exists() : "Config home must be a valid file path!";
    	assert !hadInitialized: "Config home had been initialized as " + System.getProperty("APP_CONFIG_HOME");
		if(home != null && !home.endsWith(fileSeparator)) {
			home += fileSeparator;
		}
		hadInitialized = true;
		System.setProperty("APP_CONFIG_HOME", home);
    }
    
	public static String getConfigHome() {
		String home = System.getProperty("APP_CONFIG_HOME");
		if(home == null && !hadInitialized) {
			synchronized(ConfigUtils.class) {
				try {
					File f = new File(Thread.currentThread().getContextClassLoader().getResource("/").toURI());
					home = f.getAbsolutePath();
					if(home != null && !home.endsWith(fileSeparator)) {
						home += fileSeparator;
					}
					System.setProperty("APP_CONFIG_HOME", home);
				} catch (Exception e) {
					try {
						File f = new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI());
						home = f.getAbsolutePath();
						if(home != null && !home.endsWith(fileSeparator)) {
							home += fileSeparator;
						}
						System.setProperty("APP_CONFIG_HOME", home);
					} catch (Exception e2) {
						throw new AppException("APP_CONFIG_HOME not configured!", e);
					}
				}
				hadInitialized = true;
			}
		}
		return home;
	}
	
	private static Map<String, PropertiesConfiguration> configurations = Utils.newHashMap();
	/**
	 * short file name,eg:application.properties
	 * @param file
	 * @return
	 */
	public static PropertiesConfiguration getPropertiesConfiguration(String file) {
		if(configurations.containsKey(file))  return configurations.get(file);
		synchronized(file) {
			if(configurations.containsKey(file))  return configurations.get(file);

			FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
			fileChangedReloadingStrategy.setRefreshDelay(30000);
			PropertiesConfiguration props = new PropertiesConfiguration();
			props.setReloadingStrategy(fileChangedReloadingStrategy);
			try {
				props.load(new File(getFilePath(file)));
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			configurations.put(file, props);
			return props;
		}
	}
	
	public static String getFilePath(String file) {
		return getConfigHome() + file;
	}
	
	public static void main(String[] args) {
		ConfigUtils.setConfigHome("E:\\a");
	}
}
