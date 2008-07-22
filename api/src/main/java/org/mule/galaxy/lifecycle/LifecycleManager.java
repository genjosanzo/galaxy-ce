package org.mule.galaxy.lifecycle;

import java.util.Collection;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.User;

public interface LifecycleManager {
    
    Collection<Lifecycle> getLifecycles();
    
    Lifecycle getDefaultLifecycle();
    
    void setDefaultLifecycle(Lifecycle l);
    
    boolean isTransitionAllowed(EntryVersion a, Phase p);
    
    void transition(EntryVersion a, Phase p, User user) 
        throws TransitionException, PolicyException;
    
    Lifecycle getLifecycle(String lifecycleName);

    Lifecycle getLifecycleById(String id);
    
    /**
     * Save/update a lifecycle.
     * @param newLc
     */
    void save(Lifecycle newLc) throws DuplicateItemException, NotFoundException;
    
    /**
     * 
     * @param lifecycleName The id of the lifecycle to delete.
     * @param fallbackLifecycle The lifecycle to move artifacts to
     * if they use the deleted lifecycle.
     * @throws NotFoundException 
     */
    void delete(String lifecycleId, String fallbackLifecycleId) throws NotFoundException;

    Phase getPhaseById(String phase);
    
}
