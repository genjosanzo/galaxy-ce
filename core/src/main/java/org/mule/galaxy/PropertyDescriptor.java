package org.mule.galaxy;

public class PropertyDescriptor implements Identifiable {
    private String property;
    private String name;
    private boolean multivalued;
    
    public PropertyDescriptor(String property, String name, boolean multivalued) {
        super();
        this.property = property;
        this.name = name;
        this.multivalued = multivalued;
    }
    public PropertyDescriptor() {
        super();
    }
    
    public String getId() {
        return getProperty();
    }
    public void setId(String id) {
        setProperty(id);
    }
    
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isMultivalued() {
        return multivalued;
    }
    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }
    
}
