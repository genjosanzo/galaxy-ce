package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;

public class PropertyInfoImpl implements PropertyInfo {

    private Node node;
    private String name;
    private boolean index;
    private String description;
    private PropertyDescriptor desc;
    private boolean loadedDescriptor;
    private TypeManager tm;
    private final Item item;
    private final Object value;
    
    public PropertyInfoImpl(Item item, String name, Node node, TypeManager tm) {
        this(item, name, node, null, null);
        this.tm = tm;
    }

    public PropertyInfoImpl(Item item, String name, Node node, PropertyDescriptor pd, Object value) {
        this.item = item;
        this.node = node;
        this.name= name;
        this.desc = pd;
        this.value = value;
        if (pd != null) {
            loadedDescriptor = true;
            description = pd.getDescription();
        }
    }
    public String getName() {
        return name;
    }

    public Object getValue() {
        if (value != null) {
            return value;
        } else {
            return item.getProperty(getName());
        }
    }

    public Object getInternalValue() {
        return item.getInternalProperty(getName());
    }

    public boolean isLocked() {
        Boolean b = JcrUtil.getBooleanOrNull(node, getName() + JcrItem.LOCKED);
        if (b == null) {
            return false;
        }
        return b;
    }

    public boolean isVisible() {
        Boolean vis = JcrUtil.getBooleanOrNull(node, getName() + JcrItem.VISIBLE);
        if (vis == null) {
            return true;
        }
        
        return vis;
    }

    public String getDescription() {
        loadProperty();
        return description;
    }

    private void loadProperty() {
        if (loadedDescriptor) return;
        
        desc = tm.getPropertyDescriptorByName(getName());
        
        if (desc != null) {
            description = desc.getDescription();
        }
        
        loadedDescriptor = true;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        loadProperty();
        if (!index) {
            return (PropertyDescriptor) desc;
        }
        return null;
    }

    public boolean isIndex() {
        loadProperty();
        return index;
    }

}
