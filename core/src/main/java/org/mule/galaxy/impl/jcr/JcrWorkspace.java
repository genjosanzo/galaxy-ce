package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.springmodules.jcr.JcrCallback;

public class JcrWorkspace extends AbstractJcrItem implements org.mule.galaxy.Workspace {

    public static final String NAME = "name";
    public static final String CREATED = "updated";
    public static final String LIFECYCLE = "lifecycle";
    private List<Workspace> workspaces;
    private Lifecycle lifecycle;
    private final JcrWorkspaceManager manager;
    
    public JcrWorkspace(JcrWorkspaceManager manager, 
        Node node) throws RepositoryException  {
        super(node, manager);
        this.manager = manager;
    }

    public String getName() {
        return getStringOrNull(NAME);
    }

    public Workspace getParent() {
        try {
            Node parent = node.getParent();
            if (parent.getPrimaryNodeType().getName().equals("galaxy:workspace")) {
                return new JcrWorkspace(manager, parent);
            } 
            
            return null;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Workspace> getWorkspaces() {
        if (workspaces == null) {
            workspaces = new ArrayList<Workspace>();
            try {
                NodeIterator nodes = node.getNodes();
                while (nodes.hasNext()) {
                    Node n = nodes.nextNode();
                    
                    if (n.getPrimaryNodeType().getName().equals("galaxy:workspace")) {
                        workspaces.add(new JcrWorkspace(manager, n));
                    }
                }
                Collections.sort(workspaces, new WorkspaceComparator());
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            } 
        }
        
        return workspaces;
    }

    public Node getNode() {
        return node;
    }

    public void setName(final String name) {
        update();
        
        try {
            if (!node.getName().equals(name)) {
                manager.execute(new JcrCallback() {
    
                    public Object doInJcr(Session session) throws IOException, RepositoryException {
                        String dest = node.getParent().getPath() + "/" + name;
                        session.move(node.getPath(), dest);
                        return null;
                    }
                    
                });
            }
            node.setProperty(NAME, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }

    public Calendar getCreated() {
        return getCalendarOrNull(CREATED);
    }
    
    public Workspace getWorkspace(String name) {
        try {
            NodeIterator nodes = node.getNodes();
            while (nodes.hasNext()) {
                Node n = nodes.nextNode();
                if (n.getDefinition().getName().equals("galaxy:workspace")) {
                    String wname = JcrUtil.getStringOrNull(n, NAME);
                    
                    if (wname != null && wname.equals(name)) {
                        return new JcrWorkspace(manager, n);
                    }
                }
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        } 
        return null;
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

    public Lifecycle getDefaultLifecycle() {
        if (lifecycle == null) {
            String id = (String) getStringOrNull(LIFECYCLE);
            
            if (id == null) {
                Workspace parent = getParent();
                if (parent == null) {
                    return getLifecycleManager().getDefaultLifecycle();
                } else {
                    return parent.getDefaultLifecycle();
                }
            } else {
                lifecycle = getLifecycleManager().getLifecycleById(id);
            }
        }
        return lifecycle;
    }

    public void setDefaultLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        update();
        
        try {
            if (lifecycle == null) {
                node.setProperty(LIFECYCLE, (String) null);
            } else {
                node.setProperty(LIFECYCLE, lifecycle.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EntryResult createArtifact(Object data, String versionLabel,
	    User user) throws DuplicateItemException, RegistryException,
	    PolicyException, MimeTypeParseException, AccessException {
	return manager.createArtifact(this, data, versionLabel, user);
    }

    public EntryResult createArtifact(String contentType, String name,
	    String versionLabel, InputStream inputStream, User user)
	    throws DuplicateItemException, RegistryException,
	    PolicyException, IOException, MimeTypeParseException,
	    AccessException {
	return manager.createArtifact(this, contentType, name, versionLabel, inputStream, user);
    }

    public EntryResult newEntry(String name, String versionLabel)
	    throws DuplicateItemException, RegistryException, PolicyException,
	    AccessException {
	return manager.newEntry(this, name, versionLabel);
    }

    public void delete() throws RegistryException, AccessException {
	manager.delete(this);
    }

    public LifecycleManager getLifecycleManager() {
      return manager.getLifecycleManager();
    }

    public CommentManager getCommentManager() {
        return manager.getCommentManager();
    }
    
}
