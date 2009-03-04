package org.mule.galaxy;

public class Link implements Identifiable {
    
    private String id;
    private Item item;
    private Item linkedTo;
    private String linkedToPath;
    private boolean isAutoDetected;
    private String property;
    
    public Link(Item item, 
                Item linkedTo, 
                String linkedToPath,
	        boolean isAutoDetected) {
	super();
	this.item = item;
	this.linkedTo = linkedTo;
	this.linkedToPath = linkedToPath;
	this.isAutoDetected = isAutoDetected;
    }
    public Link() {
	super();
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
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    
    public String toString() {
	if (linkedToPath != null) {
	    return linkedToPath;
	}
	
	if (linkedTo != null) {
	    return linkedTo.getPath();
	}
	
	return super.toString();
    }
}
