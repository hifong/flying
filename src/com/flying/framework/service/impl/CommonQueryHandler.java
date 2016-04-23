package com.flying.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.CommonQuery;
import com.flying.framework.data.Data;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class CommonQueryHandler implements ServiceHandler<CommonQuery> {
	private final static Logger log = Logger.getLogger(CommonQueryHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public void handle(CommonQuery annotation, Data request, ServiceHandlerContext context) throws Exception {
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();

		if (annotation.position() == CommonQuery.Position.after_body) {
			context.doChain(request);
			if (context.getResult() != null && context.getResult().contains(Codes.CODE))
				return;
		}
		JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getModule().getSpringBeanFactory().getBean("jdbcTemplate");
		final int page = request.getInt("page", 1) - 1;
		final int size = request.getInt("rows", 10);
		//
		final String[] params = annotation.params();
		final Object[] queryParams = new Object[params.length + (annotation.pageable() && !annotation.single()?2:0)];
		final Object[] totalParams = new Object[params.length];
		for (int i = 0; i < annotation.params().length; i++) {
			if (params[i].indexOf(":") < 0) {
				queryParams[i] = request.getString(params[i]);
				totalParams[i] = request.getString(params[i]);
			} else {
				String[] x = params[i].split(":");
				queryParams[i] = request.getString(x[0], x[1]);
				totalParams[i] = request.getString(x[0], x[1]);
			}
		}
		if(annotation.pageable() && !annotation.single()) {
			queryParams[queryParams.length - 2] = page * size;
			queryParams[queryParams.length - 1] = size;
		}
		//
		final String querySQL = StringUtils.isEmpty(annotation.qsql()) ? context.getModule().getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName)
						: annotation.qsql();
		final String countSQL = StringUtils.isEmpty(annotation.csql()) ? context.getModule().getModuleConfig().getServiceConfig(serviceId)
						.getConfig("sql." + methodName + ".count") : annotation.csql();
		
		log.debug(serviceId+":"+methodName+"-->[" + querySQL+"] with [" + queryParams +"]");
		log.debug(serviceId+":"+methodName+"-->[" + countSQL+"] with [" + totalParams +"]");
						//
		Data data = null;
		@SuppressWarnings("rawtypes")
		final List rows = jdbcTemplate.queryForList(querySQL, queryParams);
		if (annotation.single()) {
			data = rows.isEmpty() ? request : new Data((Map<String, Object>) (rows.get(0))).merge(request, false);
		} else {
			final int total = StringUtils.isEmpty(countSQL)?0: jdbcTemplate.queryForInt(countSQL, totalParams);
			data = new Data(Codes.CODE, Codes.SUCCESS, "rows", rows, "total", total).merge(request, false);
			
			int pageCount = total % size == 0?total/size: (total/size + 1);
			List<Integer> pages = Utils.newArrayList();
			for(int i= Math.max(1, page - 5); i <= Math.min(pageCount, page + 5); i++) {
				pages.add(i);
			}
			data.put("pageNums", pages);
			data.put("pageNum", page + 1);
			data.put("pageSize", size);
			data.put("pageCount", pageCount);
		}
		context.setResult(data);
		if (annotation.position() == CommonQuery.Position.before_body) {
			context.doChain(data);
		}
	}

}
