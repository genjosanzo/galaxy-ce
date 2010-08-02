package org.mule.galaxy.impl.security;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.security.Permission;

public class PermissionDaoImpl extends AbstractReflectionDao<Permission> {

    private PermissionDaoImpl() throws Exception {
        super(Permission.class, "permissions", false);
    }

    @Override
    protected void doCreateInitialNodes(Session session, Node objects) throws RepositoryException {
        Node objectsNode = JcrUtil.getOrCreate(session.getRootNode(), "permissions");

        addPermission(objectsNode, "READ_ITEM", "Repository Item - Read", true);
        addPermission(objectsNode, "MODIFY_ITEM", "Repository Item - Modify", true);
        addPermission(objectsNode, "DELETE_ITEM", "Repository Item - Delete", true);
        addPermission(objectsNode, "VIEW_ACTIVITY", "View Activity", false);
        addPermission(objectsNode, "MANAGE_USERS", "Manage Users", false);
        addPermission(objectsNode, "MANAGE_GROUPS", "Manage Groups", false);
        addPermission(objectsNode, "MANAGE_INDEXES", "Manage Indexes", false);
        addPermission(objectsNode, "MANAGE_POLICIES", "Manage Policies", true);
        addPermission(objectsNode, "MANAGE_PROPERTIES", "Manage Properties", false);
        addPermission(objectsNode, "MANAGE_LIFECYCLES", "Manage Lifecycles", false);
        addPermission(objectsNode, "EXECUTE_ADMIN_SCRIPTS", "Execute Scripts", false);
    }

    protected void addPermission(Node objectsNode, String id, String name, boolean objectPermission) throws RepositoryException {
        Node node = JcrUtil.getOrCreate(objectsNode, id);
        node.addMixin("mix:lockable");
        node.setProperty("name", name);
        if (objectPermission) {
            node.setProperty("objectPermission", objectPermission);
        }
    }

    @Override
    protected String getId(Permission t, Node node, Session session) throws RepositoryException {
        return node.getName();
    }
    
    protected String getObjectNodeName(Permission t) {
        return t.getId();
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        try {
            return getObjectsNode(session).getNode(id);
        } catch (ItemNotFoundException e) {
            return null;
        } catch (PathNotFoundException e) {
            return null;
        }
    }
    
}
