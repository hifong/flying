package org.lilystudio.smarty4j.statement.modifier;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

public class $capitialize extends Modifier {
	private static ParameterCharacter[] definitions = { new ParameterCharacter(
			ParameterCharacter.OBJECT, new StringExpression("")) };

	
	public Object execute(Object arg0, Context arg1, Object[] arg2) {
		return StringUtils.capitalize(arg0.toString());
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
