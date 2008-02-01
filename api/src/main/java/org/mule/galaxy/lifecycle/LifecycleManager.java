package org.mule.galaxy.lifecycle;

import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;

public interface LifecycleManager {
    Collection<Lifecycle> getLifecycles();
    
    Lifecycle getDefaultLifecycle();
    
    Lifecycle getLifecycle(Workspace workspace);
    
    boolean isTransitionAllowed(Artifact a, Phase p);
    
    void transition(Artifact a, Phase p, User user) 
        throws TransitionException, ArtifactPolicyException;
    
    Lifecycle getLifecycle(String lifecycleName);

    // Collection<PhaseAssessor> getPhaseAssessors(Artifact a);
    
}
