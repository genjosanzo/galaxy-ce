package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WGovernanceInfo implements IsSerializable {
    private Collection nextPhases;
    private String currentPhase;
    private String lifecycle;
    
    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * @gwt typeArgs java.lang.String
     * @return
     * @throws Exception 
     */
    public Collection getNextPhases() {
        return nextPhases;
    }

    public void setNextPhases(Collection nextPhases) {
        this.nextPhases = nextPhases;
    }
    
    
}
