package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Map;

import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.client.admin.PasswordChangeException;


public interface SecurityService extends RemoteService {
    
    int ITEM_PERMISSIONS = 0;
    int GLOBAL_PERMISSIONS = 1;
    
    WUser getUser(String id) throws RPCException;

    Collection<WUser> getUsers();
    
    String addUser(WUser user, String password) throws ItemExistsException;
    
    void updateUser(WUser user, String password, String confirm) 
        throws PasswordChangeException, ItemNotFoundException, RPCException;
    
    void deleteUser(String userId);

    Collection<WPermission> getPermissions(int permissionType);

    Map<WRole, Collection<WPermissionGrant>> getGroupPermissions();

    Map<WRole, Collection<WPermissionGrant>> getGroupPermissions(String itemId) throws RPCException;
    
    /**
     * @throws RPCException 
     * @throws ItemExistsException 
     */
    void save(WRole role) throws RPCException, ItemExistsException;
    
    WRole getGroup(String id) throws RPCException;
    
    void deleteGroup(String id) throws RPCException;
    

    void applyPermissions(Map<WRole, Collection<WPermissionGrant>> group2Permissions) throws RPCException;

    void applyPermissions(String itemId, Map<WRole, Collection<WPermissionGrant>> group2Permissions) throws RPCException;

    Collection<WRole> getGroups() throws RPCException;
}
