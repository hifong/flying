package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.log.Logger;
import com.flying.common.util.Utils;

public class $eval extends LineFunction {
	private Logger logger = Logger.getLogger($widget.class);

	private static ParameterCharacter[] definitions = {
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "value"),
			new ParameterCharacter(ParameterCharacter.STROBJECT, null, "param")
	};


	public ParameterCharacter[] getDefinitions() {
		// TODO Auto-generated method stub
		return definitions;
	}

	@Override
	public void execute(Context arg0, Writer arg1, Object[] arg2)
			throws Exception {
		String value = (String)arg2[0];
		String param = (String)arg2[1];
		try {
			Context context = new Context(arg0);
			
			if(StringUtils.isNotBlank(param)){
				String[] pArray = param.split(",");
				Map<String,Object> map = Utils.newHashMap();
				for (String pair : pArray) {
					String[] kv = pair.split("=");
					if(kv.length>1){
						map.put(kv[0], kv[1]);
					}else{
						map.put(kv[0], "");
					}
				}
				context.putAll(map);
			}
			
			Engine engine = context.getTemplate().getEngine();
			Template t = new Template(engine, value);
			t.merge(context, arg1);
		} catch (Exception e) {
			logger.error("$eval:execute",e);
		}
	}

}
