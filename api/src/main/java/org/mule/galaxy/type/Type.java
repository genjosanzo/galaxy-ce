package org.mule.galaxy.type;

import java.util.List;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;


/**
 * Describes a type of entry inside the registry.
 */
public class Type implements Identifiable {
    private String id;
    private String name;
    private boolean artifact;
    private List<PropertyDescriptor> properties;
    private List<Type> superTypes;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isArtifact() {
        return artifact;
    }
    public void setArtifact(boolean artifact) {
        this.artifact = artifact;
    }
    @OneToMany(componentType=Type.class)
    public List<Type> getSuperTypes() {
        return superTypes;
    }
    public void setSuperTypes(List<Type> superTypes) {
        this.superTypes = superTypes;
    }
    
    @OneToMany(componentType=PropertyDescriptor.class)
    public List<PropertyDescriptor> getProperties() {
        return properties;
    }
    public void setProperties(List<PropertyDescriptor> properties) {
        this.properties = properties;
    }
}
