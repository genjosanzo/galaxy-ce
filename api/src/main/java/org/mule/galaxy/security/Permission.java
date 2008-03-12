package org.mule.galaxy.security;

import org.mule.galaxy.Identifiable;

public class Permission {
    private String name;
    private String description;
    private boolean global;
    
    public Permission() {
        super();
    }
    public Permission(String name, String description, boolean global) {
        super();
        this.name = name;
        this.description = description;
        this.global = global;
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
    public boolean isGlobalOnly() {
        return global;
    }
    public void setGlobalOnly(boolean global) {
        this.global = global;
    }
}
