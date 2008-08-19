package org.mule.galaxy;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;

/**
 * An item which exists inside the registry.
 */
public interface Item {

    String getId();

    Item getParent();
    
    String getName();
    
    void setName(String name);
    
    String getPath();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    /**
     * Set the property value. May be intercepted/validated by an Extension.
     * @param name
     * @param value
     * @throws PropertyException
     * @throws PolicyException Thrown if this is not a valid value.
     */
    void setProperty(String name, Object value) throws PropertyException, PolicyException;

    /**
     * Set the property value direct - skipping any extensions. Extension.validate() is still called.
     * @param name
     * @param value
     * @throws PropertyException
     * @throws PolicyException
     */
    void setInternalProperty(String name, Object value) throws PropertyException, PolicyException;
    
    Object getProperty(String name);

    Object getInternalProperty(String name);
    
    boolean hasProperty(String name);

    Collection<PropertyInfo> getProperties();
    
    PropertyInfo getPropertyInfo(String name);

    void setLocked(String name, boolean locked);

    void setVisible(String property, boolean visible);

    void delete() throws RegistryException, AccessException;
}
