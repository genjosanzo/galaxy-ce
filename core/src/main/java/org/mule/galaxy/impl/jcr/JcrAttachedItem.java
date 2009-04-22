package org.mule.galaxy.impl.jcr;

import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mule.galaxy.AttachedItem;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.workspace.WorkspaceManager;

public class JcrAttachedItem extends JcrItem implements AttachedItem {

    public static final String WORKSPACE_MANAGER_FACTORY = "workspaceManagerFactory";
    private static final String CONFIGURATION = "configuration";
    private WorkspaceManager workspaceManager;
    private Map<String, String> configuration;
    private List<Item> items;

    public JcrAttachedItem(Node node, JcrWorkspaceManager manager) throws RepositoryException {
        super(node, manager);
    }
    
    public Item getChild(String name) {
        throw new UnsupportedOperationException();
    }    
    
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        
        Item w = this;
        while (w != null) {
            sb.insert(0, w.getName());
            sb.insert(0, '/');
            w = ((Item)w.getParent());
        }
        return sb.toString();
    }

    public NewItemResult newItem(String name, Type type, Map<String, Object> initialProperties)
            throws DuplicateItemException, RegistryException, PolicyException, AccessException, PropertyException {
        return getWorkspaceManager().newItem(this, name, type, initialProperties);
    }

    public NewItemResult newItem(String name, Type type) throws DuplicateItemException, RegistryException,
            PolicyException, AccessException, PropertyException {
        return getWorkspaceManager().newItem(this, name, type, null);
    }

    public CommentManager getCommentManager() {
        return manager.getCommentManager();
    }

    public Lifecycle getDefaultLifecycle() {
        return null;
    }

    public List<Item> getItems() throws RegistryException {
        if (items == null) {
            items = getWorkspaceManager().getItems(this);
        }
        return items;
    }

    public Item getItem(String name) throws RegistryException, NotFoundException, AccessException {
        return getWorkspaceManager().getItem(this, name);
    }

    public LifecycleManager getLifecycleManager() {
        return null;
    }

    public void setDefaultLifecycle(Lifecycle l) {
        throw new UnsupportedOperationException();
    }

    public void setConfiguration(Map<String, String> configuration) {
        try {
            this.configuration = configuration;
            JcrUtil.setProperty(CONFIGURATION, configuration, node);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getConfiguration() {
        if (configuration == null) {
            configuration = (Map<String, String>) JcrUtil.getProperty(CONFIGURATION, node);
        }
        return configuration;
    }

    public WorkspaceManager getWorkspaceManager() {
        if (workspaceManager == null) {
            try {
                workspaceManager = ((JcrRegistryImpl)manager.getRegistry()).getWorkspaceManager(this);
            } catch (RegistryException e) {
                throw new RuntimeException(e);
            }
        }
        return workspaceManager;
    }

    public String getWorkspaceManagerFactory() {
        return getStringOrNull(WORKSPACE_MANAGER_FACTORY);
    }
    
}
