package org.mule.galaxy.impl.render;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Entry;
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

    public Object getValue(Object entry) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("entry", entry);
        vars.put("version", ((Entry)entry).getDefaultOrLastVersion());
        return MVEL.eval(expression, vars);
    }

}
