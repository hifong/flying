package com.flying.framework.request.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.common.log.Logger;
import com.flying.common.util.Constants;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.data.JSONResult;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.ObjectNotFoundException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestService;
import com.flying.framework.request.RequestService;
import com.flying.framework.service.ServiceInvoker;

/**
 * @author wanghaifeng
 *
 */
public class JsonRequestService extends AbstractRequestService implements RequestService {

	private final static Logger logger = Logger.getLogger(JsonRequestService.class);

	public void service(LocalModule module, String[] uris, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Data request = DataUtils.convert(module, req);
		request.put(Constants.ACTION_TYPE, Constants.ACTION_TYPE_ACTION);
		resp.setContentType("application/json");

		JSONResult result = new JSONResult();
		result.setRequest(request);

		String serviceId = uris[0];
		if (!module.getModuleConfig().getServiceConfigs().containsKey(serviceId))
			serviceId += "Action";
		String method = uris[1];
		String serviceModuleId = uris.length == 3 ? uris[2] : module.getId();
		try {
			final Data data = ServiceInvoker.invoke(module.getId(), serviceModuleId, serviceId + ":" + method, request);
			String results = (String) data.get("result");
			result.setResult(results);
			result.setData(data);
		} catch (ObjectNotFoundException e) {
			resp.sendError(404);
			return;
		} catch (AppException e) {
			logger.error(this.getClass().getName(), e);
			result.setResult(e.getErrorCode());
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			logger.error(this.getClass().getName(), e);
			result.setResult("1");
			result.setMessage(e.getMessage());
		}
		String jsonString = result.toJSONString();
		logger.debug(jsonString);
		resp.getWriter().append(jsonString);
		resp.flushBuffer();
	}

}
