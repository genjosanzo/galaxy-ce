package org.mule.galaxy.security;

import org.mule.galaxy.Identifiable;

public enum Permission {
    READ_ARTIFACT("Read Artifacts", false),
    MODIFY_ARTIFACT("Modify Artifacts", false),
    DELETE_ARTIFACT("Delete Artifacts", false),
    VIEW_ACTIVITY("View Activity Log", true),
    MANAGE_USERS("Manage Users", true),
    MANAGE_GROUPS("Manage Groups", true),
    MANAGE_POLICIES("Manage Policies", true),
    MANAGE_LIFECYCLES("Manage Lifecycles", true),
    MANAGE_ARTIFACT_TYPES("Manage Artifact Types", true);
    
    private String description;
    private boolean global;
    
    Permission(String description, boolean globalOnly) {
        this.description = description;
        this.global = globalOnly;
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
