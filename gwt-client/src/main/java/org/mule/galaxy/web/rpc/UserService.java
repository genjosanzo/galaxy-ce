package org.mule.galaxy.web.rpc;

import org.mule.galaxy.api.NotFoundException;
import org.mule.galaxy.web.client.admin.PasswordChangeException;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;


public interface UserService extends RemoteService {
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WUser>
     * @return
     */
    Collection getUsers();
    
    String addUser(WUser user, String password) throws ItemExistsException;
    
    void updateUser(WUser user, String password, String confirm) 
        throws PasswordChangeException, NotFoundException;
}
