package org.mule.galaxy.event;

import org.mule.galaxy.security.User;

public class PropertyChangedEvent extends GalaxyEvent {

    private String artifactPath;
    private String propertyName;
    private Object newValue;

    public PropertyChangedEvent(User user, String artifactPath, String propertyName, Object newValue) {
        setUser(user);
        this.artifactPath = artifactPath;
        this.propertyName = propertyName;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public String getArtifactPath() {
        return artifactPath;
    }
}
