package org.mule.galaxy.event;

public class EntryVersionCreatedEvent extends GalaxyEvent {

    private String artifactPath;
    private String versionLabel;
    private String itemId;

    public EntryVersionCreatedEvent(final String itemId, final String path, final String versionLabel) {
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