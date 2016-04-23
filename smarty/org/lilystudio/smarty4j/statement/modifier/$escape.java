package org.lilystudio.smarty4j.statement.modifier;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.util.Utils;

public class $escape extends Modifier
{
  private static ParameterCharacter[] definitions = { 
    new ParameterCharacter(3, new StringExpression("html")) };
  private int type;

  public void init(Template parent, boolean ransack, List<IExpression> values)
    throws ParseException
  {
    super.init(parent, ransack, values);
    String value = getParameter(0).toString();
    if (value.equals("htmlall"))
      this.type = 0;
    else if (value.equals("html"))
      this.type = 1;
    else if (value.equals("url"))
      this.type = 2;
    else if ((value.equals("quotes")) || (value.equals("javascript")))
      this.type = 3;
    else if (value.equals("hex"))
      this.type = 4;
    else if (value.equals("hexentity"))
      this.type = 5;
    else
      throw new ParseException("不支持的参数");
  }

  public Object execute(Object obj, Context context, Object[] values)
  {
	if(obj==null){
		return obj;
	}
    String s = obj.toString();
    if(StringUtils.isBlank(s)){
    	return s;
    }
    s = Utils.escapeSecurity(s,"\\(|\\)|\\{|\\}|[|]|;|\"|\'|\r|\n");

    int len = s.length();
    StringBuffer buf = new StringBuffer(256);

    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);
      
      if(c=='\r'||c=='\n'){
    	  continue;
      }
      
      switch (this.type) {
      case 0:
        if (c != ' ') break;
        buf.append("&#32;");
        continue;
      case 1:
        if (c == '&') {
          buf.append("&#38;");
          continue;
        }if (c == '"') {
          buf.append("&#34;");
          continue;
        }if (c == '\'') {
          buf.append("&#39;");
          continue;
        }if (c == '<') {
          buf.append("&#60;");
          continue;
        }if (c == '>'){
        	 buf.append("&#62;");
             continue;
        }
         
        break;
      case 2:
        if (c == '+') {
          buf.append("%2B");
          continue;
        }if (c == ' ') {
          c = '+'; } else {
          if (c == '/') {
            buf.append("%2F");
            continue;
          }if (c == '?') {
            buf.append("%3F");
            continue;
          }if (c == '%') {
            buf.append("%25");
            continue;
          }if (c == '#') {
            buf.append("%23");
            continue;
          }if (c == '&') {
            buf.append("%26");
            continue;
          }if (c == '='){
        	  buf.append("%3D"); 
        	  continue;
          }
        }
        break;
      case 3:
        if ((c == '"') || (c == '\'') || (c == '\\')) {
          buf.append('\\');
          continue;
        }
        break;
      case 4:
        if (c < 'Ā') {
          buf.append('%');
          buf.append(String.format("%x", new Object[] { Character.valueOf(c) }));
          continue;
        }
        break;
      case 5:
        if (c < 'Ā') {
          buf.append("&#x");
          buf.append(String.format("%X", new Object[] { Character.valueOf(c) }));
          buf.append(';'); continue;
        }
        buf.append("&#u");
        buf.append(String.format("%X", new Object[] { Character.valueOf(c) }));
        buf.append(';');

        continue;
       default:
    	   
    	   break;
      }
      buf.append(c);
    }

    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}