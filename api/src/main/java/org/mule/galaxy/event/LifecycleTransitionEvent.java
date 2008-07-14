package org.mule.galaxy.event;

public class LifecycleTransitionEvent extends GalaxyEvent {

    private String artifactPath;
    private String versionLabel;
    private String oldPhaseName;
    private String newPhaseName;
    private String lifecycleName;

    public LifecycleTransitionEvent(final String artifactPath, final String versionLabel,
                                    final String oldPhaseName, final String newPhaseName, final String lifecycleName) {
        this.artifactPath = artifactPath;
        this.versionLabel = versionLabel;
        this.newPhaseName = newPhaseName;
        this.lifecycleName = lifecycleName;
    }

    public String getArtifactPath() {
        return artifactPath;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public String getNewPhaseName() {
        return newPhaseName;
    }

    public String getLifecycleName() {
        return lifecycleName;
    }
}