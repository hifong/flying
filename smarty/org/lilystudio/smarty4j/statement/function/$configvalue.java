package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.cache.Cache;
import com.flying.common.cache.CacheManager;
import com.flying.common.cache.HashMapCache;
import com.flying.common.util.Constants;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

public class $configvalue extends LineFunction {
	private static ParameterCharacter[] definitions = {
		new ParameterCharacter(ParameterCharacter.STROBJECT, null, "group"),
		new ParameterCharacter(ParameterCharacter.STROBJECT, null, "key")
	};

	
	public void execute(Context context, Writer writer, Object[] args)
			throws Exception {
		String group = (String)args[0];
		String key = (String)args[1];
		LocalModule module = (LocalModule) context.get(Constants.MODULE_UID);
		String value = getConfigValue(module, group, key);
		if(value == null) value = "";
		writer.write(value);
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}

	//-----------------------获取配置
	@SuppressWarnings("unchecked")
	private static synchronized Cache<Data> getConfigCache(String moduleId) {
		String cid = moduleId + ".Config";
		if(CacheManager.getInstance().getCaches().containsKey(cid)) {
			return CacheManager.getInstance().getCaches().get(cid);
		} else {
			Cache<Data> c = new HashMapCache<Data>(cid);
			return c;
		}
	}
	
	//根据配置键，码返回配置记录，参数：KEY，CODE，
	public static Data getConfigItem(LocalModule module,String group, String key) throws Exception {
		final String uid = group + "." + key;
		Cache<Data> cache = getConfigCache(module.getId());
		if(cache.containsKey(uid) && Application.getInstance().isProductMode()) {
			return cache.get(uid);
		} else {
			Data req = new Data();
			req.put("code", group);
			req.put("key", key);
			Data res = ServiceHelper.invoke("pas", "ConfigAction:getConfigItem", req);
			cache.put(uid, res);
			return res; 
		}
	}
	
	public static String getConfigValue(LocalModule module, String group, String key) throws Exception {
		Data values = getConfigItem(module, group, key);
		if(values != null && !values.isEmpty()) 
			return (String)values.get("value");
		else
			return null;
	}
}