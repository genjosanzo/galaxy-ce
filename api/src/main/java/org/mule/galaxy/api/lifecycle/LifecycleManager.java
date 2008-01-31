package org.mule.galaxy.api.lifecycle;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactPolicyException;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.security.User;

import java.util.Collection;

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
