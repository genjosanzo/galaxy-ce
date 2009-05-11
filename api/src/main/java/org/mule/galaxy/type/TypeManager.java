package org.mule.galaxy.type;

import java.util.Collection;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.security.AccessException;

public interface TypeManager {

    String VERSIONED = "Versioned";
    String VERSION = "Version";
    String DEFAULT_VERSION = "default.version";
    String WORKSPACE = "Workspace";
    String ARTIFACT_VERSION = "Artifact Version";
    String ARTIFACT = "Artifact";
    String BASE_TYPE = "Base Type";

    Collection<Type> getTypes();

    Type getType(String id) throws NotFoundException;

    Type getTypeByName(String string) throws NotFoundException;

    void saveType(Type pd) throws AccessException, DuplicateItemException, NotFoundException;
    
    void deleteType(String id);
    
    Type getDefaultType();
    
    /**
     * Get the global property descriptors, i.e. the ones not associated with a type.
     * @param includeIndex
     * @return
     */
    Collection<PropertyDescriptor> getGlobalPropertyDescriptors(boolean includeIndex);

    /**
     * Get the property descriptors associated with this type.
     * @param includeIndex
     * @return
     */
    Collection<PropertyDescriptor> getPropertyDescriptors(Type type);

    PropertyDescriptor getPropertyDescriptor(String propertyId) throws NotFoundException;

    void savePropertyDescriptor(PropertyDescriptor pd) throws AccessException, DuplicateItemException, NotFoundException;
    
    void deletePropertyDescriptor(String id);
    
    Collection<PropertyDescriptor> getPropertyDescriptorsForExtension(String extensionId);
    
    PropertyDescriptor getPropertyDescriptorByName(final String propertyName, Type type);
    
}
