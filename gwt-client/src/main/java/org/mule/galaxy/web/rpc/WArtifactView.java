package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Set;

public class WArtifactView implements IsSerializable {
    private String id;
    private String name;
    private Set predicates;
    private boolean shared;
    
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
    public Set getPredicates() {
        return predicates;
    }
    public void setPredicates(Set predicates) {
        this.predicates = predicates;
    }
    public boolean isShared() {
        return shared;
    }
    public void setShared(boolean shared) {
        this.shared = shared;
    }
    
}
