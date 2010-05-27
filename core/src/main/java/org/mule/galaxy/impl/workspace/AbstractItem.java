package org.mule.galaxy.impl.workspace;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractItem implements Item {

    protected String id;
    
    protected final WorkspaceManager manager;

    protected List<Item> items;
    
    public AbstractItem(WorkspaceManager manager) {
        super();
        this.manager = manager;
    }

    public NewItemResult newItem(String name, Type type, Map<String, Object> initialProperties)
            throws DuplicateItemException, RegistryException, PolicyException, AccessException, PropertyException {
        items = null;
        return manager.newItem(this, name, type, initialProperties);
    }

    public NewItemResult newItem(String name, Type type) throws DuplicateItemException, RegistryException,
            PolicyException, AccessException, PropertyException {
        items = null;
        return manager.newItem(this, name, type, null);
    }
    
    public List<Item> getItems() throws RegistryException {
        if (items == null) {
            items = manager.getItems(this);
        }
        
        return items;
    }
    
    public Item getPrevious() throws RegistryException {
        List<Item> parentItems = getParent().getItems();

        Item prev = null;
        for (Item i : parentItems) {
            if ((prev != null && i.getCreated().after(prev.getCreated()) && i.getCreated().before(getCreated()))
                || (prev == null && i.getCreated().before(getCreated()))) {
                prev = i;
            }
        }
        
        return prev;
    }

    public Item getItem(String name) throws RegistryException, NotFoundException, AccessException {
        if (items != null) {
            for (Item i : items) {
                if (name.equals(i.getName())) {
                    return i;
                }
            }
            return null;
        }
        return manager.getItem(this, name);
    }
    
    public Item getLatestItem() throws RegistryException {
        Item latest = null;
        for (Item i : getItems()) {
            if (latest == null) {
                latest = i;
            } else if (i.getCreated().after(latest.getCreated())) {
                latest = i;
            }
        }
        return latest;
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();
        
        Item w = this;
        while (w != null) {
            sb.insert(0, w.getName());
            sb.insert(0, '/');
            w = ((Item)w.getParent());
        }
        
        return sb.toString();
    }
    public String getId() {
        return id;
    }
    
    public boolean isLocal() {
        return false;
    }

    public void delete() throws RegistryException, AccessException, PolicyException {
        manager.delete((Item) this);
    }

}
