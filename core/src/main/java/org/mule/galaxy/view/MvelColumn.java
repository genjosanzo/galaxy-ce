package org.mule.galaxy.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mvel.MVEL;

/**
 * Takes an MVEL expression which will return a value from the artifact.
 */
public class MvelColumn implements ColumnEvaluator {

    private Serializable compiled;        
    public MvelColumn(String expression) {
        super();
        compiled = MVEL.compileExpression(expression);
    }

    public Object getValue(Object artifact) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("artifact", artifact);
        return MVEL.executeExpression(compiled, vars);
    }

}
