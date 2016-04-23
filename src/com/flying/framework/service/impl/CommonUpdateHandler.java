package com.flying.framework.service.impl;

import java.lang.reflect.Array;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.helper.SequenceHelper;
import com.flying.common.util.Codes;
import com.flying.framework.annotation.CommonUpdate;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class CommonUpdateHandler implements ServiceHandler<CommonUpdate> {
	private final static Logger log = Logger.getLogger(CommonQueryHandler.class);
	
	@Override
	public void handle(CommonUpdate annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();

		if (annotation.position() == CommonUpdate.Position.after_body) {
			context.doChain(request);
			if (context.getResult() != null && context.getResult().contains(Codes.CODE))
				return;
		}
		JdbcTemplate jdbcTemplate = (JdbcTemplate) module.getSpringBeanFactory().getBean("jdbcTemplate");
		final String sql = StringUtils.isEmpty(annotation.usql()) ? module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName) : annotation
				.usql();
		final String[] params = annotation.params();

		final Data result;
		//对删除动作特别处理
		if((context.getMethodName().indexOf("remove") >= 0 || context.getMethodName().indexOf("unpublish") >= 0) && params.length == 1 && request.get(params[0]) != null && request.get(params[0]).getClass().isArray()) {
			String[] ids = request.get(params[0]);
			for(String id: ids) {
				jdbcTemplate.update(sql, new String[]{id});
			}
			result = new Data(Codes.CODE, Codes.SUCCESS);
		} else if(context.getMethodName().indexOf("publish") >= 0 && params.length == 2 && request.get(params[1]) != null && request.get(params[1]).getClass().isArray()) {
			String[] ids = request.get(params[1]);
			for(String id: ids) {
				jdbcTemplate.update(sql, new String[]{request.getString("publisher"), id});
			}
			result = new Data(Codes.CODE, Codes.SUCCESS);
		} else {
			final Object[] values = new Object[params.length];
			for (int i = 0; i < params.length; i++) {
				if (params[i].startsWith("$SEQ.")) {
					values[i] = SequenceHelper.nextVal(params[i].substring(5));
					request.put(params[i], values[i]);
				} else {
					if (params[i].indexOf(":") < 0) {
						Object val = request.get(params[i]);
						if(val != null && val.getClass().isArray()) {
							val = Array.get(val, 0);
						}
						values[i] = val;
					} else {
						String[] x = params[i].split(":");
						values[i] = request.getString(x[0], x[1]);
					}
				}
			}
			//
			log.debug(serviceId+":"+methodName+"-->[" + sql+"] with [" + values +"]");
			//
			final int res = jdbcTemplate.update(sql, values);
			//
			result = new Data(Codes.CODE, res == 1 ? Codes.SUCCESS : Codes.FAIL);
		}
		//result.put(chain.getMethodName() + ".params", params);
		//result.put(chain.getMethodName() + ".values", values);
		context.setResult(result);
		//
		if (annotation.position() == CommonUpdate.Position.before_body) {
			context.doChain(request);
		}
	}

}
