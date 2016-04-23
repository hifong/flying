package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.util.Constants;
import com.flying.framework.module.LocalModule;
import com.flying.framework.util.SmartyUtils;

/**
 * @author wanghaifeng
 * 
 */
public class $include2 extends LineFunction {

	private static ParameterCharacter[] definitions = {
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "file"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "params") };

	public void execute(Context context, Writer writer, Object[] args) throws Exception {
		String file = (String) args[0];
		String params = (String) args[1];

		if (params != null) {
			String[] tmps = StringUtils.split(params, ";");
			for (String tmp : tmps) {
				if (tmp != null) {
					String[] kv = StringUtils.split(tmp, "=");
					if (kv.length == 2)
						context.set(kv[0].trim(), kv[1].trim());
				}
			}
		}

		LocalModule module = (LocalModule) context.get(Constants.MODULE_UID);
		Engine engine = SmartyUtils.getEngine(module);

		Template t = engine.getTemplate(file);
		t.merge(context, writer);
	}

	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
