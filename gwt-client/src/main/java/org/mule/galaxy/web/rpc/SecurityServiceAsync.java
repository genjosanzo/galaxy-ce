package org.mule.galaxy.web.rpc;

import java.util.Collection;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SecurityServiceAsync {
    void getUsers(AsyncCallback callback);
    
    void addUser(WUser user, String password, AsyncCallback callback);
    
    void updateUser(WUser user, String password, String confirm, AsyncCallback callback);

    void deleteUser(String userId, AsyncCallback callback);

    void getPermissions(int permissionType, AsyncCallback callback);

    void save(WGroup role, AsyncCallback callback);
    
    void getGroupPermissions(AsyncCallback callback);
    
    void applyPermissions(Map<WGroup, Collection<WPermissionGrant>> group2Permissions, AsyncCallback callback);

    void getGroupPermissions(String itemId, AsyncCallback callback);
    
    void applyPermissions(String itemId, Map<WGroup, Collection<WPermissionGrant>> group2Permissions, AsyncCallback callback);

    void getGroups(AsyncCallback abstractCallback);

    void deleteGroup(String itemId, AsyncCallback deleteCallback);

    void getGroup(String id, AsyncCallback fetchCallback);

    void getUser(String id, AsyncCallback fetchCallback);

    void setUserProperty(String property, String value, AsyncCallback abstractCallback);
}
