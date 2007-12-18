package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WorkspaceInfo implements IsSerializable {
    private String id;
    private String name;
    private Collection workspaces;
    
    public WorkspaceInfo() {
        super();
    }
    public WorkspaceInfo(String id2, String name2) {
        this.id = id2;
        this.name = name2;
    }
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
    public Collection getWorkspaces() {
        return workspaces;
    }
    public void setWorkspaces(Collection workspaces) {
        this.workspaces = workspaces;
    }
    
}
