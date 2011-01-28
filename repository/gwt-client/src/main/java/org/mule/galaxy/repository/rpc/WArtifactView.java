package org.mule.galaxy.repository.rpc;

import java.util.HashSet;
import java.util.Set;

import org.mule.galaxy.web.rpc.SearchPredicate;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WArtifactView implements IsSerializable {
    private String id;
    private String name;
    private Set<SearchPredicate> predicates = new HashSet<SearchPredicate>();
    private boolean shared;
    private String workspace;
    private boolean workspaceSearchRecursive;
    private String queryString;
    
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
    public Set<SearchPredicate> getPredicates() {
        return predicates;
    }
    public void setPredicates(Set<SearchPredicate> predicates) {
        this.predicates = predicates;
    }
    public boolean isShared() {
        return shared;
    }
    public void setShared(boolean shared) {
        this.shared = shared;
    }
    public String getWorkspace() {
        return workspace;
    }
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
    public boolean isWorkspaceSearchRecursive() {
        return workspaceSearchRecursive;
    }
    public void setWorkspaceSearchRecursive(boolean workspaceSearchRecursive) {
        this.workspaceSearchRecursive = workspaceSearchRecursive;
    }
    public String getQueryString() {
        return queryString;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
}
