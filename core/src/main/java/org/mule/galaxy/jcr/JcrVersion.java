package org.mule.galaxy.jcr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.util.JcrUtil;

public class JcrVersion extends AbstractJcrObject implements ArtifactVersion {
    public static final String CREATED = "created";
    public static final String DATA = "data";
    public static final String VALUE = "value";
    
    private JcrArtifact parent;
    private Object data;
    
    public JcrVersion(JcrArtifact parent, Node v) {
        super(v);
        this.parent = parent;
    }

    public Object getData() {
        return data;
    }

    public Artifact getParent() {
        return parent;
    }

    public String getLabel() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setVersion(String vname) {
        try {
            parent.getVersionHistory().addVersionLabel(node.getName(), vname, false);
        } catch (VersionException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Calendar getCreated() {
        return getDateOrNull(CREATED);
    }
    
    public InputStream getStream() {
        try {
            Value v = getValueOrNull(DATA);
            
            if (v != null) {
                return v.getStream();
            }
            
            return null;
        } catch (PathNotFoundException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public ArtifactVersion getPrevious() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProperty(String name, Object value) {
        try {
            setProperty(name, value, node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setProperty(String name, Object value, Node n) throws ItemExistsException,
        PathNotFoundException, VersionException, ConstraintViolationException, LockException,
        RepositoryException {
        if (value instanceof Collection) {
            Node child = n.addNode(name);
            
            Collection c = (Collection) value;
            
            for (Object o : c) {
                Node valueNode = child.addNode(VALUE);
                valueNode.setProperty(VALUE, o.toString());
            }
        }
    }

    public Object getProperty(String name) {
        try {
            Node child = node.getNode(name);
            
            List<String> values = new ArrayList<String>();
            for (NodeIterator itr = child.getNodes(); itr.hasNext();) {
                Node next = itr.nextNode();

                String value = JcrUtil.getStringOrNull(next, VALUE);
                
                values.add(value);
            }
            return values;
        } catch (PathNotFoundException e) {
            return null;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        
        
    }

    public void setNode(Node versionNode) {
        this.node = versionNode;
    }
}
