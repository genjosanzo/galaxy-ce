package org.mule.galaxy.event;

public class ItemVersionCreatedEvent extends GalaxyEvent {

    private String artifactPath;
    private String versionLabel;
    private String itemId;

    public ItemVersionCreatedEvent(final String itemId, final String path, final String versionLabel) {
        this.itemId = itemId;
        this.artifactPath = path;
        this.versionLabel = versionLabel;
    }

    public String getArtifactPath() {
        return artifactPath;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public String getItemId() {
        return itemId;
    }
}