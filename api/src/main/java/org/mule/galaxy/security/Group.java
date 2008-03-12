package org.mule.galaxy.security;

import java.util.Set;

import org.mule.galaxy.Identifiable;

public class Group implements Identifiable {
    private String id;
    private String name;
    private Set<String> userIds;
    
    public Group() {
        super();
    }
    public Group(String name, Set<String> userId) {
        super();
        this.name = name;
        this.userIds = userId;
    }
    public Group(String id, String name, Set<String> userId) {
        super();
        this.id = id;
        this.name = name;
        this.userIds = userId;
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
    public Set<String> getUserIds() {
        return userIds;
    }
    public void setUserIds(Set<String> userIds) {
        this.userIds = userIds;
    }
    
}
