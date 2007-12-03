package org.mule.galaxy.policy;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;

/**
 * A VersionAssessor allows custom criteria to be setting a new Active ArtifactVersion. 
 * For instance, you could implement a WSDL versioning policy which enforced backward 
 * compatability when moving.
 */
public interface ArtifactPolicy {
    
    String getName();
    
    String getDescription();
    
    Approval isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next);
    
}
