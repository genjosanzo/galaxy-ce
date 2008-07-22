package org.mule.galaxy.event;

public class EntryCreatedEvent extends GalaxyEvent {

    private String artifactPath;
    private String itemId;

    public EntryCreatedEvent(final String itemId, final String path) {
        this.itemId = itemId;
        this.artifactPath = path;
    }

    public String getArtifactPath() {
        return artifactPath;
    }

    public String getItemId() {
        return itemId;
    }
}