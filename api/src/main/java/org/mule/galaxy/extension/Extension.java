package org.mule.galaxy.extension;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.type.PropertyDescriptor;

/**
 * An extension makes it easy to support properties which are not simple types, like
 * Strings, on {@link Item}s. It intercepts property storage/retrieval allow other
 * values to be replaced. 
 * <p>
 * There are two types of extensions - those that store properties directly on
 * the {@link Item} through use of the setInternalProperty and those that store
 * things in their own separate location.
 * @author Dan
 *
 */
public interface Extension extends Identifiable {

    /**
     * Store a property value.
     * 
     * @param entry
     * @param properties
     * @throws PropertyException 
     */
    void store(Item entry, PropertyDescriptor pd, Object value) throws PolicyException, PropertyException;
    
    /**
     * Get the value of a property.
     * @param entry
     * @param pd
     * @param getWithNoData TODO
     * @return
     */
    Object get(Item entry, PropertyDescriptor pd, boolean getWithNoData);

    
    /**
     * Called when Item.setInternalValue is called. This allows Extensions to ensure
     * the value is valid.
     * @param entry
     * @param pd
     * @param valueToStore
     * @throws PolicyException Thrown if the value is not valid.
     */
    void validate(Item entry, PropertyDescriptor pd, Object valueToStore) throws PolicyException;
    
    /**
     * Whether or not this Extension supports differentiating between multivalue and non multivalue
     * properties. If this returns false, it does NOT mean that the extension doesn't have multiple
     * values, it just means the extension doesn't differentiate between multivalue/singlevalue. 
     * @return
     */
    boolean isMultivalueSupported();
    
    /**
     * A user readable name of this extension type (for the Properties editor).
     * @return
     */
    String getName();
    
    /**
     * Configuration values which a PropertyDescriptor MUST have to work with this extension.
     * @return
     */
    List<String> getPropertyDescriptorConfigurationKeys();

    /**
     * Properties which this extension supports searching for.
     * @param pd
     * @return
     */
    Map<String, String> getQueryProperties(PropertyDescriptor pd);
}
