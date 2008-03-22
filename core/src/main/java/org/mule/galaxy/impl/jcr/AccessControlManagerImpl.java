package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.util.ISO9075;
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
import org.mule.galaxy.util.UserUtils;
import org.springmodules.jcr.JcrCallback;

public class AccessControlManagerImpl extends AbstractDao<Group> implements AccessControlManager {
    private static final String GRANTS = "grants";
    private static final String REVOCATIONS = "revocations";
    private static final String USER_IDS = "userIds";
    private UserManager userManager;
    
    public AccessControlManagerImpl() throws Exception {
        super("groups", false);
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
        Set<String> userIds = new HashSet<String>();
        userIds.add(userManager.getByUsername("admin").getId());

        try {
            Group group = new Group("Administrators", userIds);
            Node gNode = objects.addNode(group.getName(), getNodeType());
            gNode.addMixin("mix:referenceable");
            persist(group, gNode, session);
            grant(group, Arrays.asList(Permission.values()));
            
            group = new Group("Users", userIds);
            gNode = objects.addNode(group.getName(), getNodeType());
            gNode.addMixin("mix:referenceable");
            persist(group, gNode, session);
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
        
        try {
            Property userNode = node.getProperty(USER_IDS);
            Set<String> userIds = new HashSet<String>();
            for (Value v : userNode.getValues()) {
                userIds.add(v.getString());
            }
            group.setUserIds(userIds);
        } catch (PathNotFoundException e) {
        }
        return group;
    }

    @Override
    protected String getObjectNodeName(Group t) {
        return t.getName();
    }

    @Override
    protected void persist(Group group, Node node, Session session) throws Exception {
        group.setId(node.getUUID());
        
        Set<String> userIds = group.getUserIds();
        
        if (userIds != null) {
            node.setProperty(USER_IDS, (String[]) userIds.toArray(new String[userIds.size()]));
        }
    }

    public List<Group> getGroups() {
        return listAll();
    }

    public Group getGroup(String id) {
        return get(id);
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
                getGrants(groupNode, pgs, true);
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

    protected void getPermissionGrants(final Group group, final Item item, 
                                       final Set<PermissionGrant> pgs,
                                       Session session) throws RepositoryException, ValueFormatException {
        Node groupNode = findNode(group.getId(), session);
        
        try {
            Node itemNode = groupNode.getNode(item.getId());

            getGrants(itemNode, pgs, false);
        } catch (PathNotFoundException e) {
            for (Permission p : getPermissions()) {
                if (!p.isGlobalOnly()) {
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.INHERITED));
                }
            }
        }
    }

    protected void getGrants(Node wkspcNode, final Set<PermissionGrant> pgs, boolean global)
        throws RepositoryException, ValueFormatException {
        List<Permission> permissions = getPermissions();
        try {   
            Property property = wkspcNode.getProperty(GRANTS);
        
            for (Value v : property.getValues()) {
                Permission p = Permission.valueOf(v.getString());
                if (global || !p.isGlobalOnly()) {
                    permissions.remove(p);
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.GRANTED));
                }
            }
        } catch (PathNotFoundException e) {
        }

        try {   
            Property property = wkspcNode.getProperty(REVOCATIONS);
            
            for (Value v : property.getValues()) {
                Permission p = Permission.valueOf(v.getString());
                if (global || !p.isGlobalOnly()) {
                    permissions.remove(p);
                    pgs.add(new PermissionGrant(p, PermissionGrant.Grant.REVOKED));
                }
            }
        } catch (PathNotFoundException e) {
        }
        
        for (Permission p : permissions) {
            if (global || !p.isGlobalOnly()) {
                if (global) {
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

    @SuppressWarnings("unchecked")
    public List<Group> getGroups(final User user) {
        return (List<Group>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return query("//element(*, galaxy:group)[@userIds = " + JcrUtil.stringToXPathLiteral(user.getId()) + "]", session);
            }
        });
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
        execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                NodeIterator nodes = query("//element(*, galaxy:group)[@userIds = '" + user.getId() + "']").getNodes();

                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    
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

    protected void extractPermissions(final User user, final Item item, final Set<Permission> perms,
                                      final Set<Permission> revocations) throws RepositoryException {
        NodeIterator nodes = query("//element(*, galaxy:group)[@userIds = '" + user.getId() + "']/" 
                                   + ISO9075.encode(item.getId())).getNodes();

        while (nodes.hasNext()) {
            Node node = nodes.nextNode();
            
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

    public void grant(Group group, Permission p, Item item) {
        grant(group, Arrays.asList(p), item);
    }

    public void grant(final Group group, final Permission p) {
        grant(group, Arrays.asList(p));
    }
    
    public void grant(final Group group, final Collection<Permission> perms) {
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
    
    public void clear(final Group group, final Item item) {
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

    public void revoke(Group group, Permission p, Item item) {
        revoke(group, Arrays.asList(p), item);
    }

    public void grant(final Group group, final Collection<Permission> perms, final Item item) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, item, session, GRANTS, REVOCATIONS);
                return null;
            }
        });
    }

    public void revoke(final Group group, final Collection<Permission> perms, final Item item) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, item, session, REVOCATIONS, GRANTS);
                return null;
            }
        });
    }
    
    public void revoke(final Group group, final Permission p) {
        revoke(group, Arrays.asList(p));
    }
    
    public void revoke(final Group group, final Collection<Permission> perms) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {                
                modifyPermissions(group, perms, session, REVOCATIONS, GRANTS);
                return null;
            }
        });
    }

    public void assertAccess(Permission p) throws AccessException {
        User currentUser = UserUtils.getCurrentUser();
        
        if (currentUser == null) {
            throw new AccessException();
        }
        
        if (currentUser.equals(UserUtils.SYSTEM_USER)) {
            return;
        }
        
        Set<Permission> perms = getGrantedPermissions(currentUser);
        
        if (!perms.contains(p)) {
            throw new AccessException();
        }
    }

    public void assertAccess(Permission p, Item item) throws AccessException {
        User currentUser = UserUtils.getCurrentUser();

        if (currentUser == null) {
            throw new AccessException();
        }
        
        if (currentUser.equals(UserUtils.SYSTEM_USER)) {
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
