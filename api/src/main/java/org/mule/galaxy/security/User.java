package org.mule.galaxy.security;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;

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

    @OneToMany
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Map<String, Object> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
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

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final User user = (User) o;

        if (!id.equals(user.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
