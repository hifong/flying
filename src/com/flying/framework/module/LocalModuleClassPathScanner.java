package com.flying.framework.module;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Service;

public class LocalModuleClassPathScanner {
	private final static Logger log = Logger.getLogger(LocalModuleClassPathScanner.class);
	
	private final LocalModule module;
	private final String classPath;
	
	public LocalModuleClassPathScanner(LocalModule module) {
		this.module = module;
		this.classPath = module.getModuleConfig().getClassesPath();
	}
	
	public Set<ServiceClass> scan() {
		return this.scan(new File(this.classPath));
	}
	
	public Set<ServiceClass> scan(final File dir) {
		final Set<ServiceClass> set = Utils.newHashSet();
		File[] files = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class") && name.indexOf("$") < 0 || name.indexOf(".") < 0;
			}
			
		});
		for(File f: files) {
			if(f.isFile()) {
				//get class full name
				String fp = f.getAbsolutePath().substring(this.classPath.length() - 1);
				fp = fp.substring(0, fp.lastIndexOf("."));
				fp = StringUtils.replace(fp, File.separator, ".");
				//load class and check service annotation
				try {
					Class<?> cls = module.getClassLoader().loadClass(fp);
					Service service = cls.getAnnotation(Service.class);
					if(service != null) {
						set.add(new ServiceClass(service, cls));
					}
				} catch (ClassNotFoundException e) {
					log.warn("LocalModuleClassPathScanner load class "+fp +" fail ,because of " + e);
				}
			} else if(f.isDirectory()) {
				set.addAll(scan(f));
			}
		}
		return set;
	}
	
	public class ServiceClass {
		private final Service service;
		private final Class<?> serviceClass;
		
		public ServiceClass(Service service, Class<?> serviceClass) {
			this.service = service;
			this.serviceClass = serviceClass;
		}

		public Service getService() {
			return service;
		}

		public Class<?> getServiceClass() {
			return serviceClass;
		}
		
		public String toString() {
			return "serviceId:"+service.value() + "; serviceClass" + serviceClass;
		}
	}
}
