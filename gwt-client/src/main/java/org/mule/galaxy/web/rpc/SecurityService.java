package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Map;

import org.mule.galaxy.web.client.admin.PasswordChangeException;


public interface SecurityService extends RemoteService {
    
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
     */
    Collection getPermissions();
    
    /**
     * @return
     */
    Map getGroupPermissions();
    
    /**
     * @gwt.typeArgs group <java.lang.String>
     */
    void save(WGroup group);
    

    /**
     * @gwt.typeArgs group2Permissions <org.mule.galaxy.web.rpc.WGroup, java.util.Collection<org.mule.galaxy.web.rpc.WPermissionGrant>>
     */
    void applyPermissions(Map group2Permissions);
    
}
