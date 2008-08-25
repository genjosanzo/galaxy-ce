package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mule.galaxy.AttachedWorkspace;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;

public class JcrAttachedWorkspace extends AbstractJcrItem implements AttachedWorkspace {

    public static final String WORKSPACE_MANAGER_FACTORY = "workspaceManagerFactory";
    private static final String CONFIGURATION = "configuration";
    private WorkspaceManager workspaceManager;
    private Map<String, String> configuration;

    public JcrAttachedWorkspace(Node node, JcrWorkspaceManager manager) throws RepositoryException {
        super(node, manager);
    }

    public EntryResult createArtifact(Object data, String versionLabel)
        throws DuplicateItemException, RegistryException, PolicyException, MimeTypeParseException,
        AccessException {
        return getWorkspaceManager().createArtifact(this, data, versionLabel);
    }

    public EntryResult createArtifact(String contentType, String name, String versionLabel,
                                      InputStream inputStream) throws DuplicateItemException,
        RegistryException, PolicyException, IOException, MimeTypeParseException, AccessException {
        return getWorkspaceManager().createArtifact(this, contentType, name, versionLabel, inputStream);
    }

    public Workspace newWorkspace(String name) throws DuplicateItemException, RegistryException,
        AccessException {
        return getWorkspaceManager().newWorkspace(this, name);
    }

    public Workspace getWorkspace(String name) {
        throw new UnsupportedOperationException();
    }    
    
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        
        Workspace w = this;
        while (w != null) {
            sb.insert(0, '/');
            sb.insert(0, w.getName());
            w = ((Workspace)w.getParent());
        }
        sb.insert(0, '/');
        return sb.toString();
    }

    public Collection<Workspace> getWorkspaces() throws RegistryException {
        return getWorkspaceManager().getWorkspaces(this);
    }

    public EntryResult newEntry(String name, String versionLabel) throws DuplicateItemException,
        RegistryException, PolicyException, AccessException {
        return getWorkspaceManager().newEntry(this, name, versionLabel);
    }

    public CommentManager getCommentManager() {
        return manager.getCommentManager();
    }

    public Lifecycle getDefaultLifecycle() {
        return null;
    }

    public List<Item> getItems() throws RegistryException {
        return getWorkspaceManager().getItems(this);
    }

    public Item getItem(String name) throws RegistryException, NotFoundException, AccessException {
        return getWorkspaceManager().getItem(this, name);
    }

    public LifecycleManager getLifecycleManager() {
        return null;
    }

    public Workspace getParent() {
        try {
            Node parent = node.getParent();
            if (JcrWorkspaceManager.WORKSPACE_NODE_TYPE.equals(parent.getPrimaryNodeType().getName())) {
                return new JcrWorkspace(getManager(), parent);
            } else {
                return null;
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
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
