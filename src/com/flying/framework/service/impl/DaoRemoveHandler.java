package com.flying.framework.service.impl;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.DaoRemove;
import com.flying.framework.data.Data;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;
import com.flying.framework.service.ServiceProxy.MethodParam;

public class DaoRemoveHandler implements ServiceHandler<DaoRemove> {
	private final static Logger log = Logger.getLogger(DaoRemoveHandler.class);
	
	@Override
	public void handle(DaoRemove annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();

		if (annotation.position() == DaoRemove.Position.after_body) {
			context.doChain(request);
			if (context.getResult() != null && context.getResult().contains(Codes.CODE))
				return;
		}
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final MethodParam[] methodParams = context.getMethodParams();
		
		final StringBuffer entitySql = new StringBuffer("delete from " + metadata.getTable() + " where ");
		final List<String> params = Utils.newArrayList();
		for(MethodParam mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			if(!params.isEmpty()) entitySql.append(" and ");
			entitySql.append(mp.getParam().value()).append("=? ");
			params.add(mp.getParam().value());
		}
		if(params.isEmpty()) {						//delete rows by primary key
			String[] pks = metadata.getPrimaryKey();
			for(int i=0; i< pks.length; i++) {
				if(i > 0) entitySql.append(" and ");
				entitySql.append(pks[i]).append("=? ");
				params.add(pks[i]);
			}
		}
		//
		final String configSql = module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName);
		final String sql = !StringUtils.isEmpty(annotation.sql()) ? annotation.sql(): !StringUtils.isEmpty(configSql)?configSql: entitySql.toString();
		

		final Data result;

		final Object[] values = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			Object val = request.get(params.get(i));
			if(val != null && val.getClass().isArray()) {
				val = Array.get(val, 0);
			}
			values[i] = val;
		}
		//
		log.debug(serviceId+":"+methodName+"-->[" + sql+"] with [" + StringUtils.join(values, ",") +"]");
		//
		JdbcTemplate jdbcTemplate = (JdbcTemplate) module.getSpringBeanFactory().getBean("jdbcTemplate");
		final int res = jdbcTemplate.update(sql, values);
		//
		result = new Data(Codes.CODE, Codes.SUCCESS, Codes.EFFECT_ROWS, res);
		
		context.setResult(result);
		//
		if (annotation.position() == DaoRemove.Position.before_body) {
			context.doChain(request);
		}
	}
}
