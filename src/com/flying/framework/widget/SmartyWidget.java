package com.flying.framework.widget;

import java.io.Writer;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;

import com.flying.common.util.Constants;
import com.flying.framework.config.PageConfig;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceInvoker;
import com.flying.framework.util.SmartyUtils;

/**
 * @author wanghaifeng
 *
 */
public class SmartyWidget implements Widget {
	private final LocalModule module;
	
	private Context parentContext;
	
	private String widgetId;
	private String serviceModuleId;
	private String serviceId;
	private String template;	//涉外模板采用#分开，第一个从moduleConfig获取配置，其他的替换{x}，x为分割的序号
	
	public SmartyWidget(Context parentContext, LocalModule module, String widgetId) {
		this.module = module;
		this.widgetId = widgetId;
		
		PageConfig widgetConfig = this.module.getModuleConfig().getPageConfig(widgetId);
		this.serviceId = widgetConfig.getServiceId();
		this.template = widgetConfig.getTemplate();
		this.parentContext = parentContext;
	}

	public SmartyWidget(Context parentContext, LocalModule module, String serviceModuleId, String serviceId, String template) {
		this.module = module;
		this.template = template;
		this.serviceModuleId = serviceModuleId;
		this.serviceId = serviceId;
		this.parentContext = parentContext;
	}
	
	public void output(Data request, Writer writer) throws Exception {
		Context context = null;
		if(parentContext == null){
			context = new Context();
			context.set(Constants.REQUEST, request);
		}else{
			context = new Context(parentContext);
		}

		//执行业务
		if(this.serviceId != null) {
			final Data data = ServiceInvoker.invoke(module.getId(), serviceModuleId, serviceId, request);
			if(data != null) context.putAll(data.getValues());
		}
		//
		context.set(Constants.MODULE, this.module);
		if(widgetId != null) context.set(Constants.WIDGETID, widgetId);
		if(this.serviceId != null) context.set(Constants.SERVICEID, this.serviceId);
		//
		Template t = null;
		Engine engine;
		if(parentContext != null) {
			engine = parentContext.getTemplate().getEngine();
		} else {
			engine = SmartyUtils.getEngine(module);
		}
		//
		t = engine.getTemplate(template);
		t.merge(context, writer);
	}
	
	
}
