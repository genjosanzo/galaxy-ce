package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.mule.galaxy.Workspace;

public class JcrWorkspace extends AbstractJcrObject implements org.mule.galaxy.Workspace {

    public static final String NAME = "name";
    private List<Workspace> workspaces;
    
    public JcrWorkspace(Node node) throws RepositoryException  {
        super(node, null);
    }

    public String getId() {
        try {
            return node.getUUID();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return getStringOrNull(NAME);
    }

    public Workspace getParent() {
        try {
            Node parent = node.getParent();
            if (parent.getPrimaryNodeType().getName().equals("galaxy:workspace")) {
                return new JcrWorkspace(parent);
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
                        workspaces.add(new JcrWorkspace(n));
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
