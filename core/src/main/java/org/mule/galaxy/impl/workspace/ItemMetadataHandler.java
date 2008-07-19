package org.mule.galaxy.impl.workspace;

import java.util.Iterator;

import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;

public abstract class ItemMetadataHandler {
    
    public abstract void setProperty(AbstractItem o, String name, Object value) throws PropertyException;
    
    public abstract Object getProperty(AbstractItem o, String name);

    public abstract boolean hasProperty(AbstractItem o, String name);

    public abstract Iterator<PropertyInfo> getProperties(AbstractItem o);
    
    public abstract PropertyInfo getPropertyInfo(AbstractItem o, String name);

    public abstract void setLocked(AbstractItem o, String name, boolean locked);

    public abstract void setVisible(AbstractItem o, String property, boolean visible);
}
