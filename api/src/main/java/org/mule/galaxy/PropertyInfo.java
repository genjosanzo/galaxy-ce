package org.mule.galaxy;

public interface PropertyInfo {
    String getName();

    String getDescription();
    
    Object getValue();
    
    PropertyDescriptor getPropertyDescriptor();
    
    boolean isLocked();
    
    boolean isVisible();
}
