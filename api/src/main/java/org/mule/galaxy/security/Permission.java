package org.mule.galaxy.security;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;

public enum Permission {
    READ_ARTIFACT("Read Artifact", Artifact.class, Workspace.class),
    MODIFY_ARTIFACT("Modify Artifact", Artifact.class, Workspace.class),
    DELETE_ARTIFACT("Delete Artifact", Artifact.class, Workspace.class),
    READ_WORKSPACE("Read Workspace", Workspace.class),
    MODIFY_WORKSPACE("Modify Workspace", Workspace.class),
    DELETE_WORKSPACE("Delete Workspace", Workspace.class),
    VIEW_ACTIVITY("View Activity Log"),
    MANAGE_USERS("Manage Users"),
    MANAGE_INDEXES("Manage Indexes"),
    MANAGE_GROUPS("Manage Groups"),
    MANAGE_POLICIES("Manage Policies"),
    MANAGE_LIFECYCLES("Manage Lifecycles"),
    MANAGE_ARTIFACT_TYPES("Manage Artifact Types");
    
    private String description;
    private Class<? extends Item>[] appliesTo;
    
    Permission(String description, Class<? extends Item>... appliesTo) {
        this.description = description;
        this.appliesTo = appliesTo;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Class[] getAppliesTo() {
        return appliesTo;
    }
    public void setAppliesTo(Class[] appliesTo) {
        this.appliesTo = appliesTo;
    }
}
