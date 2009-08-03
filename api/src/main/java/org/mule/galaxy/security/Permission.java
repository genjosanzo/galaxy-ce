package org.mule.galaxy.security;

public enum Permission {
    READ_ITEM("Read Artifact/Entry", true),
    MODIFY_ITEM("Modify Artifact/Entry", true),
    DELETE_ITEM("Delete Artifact/Entry", true),
    VIEW_ACTIVITY("View Activity Log", false),
    MANAGE_USERS("Manage Users", false),
    MANAGE_INDEXES("Manage Indexes", false),
    MANAGE_GROUPS("Manage Groups", false),
    MANAGE_POLICIES("Manage Policies", true),
    MANAGE_PROPERTIES("Manage Properties", false),
    MANAGE_LIFECYCLES("Manage Lifecycles", false),
    MANAGE_ARTIFACT_TYPES("Manage Artifact Types", false),
    EXECUTE_ADMIN_SCRIPTS("Execute Admin Scripts", false);
    
    private String description;
    private boolean itemPermission;
    
    Permission(String description, boolean itemPermission) {
        this.description = description;
        this.itemPermission = itemPermission;
    }
    public String getDescription() {
        return description;
    }
    public boolean isItemPermission() {
        return itemPermission;
    }
    
}
