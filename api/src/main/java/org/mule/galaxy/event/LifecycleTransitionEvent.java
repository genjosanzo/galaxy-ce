package org.mule.galaxy.event;

public class LifecycleTransitionEvent extends GalaxyEvent {

    private String artifactPath;
    private String oldPhaseName;
    private String newPhaseName;
    private String lifecycleName;

    public LifecycleTransitionEvent(final String artifactPath, 
                                    final String oldPhaseName, 
                                    final String newPhaseName, 
                                    final String lifecycleName) {
        this.artifactPath = artifactPath;
        this.newPhaseName = newPhaseName;
        this.lifecycleName = lifecycleName;
    }

    public String getArtifactPath() {
        return artifactPath;
    }

    public String getNewPhaseName() {
        return newPhaseName;
    }

    public String getLifecycleName() {
        return lifecycleName;
    }
}