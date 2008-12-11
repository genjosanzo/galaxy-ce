package org.mule.galaxy.security;

import org.mule.galaxy.Dao;
import org.mule.galaxy.NotFoundException;

public interface UserManager extends Dao<User> {
    
    User authenticate(String username, String password);
    
    void create(User user, String password) throws UserExistsException;
    
    boolean setPassword(String username, String oldPassword, String newPassword);

    void setPassword(User user, String password);

    User getByUsername(String string) throws NotFoundException;
}
