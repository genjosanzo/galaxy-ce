package org.mule.galaxy.api.policy;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Registry;

import java.util.Collection;

/**
 * An ArtifactPolicy allows custom criteria to be setting a new Active ArtifactVersion. 
 * For instance, you could implement a WSDL versioning policy which enforced backward 
 * compatability.
 */
public interface ArtifactPolicy {
    
    String getId();
    
    String getName();
    
    String getDescription();
    
    boolean applies(Artifact a);
    
    Collection<ApprovalMessage> isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next);
    
    void setRegistry(Registry registry);
}
