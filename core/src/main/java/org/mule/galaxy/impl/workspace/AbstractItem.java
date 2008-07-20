package org.mule.galaxy.impl.workspace;

import java.util.Iterator;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractItem {

    protected String id;
    
    protected ItemMetadataHandler metadata;

    protected final WorkspaceManager manager;
    
    public AbstractItem(WorkspaceManager manager, ItemMetadataHandler metadata) {
        super();
        this.manager = manager;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public Iterator<PropertyInfo> getProperties() {
        return metadata.getProperties(this);
    }

    public Object getProperty(String name) {
        return metadata.getProperty(this, name);
    }

    public PropertyInfo getPropertyInfo(String name) {
        return metadata.getPropertyInfo(this, name);
    }

    public boolean hasProperty(String name) {
    return metadata.hasProperty(this, name);
    }

    public void setLocked(String name, boolean locked) {
        metadata.setLocked(this, name, locked);
    }

    public void setProperty(String name, Object value) throws PropertyException {
        metadata.setProperty(this, name, value);
    }

    public void setVisible(String name, boolean visible) {
        metadata.setVisible(this, name, visible);
    }
    
    public void delete() throws RegistryException, AccessException {
        manager.delete((Item) this);
    }

}
