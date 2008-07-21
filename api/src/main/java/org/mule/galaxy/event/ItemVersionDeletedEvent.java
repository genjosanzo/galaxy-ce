package org.mule.galaxy.event;

public class ItemVersionDeletedEvent extends GalaxyEvent {

    private String artifactPath;
    private String versionLabel;

    public ItemVersionDeletedEvent(final String path, final String versionLabel) {
        this.artifactPath = path;
        this.versionLabel = versionLabel;
    }

    public String getArtifactPath() {
        return artifactPath;
    }

    public String getVersionLabel() {
        return versionLabel;
    }
}