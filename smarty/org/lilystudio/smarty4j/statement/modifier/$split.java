package org.lilystudio.smarty4j.statement.modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.check.FalseCheck;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;


public class $split extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression("")) };

  public Object execute(Object obj, Context context, Object[] values) {
	  try {
		  String value = (String)obj;
		  
		  String delimiter = (String)values[0];
		  
		  return value.split(delimiter);
	  } catch (Exception e) {
		  return obj;
	  }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}