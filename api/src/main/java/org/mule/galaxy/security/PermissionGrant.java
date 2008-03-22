package org.mule.galaxy.security;

public class PermissionGrant {
    public enum Grant {
        GRANTED,
        REVOKED,
        INHERITED
    }
    
    public PermissionGrant(Permission permission, Grant grant) {
        super();
        this.permission = permission;
        this.grant = grant;
    }
    public PermissionGrant() {
        super();
    }
    private Permission permission;
    private Grant grant;
    
    public Permission getPermission() {
        return permission;
    }
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    public Grant getGrant() {
        return grant;
    }
    public void setGrant(Grant grant) {
        this.grant = grant;
    }
}
