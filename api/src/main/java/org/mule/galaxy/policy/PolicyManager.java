package org.mule.galaxy.policy;

import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
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
    public List<ApprovalMessage> approve(EntryVersion previous, EntryVersion next);
    
    Collection<ArtifactPolicy> getPolicies();
    
    Collection<ArtifactPolicy> getActivePolicies(EntryVersion a);

    Collection<PolicyInfo> getActivePolicies(Entry a, boolean includeInherited);

    Collection<ArtifactPolicy> getActivePolicies(Phase p);

    Collection<ArtifactPolicy> getActivePolicies(Lifecycle p);

    Collection<ArtifactPolicy> getActivePolicies(Workspace w, Phase p);

    Collection<ArtifactPolicy> getActivePolicies(Workspace w, Lifecycle p);
    
    
    void setActivePolicies(Workspace w, Collection<Phase> phases, ArtifactPolicy... policies)
        throws ArtifactCollectionPolicyException, RegistryException;
    
    void setActivePolicies(Entry a, Collection<Phase> phases, ArtifactPolicy... policies)
        throws PolicyException;
    
    void setActivePolicies(Collection<Phase> phases, ArtifactPolicy... policies)
        throws ArtifactCollectionPolicyException, RegistryException;
    
    void setActivePolicies(Workspace w, Lifecycle lifecycle, ArtifactPolicy... policies) 
        throws ArtifactCollectionPolicyException, RegistryException;
    
    void setActivePolicies(Entry a, Lifecycle lifecycle, ArtifactPolicy... policies)
        throws PolicyException;

    void setActivePolicies(Lifecycle lifecycle, ArtifactPolicy... policies) 
        throws ArtifactCollectionPolicyException, RegistryException;
    

    ArtifactPolicy getPolicy(String id);

    void addPolicy(ArtifactPolicy artifactPolicy);
}
