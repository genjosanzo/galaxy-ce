package org.mule.galaxy.impl.jcr.onm;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class PersisterManager {
    private Map<String, FieldPersister> persisters = new HashMap<String, FieldPersister>();
    private FieldPersister defaultPersister = new DefaultPersister();
    
    public FieldPersister getPersister(Class c) {
        FieldPersister p = persisters.get(c.getName());
        if (p == null) {
            return defaultPersister;
        }
        return p;
    }

    public Map<String, FieldPersister> getPersisters() {
        return persisters;
    }

    public void setPersisters(Map<String, FieldPersister> persisters) {
        // Working around some weird spring bug...
        this.persisters.putAll(persisters);
    }

    public FieldPersister getDefaultPersister() {
        return defaultPersister;
    }

    public void setDefaultPersister(FieldPersister defaultPersister) {
        this.defaultPersister = defaultPersister;
    }
    
}
