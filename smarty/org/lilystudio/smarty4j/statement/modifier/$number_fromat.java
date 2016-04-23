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
public class $number_fromat extends Modifier {
	  private static ParameterCharacter[] definitions = { new ParameterCharacter(
		      ParameterCharacter.OBJECT, new StringExpression("")) };

	
	public Object execute(Object arg0, Context arg1, Object[] arg2) {
		if(arg2.length>0){
			int num = (Integer)arg2[0];
			String tmp = String.valueOf(arg0);
			tmp.replaceAll(".", "");
			int tmplen = tmp.length();
			while(num>tmplen){
				tmp = "0" + tmp;
				tmplen = tmp.length();
			}
			
			if(num==tmplen){
				tmp = "0." + tmp;
			}else{
				tmp = tmp.subSequence(0, tmplen-num) + "." + tmp.substring(tmplen-num);
			}
			
			return tmp;
		}
		return arg0;
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
