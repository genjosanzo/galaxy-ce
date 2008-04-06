package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WPropertyDescriptor implements IsSerializable {
    private String id;
    private boolean multiValued;
    private String name;
    private String description;
    
    public WPropertyDescriptor(String id, String name, String description, boolean multiValued) {
        super();
        this.id = id;
        this.multiValued = multiValued;
        this.name = name;
        this.description = description;
    }
    public WPropertyDescriptor() {
        super();
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean isMultiValued() {
        return multiValued;
    }
    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
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
}
