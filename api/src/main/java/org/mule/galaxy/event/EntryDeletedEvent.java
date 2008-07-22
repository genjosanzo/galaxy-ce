package org.mule.galaxy.event;

public class EntryDeletedEvent extends GalaxyEvent {

    private String artifactPath;

    public EntryDeletedEvent(final String path) {
        this.artifactPath = path;
    }

    public String getArtifactPath() {
        return artifactPath;
    }
}