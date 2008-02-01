package org.mule.galaxy.policy;

import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;

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
