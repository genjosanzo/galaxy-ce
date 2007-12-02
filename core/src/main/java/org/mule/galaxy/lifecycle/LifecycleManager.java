package org.mule.galaxy.lifecycle;

import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrArtifact;

public interface LifecycleManager {
    Collection<Lifecycle> getLifecycles();
    
    Lifecycle getDefaultLifecycle();
    
    Lifecycle getLifecycle(Workspace workspace);
    
    boolean isTransitionAllowed(Artifact a, Phase p);
    
    void transition(Artifact a, Phase p) throws TransitionException;
    
    Lifecycle getLifecycle(String lifecycleName);

    // Collection<PhaseAssessor> getPhaseAssessors(Artifact a);
    
}
