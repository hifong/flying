package com.flying.framework.web.servlet;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import com.flying.common.util.Constants;
import com.flying.common.util.Utils;
/**
 * Provide initialized parameters to axis engine;
 * axis.path 指定axis引擎处理的uri前缀，只有符合该值定义的url请求才会交与axis引擎处理
 * axis.requestType 指定axis 请求用module.xml中定义的那个service.request.* 来处理
 * axis.servicesPath 指定axis提供的服务的uri前缀，必须和server-config.wsdd的配置一致
 * <load-on-startup>1</load-on-startup> 本servlet必须配置该节点，表示在容器启动时就初始化该servlet
 * @author liuyuan
 *
 */
@SuppressWarnings({ "unchecked", "serial" })
public class AxisServlet  extends HttpServlet {
	public void init(){
		Map<String,String> map = (Map<String,String>)this.getServletContext().getAttribute(Constants.AXIS_PATH_MAP);
		if(map==null){
			map = Utils.newHashMap();
			this.getServletContext().setAttribute(Constants.AXIS_PATH_MAP, map);
		}
		Enumeration<String> enumeration = this.getInitParameterNames();
		while(enumeration.hasMoreElements()){
			String name = enumeration.nextElement();
			if(name.startsWith(Constants.AXIS_PATH)){
				String moduleId = name.substring(name.lastIndexOf(".") + 1);
				String value = this.getInitParameter(name);
				map.put(moduleId, value);
			}
		}
		map.put(Constants.AXIS_REQUEST_TYPE, this.getInitParameter(Constants.AXIS_REQUEST_TYPE));
		
		this.getServletContext().setAttribute(Constants.AXIS_SERVLET, this);
	}
}
