package com.flying.framework.config;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author wanghaifeng
 *
 */
public class PageConfig extends BaseConfig {
	private static final long serialVersionUID = 2087070638711136399L;
	private String title;
	private final String moduleId;
	private final String serviceId;
	private final String template;

	public PageConfig(String id, String moduleId, String serviceId, String template) {
		super(id);
		this.moduleId = moduleId;
		this.serviceId = serviceId;
		this.template = template;
	}
	
	public PageConfig(String id, String serviceId, String template) {
		this(id, null, serviceId, template);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getTemplate() {
		return this.template;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getModuleId() {
		return moduleId;
	}
}
