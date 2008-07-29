package org.mule.galaxy.policy;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;

import java.util.Collection;

/**
 * An ArtifactPolicy allows custom criteria to be setting a new Active ArtifactVersion. 
 * For instance, you could implement a WSDL versioning policy which enforced backward 
 * compatability.
 */
public interface Policy {
    
    String getId();
    
    String getName();
    
    String getDescription();
    
    boolean applies(Item item);
    
    Collection<ApprovalMessage> isApproved(Item item);
    
    void setRegistry(Registry registry);
}
