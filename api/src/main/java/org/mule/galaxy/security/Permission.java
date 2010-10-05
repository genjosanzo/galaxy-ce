package org.mule.galaxy.security;

import java.io.Serializable;

/**
 * A permission in the system. Permissions have a unique ID and a descriptive name. If
 * they can be applied to specific objects (Items, Scripts, Servers, etc), then 
 * objectPermission is set to true.
 */
public class Permission implements Serializable {

    public static final String READ_ITEM = "READ_ITEM";
    public static final String MODIFY_ITEM = "MODIFY_ITEM";
    public static final String DELETE_ITEM = "DELETE_ITEM";
    public static final String VIEW_ACTIVITY = "VIEW_ACTIVITY";
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String MANAGE_INDEXES = "MANAGE_INDEXES";
    public static final String MANAGE_GROUPS = "MANAGE_GROUPS";
    public static final String MANAGE_POLICIES = "MANAGE_POLICIES";
    public static final String MANAGE_PROPERTIES = "MANAGE_PROPERTIES";
    public static final String MANAGE_LIFECYCLES = "MANAGE_LIFECYCLES";
    public static final String EXECUTE_ADMIN_SCRIPTS = "EXECUTE_ADMIN_SCRIPTS";
    
    private String id;
    private String name;
    private boolean objectPermission;
    
    public Permission() {
        super();
    }
    
    public Permission(String id, String name) {
        this(id, name, false);
    }
    
    public Permission(String id, String name, boolean objectPermission) {
        this.id = id;
        this.name = name;
        this.objectPermission = objectPermission;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isObjectPermission() {
        return objectPermission;
    }
    public void setObjectPermission(boolean objectPermission) {
        this.objectPermission = objectPermission;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permission other = (Permission) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return id;
    }
    
}
