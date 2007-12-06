package org.mule.galaxy.impl.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyManager;

public class PolicyManagerImpl implements PolicyManager {
    private List<ArtifactPolicy> policies = new ArrayList<ArtifactPolicy>();

    public List<ArtifactPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<ArtifactPolicy> policies) {
        this.policies = policies;
    }

    public void activatePolicy(ArtifactPolicy p, Artifact a, Collection<Phase> phases) {
        // TODO Auto-generated method stub
        
    }

    public void activatePolicy(ArtifactPolicy p, Artifact a, Lifecycle lifecycle) {
        // TODO Auto-generated method stub
        
    }

    public void activatePolicy(ArtifactPolicy p, Collection<Phase> phases) {
        // TODO Auto-generated method stub
        
    }

    public void activatePolicy(ArtifactPolicy p, Lifecycle lifecycle) {
        // TODO Auto-generated method stub
        
    }

    public void activatePolicy(ArtifactPolicy p, Workspace w, Collection<Phase> phases) {
        // TODO Auto-generated method stub
        
    }

    public void activatePolicy(ArtifactPolicy p, Workspace w, Lifecycle lifecycle) {
        // TODO Auto-generated method stub
        
    }

    public List<ArtifactPolicy> getActivePolicies(Artifact a) {
        return Collections.EMPTY_LIST;
    }

    public List<ArtifactPolicy> getActivePolicies(Workspace w) {
        return Collections.EMPTY_LIST;
    }
    
}
