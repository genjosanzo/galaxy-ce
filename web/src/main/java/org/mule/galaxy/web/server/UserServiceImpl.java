package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.web.client.admin.PasswordChangeException;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.WUser;

public class UserServiceImpl implements UserService {

    private UserManager userManager;
    
    public String addUser(WUser user, String password) throws ItemExistsException {
        try {
            User u = createUser(user);
            userManager.create(u, password);
            return u.getId();
        } catch (UserExistsException e) {
            throw new ItemExistsException();
        }
    }
    
    private User createUser(WUser user) {
        User u = new User();
        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setUsername(user.getUsername());
        return u;
    }

    public Collection getUsers() {
        List<User> users = userManager.listAll();
        
        ArrayList<WUser> webUsers = new ArrayList<WUser>();
        for (User user : users) {
            WUser w = createWUser(user);
            webUsers.add(w);
        }
        return webUsers;
    }

    private WUser createWUser(User user) {
        WUser w = new WUser();
        w.setName(user.getName());
        w.setId(user.getId());
        w.setUsername(user.getUsername());
        w.setEmail(user.getEmail());
        return w;
    }

    public void updateUser(WUser user, String password, String confirm) 
        throws ItemNotFoundException, PasswordChangeException {
        try {
            User u = userManager.get(user.getId());
            
            if (u == null) {
                throw new ItemNotFoundException();
            }
            
            u.setName(user.getName());
            u.setEmail(user.getEmail());
            
            if (password != null && password.equals(confirm) && !password.equals("")) {
                userManager.setPassword(u, password);
            }
            
            userManager.save(u);
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        }
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    
}
