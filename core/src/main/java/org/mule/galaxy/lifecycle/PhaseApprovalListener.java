package org.mule.galaxy.lifecycle;

import org.mule.galaxy.Artifact;

/**
 * A PhaseApprovalListener allows custom criteria to be met when
 * moving from one phase to the next. For instance, you could implement
 * a WSDL versioning policy which enforced backward compatability when moving
 * from Staging to Production.
 */
public interface PhaseApprovalListener {
    
    PhaseApproval isApproved(Artifact a, Phase p);  
    
}
