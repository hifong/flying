package org.lilystudio.smarty4j.statement.function;

import java.io.Writer;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.statement.LineFunction;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * @author wanghaifeng
 * 
 */
public class $rowindex extends LineFunction {

	
	public void execute(Context context, Writer writer, Object[] args)
			throws Exception {
		Integer r = (Integer)context.get("rowindex");
		if(r == null) {
			r = new Integer(0);
		} else {
			r = new Integer(r.intValue() + 1);
		}
		context.set("rowindex", r);
	}

	
	public ParameterCharacter[] getDefinitions() {
		return null;
	}

}
