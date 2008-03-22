package org.mule.galaxy.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;

public interface AccessControlManager {

    List<Group> getGroups();
    
    List<Group> getGroups(User user);
    
    /**
     * Grant a global permission.
     * @param role
     * @param p
     */
    void grant(Group group, Permission p);
    
    void grant(Group group, Collection<Permission> perms);
    
    void revoke(Group group, Permission p);
    
    void revoke(Group group, Collection<Permission> perms);
    
    /**
     * Get all the global permissions which are available.
     * @return
     */
    List<Permission> getPermissions();

    Set<PermissionGrant> getPermissionGrants(Group group);
    
    Set<PermissionGrant> getPermissionGrants(Group group, Item item);
    
    Set<Permission> getGrantedPermissions(Group user);

    Set<Permission> getGrantedPermissions(User user);
    
    /**
     * Grant a permission on a specific workspace.
     * @param group
     * @param p
     * @param w
     */
    void grant(Group group, Permission p, Item item);
    
    void revoke(Group group, Permission p, Item item);
    
    /**
     * Clear permission grants/revocations on a specific item for a Group. This means 
     * permissions will be inherited from the parent item.
     * 
     * @param group
     * @param item
     */
    void clear(Group group, Item item);
    
    Set<Permission> getPermissions(Group group, Item item);
    
    Set<Permission> getPermissions(User user, Item item);

    void assertAccess(Permission permission) throws AccessException;

    void assertAccess(Permission permission, Item item) throws AccessException;

    Group getGroup(String id);

    void save(Group group);
    
    
}       