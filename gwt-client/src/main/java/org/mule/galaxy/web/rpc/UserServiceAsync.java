package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;


public interface UserServiceAsync extends RemoteService {
    void getUsers(AsyncCallback callback);
    
    void addUser(String username, String fullname, String password, AsyncCallback callback);
    
    void updateUser(WUser user, String oldPassword, String password, String confirm, AsyncCallback callback);
}
