package org.mule.galaxy.impl.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mule.galaxy.query.AbstractFunction;
import org.mule.galaxy.query.FunctionRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class FunctionRegistryImpl implements FunctionRegistry, ApplicationContextAware {
    private Map<String, AbstractFunction> functions = new ConcurrentHashMap<String, AbstractFunction>();
    private ApplicationContext ctx;
    
    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }
    
    public void initialize() throws Exception {
        Map beans = ctx.getBeansOfType(AbstractFunction.class);
        
        for (Object o : beans.values()) {
            AbstractFunction f = (AbstractFunction) o;
            
            register(f);
        }
    }

    public List<AbstractFunction> getFunctions() {
        List<AbstractFunction> fns = new ArrayList<AbstractFunction>();
        fns.addAll(functions.values());
        return fns;
    }

    public void register(AbstractFunction f) {
        String module =  f.getModule();
        if (module == null) module = "";
        
        String name = f.getName();
        
        if (name == null) {
            throw new IllegalStateException("Module names cannot be null/empty!");
        }
        
        functions.put(module + ":"  + name, f);
    }

    public AbstractFunction getFunction(String module, String name) {
        if (module == null) {
            module = "";
        }
        
        return functions.get(module + ":" + name);
    }

}
