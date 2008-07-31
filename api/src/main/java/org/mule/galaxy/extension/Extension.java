package org.mule.galaxy.extension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.type.PropertyDescriptor;

/**
 * An extension makes it easy to support properties which are not simple types, like
 * Strings, on {@link Item}s. It intercepts property storage/retrieval allow other
 * values to be replaced. 
 * <p>
 * For example, you may want to store a User as a property. getInternalValue would return
 * a user ID which would be stored inside the actual artifact. getExternalValue would
 * be called when item.getProperty() was called. It would give you the user ID back as
 * the "storedValue" and allow you to retrieve the actual user. 
 * @author Dan
 *
 */
public interface Extension extends Identifiable {

    /**
     * Get the value to store inside the property.
     * 
     * @param entry
     * @param properties
     */
    Object getInternalValue(Item entry, PropertyDescriptor pd, Object externalValue) throws PolicyException;
    
    /**
     * Get the user facing value of a property (e.g. a User instead of a user id).
     * @param entry
     * @param pd
     * @param storedValue
     * @return
     */
    Object getExternalValue(Item entry, PropertyDescriptor pd, Object storedValue);
    
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
}
