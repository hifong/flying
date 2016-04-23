package com.flying.framework.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.flying.common.log.Logger;
import com.flying.common.util.Utils;
import com.flying.common.util.XMLCursor;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.PageNotFoundException;
import com.flying.framework.exception.ServiceNotFoundException;

public class ModuleConfig extends Data {
	private static final long serialVersionUID = 4046355943075338321L;
	private static final Logger log = Logger.getLogger(ModuleConfig.class);

	private final String moduleHome;
	private final String moduleConfigFile;
	private final List<String> filters = Utils.newArrayList();
	private final List<String[]> imports = Utils.newArrayList();
	private final List<String[]> events = Utils.newArrayList();
	private final Map<String, String> requests = Utils.newHashMap();
	private final Map<String, String> converters = Utils.newHashMap();
	private final Map<String, String> annotations = Utils.newHashMap();
	private final Map<String, String> configs = Utils.newHashMap();
	private final Map<String, String> beans = Utils.newHashMap();

	private final Map<String, ServiceConfig> serviceConfigs = Utils.newHashMap();
	private final Map<String, PageConfig> pageConfigs = Utils.newHashMap();

	public ModuleConfig(String moduleHome) {
		this(new File(moduleHome + File.separator + "config"+File.separator+"module.xml"));
	}

	public ModuleConfig(File moduleFile) {
		this.moduleConfigFile = moduleFile.getAbsolutePath();
		log.debug("ModuleConfig moduleConfigFile[" + this.moduleConfigFile+"]");
		Map<String, Object> data = loadModuleConfigFromFile(moduleFile);
		super.putAll(data);
		this.moduleHome = this.moduleConfigFile.substring(0, this.moduleConfigFile.indexOf("config"));
		log.debug("ModuleConfig Module[" + this.getId() + "]@ModuleHome[" + moduleHome +"]");
		this.processImports();
		this.loadProperties();
	}

	@SuppressWarnings("unchecked")
	private void loadProperties() {
		this.filters.clear();
		this.filters.addAll((List<String>) super.get("filters"));
		log.debug("filters:"+this.filters);

		this.imports.clear();
		this.imports.addAll((List<String[]>) super.get("imports"));
		log.debug("imports:"+this.imports);

		this.events.clear();
		this.events.addAll((List<String[]>) super.get("events"));
		log.debug("events:"+this.events);

		this.requests.clear();
		this.requests.putAll((Map<String, String>) super.get("requests"));
		log.debug("requests:"+this.requests);

		this.converters.clear();
		this.converters.putAll((Map<String, String>) super.get("converters"));
		log.debug("converters:"+this.converters);

		this.annotations.clear();
		this.annotations.putAll((Map<String, String>) super.get("annotations"));
		log.debug("annotations:"+this.annotations);

		this.configs.clear();
		this.configs.putAll((Map<String, String>) super.get("configs"));
		log.debug("configs:"+this.configs);

		this.beans.clear();
		this.beans.putAll((Map<String, String>) super.get("beans"));
		log.debug("beans:"+this.beans);

		this.serviceConfigs.clear();
		if(super.contains("serviceConfigs"))
			this.serviceConfigs.putAll((Map<String, ServiceConfig>) super.get("serviceConfigs"));
		log.debug("serviceConfigs:"+this.serviceConfigs);

		this.pageConfigs.clear();
		if(super.contains("pageConfigs"))
			this.pageConfigs.putAll((Map<String, PageConfig>) super.get("pageConfigs"));
		log.debug("pageConfigs:"+this.pageConfigs);
	}
	
	@SuppressWarnings("unchecked")
	static Map<String, Object> loadModuleConfigFromFile(File file) {
		log.info("Module reading config file:" + file.getAbsolutePath());
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

		final List<String> filters = Utils.newArrayList();
		final List<String[]> imports = Utils.newArrayList();
		final List<String[]> events = Utils.newArrayList();
		final Map<String, String> requests = Utils.newHashMap();
		final Map<String, String> converters = Utils.newHashMap();
		final Map<String, String> annotations = Utils.newHashMap();
		final Map<String, String> configs = Utils.newHashMap();
		final Map<String, String> beans = Utils.newHashMap();

		List<Element> l = root.getChildren();
		for (Element e : l) {
			final String nodeName = e.getName();
			if ("filters".equals(nodeName)) {
				final List<Element> filterNodes = e.getChildren();
				for (Element filterNode : filterNodes) {
					final String filterService = filterNode.getAttributeValue("service");
					filters.add(filterService);
				}
				data.put("filters", filters);
			} else if ("imports".equals(nodeName)) {
				final List<Element> importNodes = e.getChildren();
				for (Element importNode : importNodes) {
					imports.add(new String[] { importNode.getAttributeValue("type"), importNode.getAttributeValue("path") });
				}
				data.put("imports", imports);
			} else if ("events".equals(nodeName)) {
				final List<Element> eventNodes = e.getChildren();
				for (Element eventNode : eventNodes) {
					events.add(new String[] { eventNode.getAttributeValue("type"), eventNode.getAttributeValue("service") });
				}
				data.put("events", events);
			} else if ("requests".equals(nodeName)) {
				final List<Element> requestNodes = e.getChildren();
				for (Element requestNode : requestNodes) {
					requests.put(requestNode.getAttributeValue("type"), requestNode.getAttributeValue("service"));
				}
				data.put("requests", requests);
			} else if ("converters".equals(nodeName)) {
				final List<Element> converterNodes = e.getChildren();
				for (Element converterNode : converterNodes) {
					converters.put(converterNode.getAttributeValue("type"), converterNode.getAttributeValue("service"));
				}
				data.put("converters", converters);
			} else if ("annotations".equals(nodeName)) {
				final List<Element> annotationNodes = e.getChildren();
				for (Element annotationNode : annotationNodes) {
					annotations.put(annotationNode.getAttributeValue("type"), annotationNode.getAttributeValue("service"));
				}
				data.put("annotations", annotations);
			} else if ("configs".equals(nodeName)) {
				final List<Element> configNodes = e.getChildren();
				for (Element configNode : configNodes) {
					configs.put(configNode.getAttributeValue("name"), configNode.getValue().trim());
				}
				data.put("configs", configs);
			} else if ("beans".equals(nodeName)) {
				final List<Element> beanNodes = e.getChildren();
				for (Element beanNode : beanNodes) {
					beans.put(beanNode.getAttributeValue("name"), beanNode.getValue().trim());
				}
				data.put("beans", beans);
			} else {
				final String value = e.getText();
				data.put(nodeName, value);
			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	private void processImports() {
		List<String[]> imports = (List<String[]>) super.get("imports");
		if (imports != null) {
			final List<String> importModules = Utils.newArrayList();
			for (String[] im : imports) {
				String type = im[0];
				String file = im[1];
				log.debug("ModuleConfig processImport type[" + type +"], file[" + file +"]");
				File importConfigFile = new File(moduleHome + File.separator + "config" + File.separator + file);
				if(this.moduleConfigFile.equals(importConfigFile.getAbsolutePath()) || !importConfigFile.exists())
					importConfigFile = new File(Application.getInstance().getConfigFileInHome(file));
				if (!importConfigFile.exists())
					throw new RuntimeException("Module["+this.getId()+"]Import file " + file + " can not be find in " + moduleHome + File.separator + "config" + File.separator + " or " + Application.getInstance().getHome());
				if ("module".equalsIgnoreCase(type)) {
					importModules.add(importConfigFile.getAbsolutePath());
					continue;
				}
				if ("service".equalsIgnoreCase(type)) {
					Map<String, ServiceConfig> serviceConfigs = (Map<String, ServiceConfig>) super.get("serviceConfigs");
					if (serviceConfigs == null) {
						serviceConfigs = Utils.newHashMap();
						super.put("serviceConfigs", serviceConfigs);
					}
					serviceConfigs.putAll(this.loadServiceConfigsFromFile(importConfigFile.getAbsolutePath()));
				} else if ("page".equalsIgnoreCase(type)) {
					Map<String, PageConfig> pageConfigs = (Map<String, PageConfig>) super.get("pageConfigs");
					if (pageConfigs == null) {
						pageConfigs = Utils.newHashMap();
						super.put("pageConfigs", pageConfigs);
					}
					pageConfigs.putAll(this.loadPageConfigsFromFile(importConfigFile.getAbsolutePath()));
				} else {
					log.warn("Module import, unknown import type[" + type + "], import file " + file + " ignored!");
				}
			}
			for (String m : importModules) {
				File f = new File(m);
				ModuleConfig importModule = new ModuleConfig(f);
				this.merge(importModule, false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, ServiceConfig> loadServiceConfigsFromFile(String serviceConfigFileName) {
		log.info("Module import for service from file:" + serviceConfigFileName);
		Map<String, ServiceConfig> serviceConfigs = Utils.newHashMap();
		XMLCursor xml = new XMLCursor(serviceConfigFileName);
		xml.moveIn("service");
		int row = 0;
		while (xml.moveTo(row)) {

			String serviceId = xml.getValue("id");
			String type = xml.getValue("type");
			String target = xml.getValue("target");
			String desc = xml.getValue("desc");
			boolean loadOnStartup = "true".equalsIgnoreCase(xml.getValue("load-on-startup"));
			boolean isSingleInstance = "true".equalsIgnoreCase(xml.getValue("single-instance")) || StringUtils.isEmpty(xml.getValue("single-instance"));

			Map<String, String> configs = null;
			if (xml.moveIn("configs")) {
				configs = xml.getConfigs();
				xml.moveIn("..");
			}

			List<EventConfig> eventConfigs = Utils.newArrayList();
			if (xml.moveIn("events")) {
				List<Element> eventElements = xml.getCurrent().getChildren("event");
				for (Element eventElement : eventElements) {
					String id = eventElement.getAttributeValue("id");
					String sender = eventElement.getAttributeValue("sender");

					List<Element> handlerElements = eventElement.getChildren("handler");
					List<EventHandler> handlers = Utils.newArrayList();
					for (Element e : handlerElements) {
						String mid = e.getAttributeValue("moduleId");
						EventHandler handler = new EventHandler(mid == null ? this.getId() : mid, e.getAttributeValue("serviceId"));

						List<Element> conditionElements = e.getChildren("condition");
						if (conditionElements != null)
							for (Element ce : conditionElements) {
								String k = ce.getAttributeValue("name");
								String v = ce.getText();
								handler.addCondition(k, v);
							}
						handlers.add(handler);
					}
					EventConfig ec = new EventConfig(id, sender);
					ec.getHandlers().addAll(handlers);
					eventConfigs.add(ec);
				}
				xml.moveIn("..");
			}
			ServiceConfig serviceConfig = new ServiceConfig(serviceId, target, desc, type, configs, eventConfigs, loadOnStartup, isSingleInstance);
			serviceConfigs.put(serviceId, serviceConfig);
			serviceConfigs.put(target, serviceConfig);

			row++;
		}
		return serviceConfigs;
	}

	private Map<String, PageConfig> loadPageConfigsFromFile(String pageConfigFileName) {
		log.info("Module import for page " + pageConfigFileName);
		Map<String, PageConfig> pageConfigs = Utils.newHashMap();

		XMLCursor xml = new XMLCursor(pageConfigFileName);
		xml.moveIn("page");
		int row = 0;
		while (xml.moveTo(row)) {
			String id = xml.getValue("id");
			String serviceId = xml.getValue("service-id");
			String moduleId = xml.getValue("module-id");
			String template = xml.getValue("template");
			
			PageConfig pc = new PageConfig(id, StringUtils.isEmpty(moduleId)?this.getId():moduleId, serviceId, template);
			pc.setTitle(xml.getValue("title"));

			if (xml.moveIn("configs")) {
				Map<String, String> configs = xml.getConfigs();
				pc.getConfigs().putAll(configs);
				xml.moveIn("..");
			}

			pageConfigs.put(pc.getId(), pc);
			row++;
		}
		return pageConfigs;
	}

	public String getId() {
		return getString("id");
	}

	public String getName() {
		return getString("name");
	}

	public String getDesc() {
		return getString("desc");
	}

	public String getVersion() {
		return getString("version");
	}

	public String getInnerVersion() {
		return getString("inner-version");
	}

	public String getStaticPath() {
		String path = getString("static-path");
		if(!path.endsWith(File.separator))
			path += File.separator;
		return path;
	}

	public String getTemplateType() {
		return getString("template-type");
	}

	public List<String> getFilters() {
		return this.filters;
	}

	public List<String[]> getImports() {
		return this.imports;
	}

	public List<String[]> getEvents() {
		return this.events;
	}

	public Map<String, String> getRequests() {
		return this.requests;
	}

	public Map<String, String> getConverters() {
		return this.converters;
	}

	public Map<String, String> getAnnotations() {
		return this.annotations;
	}

	public Map<String, String> getConfigs() {
		return this.configs;
	}

	public String getConfig(String key) {
		return this.configs.get(key);
	}

	public Map<String, String> getBeans() {
		return this.beans;
	}

	public String getModuleHome() {
		return this.moduleHome;
	}

	public String getTemplatePath() {
		return this.moduleHome + File.separator + "template" + File.separator;
	}

	public Map<String, ServiceConfig> getServiceConfigs() {
		return serviceConfigs;
	}

	public ServiceConfig getServiceConfig(String serviceId) {
		if (!this.serviceConfigs.containsKey(serviceId))
			throw new ServiceNotFoundException(this.getId(), serviceId);
		return this.serviceConfigs.get(serviceId);
	}

	public Map<String, PageConfig> getPageConfigs() {
		return pageConfigs;
	}

	public PageConfig getPageConfig(String pageId) {
		if (!this.pageConfigs.containsKey(pageId))
			throw new PageNotFoundException(this.getId(), pageId);
		return this.pageConfigs.get(pageId);
	}

	public String getLibPath() {
		return getModuleHome() + File.separator + "WEB-INF" + File.separator + "lib" + File.separator;
	}

	public String getClassesPath() {
		return getModuleHome() + File.separator + "WEB-INF" + File.separator + "classes" + File.separator;
	}

}