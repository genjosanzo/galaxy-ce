package org.mule.galaxy.web.server;

import org.mule.galaxy.api.NotFoundException;
import org.mule.galaxy.api.security.User;
import org.mule.galaxy.api.security.UserExistsException;
import org.mule.galaxy.api.security.UserManager;
import org.mule.galaxy.web.client.admin.PasswordChangeException;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.WUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserServiceImpl implements UserService
{

    private UserManager userManager;

    public String addUser(WUser user, String password) throws ItemExistsException
    {
        try
        {
            User u = createUser(user);
            userManager.create(u, password);
            return u.getId();
        }
        catch (UserExistsException e)
        {
            throw new ItemExistsException();
        }
    }

    private User createUser(WUser user)
    {
        User u = new User();
        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setUsername(user.getUsername());

        if (user.isAdmin())
        {
            u.getRoles().add(UserManager.ROLE_ADMINISTRATOR);
        }
        else
        {
            u.getRoles().remove(UserManager.ROLE_ADMINISTRATOR);
        }

        return u;
    }

    public Collection getUsers()
    {
        List<User> users = userManager.listAll();

        ArrayList<WUser> webUsers = new ArrayList<WUser>();
        for (User user : users)
        {
            WUser w = createWUser(user);
            webUsers.add(w);
        }
        return webUsers;
    }

    public static WUser createWUser(User user)
    {
        WUser w = new WUser();
        w.setName(user.getName());
        w.setId(user.getId());
        w.setUsername(user.getUsername());
        w.setEmail(user.getEmail());
        w.setAdmin(user.getRoles().contains(UserManager.ROLE_ADMINISTRATOR));
        return w;
    }

    public void updateUser(WUser user, String password, String confirm)
            throws NotFoundException, PasswordChangeException
    {
        User u = userManager.get(user.getId());

        if (u == null)
        {
            throw new NotFoundException(user.getId());
        }

        u.setName(user.getName());
        u.setEmail(user.getEmail());

        if (user.isAdmin())
        {
            u.getRoles().add(UserManager.ROLE_ADMINISTRATOR);
        }
        else
        {
            u.getRoles().remove(UserManager.ROLE_ADMINISTRATOR);
        }

        if (password != null && password.equals(confirm) && !password.equals(""))
        {
            userManager.setPassword(u, password);
        }

        userManager.save(u);
    }

    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

}
