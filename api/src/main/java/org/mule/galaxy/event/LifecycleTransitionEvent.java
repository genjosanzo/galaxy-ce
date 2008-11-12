package org.mule.galaxy.event;

import org.mule.galaxy.security.User;
import org.mule.galaxy.Item;

public class LifecycleTransitionEvent extends ItemEvent {

    private String artifactPath;
    private String oldPhaseName;
    private String newPhaseName;
    private String lifecycleName;


    @Deprecated
    public LifecycleTransitionEvent(final String artifactPath,
                                    final String oldPhaseName,
                                    final String newPhaseName,
                                    final String lifecycleName) {
        this.artifactPath = artifactPath;
        this.oldPhaseName = oldPhaseName;
        this.newPhaseName = newPhaseName;
        this.lifecycleName = lifecycleName;
    }


    public LifecycleTransitionEvent(final User user,
                                    final Item item,
                                    final String oldPhaseName,
                                    final String newPhaseName,
                                    final String lifecycleName) {
	    super(item);
        setUser(user);
        this.oldPhaseName = oldPhaseName;
        this.newPhaseName = newPhaseName;
        this.lifecycleName = lifecycleName;
    }


    @Deprecated
    public String getArtifactPath() {
        return artifactPath;
    }

    public String getOldPhaseName() {
        return oldPhaseName;
    }

    public String getNewPhaseName() {
        return newPhaseName;
    }

    public String getLifecycleName() {
        return lifecycleName;
    }
}