package org.mule.galaxy.extension;

import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.policy.PolicyException;

public interface Extension {

    /**
     * Properties could be lifecycle information, associated contacts, links, etc.
     * This method will store them in  
     * @param entry
     * @param properties
     */
    Object getInternalValue(Item entry, PropertyDescriptor pd, Object externalValue) throws PolicyException;
    
    Object getExternalValue(Item entry, PropertyDescriptor pd, Object storedValue);
    
}
