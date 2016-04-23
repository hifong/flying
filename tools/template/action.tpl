package {$PackageName}.action;

import java.util.Map;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import {$PackageName}.service.{$ClassName}Service;
import {$PackageName}.entity.{$ClassName};

import com.aspire.common.util.Constants;
import com.aspire.common.util.Utils;
import com.aspire.framework.annotation.ServiceDefaultAction;
import com.aspire.framework.config.ServiceConfig;
import com.aspire.framework.module.Module;
import com.aspire.framework.proxy.Request;
import com.aspire.framework.service.DispatchActionService;
import com.aspire.framework.util.RequestUtils;

@ServiceDefaultAction(action = "list")
public class {$ClassName}Action extends DispatchActionService {
    
	public Map<String, Object> create(Module module, ServiceConfig config,
			Request request) throws Exception {
		{$ClassName} instance = RequestUtils.newEntityInstance({$ClassName}.class, request);
		{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
		service.create(instance);
		request.getParameters().clear();
		return list(module, config, request);
	}

	public Map<String, Object> edit(Module module, ServiceConfig config,
			Request request) throws Exception {
		Map<String, Object> res = Utils.newHashMap();
		String pk = request.getParameter("{$pk.propertyName}");
		if(pk != null) {
			{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
			res.put("instance", service.findByPrimaryKey(pk));
		}
		res.put(Constants.WIDGETIDS, new String[]{literal}{{/literal}"{$ClassName}.edit"});
		return res;
	}

	public Map<String, Object> update(Module module, ServiceConfig config,
			Request request) throws Exception {
		{$ClassName} instance = RequestUtils.newEntityInstance({$ClassName}.class, request);
		{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
		service.update(instance);
		request.getParameters().clear();
		return list(module, config, request);
	}
	
	public Map<String, Object> view(Module module, ServiceConfig config,
			Request request) throws Exception {
		{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
		Map<String, Object> res = Utils.newHashMap();
		res.put("instance", service.findByPrimaryKey(request.getParameter("{$pk.propertyName}")));
		res.put(Constants.WIDGETIDS, new String[]{literal}{{/literal}"{$ClassName}.view"});
		return res;
	}
	
	public Map<String, Object> list(Module module, ServiceConfig config,
			Request request) throws Exception {
	    int page;
	    int size=10;
	    int countPage = 0;
		{$ClassName} instance = RequestUtils.newEntityInstance({$ClassName}.class, request);
		String pa =request.getParameter("page");
		if ((pa == null) || (pa.length() <= 0)) {
			page = 1;
		} else {
			page = Integer.parseInt(pa);
			if(page<0)
			{
				page=1;
			}
		}
		{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
		int s=service.getSize(instance);
		if(s>0)
		{
			int rs[] = pageAll(s,page,countPage,size);
			page = rs[0];
			countPage = rs[1];
		}
		Map<String, Object> res = Utils.newHashMap();
		List<Map<String, Serializable>> plist = new ArrayList<Map<String, Serializable>>();
		calculatePage(plist,page,countPage);
		res.put("plist", plist);
		res.put("page", page);
		res.put("countPage", countPage);
		res.put("instances", service.list(instance, page, size));
		res.put(Constants.WIDGETIDS, new String[]{literal}{{/literal}"{$ClassName}.list"});
		return res;
	}
		public Map<String, Object> search(Module module, ServiceConfig config,
			Request request) throws Exception {
		int page;
	    int size=10;
	    int countPage = 0;;	
		{$ClassName} instance = RequestUtils.newEntityInstance({$ClassName}.class, request);
		String pa =request.getParameter("page");
		if ((pa == null) || (pa.length() <= 0)) {
			page = 1;
		} else {
			page = Integer.parseInt(pa);
			if(page<0)
			{
				page=1;
			}
		}
		{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
		int s=service.getSize(instance);
		if(s>0)
		{
			int rs[] = pageAll(s,page,countPage,size);
			page = rs[0];
			countPage = rs[1];
		}
		Map<String, Object> res = Utils.newHashMap();
		List<Map<String, Serializable>> plist = new ArrayList<Map<String, Serializable>>();
		calculatePage(plist,page,countPage);
		res.put("instance", instance);
		res.put("plist", plist);
		res.put("page", page);
		res.put("countPage", countPage);
		res.put("instances", service.list(instance, page, size));
		res.put(Constants.WIDGETIDS, new String[]{literal}{{/literal}"{$ClassName}.list"});
		return res;
	}
	public Map<String, Object> delete(Module module, ServiceConfig config,
			Request request) throws Exception {
		{$ClassName}Service service = ({$ClassName}Service)module.getSpringBeanFactory().getBean("{$ClassName}Service");
		String[] pks = request.getParameterValues("{$pk.propertyName}");
		if(pks != null)
			for(String pk: pks) {
				service.delete(pk);
			}
		request.getParameters().clear();
		return list(module, config, request);
	}
}