package com.flying.framework.remote.hessian;

import com.flying.framework.security.Principal;

/**
 * @author wanghaifeng
 *
 */
public interface RemoteService {
	RemoteValue invoke(Principal principal, String moduleId, String serviceId, RemoteValue request) throws Exception;
}
