package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;

import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.index.Index;

public class PropertyInfoImpl implements PropertyInfo {

    private Node node;
    private String name;
    private Registry registry;
    private boolean index;
    private String description;
    private Object desc;
    private boolean loadedDescriptor;
    
    public PropertyInfoImpl(String name, Node node, Registry registry) {
        this.node = node;
        this.name= name;
        this.registry = registry;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return JcrUtil.getProperty(getName(), node);
    }

    public boolean isLocked() {
        Boolean b = JcrUtil.getBooleanOrNull(node, getName() + JcrVersion.LOCKED);
        if (b == null) {
            return false;
        }
        return b;
    }

    public boolean isVisible() {
        Boolean vis = JcrUtil.getBooleanOrNull(node, getName() + JcrVersion.VISIBLE);
        if (vis == null) {
            return true;
        }
        
        return vis;
    }

    public String getDescription() {
        loadPropertyOrIndex();
        return description;
    }

    private void loadPropertyOrIndex() {
        if (loadedDescriptor) return;
        
        desc = registry.getPropertyDescriptorByName(getName());
        
        if (desc instanceof Index) {
            index = true;
            description = ((Index) desc).getDescription();
        } else if (desc != null) {
            description = ((PropertyDescriptor) desc).getDescription();
        }
        loadedDescriptor = true;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        loadPropertyOrIndex();
        if (!index) {
            return (PropertyDescriptor) desc;
        }
        return null;
    }

    public boolean isIndex() {
        loadPropertyOrIndex();
        return index;
    }

}
