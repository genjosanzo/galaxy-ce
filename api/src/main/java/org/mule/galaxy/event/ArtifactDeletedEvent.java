package org.mule.galaxy.event;

public class ArtifactDeletedEvent extends GalaxyEvent {

    private String artifactPath;

    public ArtifactDeletedEvent(final String path) {
        this.artifactPath = path;
    }

    public String getArtifactPath() {
        return artifactPath;
    }
}