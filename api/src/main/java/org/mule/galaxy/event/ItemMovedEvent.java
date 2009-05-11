package org.mule.galaxy.event;

import org.mule.galaxy.Item;

public class ItemMovedEvent extends ItemEvent {

    private String oldPath;

    public ItemMovedEvent(Item item, final String oldPath) {
    super(item);
        this.oldPath = oldPath;
    }

    public String getOldPath() {
        return oldPath;
    }

}