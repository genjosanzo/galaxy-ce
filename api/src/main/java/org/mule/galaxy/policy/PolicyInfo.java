package org.mule.galaxy.policy;

import org.mule.galaxy.lifecycle.Phase;

public class PolicyInfo {
    private Policy artifactPolicy;
    private Object appliesTo;
    
    public PolicyInfo(Policy artifactPolicy, Object appliesTo) {
        super();
        this.artifactPolicy = artifactPolicy;
        this.appliesTo = appliesTo;
    }
    
    public Policy getPolicy() {
        return artifactPolicy;
    }
    public Object getAppliesTo() {
        return appliesTo;
    }

    @Override
    public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((appliesTo == null) ? 0 : appliesTo.hashCode());
    result = prime * result
        + ((artifactPolicy == null) ? 0 : artifactPolicy.hashCode());
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
    PolicyInfo other = (PolicyInfo) obj;
    if (appliesTo == null) {
        if (other.appliesTo != null)
        return false;
    } else if (!appliesTo.equals(other.appliesTo))
        return false;
    if (artifactPolicy == null) {
        if (other.artifactPolicy != null)
        return false;
    } else if (!artifactPolicy.equals(other.artifactPolicy))
        return false;
    return true;
    }

    public boolean appliesTo(Phase p) {
    return appliesTo.equals(p) || appliesTo.equals(p.getLifecycle());
    }
    
    
}
