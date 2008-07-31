package org.mule.galaxy.impl.lifecycle;

import org.mule.galaxy.impl.extension.ExtensionQueryBuilder;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.query.OpRestriction.Operator;

public class LifecycleQueryBuilder extends ExtensionQueryBuilder {
    private LifecycleManager lifecycleManager;

    public LifecycleQueryBuilder() {
        super(false);
        
        suffixes.add("");
        suffixes.add("id");
    }
 
    @Override
    protected String getValueAsString(Object o, String property, Operator operator) {
        if (o instanceof String) {
            if (property.endsWith(".id")) {
                return o.toString();
            } else {
                Lifecycle lifecycle = lifecycleManager.getLifecycle(o.toString());
                
                if (lifecycle != null) {
                    return lifecycle.getId();
                }
                
                return "not_found";
            }
        } else if (o instanceof Lifecycle) {
            return ((Lifecycle) o).getId();
        }
        
        return "not_found";
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

}
