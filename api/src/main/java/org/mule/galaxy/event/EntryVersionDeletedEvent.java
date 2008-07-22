package org.mule.galaxy.event;

public class EntryVersionDeletedEvent extends GalaxyEvent {

    private String artifactPath;
    private String versionLabel;

    public EntryVersionDeletedEvent(final String path, final String versionLabel) {
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