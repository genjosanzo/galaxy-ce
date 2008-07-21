package org.mule.galaxy.event;

public class ItemCreatedEvent extends GalaxyEvent {

    private String artifactPath;
    private String itemId;

    public ItemCreatedEvent(final String itemId, final String path) {
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