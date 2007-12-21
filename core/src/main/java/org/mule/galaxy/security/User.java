package org.mule.galaxy.security;

import java.util.Calendar;
import java.util.Map;

import org.mule.galaxy.Identifiable;

public class User implements Identifiable {
    private String id;
    private String username;
    private String name;
    private String email;
    private Calendar created;
    private Map<String,Object> properties;
    
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
}
