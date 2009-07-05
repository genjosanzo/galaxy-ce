package org.mule.galaxy.event;

import org.mule.galaxy.Item;

public class ItemDeletedEvent extends ItemEvent {

    public ItemDeletedEvent(Item item) {
        super(item);
    }

}