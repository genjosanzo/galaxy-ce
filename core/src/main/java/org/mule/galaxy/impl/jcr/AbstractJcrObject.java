package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.ActivityManager;
import org.mule.galaxy.api.PropertyException;
import org.mule.galaxy.api.PropertyInfo;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.util.BundleUtils;
import org.mule.galaxy.api.util.Message;
import org.mule.galaxy.util.UserUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.jackrabbit.value.StringValue;


public class AbstractJcrObject {

    public static final String PROPERTIES = "properties";
    public static final String LOCKED = ".locked";
    public static final String VISIBLE = ".visible";
    protected Node node;
    private Registry registry;

    public AbstractJcrObject(Node node, Registry registry) throws RepositoryException {
        this.node = node;
        this.registry = registry;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
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
    public void setProperty(String name, Object value) throws PropertyException {
        try {
            if (name.contains(" ")) {
                throw new PropertyException(new Message("SPACE_NOT_ALLOWED", getBundle()));
            }
            
            JcrUtil.setProperty(name, value, node);
            
            node.setProperty(name + VISIBLE, true);
            if (value == null) {
                deleteProperty(name);
            } else {
                ensureProperty(name);
            }
            
            registry.getActivityManager().logActivity(UserUtils.getCurrentUser(), "Property " + name + " was set to: " + value,
                                        ActivityManager.EventType.INFO);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasProperty(String name) {
        try {
            return node.hasProperty(name);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }


    private void deleteProperty(String name) throws RepositoryException {
        Property p = null;
        try {
            p = node.getProperty(PROPERTIES);
            
            List<Value> values = new ArrayList<Value>();
            for (Value v : p.getValues()) {
                if (!v.getString().equals(name)) {
                    values.add(v);
                }
            }
            
            p.setValue(values.toArray(new Value[values.size()]));
            
        } catch (PathNotFoundException e) {
            return;
        }
    }

    private ResourceBundle getBundle() {
        return BundleUtils.getBundle(AbstractJcrObject.class);
    }

    private void ensureProperty(String name) throws RepositoryException {
        Property p = null;
        try {
            p = node.getProperty(PROPERTIES);
            
            for (Value v : p.getValues()) {
                if (v.getString().equals(name)) {
                    return;
                }
            }
            
            List<Value> values = new ArrayList<Value>();
            for (Value v : p.getValues()) {
                values.add(v);
            }
            values.add(new StringValue(name));
            p.setValue(values.toArray(new Value[values.size()]));
            
        } catch (PathNotFoundException e) {
            Value[] values = new Value[1];
            values[0] = new StringValue(name);
            node.setProperty(PROPERTIES, values);
            
            return;
        }
    }

    public Object getProperty(String name) {
        return JcrUtil.getProperty(name, node);
    }

    public Iterator<PropertyInfo> getProperties() {
        try {
            Property p = null;
            try {
                p = node.getProperty(PROPERTIES);
            } catch (PathNotFoundException e) {
                return new Iterator<PropertyInfo>() {

                    public boolean hasNext() {
                        return false;
                    }

                    public PropertyInfo next() {
                        throw new UnsupportedOperationException();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                };
            }
            
            final Value[] values = p.getValues();
            return new Iterator<PropertyInfo>() {
                private int i = 0;
                
                public boolean hasNext() {
                    return i < values.length;
                }
    
                public PropertyInfo next() {
                    i++;
                    try {
                        return new PropertyInfoImpl(values[i-1].getString(), node, registry);
                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
            };
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public PropertyInfo getPropertyInfo(String name) {
        return new PropertyInfoImpl(name, node, registry);
    }

    public void setLocked(String name, boolean locked) {
        try {
            node.setProperty(name + LOCKED, locked);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVisible(String name, boolean visible) {
        try {
            node.setProperty(name + VISIBLE, visible);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    
}
