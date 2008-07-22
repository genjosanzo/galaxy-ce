package org.mule.galaxy.event;

public class EntryMovedEvent extends GalaxyEvent {

    private String artifactOldPath;
    private String artifactNewPath;

    public EntryMovedEvent(final String oldPath, final String newPath) {
        this.artifactOldPath = oldPath;
        this.artifactNewPath = newPath;
    }

    public String getArtifactOldPath() {
        return artifactOldPath;
    }

    public String getArtifactNewPath() {
        return artifactNewPath;
    }
}