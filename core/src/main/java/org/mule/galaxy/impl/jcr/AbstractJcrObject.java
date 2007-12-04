package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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


public class AbstractJcrObject {

    protected Node node;

    public AbstractJcrObject(Node node) {
        this.node = node;
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

    public void setProperty(String name, Object value) {
        try {
            JcrUtil.setProperty(name, value, node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Object getProperty(String name) {
        return JcrUtil.getProperty(name, node);
    }

}
