package org.mule.galaxy;

public interface PropertyInfo {
    String getName();
    
    Object getValue();
    
    boolean isLocked();
    
    boolean isVisible();
}
