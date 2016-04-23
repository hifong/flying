package org.lilystudio.smarty4j.statement.modifier;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * @author wanghaifeng
 * 翻译字典格式：code1:title1;code2:title2;
 *
 */
public class $translateas extends Modifier {
	  private static ParameterCharacter[] definitions = { new ParameterCharacter(
		      ParameterCharacter.OBJECT, new StringExpression("")) };

	
	public Object execute(Object arg0, Context arg1, Object[] arg2) {
		String val = arg0 == null?"":arg0.toString();
		String tmp = (String)arg2[0];
		String[] tmps = StringUtils.split(tmp, ";");
		for(String t: tmps) {
			String[] ts = StringUtils.split(t, ":");
			if(StringUtils.equals(ts[0], val)) {
				return ts[1];
			}
		}
		return val;
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
