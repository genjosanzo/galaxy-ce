package org.mule.galaxy.event;

public class ItemDeletedEvent extends GalaxyEvent {

    private String artifactPath;

    public ItemDeletedEvent(final String path) {
        this.artifactPath = path;
    }

    public String getArtifactPath() {
        return artifactPath;
    }
}