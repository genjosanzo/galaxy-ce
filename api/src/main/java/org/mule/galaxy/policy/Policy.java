package org.mule.galaxy.policy;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;

import java.util.Collection;

/**
 * A Policy allows you to control if items in the registry should be allowed
 * to progress to new lifecycle transitions or added to the registry at all.
 */
public interface Policy {
    
    /**
     * A unique ID for the policy.
     */
    String getId();
    
    /**
     * A short descriptive name for the policy.
     */
    String getName();

    /**
     * A description of the policy
     */
    String getDescription();

    /**
     * Whether or not the policy applies to the specified item.
     */
    boolean applies(Item item);
    
    /**
     * Approves an item according to the policy. If the item is not approved,
     * a Collection of {@link ApprovalMessage}s will be returned. Otherwise,
     * null or an empty Collection can be returned.
     */
    Collection<ApprovalMessage> isApproved(Item item);

    /**
     * Approves an item deletion according. If the item delete is not approved,
     * a Collection of {@link ApprovalMessage}s will be returned. Otherwise,
     * null or an empty Collection can be returned.
     */
    Collection<ApprovalMessage> allowDelete(Item item);
    
    /**
     * Set the {@link Registry} instance for this policy.
     */
    void setRegistry(Registry registry);
}
