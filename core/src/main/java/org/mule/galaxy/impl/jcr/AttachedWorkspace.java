package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.Item;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.impl.workspace.AbstractWorkspace;
import org.mule.galaxy.impl.workspace.ItemMetadataHandler;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.workspace.WorkspaceManager;

public class AttachedWorkspace extends AbstractJcrItem implements Workspace {

    private static final String CONFIGURATION = "configuration";
    private final WorkspaceManager remote;

    public AttachedWorkspace(Node node, JcrWorkspaceManager manager, WorkspaceManager remote) throws RepositoryException {
        super(node, manager);
        this.remote = remote;
    }

    public EntryResult createArtifact(Object data, String versionLabel, User user)
        throws DuplicateItemException, RegistryException, PolicyException, MimeTypeParseException,
        AccessException {
        return remote.createArtifact(this, data, versionLabel, user);
    }

    public EntryResult createArtifact(String contentType, String name, String versionLabel,
                                      InputStream inputStream, User user) throws DuplicateItemException,
        RegistryException, PolicyException, IOException, MimeTypeParseException, AccessException {
        return remote.createArtifact(this, contentType, name, versionLabel, inputStream, user);
    }

    public Workspace newWorkspace(String name) throws DuplicateItemException, RegistryException,
        AccessException {
        return remote.newWorkspace(this, name);
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

    public Collection<Workspace> getWorkspaces() {
        return remote.getWorkspaces(this);
    }

    public EntryResult newEntry(String name, String versionLabel) throws DuplicateItemException,
        RegistryException, PolicyException, AccessException {
        return remote.newEntry(this, name, versionLabel);
    }

    public CommentManager getCommentManager() {
        return manager.getCommentManager();
    }

    public Lifecycle getDefaultLifecycle() {
        return null;
    }

    public List<Item> getItems() {
        return remote.getItems(this);
    }

    public LifecycleManager getLifecycleManager() {
        return null;
    }

    public Item getParent() {
        try {
            return new JcrWorkspace(getManager(), node.getParent());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDefaultLifecycle(Lifecycle l) {
        throw new UnsupportedOperationException();
    }

    public void setConfiguration(Map<String, String> configuration) {
        try {
            JcrUtil.setProperty(CONFIGURATION, configuration, node);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getConfiguration() {
        return (Map<String, String>) JcrUtil.getProperty(CONFIGURATION, node);
    }
}
