package org.mule.galaxy.impl.jcr.query;

import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;

public class LifecycleQueryBuilder extends SimpleQueryBuilder {
    private LifecycleManager lifecycleManager;
    
    public LifecycleQueryBuilder() {
        super(new String[] { "lifecycle" }, false);
    }
    
    @Override
    protected String getValueAsString(Object o) {
        Lifecycle l = lifecycleManager.getLifecycle(o.toString());
        
        if (l == null) {
            return null;
        }
        
        return l.getId();
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

}
