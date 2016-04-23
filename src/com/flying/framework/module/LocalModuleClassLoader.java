package com.flying.framework.module;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.flying.framework.config.ModuleConfig;

/**
 * @author wanghaifeng
 *
 */
public class LocalModuleClassLoader extends URLClassLoader{
	
	public LocalModuleClassLoader(ModuleConfig moduleConfig, ClassLoader parent) throws MalformedURLException {
		super(getURLs(moduleConfig), parent);
	}
	
	private static URL[] getURLs(ModuleConfig moduleConfig) throws MalformedURLException {
		final String libPath = moduleConfig.getLibPath();
		File jarDir = new File(libPath);
		File[] jarFiles = jarDir.listFiles(new FilenameFilter(){
			
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(".jar") && !"framework.jar".equalsIgnoreCase(name));
			}
			
		});
		
		final String classesPath = moduleConfig.getClassesPath();
		ArrayList<URL> list = new ArrayList<URL>(64);
		if(!StringUtils.isEmpty(classesPath)) {
			File file = new File(classesPath);
			if(file.exists() && file.isDirectory()) {
				list.add(file.toURI().toURL());
			}
		}
		if(jarFiles != null)
		for(File file: jarFiles) {
			list.add(file.toURI().toURL());
		}
		URL[] urls = new URL[list.size()];
		list.toArray(urls);
		return urls;
	}
}
