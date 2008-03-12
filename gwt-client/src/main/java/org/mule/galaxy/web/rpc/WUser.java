package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

/**
 * "Web user"
 */
public class WUser implements IsSerializable  {
    private String name;
    private String id;
    private String username;
    private String email;
    private Collection permissions;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Collection getPermissions() {
        return permissions;
    }
    public void setPermissions(Collection permissions) {
        this.permissions = permissions;
    }
    
}
