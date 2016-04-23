package com.flying.framework.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.flying.common.util.Utils;
import com.flying.common.xml.XMLElementHandler;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;

/**
 * @author hifong
 *
 */
public class ApplicationConfig extends Data {
	private static final long serialVersionUID = 5036625458848013892L;
	public ApplicationConfig(final File file) {
		super(readApplicationFromFile(file));
	}

	@SuppressWarnings("unchecked")
	static Map<String, Object> readApplicationFromFile(File file) {
		final Map<String, Object> data = Utils.newHashMap();
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(file);
		} catch (JDOMException e1) {
			throw new AppException("load application config from " + file.getAbsolutePath() +" error!",e1);
		} catch (IOException e1) {
			throw new AppException("load application config from " + file.getAbsolutePath() +" error!",e1);
		}
		Element root = doc.getRootElement();
		
		List<Element> l = root.getChildren();
		for(Element e:l) {
			if("group-configs".equals(e.getName())) {
				Map<String, Object> groupConfigs = Utils.newHashMap();
				new XMLElementHandler(){
					@Override
					public void handle(Map<String, Object> data, Element ele) {
						List<Element> l = ele.getChildren();
						for(Element e: l) {
							String group = e.getAttributeValue("group");
							Map<String, Object> configs = Utils.newHashMap();
							List<Element> l2 = e.getChildren();
							for(Element e2: l2) {
								configs.put(e2.getAttributeValue("name"), e2.getTextTrim());
							}
							data.put(group, configs);
						}
					}}.handle(groupConfigs, e);
				data.put("group-configs", new Data(groupConfigs));
			} else if("modules".equals(e.getName())) {
				Map<String, Object> modules = Utils.newHashMap();
				new XMLElementHandler(){
					@Override
					public void handle(Map<String, Object> data, Element ele) {
						List<Element> l = ele.getChildren();
						for(Element e: l) {
							String id = e.getAttributeValue("id");
							Map<String, Object> m = Utils.newHashMap();
							m.put("id", id);
							m.put("locate", e.getAttributeValue("locate"));
							m.put("version", e.getAttributeValue("version"));
							m.put("path", e.getTextTrim());
							//
							List<Element> configs = e.getChildren();
							if(configs != null) {
								Data dconfigs = new Data();
								for(Element c:configs) {
									dconfigs.put(c.getAttributeValue("name"), c.getTextTrim());
								}
								m.put("configs", dconfigs);
							}
							data.put(id, new Data(m));
						}
					}}.handle(modules, e);
				data.put("modules", new Data(modules));
			} else {
				data.put(e.getName(), e.getTextTrim());
			}
		}
		return data;
	}
	
	public Data getConfigs(String group) {
		return super.getValue("group-configs");
	}
	
	public String getConfig(String group, String key) {
		return getConfigs(group).getString(key);
	}
	
	public Data getModules() {
		return super.getValue("modules");
	}
}
