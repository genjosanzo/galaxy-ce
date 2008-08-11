package org.mule.galaxy.event;

import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;

public class EntryVersionDeletedEvent extends ItemEvent {

    private String versionLabel;

    public EntryVersionDeletedEvent(Item item) {
        super(item);
        this.versionLabel = ((EntryVersion) item).getVersionLabel();
    }
    
    public String getVersionLabel() {
        return versionLabel;
    }
}