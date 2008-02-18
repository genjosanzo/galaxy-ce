package org.mule.galaxy.lifecycle;

import java.util.HashSet;
import java.util.Set;

import org.mule.galaxy.security.User;

public class Phase {
    private String name;
    private Set<Phase> nextPhases;
    private Set<User> approvedUsers;
    private Lifecycle lifecycle;
    
    public Phase(Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * The set of phases which can be transitioned to after this phase.
     * @return
     */
    public Set<Phase> getNextPhases() {
        if (nextPhases == null) {
            nextPhases = new HashSet<Phase>();
        }
        return nextPhases;
    }
    
    public void setNextPhases(Set<Phase> nextPhases) {
        this.nextPhases = nextPhases;
    }

    /**
     * The users which can manipulate this lifecycle phase.
     * @return
     */
    public Set<User> getApprovedUsers() {
        return approvedUsers;
    }
    
    public void setApprovedUsers(Set<User> approvedUsers) {
        this.approvedUsers = approvedUsers;
    }

    public String toString() {
        return getLifecycle().getName() + ":" + getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lifecycle == null) ? 0 : lifecycle.hashCode());
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
        final Phase other = (Phase)obj;
        if (lifecycle == null) {
            if (other.lifecycle != null)
                return false;
        } else if (!lifecycle.equals(other.lifecycle))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
