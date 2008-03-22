package org.mule.galaxy.security;

import org.mule.galaxy.Identifiable;

public enum Permission {
    READ_ARTIFACT("Read Artifacts", false),
    MODIFY_ARTIFACT("Modify Artifacts", false),
    DELETE_ARTIFACT("Delete Artifacts", false),
    VIEW_ACTIVITY("View Activity Log", false),
    MANAGE_USERS("Manage Users", false),
    MANAGE_GROUPS("Manage Groups", false),
    MANAGE_POLICIES("Manage Policies", false),
    MANAGE_LIFECYCLES("Manage Lifecycles", false),
    MANAGE_ARTIFACT_TYPES("Manage Artifact Types", false);
    
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
