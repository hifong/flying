package com.flying.framework.request.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;

import com.flying.common.log.Logger;
import com.flying.common.util.Constants;
import com.flying.framework.application.Application;
import com.flying.framework.config.ModuleConfig;
import com.flying.framework.config.PageConfig;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestService;
import com.flying.framework.request.RequestService;
import com.flying.framework.util.SmartyUtils;

/**
 * @author wanghaifeng
 * 
 */
public class PageRequestService extends AbstractRequestService implements RequestService {
	private final static Logger logger = Logger.getLogger(PageRequestService.class);
	private final static Pattern pattern = Pattern.compile(".+\\(.+\\)", Pattern.CASE_INSENSITIVE);

	public void service(LocalModule module, String[] uris, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String page = uris[0];

		if (pattern.matcher(page).matches()) {
			int fi = page.indexOf("(");
			page = page.substring(0, fi);
		}
		//
		ModuleConfig moduleConfig = module.getModuleConfig();
		PageConfig pageConfig = module.getModuleConfig().getPageConfig(page);
		req.setAttribute(Constants.MODULE, module);
		req.setAttribute(Constants.PAGE_CONFIG, pageConfig);
		Data request = DataUtils.convert(module, req);

		Data data = null;
		if (!StringUtils.isEmpty(pageConfig.getModuleId())) {
			try {
				data = Application.getInstance().getModules().getModule(pageConfig.getModuleId()).invoke(pageConfig.getServiceId(), request);
			} catch (Exception e) {
				throw new AppException(e, module.getId(), page);
			}
		} else {
			data = module.invoke(pageConfig.getServiceId(), request);
		}
		req.setAttribute(Constants.PAGE_DATA, data);
		//
		resp.setContentType(serviceConfig.getConfig("content-type"));
		Engine engine = SmartyUtils.getEngine(module);

		Context context = new Context();
		context.set(Constants.MODULE_UID, module);
		context.set(Constants.REQUEST_UID, request);

		context.set(Constants.REQUEST, request.getValues());
		context.set(Constants.MODULE, module);
		context.set(Constants.MODULE_CONFIG, moduleConfig);
		context.set(Constants.PAGE_CONFIG, pageConfig);
		context.set("contextPath", req.getContextPath());
		String templatePath = null;
		if (data != null) {
			context.putAll(data.getValues());
			templatePath = (String) data.get(Constants.TEMPLATE_URI);
		}
		if (StringUtils.isBlank(templatePath)) {
			templatePath = pageConfig.getTemplate();
		}

		//
		try {
			Writer writer = resp.getWriter();
			Template t = engine.getTemplate(pageConfig.getTemplate());
			t.merge(context, writer);
		} catch (Exception e){
			logger.error(e);
			throw new ServletException(e);
		}
	}

}
