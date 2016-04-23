/*------------------------------------------------------------------------------
 * COPYRIGHT Aspire 2011
 *
 * The copyright to the computer program(s) herein is the property of
 * Aspire Inc. The programs may be used and/or copied only with written
 * permission from Aspire Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

import com.flying.common.util.Utils;

public class $local extends Modifier {

    private static ParameterCharacter[] definitions = {
            new ParameterCharacter(ParameterCharacter.STRING, new StringExpression("")),
            new ParameterCharacter(ParameterCharacter.STRING, new StringExpression("")),
            new ParameterCharacter(ParameterCharacter.STRING, new StringExpression("")) };

    public ParameterCharacter[] getDefinitions() {
        return definitions;
    }

    @Override
    public Object execute(Object object, Context context, Object[] objects) {
        if (object instanceof String) {
            if (objects == null) {
                return object;
            }
            if (objects.length == 0) {
                return object;
            }
            try {
                String moduleId = null;
                String basename = null;
                String locale = "zh";

                switch (objects.length) {
                case 1:
                    basename = objects[0].toString();
                    break;
                case 2:
                    moduleId = objects[0].toString();
                    basename = objects[1].toString();
                    break;
                case 3:
                    moduleId = objects[0].toString();
                    basename = objects[1].toString();
                    locale = objects[2].toString();
                    break;
                default:
                    break;
                }

                return Utils.getLocalString(moduleId, basename, object.toString(), locale);
            } catch (Exception e) {
                return object;
            }
        }
        return object;
    }

}
