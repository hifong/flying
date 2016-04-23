package com.flying.common.job;

import java.util.Map;

import com.flying.common.log.Logger;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.data.Data;

public class JobService {
	private final static Logger log = Logger.getLogger(JobService.class);
	
	private String jobName;
	private String moduleId;
	private String serviceId;
	private String method;
	private Map<String, Object> props;

	public void setJobName(String jn) {
		this.jobName = jn;
	}
	
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setProps(Map<String, Object> props) {
		this.props = props;
	}

	public void work() throws Exception {
		Data result = null;
		Exception e = null;
		try {
			result = ServiceHelper.invoke(moduleId, serviceId+":"+method, new Data(props));
		} catch(Exception e2) {
			e = e2;
		} finally {
			log.info(String.format("Job [%s] with service:[%s:%s]@[%s], result is[%s], exception is[%s]", 
					jobName, serviceId, method, moduleId, result == null?0:result.toString(), e == null?"no error":e.toString()));
		}
	}
}
