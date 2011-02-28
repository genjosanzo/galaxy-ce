package org.mule.galaxy.security;

import java.util.Collection;

import org.mule.galaxy.event.GalaxyEvent;

public class AccessChangeEvent extends GalaxyEvent {
    public enum Type {
        GRANT, REVOKE, DELETED, CREATED
    }

    private Type type;
    private Group group;
    private Collection<Permission> permissions;
    private final Object item;

    public AccessChangeEvent(Type type, Group group, Collection<Permission> perms) {
        this(type, group, perms, null);
    }
    
    public AccessChangeEvent(Type type, Group group, Collection<Permission> perms, Object item) {
        this.type = type;
        this.group = group;
        this.permissions = perms;
        this.item = item;
    }

    public AccessChangeEvent(Type type, Group group) {
        this(type, group, null);
    }

    public Type getType() {
        return type;
    }

    public Group getGroup() {
        return group;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public Object getItem() {
        return item;
    }

}
