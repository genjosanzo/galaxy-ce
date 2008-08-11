package org.mule.galaxy.event;

import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;

public class EntryVersionCreatedEvent extends ItemEvent {

    private String versionLabel;

    public EntryVersionCreatedEvent(Item item) {
        super(item);
        this.versionLabel = ((EntryVersion) item).getVersionLabel();
    }
    
    public String getVersionLabel() {
        return versionLabel;
    }
}