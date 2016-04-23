package com.flying.framework.remote;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

public interface RemoteServiceInvoker {
	Data invoke(Principal principal,  LocalModule localModule, String remoteModuleId, String serviceId, Data request) throws Exception;
}
