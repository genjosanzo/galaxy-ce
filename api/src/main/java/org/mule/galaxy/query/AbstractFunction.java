package org.mule.galaxy.query;

import java.util.List;
import java.util.Set;

import org.mule.galaxy.Item;

public abstract class AbstractFunction {
    
    public abstract String getModule();
    
    public abstract String getName();
    
    public abstract void modifyItems(Object[] args, Set<Item> items);

    /**
     * Add any possible filters to narrow down the list of artifacts
     * returned. 
     * @param query
     */
    public List<OpRestriction> getRestrictions(Object[] arguments) {
        return null;
    }
}