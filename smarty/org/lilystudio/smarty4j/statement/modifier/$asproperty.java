package org.lilystudio.smarty4j.statement.modifier;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

public class $asproperty extends Modifier {
	private static ParameterCharacter[] definitions = { new ParameterCharacter(
			ParameterCharacter.OBJECT, new StringExpression("")) };

	
	public Object execute(Object arg0, Context arg1, Object[] arg2) {
		String[] tmps = StringUtils.split(arg0.toString(), "_");
		StringBuffer sb = new StringBuffer();
		for(String s: tmps) {
			if(s.length() <= 1) continue;
			if(sb.length() == 0) 
				sb.append(s.toLowerCase());
			else
				sb.append(StringUtils.capitalize(s));
		}
		return sb.toString();
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
