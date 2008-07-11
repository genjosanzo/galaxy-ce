package org.mule.galaxy.event;

public class ArtifactVersionDeletedEvent extends GalaxyEvent {

    private String artifactPath;
    private String versionLabel;

    public ArtifactVersionDeletedEvent(final String path, final String versionLabel) {
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