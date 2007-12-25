package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WProperty implements IsSerializable {
    private boolean locked;
    private String name;
    private String description;
    private String value;
    
    public WProperty(String name, String description, String value, boolean locked) {
        super();
        this.locked = locked;
        this.name = name;
        this.description = description;
        this.value = value;
    }
    public WProperty() {
        super();
        // TODO Auto-generated constructor stub
    }
    public boolean isLocked() {
        
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
