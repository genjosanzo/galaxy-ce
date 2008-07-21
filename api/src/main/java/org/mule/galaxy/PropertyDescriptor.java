package org.mule.galaxy;

import org.mule.galaxy.extension.Extension;

public class PropertyDescriptor implements Identifiable {
    private String id;
    private String property;
    private String description;
    private boolean multivalued;
    private Extension extension;
    
    public PropertyDescriptor(String property, String description, boolean multivalued) {
        super();
        this.property = property;
        this.description = description;
        this.multivalued = multivalued;
    }

    public PropertyDescriptor() {
        super();
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

}
