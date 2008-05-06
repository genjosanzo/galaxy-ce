package org.mule.galaxy.query;

import java.util.List;

public interface FunctionRegistry {
    
    List<AbstractFunction> getFunctions();
    
    AbstractFunction getFunction(String module, String name);
    
    void register(AbstractFunction function);
}
