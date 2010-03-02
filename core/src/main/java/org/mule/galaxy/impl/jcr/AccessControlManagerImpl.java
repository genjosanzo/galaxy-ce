package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.onm.AbstractDao;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.PermissionGrant;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.util.SecurityUtils;
import org.springmodules.jcr.JcrCallback;

public class AccessControlManagerImpl extends AbstractDao<Group> implements AccessControlManager {
    private static final String GRANTS = "grants";
    private static final String REVOCATIONS = "revocations";
    private UserManager userManager;
    
    public AccessControlManagerImpl() throws Exception {
        super(Group.class, "groups", false);
    }

    @Override
    protected String generateNodeName(Group t) {
        return t.getName();
    }
    
    protected Node findNode(String id, Session session) throws RepositoryException {
        return getNodeByUUID(id);
    }

    @Override
    protected void doCreateInitialNodes(Session session, Node objects) throws RepositoryException {
        try {
            boolean first = objects.getNodes().getSize() == 0;
            Group adminGroup; 
            try {
                adminGroup = getGroupByName("Administrators");
            } catch (NotFoundException e) {
                adminGroup = new Group("Administrators");
                Node gNode = objects.addNode(adminGroup.getName(), getNodeType());
                gNode.addMixin("mix:referenceable");
                adminGroup.setId(gNode.getUUID());
                persist(adminGroup, gNode, session);
            }
            
            if (first) {
                Group userGroup = new Group("Users");
                Node gNode = objects.addNode(userGroup.getName(), getNodeType());
                gNode.addMixin("mix:referenceable");
                userGroup.setId(gNode.getUUID());
                persist(userGroup, gNode, session);
    
                List<Permission> toGrant = new ArrayList<Permission>();
                toGrant.add(Permission.READ_ITEM);
                toGrant.add(Permission.MODIFY_ITEM);
                toGrant.add(Permission.DELETE_ITEM);
                grant(userGroup, toGrant);
                
                
                User admin = userManager.getByUsername("admin");
                if (admin != null) {
                    admin.addGroup(adminGroup);
                    admin.addGroup(userGroup);
                    userManager.save(admin);
                }
//
//                Group anonGroup = new Group("Anonymous");
//                gNode = objects.addNode(anonGroup.getName(), getNodeType());
//                gNode.addMixin("mix:referenceable");
//                anonGroup.setId(gNode.getUUID());
//                persist(anonGroup, gNode, session);
//    
//                toGrant = new ArrayList<Permission>();
//                toGrant.add(Permission.READ_ARTIFACT);
//                toGrant.add(Permission.READ_WORKSPACE);
//                grant(anonGroup, toGrant);
//
//
//                User anon = userManager.getByUsername("anonymous");
//                if (anon != null) {
//                    anon.addGroup(anonGroup);
//                    userManager.save(anon);
//                }
            }
            
            // ensure that the admin group always has all the permissions
            grant(adminGroup, Arrays.asList(Permission.values()));
        } catch (Exception e) {
            if (e instanceof RepositoryException) {
                throw (RepositoryException) e;
            }
           
            throw new RuntimeException(e);
        }
    }

    @Override
    public Group build(Node node, Session session) throws Exception {
        Group group = new Group();
        group.setId(node.getUUID());
        group.setName(node.getName());
        
        return group;
    }

    @Override
    protected String getObjectNodeName(Group t) {
        return ISO9075.encode(t.getName());
    }

    @Override
    protected void persist(Group group, Node node, Session session) throws Exception {
        if(!group.getName().equals(node.getName())) {
            session.move(node.getPath(), node.getParent().getPath() + "/" + getObjectNodeName(group));
        }
    }

    public void deleteGroup(String id) {
        delete(id);
    }

    @Override
    protected void doDelete(String id, Session session) throws RepositoryException {
        List<User> usersWithGroup = userManager.find("groups", id);
        
        for (User u : usersWithGroup) {
            for (Group g : u.getGroups()) {
                if (g.getId().equals(id)) {
                    u.getGroups().remove(g);
                    try {
                        userManager.save(u);
                    } catch (DuplicateItemException e) {
                        // can't happen
                        throw new RuntimeException(e);
                    } catch (NotFoundException e) {
                        // can't happen
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }
        super.doDelete(id, session);
    }

    public List<Group> getGroups() {
        return listAll();
    }

    public Group getGroup(String id) throws NotFoundException {
        return get(id);
    }
    

    @SuppressWarnings("unchecked")
    public Group getGroupByName(final String name) throws NotFoundException {
        List<Group> groups = (List<Group>) execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                String stmt = "/jcr:root/groups/" + ISO9075.encode(name);
                
                return query(stmt, session);
            }
            
        });
        
        if (groups.size() == 0) {
            throw new NotFoundException(name);
        }
        
        return groups.get(0);
    }

    public List<Permission> getPermissions() {
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        for (Permission p : Permission.values()) {
            permissions.add(p);
        }
        return permissions;
    }

    public Set<PermissionGrant> getPermissionGrants(final Group group) {
        final Set<PermissionGrant> pgs = new HashSet<PermissionGrant>();
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node groupNode = findNode(group.getId(), session);
                getGrants(groupNode, pgs, null);
                return null;
            }
        });
        
        return pgs;
    }

    public Set<PermissionGrant> getPermissionGrants(final Group group, final Item item) {
        final Set<PermissionGrant> pgs = new HashSet<PermissionGrant>();
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                getPermissionGrants(group, item, pgs, session);
                return null;
            }
        });
        
        return pgs;
    }

    protected void getPermissionGrants(final Group group, 
                                       final Item item, 
                                       final Set<PermissionGrant> pgs,
                                       Session session) throws RepositoryException, ValueFormatException {
        Node groupNode = findNode(group.getId(), session);
        
        try {
            Node itemNode = groupNode.getNode(item.getId());

            getGrants(itemNode, pgs, item);
        } catch (PathNotFoundException e) {
            for (Permission p : getPermissions()) {
                if (p.isItemPermission()) {
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.INHERITED));
                }
            }
        }
    }

    protected void getGrants(Node itemNode, final Set<PermissionGrant> pgs, Item item)
        throws RepositoryException, ValueFormatException {
        List<Permission> permissions = getPermissions();
        try {   
            Property property = itemNode.getProperty(GRANTS);
        
            for (Value v : property.getValues()) {
                Permission p = Permission.valueOf(v.getString());
                if (item == null || p.isItemPermission()) {
                    permissions.remove(p);
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.GRANTED));
                }
            }
        } catch (PathNotFoundException e) {
        }

        try {   
            Property property = itemNode.getProperty(REVOCATIONS);
            
            for (Value v : property.getValues()) {
                Permission p = Permission.valueOf(v.getString());
                if (item == null || p.isItemPermission()) {
                    permissions.remove(p);
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.REVOKED));
                }
            }
        } catch (PathNotFoundException e) {
        }
        
        for (Permission p : permissions) {
            if (item == null || p.isItemPermission()) {
                if (item == null) {
                    // this is a root level permission, so it can't inherit.
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.REVOKED));
                } else {
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.INHERITED));
                }
            }
        }
    }

    public Set<Permission> getGrantedPermissions(final Group group) {
        final Set<Permission> permissions = new HashSet<Permission>();
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return getGrantedPermissions(group, permissions, session);
            }
        });
        
        return permissions;
    }

    protected Object getGrantedPermissions(final Group group, 
                                           final Set<Permission> permissions,
                                           Session session) throws RepositoryException,
        ValueFormatException {
        try {
            Node groupNode = findNode(group.getId(), session);
            
            Property property = groupNode.getProperty(GRANTS);
            
            for (Value v : property.getValues()) {
                permissions.add(Permission.valueOf(v.getString()));
            }
        } catch (PathNotFoundException e) {
        }
        return null;
    }

    public Set<Permission> getPermissions(final Group group, final Item item) {
        final Set<Permission> permissions = new HashSet<Permission>();
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                getPermissions(group, item, permissions, session);
                return null;
            }
        });
        
        return permissions;
    }

    protected void getPermissions(final Group group, final Item item,
                                final Set<Permission> permissions, Session session)
        throws RepositoryException, ValueFormatException {
        try {
            Node groupNode = findNode(group.getId(), session);
            Node itemNode = null;
            try {
                itemNode = groupNode.getNode(item.getId());
            } catch (PathNotFoundException e) {
                if (item.getParent() != null) {
                    getPermissions(group, item.getParent(), permissions, session);
                    return;
                } else {
                    getGrantedPermissions(group, permissions, session);
                    return;
                }
            }
            
            Property property = itemNode.getProperty(GRANTS);
            
            for (Value v : property.getValues()) {
                permissions.add(Permission.valueOf(v.getString()));
            }
        } catch (PathNotFoundException e) {
        }
    }

    public Set<Permission> getGrantedPermissions(final User user) {
        final Set<Permission> perms = new HashSet<Permission>();
        if (user.getGroups() == null || user.getGroups().size() == 0)
            return perms;
        
        execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                for (Node node : getGroupNodesForUser(user)) {
                    try {
                        Property p = node.getProperty(GRANTS);
                        for (Value v : p.getValues()) {
                            perms.add(Permission.valueOf(v.getString()));
                        }
                    } catch (PathNotFoundException e) {
                        // do nothing
                    }
                }
                return null;
            }
            
        });
        return perms;
    }

    public Set<Permission> getPermissions(final User user, final Item item) {
        final Set<Permission> perms = new HashSet<Permission>();
        final Set<Permission> revocations = new HashSet<Permission>();
        execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                extractPermissions(user, item, perms, revocations);
                return null;
            }
            
        });
        return perms;
    }

    protected void extractPermissions(final User user, 
                                      final Item item, 
                                      final Set<Permission> perms,
                                      final Set<Permission> revocations) throws RepositoryException {
        if (user.getGroups() == null || user.getGroups().size() == 0)
            return;

        for (Node node : getItemNodesForUser(user, item.getId())) {
            try {
                Property grantsProperty = node.getProperty(GRANTS);
                for (Value v : grantsProperty.getValues()) {
                    Permission perm = Permission.valueOf(v.getString());
                    if (!revocations.contains(perm)) {
                        perms.add(perm);
                    }
                }
            } catch (PathNotFoundException e) {
                // do nothing
            }
            
            try {
                Property revocationsProperty = node.getProperty(REVOCATIONS);
                for (Value v : revocationsProperty.getValues()) {
                    Permission perm = Permission.valueOf(v.getString());
                    if (!perms.contains(perm)) {
                        revocations.add(perm);
                    }
                }
            } catch (PathNotFoundException e) {
                // do nothing
            }
        }
        
        Item parent = item.getParent();
        if (parent != null) {
            extractPermissions(user, parent, perms, revocations);
        } else {
            Set<Permission> permissions = getGrantedPermissions(user);
            for (Permission p : permissions) {
                if (!revocations.contains(p)) {
                    perms.add(p);
                }
            }
        }
    }

    private List<Node> getGroupNodesForUser(final User user) throws RepositoryException {
        return getItemNodesForUser(user, null);
    }
    private List<Node> getItemNodesForUser(final User user, String item) throws RepositoryException {
        List<Node> nodes = new ArrayList<Node>();
        for (Group g : user.getGroups()) {
            Node groupNode = getNodeByUUID(g.getId());
            
            if (item != null) {
                try {
                    nodes.add(groupNode.getNode(item));
                } catch (PathNotFoundException e) {
                }
            } else {
                nodes.add(groupNode);
            }
        }
        
        return nodes;
    }

    public void grant(Group group, Permission p, Item item) throws AccessException {
        grant(group, Arrays.asList(p), item);
    }

    public void grant(final Group group, final Permission p) throws AccessException {
        grant(group, Arrays.asList(p));
    }
    
    public void grant(final Group group, final Collection<Permission> perms) throws AccessException {
        assertAccess(Permission.MANAGE_GROUPS);
        
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, session, GRANTS, REVOCATIONS);
                return null;
            }
        });
    }

    protected void modifyPermissions(final Group group, final Collection<Permission> perms,
                                     Session session, String propertyToAddTo, String propertyToRemoveFrom) throws RepositoryException {
        Node groupNode = findNode(group.getId(), session);
        
        try {
            Property property = groupNode.getProperty(propertyToAddTo);
            Set<String> values = JcrUtil.asSet(property.getValues());
            for (Permission p : perms) {
                values.add(p.toString());
            }
            
            property.setValue(values.toArray(new String[values.size()]));
        } catch (PathNotFoundException e) {
            Set<String> values = new HashSet<String>();
            for (Permission p : perms) {
                values.add(p.toString());
            }
            groupNode.setProperty(propertyToAddTo, values.toArray(new String[values.size()]));
        }
        
        try {
            Property property = groupNode.getProperty(propertyToRemoveFrom);
            Set<String> values = JcrUtil.asSet(property.getValues());
            for (Permission p : perms) {
                values.remove(p.toString());
            }
            
            property.setValue(values.toArray(new String[values.size()]));
        } catch (PathNotFoundException e) {
        }
        session.save();
    }

    protected void modifyPermissions(final Group group, 
                                     final Collection<Permission> perms,
                                     final Item item,
                                     Session session, 
                                     String propertyName,
                                     String propertyToRemoveFrom) throws RepositoryException {
        Node groupNode = findNode(group.getId(), session);
        
        Node itemNode = JcrUtil.getOrCreate(groupNode, item.getId());
        
        try {
            Property property = itemNode.getProperty(propertyName);
            Set<String> values = JcrUtil.asSet(property.getValues());
            for (Permission p : perms) {
                values.add(p.toString());
            }
            
            property.setValue(values.toArray(new String[values.size()]));
        } catch (PathNotFoundException e) {
            Set<String> values = new HashSet<String>();
            for (Permission p : perms) {
                values.add(p.toString());
            }
            itemNode.setProperty(propertyName, values.toArray(new String[values.size()]));
        }
        

        try {
            Property property = itemNode.getProperty(propertyToRemoveFrom);
            Set<String> values = JcrUtil.asSet(property.getValues());
            for (Permission p : perms) {
                values.remove(p.toString());
            }
            
            property.setValue(values.toArray(new String[values.size()]));
        } catch (PathNotFoundException e) {
        }
        session.save();
    }
    
    public void clear(final Group group, final Item item) throws AccessException {
        assertAccess(Permission.MANAGE_GROUPS);
        assertAccess(Permission.MODIFY_ITEM, item);
        
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                Node groupNode = findNode(group.getId(), session);
                
                Node itemNode = JcrUtil.getOrCreate(groupNode, item.getId());
                
                itemNode.setProperty(GRANTS, (Value[]) null);
                itemNode.setProperty(REVOCATIONS, (Value[]) null);
                return null;
            }
        });
    }

    public void revoke(Group group, Permission p, Item item) throws AccessException {
        revoke(group, Arrays.asList(p), item);
    }

    public void grant(final Group group, final Collection<Permission> perms, final Item item) throws AccessException {
        assertAccess(Permission.MANAGE_GROUPS);
        assertAccess(Permission.MODIFY_ITEM, item);
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, item, session, GRANTS, REVOCATIONS);
                return null;
            }
        });
    }

    public void revoke(final Group group, final Collection<Permission> perms, final Item item) throws AccessException {
        assertAccess(Permission.MANAGE_GROUPS);
        assertAccess(Permission.MODIFY_ITEM, item);
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, item, session, REVOCATIONS, GRANTS);
                return null;
            }
        });
    }
    
    public void revoke(final Group group, final Permission p) throws AccessException {
        revoke(group, Arrays.asList(p));
    }
    
    public void revoke(final Group group, final Collection<Permission> perms) throws AccessException {
        assertAccess(Permission.MANAGE_GROUPS);
        
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, session, REVOCATIONS, GRANTS);
                return null;
            }
        });
    }

    public void assertAccess(Permission p) throws AccessException {
        User currentUser = SecurityUtils.getCurrentUser();
        
        if (currentUser == null) {
            throw new AccessException();
        }
        
        if (currentUser.equals(SecurityUtils.SYSTEM_USER)) {
            return;
        }
        
        Set<Permission> perms = getGrantedPermissions(currentUser);
        
        if (!perms.contains(p)) {
            throw new AccessException();
        }
    }

    public void assertAccess(Permission p, Item item) throws AccessException {
        User currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new AccessException();
        }
        
        if (currentUser.equals(SecurityUtils.SYSTEM_USER)) {
            return;
        }
        
        Set<Permission> perms = getPermissions(currentUser, item);
        
        if (!perms.contains(p)) {
            throw new AccessException();
        }
    }
    @Override
    protected String getNodeType() {
        return "galaxy:group";
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
