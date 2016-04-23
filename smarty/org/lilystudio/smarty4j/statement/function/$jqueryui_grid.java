package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateException;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.log.Logger;
import com.flying.common.util.Constants;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.module.LocalModule;
import com.flying.framework.util.SmartyUtils;

/**
 * @author wanghaifeng
 * 
 */
public class $jqueryui_grid extends LineFunction {
	private final static Logger logger = Logger.getLogger($jqueryui_grid.class);

	private static ParameterCharacter[] definitions = {
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "id"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "url"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "template"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "view"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "params")
		};

	
	public void execute(Context context, Writer writer, Object[] args)
			throws Exception {
		//
		String id 		= (String) args[0];
		String url 		= (String)args[1];
		String template = (String) args[2];
		String view 	= (String) args[3];
		String params 	= (String)args[4];
		
		LocalModule module = (LocalModule) context.get(Constants.MODULE_UID);
		Metadata metadata = module.getMetadataRepository().getMetadata(view);
		if(metadata != null) {
			context.putAll(metadata.getData());
			context.set("metadata", metadata);
		}
		
		if(params != null) {
			String[] tmps = StringUtils.split(params, ";");
			for(String tmp: tmps) {
				if(tmp != null) {
					String[] kv = StringUtils.split(tmp, "=");
					if(kv.length == 2)
						context.set(kv[0].trim(), kv[1].trim());
				}
			}
		}
		
		if(StringUtils.isEmpty(template)) template = module.getModuleConfig().getConfig("jqueryui.datagrid");
		
		Engine engine = SmartyUtils.getEngine(module);

		context.set("id", id);
		context.set("url", url);
		
		Template t;
		try {
			t = engine.getTemplate(template);
			t.merge(context, writer);
		} catch (TemplateException e) {
			logger.error(e);
		}
		
	}
	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
