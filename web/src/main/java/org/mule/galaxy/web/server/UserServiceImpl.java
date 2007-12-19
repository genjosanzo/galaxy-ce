package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.web.client.ItemNotFoundException;
import org.mule.galaxy.web.client.admin.PasswordChangeException;
import org.mule.galaxy.web.client.admin.UserService;
import org.mule.galaxy.web.client.admin.WUser;

public class UserServiceImpl implements UserService {

    private UserManager userManager;
    
    

    public String addUser(String username, String fullname, String password) {
        try {
            return userManager.create(username, password, fullname).getId();
        } catch (UserExistsException e) {
            return null;
        }
    }



    public Collection getUsers() {
        List<User> users = userManager.listAll();
        
        ArrayList<WUser> webUsers = new ArrayList<WUser>();
        for (User user : users) {
            WUser w = new WUser();
            w.setName(user.getName());
            w.setId(user.getId());
            w.setUsername(user.getUsername());
            webUsers.add(w);
        }
        return webUsers;
    }

    public void updateUser(WUser user, String oldPass, String password, String confirm) 
        throws ItemNotFoundException, PasswordChangeException {
        User u = userManager.get(user.getId());
        
        if (u == null) {
            throw new ItemNotFoundException();
        }
        
        u.setName(user.getName());
        
        if (password != null && password.equals(confirm) && !password.equals("")) {
            if (!userManager.setPassword(user.getUsername(), oldPass, password)) {
                throw new PasswordChangeException();
            }
        }
        
        userManager.save(u);
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    
}
