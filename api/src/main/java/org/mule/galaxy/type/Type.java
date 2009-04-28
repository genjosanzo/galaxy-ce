package org.mule.galaxy.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;

/**
 * Describes a type of item inside the registry.
 */
public class Type implements Identifiable {
    private String id;
    private String name;
    private List<PropertyDescriptor> properties;
    private List<Type> mixins;
    private List<Type> allowedChildren;
    private boolean systemType;
    
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
    
    @OneToMany(componentType = Type.class)
    public List<Type> getMixins() {
        return mixins;
    }

    public void setMixins(List<Type> mixins) {
        this.mixins = mixins;
    }

    @OneToMany(componentType = PropertyDescriptor.class)
    public List<PropertyDescriptor> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyDescriptor> properties) {
        this.properties = properties;
    }

    @OneToMany(componentType = Type.class)
    public List<Type> getAllowedChildren() {
        return allowedChildren;
    }

    public void setAllowedChildren(List<Type> allowedChildren) {
        this.allowedChildren = allowedChildren;
    }

    public boolean inheritsFrom(String mixin) {
        if (name.equals(mixin)) {
            return true;
        }
        
        Set<Type> checked = new HashSet<Type>();
        checked.add(this);
        return inheritsFrom(mixin, checked, mixins);
    }

    private boolean inheritsFrom(String mixin, Set<Type> checked, List<Type> mixins) {
        if (mixins != null) {
            for (Type t : mixins) {
                if (!checked.contains(t)) {
                    checked.add(t);
                    if (t.getName().equals(mixin)) {
                        return true;
                    }

                    return inheritsFrom(mixin, checked, t.getMixins());
                }
            }
        }
        return false;
    }

    public void setSystemType(boolean systemType) {
        this.systemType = systemType;
    }

    public boolean isSystemType() {
        return systemType;
    }

}
