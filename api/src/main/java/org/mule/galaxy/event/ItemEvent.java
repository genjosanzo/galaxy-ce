package org.mule.galaxy.event;

import org.mule.galaxy.Item;

/**
 * An event which relates to an Item.
 */
public class ItemEvent extends GalaxyEvent {
    private String itemId;
    private String itemPath;
    
    public ItemEvent(Item item) {
	super();
	this.itemId = item.getId();
	this.itemPath = item.getPath();
    }

    public String getItemId() {
        return itemId;
    }
    
    public String getItemPath() {
        return itemPath;
    }
    
}
