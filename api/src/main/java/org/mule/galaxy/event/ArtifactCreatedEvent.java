package org.mule.galaxy.event;

public class ArtifactCreatedEvent extends GalaxyEvent {

    private String artifactPath;

    public ArtifactCreatedEvent(final String path) {
        this.artifactPath = path;
    }

    public String getArtifactPath() {
        return artifactPath;
    }
}