package org.mule.galaxy.security;

import java.util.Collection;

import org.mule.galaxy.Dao;

public interface UserManager extends Dao<User> {
    // NOTE: These roles will be customizable in a future release
    
    String ROLE_USER = "role_user";
    String ROLE_ADMINISTRATOR = "role_administrator";
    
    User authenticate(String username, String password);
    
    void create(User user, String password) throws UserExistsException;
    
    boolean setPassword(String username, String oldPassword, String newPassword);

    void setPassword(User user, String password);
}
