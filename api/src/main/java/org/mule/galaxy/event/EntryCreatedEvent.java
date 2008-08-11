package org.mule.galaxy.event;

import org.mule.galaxy.Item;

public class EntryCreatedEvent extends ItemEvent {

    public EntryCreatedEvent(Item item) {
	super(item);
    }

}