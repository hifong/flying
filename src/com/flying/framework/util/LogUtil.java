package com.flying.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.framework.context.ThreadContext;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;

public abstract class LogUtil {
	private final static Logger invokerSimple = Logger.getLogger("invokerSimple");
	private final static Logger invokerDetail = Logger.getLogger("invokerDetail");
	/**
	 * 
	 * @param p
	 * @param type
	 * @param toModule
	 * @param serviceId
	 * @param time
	 * @param request
	 * @param response
	 */
	public static void invokeLog(Principal p, String type, String fromModule, String toModule, String serviceId, String methodName, long time, Data request, Data response, Throwable t) {
		if(invokerSimple.isInfoEnabled() || invokerDetail.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("Type:").append(type).append("|");
			sb.append("Principal:").append(p == null?"":p.getId()).append(" | ");
			sb.append("From:").append(fromModule).append(" | ");
			sb.append("To:").append(toModule).append(" | ");
			sb.append("Service:").append(serviceId).append(" | ");
			sb.append("Method:").append(methodName).append(" | ");
			sb.append("Time:").append(time).append(" | ");
			sb.append("TID:").append(ThreadContext.getContext() == null?0:ThreadContext.getContext().getTransactionId()).append(" | ");
			sb.append("RetCode:").append(response == null?"":(""+response.get(Codes.CODE))).append(" | ");
			
			if(invokerSimple.isInfoEnabled()) {
				invokerSimple.info(sb.toString());
			}
			
			if(invokerDetail.isInfoEnabled()) {
				sb.append("\n Request: --> ").append(request);
				sb.append("\n Response: --> ").append(response);
				
				if(t != null){
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					t.printStackTrace(pw);
					sb.append("\n Exception: --> ").append(response);
				}
				invokerDetail.info(sb);
			}
		}
	}
}
