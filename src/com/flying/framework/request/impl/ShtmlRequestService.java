package com.flying.framework.request.impl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateException;

import com.flying.common.config.ConfigUtils;
import com.flying.common.log.Logger;
import com.flying.common.util.Constants;
import com.flying.framework.config.ModuleConfig;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestService;
import com.flying.framework.request.RequestService;
import com.flying.framework.security.Principal;
import com.flying.framework.util.SmartyUtils;

/**
 * @author wanghaifeng
 *
 */
public class ShtmlRequestService extends AbstractRequestService implements RequestService {
	private final static Logger logger = Logger.getLogger(ShtmlRequestService.class);

	public void service(LocalModule module, String[] uris, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuffer page = new StringBuffer();
		for (int i = 0; i < uris.length; i++) {
			if (i > 0)
				page.append(ConfigUtils.fileSeparator);
			page.append(uris[i]);
		}

		Data request = DataUtils.convert(module, req);
		ModuleConfig moduleConfig = module.getModuleConfig();

		resp.setContentType(moduleConfig.getConfig("contentType"));
		Writer writer = resp.getWriter();

		Engine engine = SmartyUtils.getEngine(module);

		Context context = new Context();
		context.set(Constants.MODULE_UID, module);
		context.set(Constants.REQUEST_UID, request);

		context.set(Constants.REQUEST, request.getValues());
		context.set(Constants.MODULE, module);
		context.set(Constants.MODULE_CONFIG, moduleConfig);
		context.set("contextPath", req.getContextPath());
		context.set("requestURL", req.getRequestURL());
		context.set("requestURI", req.getRequestURI());
		context.set("principal", req.getSession().getAttribute(Principal.PRINCIPAL));
		
		//
		Template template;
		try {
			template = engine.getTemplate(moduleConfig.getStaticPath() + page + ".tpl");
			template.merge(context, writer);
		} catch (TemplateException e) {
			logger.error(e);
		}
	}

}
