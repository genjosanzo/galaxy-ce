package org.mule.galaxy.lifecycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.Identifiable;

public class Lifecycle implements Identifiable {
    private String id;
    private String name;
    private Phase initialPhase;
    private Map<String, Phase> phases;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Phase getPhaseById(String id) {
        for (Iterator itr = phases.values().iterator(); itr.hasNext();) {
            Phase p = (Phase)itr.next();
            
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    public Map<String, Phase> getPhases() {
        return phases;
    }

    public void setPhases(Map<String, Phase> phases) {
        this.phases = phases;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Lifecycle other = (Lifecycle)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public void addPhase(Phase phase) {
        if (phases == null) {
            phases = new HashMap<String, Phase>();
        }
        phases.put(phase.getName(), phase);
    }
    
}
