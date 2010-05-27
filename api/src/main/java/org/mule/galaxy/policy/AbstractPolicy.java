package org.mule.galaxy.policy;

import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;

public abstract class AbstractPolicy implements Policy {
    protected Registry registry;
    protected String id;
    protected String name;
    protected String description;
    
    protected AbstractPolicy(String id, String name, String description) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Collection<ApprovalMessage> allowDelete(Item item) {
        return null;
    }

    public boolean applies(Item item) {
        return true;
    }

    public Collection<ApprovalMessage> isApproved(Item item) {
        return null;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
}
