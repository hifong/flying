package com.flying.framework.service.impl;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.helper.SequenceHelper;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.DaoCreate;
import com.flying.framework.data.Data;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.metadata.Metadata.Field;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;
import com.flying.framework.service.ServiceProxy.MethodParam;

public class DaoCreateHandler implements ServiceHandler<DaoCreate> {
	private final static Logger log = Logger.getLogger(DaoCreateHandler.class);
	
	@Override
	public void handle(DaoCreate annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();

		if (annotation.position() == DaoCreate.Position.after_body) {
			context.doChain(request);
			if (context.getResult() != null && context.getResult().contains(Codes.CODE))
				return;
		}
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final List<Field> fields = metadata.getFields();
		final MethodParam[] methodParams = context.getMethodParams();
		
		final StringBuffer entitySql = new StringBuffer("insert into " + metadata.getTable() + " ( ");
		final List<String> params = Utils.newArrayList();
		for(MethodParam mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			if(!params.isEmpty()) entitySql.append(",");
			entitySql.append(mp.getParam().value());
			params.add(mp.getParam().value());
		}
		if(params.isEmpty()) {						//fields from entity
			for(Field f: fields) {
				String name = f.getName();
				
				if(!params.isEmpty()) entitySql.append(",");
				entitySql.append(name);
				params.add(name);
			}
		}
		for(String pk: metadata.getPrimaryKey()) {
			if(!params.contains(pk)) {
				if(!params.isEmpty()) entitySql.append(",");
				entitySql.append(pk);
				params.add(pk);
			}
		}
		entitySql.append(" ) values ( ");
		for(int i=0; i< params.size(); i++) {
			if(i > 0) entitySql.append(",");
			entitySql.append(" ? ");
		}
		entitySql.append(" ) ");
		//
		final String configSql = module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName);
		final String sql = !StringUtils.isEmpty(annotation.sql()) ? annotation.sql(): !StringUtils.isEmpty(configSql)?configSql: entitySql.toString();
		

		final Data result;

		final Object[] values = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			Map<String, Object> field = null;
			for(Map<String, Object> f: fields) {
				if(StringUtils.equalsIgnoreCase((String)f.get("name"), params.get(i))) {
					field = f;
					break;
				}
			}
			//
			final String generator = (String)field.get("generator");
			final String defaultValue = (String)field.get("default-value");
			Object val = request.get(params.get(i));
			if(!StringUtils.isEmpty(generator)) {
				val = SequenceHelper.nextVal(generator);
			}
			if(val != null && val.getClass().isArray()) {
				val = Array.get(val, 0);
			}
			if(val == null) {
				val = defaultValue;
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
		for(int i=0; i< params.size(); i++) {
			result.put(params.get(i), values[i]);
		}
		
		context.setResult(result);
		//
		if (annotation.position() == DaoCreate.Position.before_body) {
			context.doChain(request);
		}
	}
}
