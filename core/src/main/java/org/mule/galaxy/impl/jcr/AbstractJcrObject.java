package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.mule.galaxy.PropertyInfo;


public class AbstractJcrObject {

    private static final String PROPERTIES = "properties";
    public static final String LOCKED = "locked";
    public static final String VISIBLE = "visible";
    protected Node node;
    protected Node propertyNode;

    public AbstractJcrObject(Node node) throws RepositoryException {
        this.node = node;
        this.propertyNode = JcrUtil.getOrCreate(node, PROPERTIES);
    }

    public Node getNode() {
        return node;
    }

    protected String getStringOrNull(String propName) {
        return JcrUtil.getStringOrNull(node, propName);
    }
    
    protected Calendar getDateOrNull(String propName) {
        return JcrUtil.getDateOrNull(node, propName);
    }
    
    protected Value getValueOrNull(String propName) throws PathNotFoundException, RepositoryException {
        return JcrUtil.getValueOrNull(node, propName);
    }

    public void setNodeProperty(String name, String value) {
        try {
            node.setProperty(name, value);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    public void setProperty(String name, Object value) {
        try {
            Node propNode = JcrUtil.setProperty(name, value, propertyNode);
            
            if (propNode != null) {
                propNode.setProperty(VISIBLE, true);
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }


    public Object getProperty(String name) {
        return JcrUtil.getProperty(name, propertyNode);
    }

    public Iterator<PropertyInfo> getProperties() {
        try {
            final NodeIterator nodes = propertyNode.getNodes();
            return new Iterator<PropertyInfo>() {
    
                public boolean hasNext() {
                    return nodes.hasNext();
                }
    
                public PropertyInfo next() {
                    return new PropertyInfoImpl(nodes.nextNode());
                }
    
                public void remove() {
                    try {
                        nodes.nextNode().remove();
                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }
                
            };
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public PropertyInfo getPropertyInfo(String name) {
        try {
            Node property = propertyNode.getNode(name);
            
            return new PropertyInfoImpl(property);
        } catch (PathNotFoundException e) {
            return null;  
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLocked(String name, boolean locked) {
        try {
            Node property = propertyNode.getNode(name);
            
            property.setProperty(LOCKED, locked);
        } catch (PathNotFoundException e) {
            return;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVisible(String name, boolean visible) {
        try {
            Node property = propertyNode.getNode(name);
            property.setProperty(VISIBLE, visible);
        } catch (PathNotFoundException e) {
            return;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    
}
