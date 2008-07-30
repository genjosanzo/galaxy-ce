package org.mule.galaxy.extension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.type.PropertyDescriptor;

public interface Extension extends Identifiable {

    /**
     * Properties could be lifecycle information, associated contacts, links, etc.
     * 
     * @param entry
     * @param properties
     */
    Object getInternalValue(Item entry, PropertyDescriptor pd, Object externalValue) throws PolicyException;
    
    Object getExternalValue(Item entry, PropertyDescriptor pd, Object storedValue);
    
    void validate(Item entry, PropertyDescriptor pd, Object valueToStore) throws PolicyException;
    
    boolean isMultivalueSupported();
    
    String getName();
    
    List<String> getPropertyDescriptorConfigurationKeys();
}
