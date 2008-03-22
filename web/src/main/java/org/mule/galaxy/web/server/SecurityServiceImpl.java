package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.PermissionGrant;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.web.client.admin.PasswordChangeException;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WPermission;
import org.mule.galaxy.web.rpc.WPermissionGrant;
import org.mule.galaxy.web.rpc.WUser;

public class SecurityServiceImpl implements SecurityService {

    private UserManager userManager;
    private AccessControlManager accessControlManager;
    
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
            
            List<Group> groups = accessControlManager.getGroups(user);
            ArrayList<String> groupIds = new ArrayList<String>();
            
            for (Group g : groups) {
                groupIds.add(g.getId());
            }
            
            w.setGroupIds(groupIds);
            
            webUsers.add(w);
        }
        return webUsers;
    }

    public static WUser createWUser(User user) {
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

    public void deleteUser(String userId) {
        userManager.delete(userId);
    }

    public void applyPermissions(Map groupToPermissionGrant) {
        for (Iterator itr = groupToPermissionGrant.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry)itr.next();
            
            WGroup wGroup = (WGroup) e.getKey();
            Collection permGrants = (Collection) e.getValue();
            
            Group group = accessControlManager.getGroup(wGroup.getId());
            
            List<Permission> grants = new ArrayList<Permission>();
            List<Permission> revocations = new ArrayList<Permission>();
            
            for (Iterator pgItr = permGrants.iterator(); pgItr.hasNext();) {
                WPermissionGrant permGrant = (WPermissionGrant)pgItr.next();
                
                Permission p = Permission.valueOf(permGrant.getPermission());
                if (permGrant.getGrant() == WPermissionGrant.GRANTED) {
                    grants.add(p);
                } else {
                    revocations.add(p);
                }
            }
            
            accessControlManager.grant(group, grants);
            accessControlManager.revoke(group, revocations);
        }
    }

    public Map getGroupPermissions() {
        Map<WGroup, Collection<WPermissionGrant>> wgroups = new HashMap<WGroup, Collection<WPermissionGrant>>();
        List<Group> groups = accessControlManager.getGroups();
        
        for (Group g : groups) {
            WGroup wgroup = toWeb(g);
            List<WPermissionGrant> wpgs = new ArrayList<WPermissionGrant>();
            
            Set<PermissionGrant> grants = accessControlManager.getPermissionGrants(g);
            for (PermissionGrant pg : grants) {
                WPermissionGrant wpg = new WPermissionGrant();
                
                switch (pg.getGrant()) {
                case REVOKED:
                    wpg.setGrant(WPermissionGrant.REVOKED);
                    break;
                case INHERITED:
                    wpg.setGrant(WPermissionGrant.INHERITED);
                    break;
                case GRANTED:
                    wpg.setGrant(WPermissionGrant.GRANTED);
                    break;
                }
                wpg.setPermission(pg.getPermission().toString());
                wpgs.add(wpg);
            }
            
            wgroups.put(wgroup, wpgs);
        }
        return wgroups;
    }

    @SuppressWarnings("unchecked")
    public void save(WGroup wgroup) {
        Group g = null;
        if (wgroup.getId() != null) {
            g = accessControlManager.getGroup(wgroup.getId());
        } else {
            g = new Group();
        }
        g.setName(wgroup.getName());
        
        accessControlManager.save(g);
    }

    private WGroup toWeb(Group g) {
        return new WGroup(g.getId(), g.getName());
    }

    public Collection getPermissions() {
        List<Permission> permissions = accessControlManager.getPermissions();
        ArrayList<WPermission> wperms = new ArrayList<WPermission>();
        
        for (Permission p : permissions) {
            wperms.add(new WPermission(p.toString(), p.getDescription()));
        }
        return wperms;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
}
