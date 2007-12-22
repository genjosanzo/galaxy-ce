package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.mule.galaxy.PropertyInfo;

public class PropertyInfoImpl implements PropertyInfo {

    private Node node;
    private String name;

    public PropertyInfoImpl(String name, Node node) {
        this.node = node;
        this.name= name;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return JcrUtil.getProperty(getName(), node);
    }

    public boolean isLocked() {
        return JcrUtil.getBooleanOrNull(node, getName() + JcrVersion.LOCKED);
    }

    public boolean isVisible() {
        return JcrUtil.getBooleanOrNull(node, getName() + JcrVersion.VISIBLE);
    }

}
