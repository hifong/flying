package com.flying.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.DaoQuery;
import com.flying.framework.data.Data;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.metadata.Metadata.Field;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;
import com.flying.framework.service.ServiceProxy.MethodParam;

public class DaoQueryHandler implements ServiceHandler<DaoQuery> {
	private final static Logger log = Logger.getLogger(DaoQueryHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public void handle(DaoQuery annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();

		if (annotation.position() == DaoQuery.Position.after_body) {
			context.doChain(request);
			if (context.getResult() != null && context.getResult().contains(Codes.CODE))
				return;
		}
		JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getModule().getSpringBeanFactory().getBean("jdbcTemplate");
		final int page = request.getInt("page", 1) - 1;
		final int size = request.getInt("rows", 10);
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final MethodParam[] methodParams = context.getMethodParams();
		
		final String qsql = StringUtils.isEmpty(annotation.qsql()) ? module.getModuleConfig().getServiceConfig(serviceId).getConfig("qsql." + methodName) : annotation.qsql();
		final String csql = StringUtils.isEmpty(annotation.csql()) ? module.getModuleConfig().getServiceConfig(serviceId).getConfig("csql." + methodName) : annotation.csql();
		
		//find reference columns
		StringBuffer rsql = new StringBuffer();
		for(Field f: metadata.getFields()) {
			if("reference".equalsIgnoreCase((String)f.get("type"))) {
				rsql.append(",");
				final String fieldName 			= f.getName();
				final String referenceEntity 	= f.getRefEntity();
				final String refValueField 		= f.getValueField();
				final String refRelField 		= f.getRefRelField();
				final String relField 			= f.getRelField();
				Metadata rmd = module.getMetadataRepository().getMetadata(referenceEntity);
				rsql.append(" (select f.").append(refValueField).append(" from ").append(rmd.getTable()).append(" f ");
				rsql.append(" where f.").append(refRelField).append("=t.").append(relField).append(") as ").append(fieldName);
			}
		}
		//
		final StringBuffer querySql = new StringBuffer( StringUtils.isEmpty(qsql) ? ("select t.*"+rsql+"   from " + metadata.getTable() +" t "): qsql );
		final StringBuffer countSql = new StringBuffer( StringUtils.isEmpty(csql) ? ("select count(1) as c from " + metadata.getTable()): csql );
		
		final List<String> params = Utils.newArrayList();
		for(MethodParam mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			if(!request.contains(mp.getParam().value())) continue;
			final String svalue = request.getString(mp.getParam().value());
			if(StringUtils.isEmpty(svalue)) continue;
			
			if(params.isEmpty()) {
				querySql.append(" where ");
				countSql.append(" where ");
			} else {
				querySql.append(" and ");
				countSql.append(" and ");
			}
			querySql.append(mp.getParam().value()).append("=?");
			countSql.append(mp.getParam().value()).append("=?");
			params.add(mp.getParam().value());
		}

		final Object[] queryParams = new Object[params.size() + (annotation.pageable() && !annotation.single()?2:0)];
		final Object[] countParams = new Object[params.size()];
		for(int i=0; i < params.size(); i++) {
			queryParams[i] = request.get(params.get(i));
			countParams[i] = request.get(params.get(i));
		}
		if(!StringUtils.isEmpty(annotation.osql()))
			querySql.append(" ").append(annotation.osql());
		if(annotation.pageable() && !annotation.single()) {
			queryParams[queryParams.length - 2] = page * size;
			queryParams[queryParams.length - 1] = size;
			querySql.append(" limit ?, ? ");
		}
		//
		log.debug(serviceId+":"+methodName+"-->[" + querySql+"] with [" + StringUtils.join(queryParams, ",") +"]");
		log.debug(serviceId+":"+methodName+"-->[" + countSql+"] with [" + StringUtils.join(countParams, ",") +"]");
						//
		Data data = null;
		final List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql.toString(), queryParams);
		//serviceResult fields
		final List<Field> serviceResultFields = metadata.getServiceFields();
		if(!serviceResultFields.isEmpty()) {
			for(Map<String, Object> r: rows) {
				for(Field f: serviceResultFields) {
					//invoke service
					Map<String, Object> serviceParamsMap = (Map<String, Object>)f.get("params");
					Data serviceParams = new Data();
					for(String pn: serviceParamsMap.keySet()) {
						Object pv = serviceParamsMap.get(pn);
						if(pn instanceof String && ((String)pv).startsWith("$")) {
							pv = r.get(((String)pv).substring(1));
						}
						serviceParams.put(pn, pv);
					}
					Data d = ServiceHelper.invoke(f.getModuleId(), f.getServiceId(), serviceParams);
					List<Map<String, Object>> drows = d.get("rows");
					//get result
					List<String> list = Utils.newArrayList();
					for(Map<String, Object> dr: drows) {
						Object o = dr.get(f.get("valueField"));
						if(o != null)
							list.add(o.toString());
					}
					//
					r.put(f.getName(), list);
				}
			}
		}
		//
		if (annotation.single()) {
			data = rows.isEmpty() ? request : new Data((Map<String, Object>) (rows.get(0))).merge(request, false);
		} else {
			final int total = StringUtils.isEmpty(countSql.toString()) || !annotation.pageable() || annotation.single()?rows.size() : jdbcTemplate.queryForInt(countSql.toString(), countParams);
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
		if (annotation.position() == DaoQuery.Position.before_body) {
			context.doChain(data);
		}
	}

}
