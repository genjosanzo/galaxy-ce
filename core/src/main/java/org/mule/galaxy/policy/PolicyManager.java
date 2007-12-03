package org.mule.galaxy.policy;

import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;

public interface PolicyManager {
    List<ArtifactPolicy> getPolicies();
    
    List<ArtifactPolicy> getActivePolicies(Artifact a);

    List<ArtifactPolicy> getActivePolicies(Workspace w);
    
    void activatePolicy(ArtifactPolicy p, Workspace w, Collection<Phase> phases);
    
    void activatePolicy(ArtifactPolicy p, Artifact a, Collection<Phase> phases);

    void activatePolicy(ArtifactPolicy p, Collection<Phase> phases);
    
    void activatePolicy(ArtifactPolicy p, Workspace w, Lifecycle lifecycle);
    
    void activatePolicy(ArtifactPolicy p, Artifact a, Lifecycle lifecycle);

    void activatePolicy(ArtifactPolicy p, Lifecycle lifecycle);
}
