package org.mule.galaxy.security;

import java.util.Collection;

import org.mule.galaxy.Dao;

public interface UserManager extends Dao<User> {
    User authenticate(String username, String password);
    
    User create(String username, String password, String name) throws UserExistsException;
    
    boolean setPassword(String username, String oldPassword, String newPassword);
}
