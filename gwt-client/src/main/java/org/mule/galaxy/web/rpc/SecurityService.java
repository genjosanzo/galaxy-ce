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

    Map<WGroup, Collection<WPermissionGrant>> getGroupPermissions();

    Map<WGroup, Collection<WPermissionGrant>> getGroupPermissions(String itemId) throws RPCException;
    
    /**
     * @throws RPCException 
     * @throws ItemExistsException 
     */
    void save(WGroup group) throws RPCException, ItemExistsException;
    
    WGroup getGroup(String id) throws RPCException;
    
    void deleteGroup(String id) throws RPCException;
    

    void applyPermissions(Map<WGroup, Collection<WPermissionGrant>> group2Permissions) throws RPCException;

    void applyPermissions(String itemId, Map<WGroup, Collection<WPermissionGrant>> group2Permissions) throws RPCException;

    Collection getGroups() throws RPCException;
}
