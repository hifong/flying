package com.flying.common.helper;

import com.flying.common.util.ServiceHelper;
import com.flying.framework.data.Data;

public abstract class ConfigHelper {
	public static Data getConfig(String category) throws Exception {
		return ServiceHelper.invoke("pas", "ConfigService:getConfigsByCategory", new Data("category", category));
	}
}
