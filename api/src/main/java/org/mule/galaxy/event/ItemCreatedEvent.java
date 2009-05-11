package org.mule.galaxy.event;

import org.mule.galaxy.Item;

public class ItemCreatedEvent extends ItemEvent {

    public ItemCreatedEvent(Item item) {
        super(item);
    }

}