package com.flying.framework.metadata;

import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.Utils;
import com.flying.framework.module.LocalModule;

import net.sf.json.JSONObject;

public class MetadataRepository {
	private final static Logger logger = Logger.getLogger(MetadataRepository.class);
	
	private Map<String, Metadata> repository = Utils.newHashMap();
	private LocalModule module;
	private String moduleHome;
	private String files[];
	
	public MetadataRepository(LocalModule module) {
		String metadata = module.getModuleConfig().getConfig("metadata-files");
		if(!StringUtils.isEmpty(metadata)) {
			files = StringUtils.split(metadata, ";");
			this.module = module;
			this.moduleHome = module.getModuleConfig().getModuleHome();
			this.load();
		}
	}
	
	public Map<String, Metadata> getRepository(){
		return this.repository;
	}
	
	public LocalModule getModule() {
		return this.module;
	}
	
	public Metadata getMetadata(String key) {
		return this.repository.get(key);
	}
	
	public void load() {
		this.repository.clear();
		if(this.files != null)
		for(String f: files) {
			try {
				this.repository.putAll(loadFromFile(moduleHome + f.trim()));
			} catch (Exception e) {
				logger.error("Load metadata fail from file["+moduleHome + f +"]", e);
			}
		}
	}
	
	private Map<String, Metadata> loadFromFile(String file) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		StringBuffer sb = new StringBuffer();
		String line = raf.readLine();
		while(line != null) {
			sb.append(line);
			line = raf.readLine();
		}
		raf.close();
		JSONObject jo = JSONObject.fromObject(new String(sb.toString().getBytes("ISO-8859-1"), "UTF-8"));
		
		Map<String, Metadata> res = Utils.newHashMap();
		for(@SuppressWarnings("rawtypes") Iterator it = jo.keys(); it.hasNext();) {
			String key = it.next().toString();
			res.put(key, new Metadata(jo.getJSONObject(key), this));
		}
		//
		logger.info("Load metadata from file [" + file +"] successfully! size:" + res.size() + "; keys:" + res.keySet());
		//
		return res;
	}
}
