package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.mule.galaxy.Workspace;

public class JcrWorkspace extends AbstractJcrObject implements org.mule.galaxy.Workspace {

    public static final String NAME = "name";
    private Collection<Workspace> workspaces;
    
    public JcrWorkspace(Node node) throws RepositoryException  {
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
        return getStringOrNull(NAME);
    }

    public Workspace getParent() {
        return null;
    }

    public Collection<Workspace> getWorkspaces() {
        if (workspaces == null) {
            workspaces = new ArrayList<Workspace>();
            try {
                NodeIterator nodes = node.getNodes();
                while (nodes.hasNext()) {
                    Node n = nodes.nextNode();
                    if (n.getDefinition().getName().equals("galaxy:workspace")) {
                        workspaces.add(new JcrWorkspace(n));
                    }
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            } 
        }
        
        return workspaces;
    }

    public Node getNode() {
        return node;
    }

    public void setName(String name) {
        try {
            node.setProperty(NAME, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }

    public Workspace getWorkspace(String name) {
        try {
            NodeIterator nodes = node.getNodes();
            while (nodes.hasNext()) {
                Node n = nodes.nextNode();
                if (n.getDefinition().getName().equals("galaxy:workspace")) {
                    String wname = JcrUtil.getStringOrNull(n, NAME);
                    
                    if (wname != null && wname.equals(name)) {
                        return new JcrWorkspace(n);
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
            w = w.getParent();
        }
        sb.insert(0, '/');
        return sb.toString();
    }
}
