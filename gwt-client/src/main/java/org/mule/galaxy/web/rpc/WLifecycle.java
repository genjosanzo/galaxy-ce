package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;
import java.util.Iterator;

public class WLifecycle implements IsSerializable {
    
    private String name;
    private String id;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WPhase>
     */
    private Collection phases;
    private WPhase initialPhase;
    private boolean defaultLifecycle;
    
    public WLifecycle() {
    }

    public WLifecycle(String id, String name, boolean defaultLifecyle) {
        this.id = id;
        this.name = name;
        defaultLifecycle = defaultLifecyle;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDefaultLifecycle() {
        return defaultLifecycle;
    }

    public void setDefaultLifecycle(boolean defaultLifecycle) {
        this.defaultLifecycle = defaultLifecycle;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Collection getPhases() {
        return phases;
    }
    public void setPhases(Collection phases) {
        this.phases = phases;
    }

    public WPhase getInitialPhase() {
        return initialPhase;
    }

    public void setInitialPhase(WPhase initialPhase) {
        this.initialPhase = initialPhase;
    }

    public WPhase getPhase(String name) {
        if (phases == null) return null;
        
        for (Iterator itr = phases.iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public WPhase getPhaseById(String id) {
        if (phases == null) return null;
        
        for (Iterator itr = phases.iterator(); itr.hasNext();) {
            WPhase p = (WPhase)itr.next();
            if (p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
   
}