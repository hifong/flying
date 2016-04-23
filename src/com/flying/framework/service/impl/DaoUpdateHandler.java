package com.flying.framework.service.impl;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.DaoUpdate;
import com.flying.framework.data.Data;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.metadata.Metadata.Field;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;
import com.flying.framework.service.ServiceProxy.MethodParam;

public class DaoUpdateHandler implements ServiceHandler<DaoUpdate> {
	private final static Logger log = Logger.getLogger(DaoUpdateHandler.class);
	
	@Override
	public void handle(DaoUpdate annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();

		if (annotation.position() == DaoUpdate.Position.after_body) {
			context.doChain(request);
			if (context.getResult() != null && context.getResult().contains(Codes.CODE))
				return;
		}
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final List<Field> fields = metadata.getFields();
		final MethodParam[] methodParams = context.getMethodParams();
		
		final List<String> params = Utils.newArrayList();
		final String[] pks = metadata.getPrimaryKey();
		for(MethodParam mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			params.add(mp.getParam().value());
		}
		if(params.isEmpty()) {						//fields from entity
			for(Field f: fields) {
				String name = f.getName();
				boolean isPK = false;
				for(String pk: pks) {
					if(StringUtils.equalsIgnoreCase(pk, name)) {
						isPK = true;
						break;
					}
				}
				if(isPK) continue;
				//
				params.add(name);
			}
			//
			for(int i=0; i < pks.length; i++) {
				params.add(pks[i]);
			}
		}
		//
		final String configSql = module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName);
		final String sql;
		if(StringUtils.isEmpty(annotation.sql()) && StringUtils.isEmpty(configSql)) {	//build entity sql
			final StringBuffer entitySql = new StringBuffer("update " + metadata.getTable() + " set ");
			boolean hasWhere = false;
			int c = 0;
			for(String name: params) {
				
				if(!hasWhere) {
					boolean isPK = false;
					for(String pk: pks) {
						if(StringUtils.equalsIgnoreCase(pk,name)) {
							isPK = true;
							break;
						}
					}
					
					if(isPK) {
						hasWhere = true;
						c = 0;
						entitySql.append(" where ");
					}
				}
				
				if(!hasWhere) {
					if(c > 0) entitySql.append(" , ");
					entitySql.append(name).append("=?");
				}else {
					if(c > 0) entitySql.append(" and ");
					entitySql.append(name).append("=?");
				}
				c++;
			}
			sql = entitySql.toString();
		} else {
			sql = !StringUtils.isEmpty(annotation.sql()) ? annotation.sql():configSql;
		}
		//

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
		for(int i=0; i< params.size(); i++) {
			result.put(params.get(i), values[i]);
		}
		
		context.setResult(result);
		//
		if (annotation.position() == DaoUpdate.Position.before_body) {
			context.doChain(request);
		}
	}
}
