package org.mule.galaxy;

import org.mule.galaxy.lifecycle.Phase;

/**
 * A VersionAssessor allows custom criteria to be setting a new Active ArtifactVersion. 
 * For instance, you could implement a WSDL versioning policy which enforced backward 
 * compatability when moving.
 */
public interface VersionAssessor {
    
    VersionApproval isApproved(Artifact a, ArtifactVersion next);  
    
}
