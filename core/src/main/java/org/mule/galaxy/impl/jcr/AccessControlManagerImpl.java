package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.onm.AbstractDao;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.springmodules.jcr.JcrCallback;

public class AccessControlManagerImpl extends AbstractDao<Group> implements AccessControlManager {
    private static final String PERMISSIONS = "permissions";
    private static final String USER_IDS = "userIds";
    private Map<String, Permission> permissions = new HashMap<String, Permission>();
    private UserManager userManager;
    
    public AccessControlManagerImpl() throws Exception {
        super("groups", false);
    }

    @Override
    protected String generateNodeName(Group t) {
        return t.getName();
    }

    public void initialize() throws Exception {
        add(new Permission("read_artifact", "Read Artifacts", false));
        add(new Permission("modify_artifact", "Modify Artifacts", false));
        add(new Permission("delete_artifact", "Delete Artifacts", false));
    
        add(new Permission("view_activity", "View Activity Log", true));
        add(new Permission("manage_users", "Manage Users", true));
        add(new Permission("manage_policies", "Manage Policies", true));
        add(new Permission("manage_indexes", "Manage Indexes", true));
        add(new Permission("manage_lifecycles", "Manage Lifecycles", true));
        add(new Permission("manage_artifactTypes", "Manage Artifact Types", true));
        
        super.initialize();
    }

    private void add(Permission permission) {
        permissions.put(permission.getName(), permission);
    }

    @Override
    protected void doCreateInitialNodes(Session session, Node objects) throws RepositoryException {
        Set<String> userIds = new HashSet<String>();
        userIds.add(userManager.getByUsername("admin").getId());

        try {
            Group group = new Group("Administrators", userIds);
            persist(group, objects, session);
            grant(group, permissions.values());
            
            group = new Group("Users", userIds);
            persist(group, objects, session);
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
        
        Property userNode = node.getProperty(USER_IDS);
        Set<String> userIds = new HashSet<String>();
        for (Value v : userNode.getValues()) {
            userIds.add(v.getString());
        }
        group.setUserIds(userIds);
        
        return group;
    }

    @Override
    protected String getObjectNodeName(Group t) {
        return t.getName();
    }

    @Override
    protected void persist(Group group, Node node, Session session) throws Exception {
        node = node.addNode(group.getName(), getNodeType());
        node.addMixin("mix:referenceable");
        group.setId(node.getUUID());
        
        Set<String> userIds = group.getUserIds();
        
        node.setProperty(USER_IDS, (String[]) userIds.toArray(new String[userIds.size()]));
    }

    public List<Group> getGroups() {
        return listAll();
    }

    public Set<Permission> getGlobalPermissions(final Group group) {
        final Set<Permission> permissions = new HashSet<Permission>();
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Node groupNode = findNode(group.getName(), session);
                    
                    Property property = groupNode.getProperty(PERMISSIONS);
                    
                    for (Value v : property.getValues()) {
                        permissions.add(getPermission(v.getString()));
                    }
                } catch (PathNotFoundException e) {
                }
                return null;
            }
        });
        
        return permissions;
    }

    @SuppressWarnings("unchecked")
    public List<Group> getGroups(final User user) {
        return (List<Group>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return query("//element(*, galaxy:group)[@userIds = '" + user.getId() + "']", session);
            }
        });
    }

    public Set<Permission> getPermissions(final Group group, final Workspace w) {
        final Set<Permission> permissions = new HashSet<Permission>();
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Node groupNode = findNode(group.getName(), session);
                    Node wkspcNode = groupNode.getNode(w.getId());
                    
                    Property property = wkspcNode.getProperty(PERMISSIONS);
                    
                    for (Value v : property.getValues()) {
                        permissions.add(getPermission(v.getString()));
                    }
                } catch (PathNotFoundException e) {
                }
                return null;
            }
        });
        
        return permissions;
    }

    public Set<Permission> getGlobalPermissions(final User user) {
        final Set<Permission> perms = new HashSet<Permission>();
        execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                NodeIterator nodes = query("//element(*, galaxy:group)[@userIds = '" + user.getId() + "']").getNodes();


                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    
                    try {
                        Property p = node.getProperty(PERMISSIONS);
                        for (Value v : p.getValues()) {
                            perms.add(getPermission(v.getString()));
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

    protected Permission getPermission(String string) {
        return permissions.get(string);
    }

    public Set<Permission> getPermissions(final User user, final Workspace w) {
        return null;
    }

    public void grant(Group group, Permission p, Workspace w) {
        // TODO Auto-generated method stub
        
    }

    public void grant(final Group group, final Permission p) {
        grant(group, Arrays.asList(p));
    }
    
    public void grant(final Group group, final Collection<Permission> perms) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node groupNode = findNode(group.getName(), session);
                
                try {
                    Property property = groupNode.getProperty(PERMISSIONS);
                    Set<String> values = JcrUtil.asSet(property.getValues());
                    for (Permission p : perms) {
                        values.add(p.getName());
                    }
                    
                    property.setValue(values.toArray(new String[values.size()]));
                } catch (PathNotFoundException e) {
                    Set<String> values = new HashSet<String>();
                    for (Permission p : perms) {
                        values.add(p.getName());
                    }
                    groupNode.setProperty(PERMISSIONS, values.toArray(new String[values.size()]));
                }
                session.save();
                return null;
            }
        });
    }

    public void revoke(Group group, Permission p, Workspace w) {
        // TODO Auto-generated method stub
        
    }

    public void revoke(Group group, Permission p) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected String getNodeType() {
        return "galaxy:group";
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
