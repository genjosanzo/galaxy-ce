package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WPermissionGrant implements IsSerializable {
    public static final int GRANTED = 1;
    public static final int INHERITED = 0;
    public static final int REVOKED = -1;
    
    private String permission;
    private int grant;
    
    public String getPermission() {
        return permission;
    }
    public void setPermission(String permission) {
        this.permission = permission;
    }
    public int getGrant() {
        return grant;
    }
    public void setGrant(int grant) {
        this.grant = grant;
    }
}
