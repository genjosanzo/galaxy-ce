package org.mule.galaxy;

import org.mule.galaxy.security.AccessException;

public class Link implements Identifiable {
    
    private String id;
    private Item item;
    private Item linkedTo;
    private String linkedToPath;
    private boolean isAutoDetected;
    private Registry registry;
    
    public Link(Item item, Item linkedTo, String linkedToPath,
	    boolean isAutoDetected) {
	super();
	this.item = item;
	this.linkedTo = linkedTo;
	this.linkedToPath = linkedToPath;
	this.isAutoDetected = isAutoDetected;
    }
    public Link() {
	super();
	// TODO Auto-generated constructor stub
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }
    public Item getLinkedTo() {
        if (registry != null && linkedTo == null && linkedToPath != null) {
            return registry.resolve(item, linkedToPath);
        }
        return linkedTo;
    }
    public void setLinkedTo(Item linkedTo) {
        this.linkedTo = linkedTo;
    }
    public String getLinkedToPath() {
        return linkedToPath;
    }
    public void setLinkedToPath(String linkedToPath) {
        this.linkedToPath = linkedToPath;
    }
    public boolean isAutoDetected() {
        return isAutoDetected;
    }
    public void setAutoDetected(boolean isAutoDetected) {
        this.isAutoDetected = isAutoDetected;
    }
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
}
