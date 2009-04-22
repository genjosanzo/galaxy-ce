package org.mule.galaxy.impl.lifecycle;

import org.mule.galaxy.impl.extension.ExtensionQueryBuilder;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class PhaseQueryBuilder extends ExtensionQueryBuilder {
    private LifecycleManager lifecycleManager;
    
    public PhaseQueryBuilder() {
        suffixes.add("phase");
    }
    
    @Override
    protected String getValueAsString(Object o, String property, Operator operator) throws QueryException {
        if (o instanceof Phase) {
            return ((Phase) o).getId();
        }
        String[] lp = o.toString().split(":");
        
        if (lp.length != 2) {
            throw new QueryException(new Message("INVALID_PHASE_FORMAT", BundleUtils.getBundle(getClass()), o.toString()));
        }
        
        Lifecycle l = lifecycleManager.getLifecycle(lp[0]);
        String pid = null;
        if (l != null) {
            Phase p = l.getPhase(lp[1]);
            
            if (p != null) pid = p.getId();
        }
        
        return pid;
    }
    
    protected String getProperty(String property) {
        return property.substring(0, property.length() - ".phase".length());
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

}
