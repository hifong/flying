package com.flying.framework.module;

import java.util.Map;

import com.flying.common.log.Logger;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;
import com.flying.framework.exception.ModuleInitializeException;
import com.flying.framework.exception.ModuleNotFoundException;

/**
 * 管理Module的生命周期
 * @author wanghaifeng
 *
 */
public class Modules {
	private final static Logger logger = Logger.getLogger(Modules.class);
	
	private Map<String, Module> modules = Utils.newHashMap();
	
	public Modules(Data moduleConfigs) {
		synchronized(this) {
			modules.clear();
			
			for(String id: moduleConfigs.keys()) {
				Data m = moduleConfigs.getValue(id);
				
				String locate = m.getString("locate");
				String path = m.getString("path");
				try {
					Module module = ModuleFactory.createModule(id, locate, path, m.getValue("configs"));
					this.registerModule(module);
				} catch (ModuleInitializeException e) {
					logger.error("load module " + id + "(path:" + path + ") fail, because of :" + e, e);
				}
			}
		}
	}
	
	public void registerModule(Module m) {
		if(m == null) return;
		logger.debug("Module " + m.getId() + " registered success!");
		if(modules.containsKey(m.getId())) {
			unregisterModule(modules.get(m.getId()));
		}
		modules.put(m.getId(), m);
		m.onLoad();
	}
	
	public void unregisterModule(Module m) {
		if(m == null) return;
		logger.debug("Module " + m.getId() + " unregistered success!");
		modules.remove(m.getId());
		m.onUnload();
	}

	public Map<String, LocalModule> getLocalModules() {
		Map<String, LocalModule> ms = Utils.newHashMap();
		for(Module m: modules.values()) {
			if(m instanceof LocalModule) {
				ms.put(m.getId(), (LocalModule)m);
			}
		}
		return ms;
	}
	
	public Module getModule(String id) {
		if(!modules.containsKey(id)) throw new ModuleNotFoundException(id);
		Module m = modules.get(id);
		return m;
	}
	
	public LocalModule getLocalModule(String id) {
		Module m = getModule(id);
		if(!(m instanceof LocalModule)) throw new ModuleNotFoundException(id);
		return (LocalModule)m;
	}
	
	public RemoteModule getRemoteModule(String id) {
		Module m = getModule(id);
		if(!(m instanceof RemoteModule)) throw new ModuleNotFoundException(id);
		return (RemoteModule)m;
	}
	
	public boolean exists(String id) {
		return modules.containsKey(id);
	}
}
