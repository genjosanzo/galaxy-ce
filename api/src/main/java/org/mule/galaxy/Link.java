package org.mule.galaxy;

public interface Link {
    
    String getPath();
    
    Item<?> getParent();
    
    Item<?> getItem();
    
    boolean exists();
    
    LinkType getType();
    
    boolean isAutoDetected();
}
