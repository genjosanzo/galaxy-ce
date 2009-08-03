package org.mule.galaxy;

import org.mule.galaxy.type.PropertyDescriptor;

public interface PropertyInfo {
    String getName();

    String getDescription();
    
    <T> T getValue();
    
    <T> T getInternalValue();
    
    PropertyDescriptor getPropertyDescriptor();
    
    boolean isLocked();
    
    boolean isVisible();
}
