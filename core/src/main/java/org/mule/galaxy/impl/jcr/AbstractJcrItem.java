package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.springmodules.jcr.JcrCallback;

import org.apache.jackrabbit.value.StringValue;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.event.PropertyChangedEvent;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.DateUtil;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.SecurityUtils;


public abstract class AbstractJcrItem implements Item {

    public static final String PROPERTIES = "properties";
    public static final String LOCKED = ".locked";
    public static final String VISIBLE = ".visible";
    public static final String UPDATED = "updated";
    public static final String NAME = "name";
    public static final String CREATED = "created";
    
    protected Node node;
    protected JcrWorkspaceManager manager;
    
    public AbstractJcrItem(Node node, JcrWorkspaceManager manager) throws RepositoryException {
        this.node = node;
        this.manager = manager;
    }

    public JcrWorkspaceManager getManager() {
        return manager;
    }

    public String getId() {
        try {
            return JcrWorkspaceManager.ID + Registry.WORKSPACE_MANAGER_SEPARATOR + node.getUUID();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Calendar getCreated() {
        return getCalendarOrNull(CREATED);
    }

    public String getName() {
        return getStringOrNull(NAME);
    }
    
    public void setName(final String name) {
        try {
            
            if (!node.getName().equals(name)) {
                manager.getTemplate().execute(new JcrCallback() {
    
                    public Object doInJcr(Session session) throws IOException, RepositoryException {
                        String dest = node.getParent().getPath() + "/" + name;
                        session.move(node.getPath(), dest);
                        return null;
                    }
                    
                });
            }
            node.setProperty(NAME, name);
            update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean isLocal() {
        return true;
    }

    public void delete() throws RegistryException, AccessException {
        manager.delete(this);
    }
    
    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Calendar getUpdated() {
        return getCalendarOrNull(UPDATED);
    }

    protected String getStringOrNull(String propName) {
        return JcrUtil.getStringOrNull(node, propName);
    }
    
    protected Date getDateOrNull(String propName) {
        return JcrUtil.getDateOrNull(node, propName);
    }
    
    protected Calendar getCalendarOrNull(String propName) {
        return JcrUtil.getCalendarOrNull(node, propName);
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
    
    public void setProperty(String name, Object value) throws PropertyException, PolicyException {
        if (name.contains(" ")) {
                throw new PropertyException(new Message("SPACE_NOT_ALLOWED", getBundle()));
        }
        
        PropertyDescriptor pd = getManager().getTypeManager().getPropertyDescriptorByName(name);
        if (pd != null && pd.getExtension() != null) {
            pd.getExtension().store(this, pd, value);
	} else {
	    setInternalProperty(name, value, false);

	    manager.getEventManager().fireEvent(
	        new PropertyChangedEvent(SecurityUtils.getCurrentUser(), getPath(), name, value));
	}    
    }

    public void setInternalProperty(String name, Object value) throws PropertyException, PolicyException {
        setInternalProperty(name, value, true);
    }
    
    private void setInternalProperty(String name, Object value, boolean log) throws PropertyException, PolicyException {
        try {
            if (name.contains(" ")) {
                throw new PropertyException(new Message("SPACE_NOT_ALLOWED", getBundle()));
            }
            
            JcrUtil.setProperty(name, value, node);
            
            if (value == null) {
                deleteProperty(name);
            } else {
                ensureProperty(name);
            }
            
            if (log) {
                // We need to re-extract the property value because we need to log the external value
                manager.getEventManager().fireEvent(
                    new PropertyChangedEvent(SecurityUtils.getCurrentUser(), getPath(), name, getProperty(name)));
            }
            update();
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
            update();
        } catch (PathNotFoundException e) {
            return;
        }
    }

    private ResourceBundle getBundle() {
        return BundleUtils.getBundle(AbstractJcrItem.class);
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
	PropertyDescriptor pd = manager.getTypeManager().getPropertyDescriptorByName(name);
	
	if (pd != null && pd.getExtension() != null) {
            return pd.getExtension().get(this, pd, true);
        } else {
	    return JcrUtil.getProperty(name, node);
	}
    }

    public Object getInternalProperty(String name) {
        return JcrUtil.getProperty(name, node);
    }

    public Collection<PropertyInfo> getProperties() {
        try {
            Property p = null;
            final Map<String, PropertyInfo> properties = new HashMap<String, PropertyInfo>();
            try {
                p = node.getProperty(PROPERTIES);
                final Value[] values = p.getValues();
                for (Value v : values) {
                    String name = v.getString();
                    properties.put(name, new PropertyInfoImpl(this, name, node, manager.getTypeManager()));
                }
            } catch (PathNotFoundException e) {
            }
            
            Collection<PropertyDescriptor> pds = manager.getTypeManager().getPropertyDescriptors(false);
            
            for (PropertyDescriptor pd : pds) {
                Extension ext = pd.getExtension();
                if (ext != null) {
                    Object value = ext.get(this, pd, false);
                    
                    if (value != null) {
                        properties.put(pd.getProperty(), new PropertyInfoImpl(this, pd.getProperty(), node, manager.getTypeManager(), value));
                    }
                }
            }
            return properties.values();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public PropertyInfo getPropertyInfo(String name) {
        try {
            Property propList = node.getProperty("properties");
            
            boolean found = false;
            for (Value v : propList.getValues()) {
                if (v.getString().equals(name)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) return null;
        } catch (PathNotFoundException e) {
            return null;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        
        return new PropertyInfoImpl(this, name, node, manager.getTypeManager());
    }

    public void setLocked(String name, boolean locked) {
        try {
            node.setProperty(name + LOCKED, locked);
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVisible(String name, boolean visible) {
        try {
            node.setProperty(name + VISIBLE, visible);
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    protected void update() {
        try {
            node.setProperty(UPDATED, DateUtil.getCalendarForNow());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        String id = getId();
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractJcrItem other = (AbstractJcrItem)obj;
        String id = getId();
        String otherId = other.getId();
        if (id == null) {
            if (otherId != null)
                return false;
        } else if (!id.equals(otherId))
            return false;
        return true;
    }
    
}
