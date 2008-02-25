package org.mule.galaxy.lifecycle;

import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;

public interface LifecycleManager {
    
    Collection<Lifecycle> getLifecycles();
    
    Lifecycle getDefaultLifecycle();
    
    void setDefaultLifecycle(Lifecycle l);
    
    boolean isTransitionAllowed(Artifact a, Phase p);
    
    void transition(Artifact a, Phase p, User user) 
        throws TransitionException, ArtifactPolicyException;
    
    Lifecycle getLifecycle(String lifecycleName);

    /**
     * Save a new lifecycle.
     * @param newLc
     */
    void save(Lifecycle newLc);

    void save(String origName, Lifecycle lifecycle);
    
    /**
     * 
     * @param lifecycleName The name of the lifecycle to delete.
     * @param fallbackLifecycle The lifecycle to move artifacts to
     * if they use the deleted lifecycle.
     * @throws NotFoundException 
     */
    void delete(String lifecycleName, String fallbackLifecycle) throws NotFoundException;
    
}
