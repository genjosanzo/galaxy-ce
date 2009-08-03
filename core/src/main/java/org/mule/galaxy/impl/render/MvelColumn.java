package org.mule.galaxy.impl.render;

import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.render.ColumnEvaluator;
import org.mvel.MVEL;

/**
 * Takes an MVEL expression which will return a value from the artifact.
 */
public class MvelColumn implements ColumnEvaluator {
     
    private final String expression;

    public MvelColumn(String expression) {
        super();
        this.expression = expression;
    }

    public Object getValue(Object item) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("item", item);
        return MVEL.eval(expression, vars);
    }

}
