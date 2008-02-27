package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WLifecycle implements IsSerializable {
    
    private String name;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WPhase>
     */
    private Collection phases;
    private WPhase initialPhase;
    
    public WLifecycle() {
    }

    public WLifecycle(String name) {
        this.name = name;
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
   
}