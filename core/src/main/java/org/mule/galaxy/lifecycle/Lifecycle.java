package org.mule.galaxy.lifecycle;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lifecycle {
    private String name;
    private Set<Phase> initialPhases;
    private Map<String, Phase> phases;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<Phase> getInitialPhases() {
        if (initialPhases == null) {
            initialPhases = new HashSet<Phase>();
        }
        return initialPhases;
    }
    
    public void setInitialPhases(Set<Phase> initialPhases) {
        this.initialPhases = initialPhases;
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
