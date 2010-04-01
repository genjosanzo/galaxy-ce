package org.mule.galaxy.security;

import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.NotFoundException;

public interface UserManager extends Dao<User> {
    
    User authenticate(String username, String password);
    
    void create(User user, String password) throws UserExistsException;
    
    boolean setPassword(String username, String oldPassword, String newPassword);

    void setPassword(User user, String password);

    User getByUsername(String string) throws NotFoundException;
    
    List<User> getUsersForGroup(String groupId);
    
    /**
     * Whether or not this implementation allows you to update users, change passwords,
     * etc, or if we're pulling from another source.
     * @return
     */
    boolean isManagementSupported();
}
