package org.mule.galaxy.event;

public class ItemCreatedEvent extends GalaxyEvent {

    private String artifactPath;

    public ItemCreatedEvent(final String path) {
        this.artifactPath = path;
    }

    public String getArtifactPath() {
        return artifactPath;
    }
}