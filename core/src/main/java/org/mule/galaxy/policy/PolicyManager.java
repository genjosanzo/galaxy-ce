package org.mule.galaxy.policy;

import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;

public interface PolicyManager {
    /**
     * Approve the next artifact version. NOTE: previous may be null here!
     * @param previous
     * @param next
     * @return
     * @throws RegistryException
     */
    public Collection<ApprovalMessage> approve(ArtifactVersion previous, 
                                        ArtifactVersion next);
    
    Collection<ArtifactPolicy> getPolicies();
    
    Collection<ArtifactPolicy> getActivePolicies(Artifact a);

    Collection<PolicyInfo> getActivePolicies(Artifact a, boolean includeInherited);
    
    void activatePolicy(ArtifactPolicy p, Workspace w, Collection<Phase> phases);
    
    void activatePolicy(ArtifactPolicy p, Artifact a, Collection<Phase> phases);

    void activatePolicy(ArtifactPolicy p, Collection<Phase> phases);
    
    void activatePolicy(ArtifactPolicy p, Workspace w, Lifecycle lifecycle);
    
    void activatePolicy(ArtifactPolicy p, Artifact a, Lifecycle lifecycle);

    void activatePolicy(ArtifactPolicy p, Lifecycle lifecycle);
    
    void deactivatePolicy(ArtifactPolicy p, Workspace w, Collection<Phase> phases);
    
    void deactivatePolicy(ArtifactPolicy p, Artifact a, Collection<Phase> phases);

    void deactivatePolicy(ArtifactPolicy p, Collection<Phase> phases);
    
    void deactivatePolicy(ArtifactPolicy p, Workspace w, Lifecycle lifecycle);
    
    void deactivatePolicy(ArtifactPolicy p, Artifact a, Lifecycle lifecycle);

    void deactivatePolicy(ArtifactPolicy p, Lifecycle lifecycle);

    ArtifactPolicy getPolicy(String id);

    void addPolicy(ArtifactPolicy artifactPolicy);
}
