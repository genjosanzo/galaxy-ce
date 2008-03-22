package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Map;

import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.client.admin.PasswordChangeException;


public interface SecurityService extends RemoteService {
    
    int ARTIFACT_PERMISSIONS = 0;
    int WORKSPACE_PERMISSIONS = 1;
    int GLOBAL_PERMISSIONS = 2;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WUser>
     * @return
     */
    Collection getUsers();
    
    String addUser(WUser user, String password) throws ItemExistsException;
    
    void updateUser(WUser user, String password, String confirm) 
        throws PasswordChangeException, ItemNotFoundException;
    
    void deleteUser(String userId);
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WPermission>
     * @param global TODO
     */
    Collection getPermissions(int permissionType);
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WGroup, java.util.Collection<org.mule.galaxy.web.rpc.WPermissionGrant>>
     */
    Map getGroupPermissions();

    /**
     * @throws RPCException 
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WGroup, java.util.Collection<org.mule.galaxy.web.rpc.WPermissionGrant>>
     */
    Map getGroupPermissions(String itemId) throws RPCException;
    
    /**
     * @gwt.typeArgs group <java.lang.String>
     */
    void save(WGroup group);
    

    /**
     * @gwt.typeArgs group2Permissions <org.mule.galaxy.web.rpc.WGroup, java.util.Collection<org.mule.galaxy.web.rpc.WPermissionGrant>>
     */
    void applyPermissions(Map group2Permissions) throws RPCException;

    /**
     * @throws RPCException 
     * @gwt.typeArgs group2Permissions <org.mule.galaxy.web.rpc.WGroup, java.util.Collection<org.mule.galaxy.web.rpc.WPermissionGrant>>
     */
    void applyPermissions(String itemId, Map group2Permissions) throws RPCException;
    
    /**
     * @throws RPCException 
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WGroup>
     */
    Collection getGroups() throws RPCException;
}
