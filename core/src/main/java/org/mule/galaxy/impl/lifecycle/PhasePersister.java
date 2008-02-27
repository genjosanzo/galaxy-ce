package org.mule.galaxy.impl.lifecycle;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.FieldDescriptor;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;

/**
 * Provides logic to persist phases to JCR nodes and recreate them from 
 * the lifecycleManager.
 */
public class PhasePersister implements FieldPersister, ApplicationContextAware {
    private ApplicationContext context;
    private LifecycleManager lifecycleManager;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public LifecycleManager getLifecycleManager() {
        if (lifecycleManager == null) {
            lifecycleManager = (LifecycleManager) context.getBean("lifecycleManager");
        }
        return lifecycleManager;
    }
    
    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        try {
            n = n.getNode(fd.getName());
        } catch (PathNotFoundException e) {
            return null;
        }
        
        String phase = JcrUtil.getStringOrNull(n, "phase");
        
        if (phase == null) return null;
        
        Phase p = getLifecycleManager().getPhaseById(phase);
        if (p == null) return null;
        
        return p;
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        Node child = JcrUtil.getOrCreate(n, fd.getName());
        Phase p = (Phase) o;
        if (p == null) return;
        
        child.setProperty("phase", p.getId());
    }

}
