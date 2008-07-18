package org.mule.galaxy;

import java.util.Iterator;

import org.mule.galaxy.security.AccessException;

/**
 * An item which exists inside the repository.
 */
public interface Item<T extends Item<?>> {

    String getId();

    T getParent();
    
    String getPath();
    
    void setProperty(String name, Object value) throws PropertyException;
    
    Object getProperty(String name);

    boolean hasProperty(String name);

    Iterator<PropertyInfo> getProperties();
    
    PropertyInfo getPropertyInfo(String name);

    void setLocked(String name, boolean locked);

    void setVisible(String property, boolean visible);

    void delete() throws RegistryException, AccessException;
}
