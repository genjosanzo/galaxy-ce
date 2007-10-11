package org.mule.galaxy.jcr;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mule.galaxy.Workspace;

public class JcrWorkspace extends AbstractJcrObject implements org.mule.galaxy.Workspace {

    public static final String NAME = "name";
    
    public JcrWorkspace(Node node) {
        super(node);
    }

    public String getId() {
        
        // this will need to be redone when we support multiple workspaces
        String id = getName();
        try {
            int idx = node.getIndex();
            
            if (idx > 1) {
                id = id + "[" + idx + "]";
            }
            return id;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return getStringOrNull( NAME);
    }

    public Workspace getParent() {
        return null;
    }

    public Collection<Workspace> getWorkspaces() {
        return null;
    }

    public Node getNode() {
        return node;
    }

    public void setName(String name) {
        try {
            node.setProperty(NAME, name);
        } catch (Exception e) {
            throw new RuntimeException(name);
        } 
    }
}
