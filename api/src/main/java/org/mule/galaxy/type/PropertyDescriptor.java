package org.mule.galaxy.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.mapping.OneToMany;

public class PropertyDescriptor implements Identifiable {
    private String id;
    private String property;
    private String description;
    private boolean multivalued;
    private Extension extension;
    private List<Class> appliesTo;
    private Map<String, String> configuration;
    private boolean index;
    private Type type;
    
    public PropertyDescriptor(String property, String description, boolean multivalued, boolean index) {
        this(property, description, multivalued, index, null);
    }

    public PropertyDescriptor(String property, 
                              String description,
                              boolean multivalued, 
                              boolean index,
                              Extension extension) {
        super();
        this.property = property;
        this.description = description;
        this.multivalued = multivalued;
        this.index = index;
        this.extension = extension;
    }

    
    public PropertyDescriptor() {
        super();
    }

    public PropertyDescriptor(String property, String description) {
        this(property, description, false, false);
    }

    public PropertyDescriptor(String property, String description, Extension extension) {
        this(property, description, false, false, extension);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMultivalued() {
        return multivalued;
    }

    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    @OneToMany(treatAsField=true)
    public List<Class> getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(List<Class> appliesTo) {
        this.appliesTo = appliesTo;
    }
    
    public void addAppliesTo(Class c) {
    if (appliesTo == null) {
        appliesTo = new ArrayList<Class>();
    }
    appliesTo.add(c);
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertyDescriptor other = (PropertyDescriptor) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
