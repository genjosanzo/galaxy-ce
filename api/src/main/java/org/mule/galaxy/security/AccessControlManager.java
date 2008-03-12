package org.mule.galaxy.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    
    Set<Permission> getGlobalPermissions(Group user);

    Set<Permission> getGlobalPermissions(User user);
    
    /**
     * Grant a permission on a specific workspace.
     * @param group
     * @param p
     * @param w
     */
    void grant(Group group, Permission p, Workspace w);
    
    void revoke(Group group, Permission p, Workspace w);
    
    Set<Permission> getPermissions(Group group, Workspace w);
    
    Set<Permission> getPermissions(User user, Workspace w);
    
}
