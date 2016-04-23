package com.flying.common.helper;

import com.flying.framework.application.Application;
import com.flying.framework.data.Data;

public abstract class SequenceHelper {
	public static long nextVal(String category) throws Exception {
		
		Data resp = Application.getInstance().getModules().getModule("pas").invoke("SequenceService:nextValue", new Data("category", category));
		return resp.getLong("value", System.currentTimeMillis());
		
	}
}
