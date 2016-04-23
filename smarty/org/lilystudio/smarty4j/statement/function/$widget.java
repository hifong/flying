package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.util.Constants;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.widget.SmartyWidget;

/**
 * @author wanghaifeng
 * 
 */
public class $widget extends LineFunction {

	private static ParameterCharacter[] definitions = {
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "widgetId"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "moduleId"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "serviceId"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "template"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "params")
		};

	
	public void execute(Context context, Writer writer, Object[] args)
			throws Exception {
		String widgetId = (String) args[0];
		String moduleId = (String) args[1];
		String serviceId = (String) args[2];
		String template = (String) args[3];
		String params = (String)args[4];
		LocalModule module = (LocalModule) context.get(Constants.MODULE_UID);
		
		Data request = new Data(((Data) context.get(Constants.REQUEST_UID)).getValues()); 
		if(params != null) {
			String[] tmps = StringUtils.split(params, ";");
			for(String tmp: tmps) {
				if(tmp != null) {
					String[] kv = StringUtils.split(tmp, "=");
					if(kv.length == 2)
						request.put(kv[0].trim(), kv[1].trim());
				}
			}
		}
		
		SmartyWidget widget = null;
		if(!StringUtils.isEmpty(widgetId))
			widget = new SmartyWidget(context, module, widgetId);
		else
			widget = new SmartyWidget(context, module, moduleId, serviceId, template);
		
		widget.output(request, writer);
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
