package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WPermission implements IsSerializable {
    private String name;
    private String description;
    
    public WPermission(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }
    public WPermission() {
        super();
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
