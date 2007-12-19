package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;

import org.mule.galaxy.web.client.ItemNotFoundException;

public interface UserServiceAsync {
    void getUsers(AsyncCallback callback);
    
    void addUser(String username, String fullname, String password, AsyncCallback callback);
    
    void updateUser(WUser user, String oldPassword, String password, String confirm, AsyncCallback callback) 
        throws PasswordChangeException, ItemNotFoundException;
}
