package org.mule.galaxy.security;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User implements Identifiable, Serializable
{
    private String id;
    private String username;
    private String name;
    private String email;
    private Calendar created;
    private Set<Group> groups;
    private Map<String,Object> properties;
    
    public User(String username) {
        this.username = username;
    }

    public User() {
        super();
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(componentType=Group.class)
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Calendar getCreated() {
        return created;
    }
    
    public void setCreated(Calendar created) {
        this.created = created;
    }

    public boolean isEnabled() {
        return true;
    }

    public void addGroup(Group g) {
        if (groups == null) {
            groups = new HashSet<Group>();
        }
        groups.add(g);
    }
}
