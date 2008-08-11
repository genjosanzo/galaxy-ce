package org.mule.galaxy.event;

import org.mule.galaxy.Item;

public class EntryDeletedEvent extends ItemEvent {

    public EntryDeletedEvent(Item item) {
	super(item);
    }

}