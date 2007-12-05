package org.mule.galaxy.impl.jcr.onm;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class PersisterManager {
    private Map<Class, FieldPersister> persisters = new HashMap<Class, FieldPersister>();
    private FieldPersister defaultPersister = new DefaultPersister();
    
    public FieldPersister getPersister(Class c) {
        FieldPersister p = persisters.get(c);
        if (p == null) {
            return defaultPersister;
        }
        return p;
    }

    public Map<Class, FieldPersister> getPersisters() {
        return persisters;
    }

    public void setPersisters(Map<Class, FieldPersister> persisters) {
        this.persisters = persisters;
    }

    public FieldPersister getDefaultPersister() {
        return defaultPersister;
    }

    public void setDefaultPersister(FieldPersister defaultPersister) {
        this.defaultPersister = defaultPersister;
    }
    
}
