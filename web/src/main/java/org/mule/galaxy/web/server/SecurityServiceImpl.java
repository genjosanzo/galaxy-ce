/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.PermissionGrant;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.client.admin.PasswordChangeException;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WPermission;
import org.mule.galaxy.web.rpc.WPermissionGrant;
import org.mule.galaxy.web.rpc.WUser;

public class SecurityServiceImpl implements SecurityService {

    private final Log log = LogFactory.getLog(getClass());

    private UserManager userManager;
    private AccessControlManager accessControlManager;
    private Registry registry;
    private Set<Permission> itemPermissions;
    
    public SecurityServiceImpl() {
        super();
        itemPermissions = new HashSet<Permission>();
        itemPermissions.add(Permission.DELETE_ITEM);
        itemPermissions.add(Permission.MANAGE_POLICIES);
        itemPermissions.add(Permission.MODIFY_ITEM);
        itemPermissions.add(Permission.READ_ITEM);
    }

    public String addUser(WUser user, String password) throws ItemExistsException {
        try {
            User u = createUser(user);
            if (user.getGroupIds() != null) {
                for (Object o : user.getGroupIds()) {
                    try {
                        u.addGroup(accessControlManager.getGroup(o.toString()));
                    } catch (NotFoundException e) {
                    }
                }
            }
            userManager.create(u, password);
            return u.getId();
        } catch (UserExistsException e) {
            throw new ItemExistsException();
        }
    }
    
    public WUser getUser(String id) throws RPCException {
        try {
            User user = userManager.get(id);
            return toWeb(user);
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private WUser toWeb(User user) {
        WUser w = createWUser(user);
        
        ArrayList<String> groupIds = new ArrayList<String>();
        if (user.getGroups() != null) {
            for (Group g : user.getGroups()) {
                groupIds.add(g.getId());
            }
        }
        w.setGroupIds(groupIds);
        
        return w;
    }

    private User createUser(WUser user) {
        User u = new User();
        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setUsername(user.getUsername());
        
        return u;
    }

    public Collection<WUser> getUsers() {
        List<User> users = userManager.listAll();
        
        ArrayList<WUser> webUsers = new ArrayList<WUser>();
        for (User user : users) {
            WUser w = createWUser(user);
            
            webUsers.add(w);
        }
        
        Collections.sort(webUsers, new Comparator<WUser>() {

            public int compare(WUser o1, WUser o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
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
        throws ItemNotFoundException, PasswordChangeException, RPCException {
        try {
            try {
                accessControlManager.assertAccess(Permission.MANAGE_USERS);
            } catch (AccessException e) {
                // TODO probably change the signature to throw AccessException
                throw new RPCException(e.getMessage());
            }

            User u = userManager.get(user.getId());

            if (u == null) {
                throw new ItemNotFoundException();
            }

            u.setName(user.getName());
            u.setEmail(user.getEmail());

            if (password != null && password.equals(confirm) && !password.equals("")) {
                userManager.setPassword(u, password);
            }

            u.getGroups().clear();
            for (Object o : user.getGroupIds()) {
                u.getGroups().add(accessControlManager.getGroup(o.toString()));
            }
            userManager.save(u);


        } catch (DuplicateItemException e) {
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        }
    }

    public void deleteUser(String userId) {
        userManager.delete(userId);
    }

    public void applyPermissions(Map groupToPermissionGrant) throws RPCException {
        for (Iterator itr = groupToPermissionGrant.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry)itr.next();
            
            WGroup wGroup = (WGroup) e.getKey();
            Collection permGrants = (Collection) e.getValue();
            
            try {
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
            } catch (AccessException e1) {
                throw new RPCException(e1.getMessage());
            } catch (NotFoundException e1) {
                throw new RPCException(e1.getMessage());
            }
        }
    }

    public Map<WGroup, Collection<WPermissionGrant>> getGroupPermissions() {
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

    public void applyPermissions(String itemId, Map groupToPermissionGrant) throws RPCException {
        try {
            Item item = registry.getItemById(itemId);
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
                    } else if (permGrant.getGrant() == WPermissionGrant.REVOKED) {
                        revocations.add(p);
                    }
                }
                
                accessControlManager.clear(group, item);
                accessControlManager.revoke(group, revocations, item);
                accessControlManager.grant(group, grants, item);
            }
        } catch (RegistryException e) {
            log.error( e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            log.error( e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public Map<WGroup, Collection<WPermissionGrant>> getGroupPermissions(String itemId) throws RPCException {
        Map<WGroup, Collection<WPermissionGrant>> wgroups = new HashMap<WGroup, Collection<WPermissionGrant>>();
        List<Group> groups = accessControlManager.getGroups();
        
        try {
            Item item = registry.getItemById(itemId);
            
            for (Group g : groups) {
                WGroup wgroup = toWeb(g);
                List<WPermissionGrant> wpgs = new ArrayList<WPermissionGrant>();
                
                Set<PermissionGrant> grants = accessControlManager.getPermissionGrants(g, item);
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
        } catch (RegistryException e) {
            log.error( e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            log.error( e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
            
    }
    
    public void save(WGroup wgroup) throws RPCException, ItemExistsException {
        try {
            Group g = null;
            if (wgroup.getId() != null) {
                g = accessControlManager.getGroup(wgroup.getId());
            } else {
                g = new Group();
            }
            g.setName(wgroup.getName());
            accessControlManager.save(g);
        } catch (AccessException e1) {
            throw new RPCException(e1.getMessage());
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }
 
    public void deleteGroup(String id) throws RPCException {
        accessControlManager.deleteGroup(id);
    }

    public WGroup getGroup(String id) throws RPCException {
        try {
            return toWeb(accessControlManager.getGroup(id));
        } catch (Exception e) {
            throw new RPCException(e.getMessage());
        }
    }

    private WGroup toWeb(Group g) {
        return new WGroup(g.getId(), g.getName());
    }

    public Collection<WPermission> getPermissions(int permissionType) {
        List<Permission> permissions = accessControlManager.getPermissions();
        ArrayList<WPermission> wperms = new ArrayList<WPermission>();
        
        for (Permission p : permissions) {
            if ((permissionType == SecurityService.ITEM_PERMISSIONS && itemPermissions.contains(p))
                    || permissionType == SecurityService.GLOBAL_PERMISSIONS) {
                wperms.add(new WPermission(p.toString(), p.getDescription()));
            }
        }
        return wperms;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Collection<WGroup> getGroups() throws RPCException {
        ArrayList<WGroup> wgroups = new ArrayList<WGroup>();
        
        for (Group g : accessControlManager.getGroups()) {
            wgroups.add(toWeb(g));
        }
        
        return wgroups;
    }
    
}
