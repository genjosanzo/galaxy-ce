package org.mule.galaxy.lifecycle;

import java.util.Collection;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.policy.PolicyException;

public interface LifecycleManager {
    
    Collection<Lifecycle> getLifecycles();
    
    Lifecycle getDefaultLifecycle();
    
    void setDefaultLifecycle(Lifecycle l);
    
    boolean isTransitionAllowed(Item item, String property, Phase p);

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
