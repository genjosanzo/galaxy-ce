package org.mule.galaxy.lifecycle;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lifecycle {
    private String name;
    private Phase initialPhase;
    private Map<String, Phase> phases;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Phase getInitialPhase() {
        return initialPhase;
    }

    public void setInitialPhase(Phase initialPhase) {
        this.initialPhase = initialPhase;
    }

    public Phase getPhase(String phase) {
        return phases.get(phase);
    }

    public Map<String, Phase> getPhases() {
        return phases;
    }

    public void setPhases(Map<String, Phase> phases) {
        this.phases = phases;
    }
}
