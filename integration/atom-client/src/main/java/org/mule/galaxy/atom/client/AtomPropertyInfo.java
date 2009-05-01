package org.mule.galaxy.atom.client;

import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;

public class AtomPropertyInfo implements PropertyInfo {
    private String name;
    private Object value;
    private boolean locked;
    private boolean visible = true;
    private final TypeManager typeManager;
    
    public AtomPropertyInfo(String name, 
                            Object value,
                            TypeManager typeManager) {
        super();
        this.name = name;
        this.value = value;
        this.typeManager = typeManager;
    }
    
    public String getDescription() {
        PropertyDescriptor pd = getPropertyDescriptor();
        
        return pd != null ? pd.getDescription() : null;
    }

    public Object getInternalValue() {
        return getValue();
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return typeManager.getPropertyDescriptorByName(name);
    }

    public String getName() {
        return name;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
