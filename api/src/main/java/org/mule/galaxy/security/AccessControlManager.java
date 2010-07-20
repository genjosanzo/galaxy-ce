package org.mule.galaxy.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;

public interface AccessControlManager {

    List<Group> getGroups();
    
    /**
     * Grant a global permission.
     * @param role
     * @param p
     */
    void grant(Group group, String p) throws AccessException;
    
    void grant(Group group, Collection<String> perms) throws AccessException;
    
    void revoke(Group group, String p) throws AccessException;
    
    void revoke(Group group, Collection<String> perms) throws AccessException;
    
    /**
     * Get all the global permissions which are available.
     * @return
     */
    List<Permission> getPermissions();

    Set<PermissionGrant> getPermissionGrants(Group group);
    
    Set<PermissionGrant> getPermissionGrants(Group group, Object object);
    
    Set<Permission> getGrantedPermissions(Group user);

    Set<Permission> getGrantedPermissions(User user);
    
    /**
     * Grant a permission on a specific item.
     * @param group
     * @param p
     * @param w
     * @throws AccessException 
     */
    void grant(Group group, String p, Object object) throws AccessException;
    
    void revoke(Group group, String p, Object object) throws AccessException;

    /**
     * Grant a permission on a specific workspace.
     * @param group
     * @param p
     * @param w
     */
    void grant(Group group, Collection<String> perms, Object object) throws AccessException;
    
    void revoke(Group group, Collection<String> perms, Object object) throws AccessException;
    
    /**
     * Clear permission grants/revocations on a specific item for a Group. This means 
     * permissions will be inherited from the parent item.
     * 
     * @param group
     * @param item
     * @throws AccessException 
     */
    void clear(Group group, Object object) throws AccessException;
    
    Set<Permission> getPermissions(Group group, Object object);
    
    Set<Permission> getPermissions(User user, Object object);

    void assertAccess(String permission) throws AccessException;

    void assertAccess(String permission, Object object) throws AccessException;

    Group getGroup(String id) throws NotFoundException;

    void save(Group group) throws AccessException, DuplicateItemException, NotFoundException;

    Group getGroupByName(String name) throws NotFoundException;

    void deleteGroup(String id);

    Permission getPermission(String permission) throws NotFoundException;

    void save(Permission permission) throws DuplicateItemException, NotFoundException;
    
    
}       