package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mule.galaxy.PropertyInfo;

public class PropertyInfoImpl implements PropertyInfo {

    private Node node;

    public PropertyInfoImpl(Node node) {
        this.node = node;
    }

    public String getName() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue() {
        try {
            return JcrUtil.getProperty(node.getName(), node.getParent());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLocked() {
        return JcrUtil.getBooleanOrNull(node, JcrVersion.LOCKED);
    }

    public boolean isVisible() {
        return JcrUtil.getBooleanOrNull(node, JcrVersion.VISIBLE);
    }

}
