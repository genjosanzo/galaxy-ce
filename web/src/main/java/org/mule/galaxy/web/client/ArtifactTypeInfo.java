package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ArtifactTypeInfo implements IsSerializable {
    private String id;
    private String description;
    
    public ArtifactTypeInfo() {
        super();
    }
    public ArtifactTypeInfo(String id, String description) {
        super();
        this.id = id;
        this.description = description;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
