package org.mule.galaxy.event;

public class ArtifactMovedEvent extends GalaxyEvent {

    private String artifactOldPath;
    private String artifactNewPath;

    public ArtifactMovedEvent(final String oldPath, final String newPath) {
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