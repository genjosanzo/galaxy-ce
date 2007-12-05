package org.mule.galaxy.impl.lifecycle;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;

import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;

/**
 * Provides logic to persist phases to JCR nodes and recreate them from 
 * the lifecycleManager.
 */
public class PhasePersister implements FieldPersister{
    private LifecycleManager lifecycleManager;
    
    public PhasePersister(LifecycleManager lifecycleManager) {
        super();
        this.lifecycleManager = lifecycleManager;
    }

    public Object build(Node n, String property) throws Exception {
        try {
            n = n.getNode(property);
        } catch (PathNotFoundException e) {
            return null;
        }
        
        String lifecycle = JcrUtil.getStringOrNull(n, "lifecycle");
        String phase = JcrUtil.getStringOrNull(n, "phase");
        
        if (phase == null || lifecycle == null) return null;
        
        Lifecycle l = lifecycleManager.getLifecycle(lifecycle);
        if (l == null) return null;
        
        return l.getPhase(phase);
    }

    public void persist(Object o, Node n, String property) throws Exception {
        Node child = JcrUtil.getOrCreate(n, property);
        Phase p = (Phase) o;
        if (p == null) return;
        
        child.setProperty("phase", p.getName());
        child.setProperty("lifecycle", p.getLifecycle().getName());
    }

}
