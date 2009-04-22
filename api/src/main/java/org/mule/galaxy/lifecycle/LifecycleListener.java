package org.mule.galaxy.lifecycle;

import org.mule.galaxy.Item;

/**
 * Allows specific actions to be taken when moving to a new lifecycle phase.
 * i.e. Sending an email to the QA team when moving to the testing phase.
 */
public interface LifecycleListener {
    
    void nextLifecyclePhase(Item item, Phase phase);
    
}
