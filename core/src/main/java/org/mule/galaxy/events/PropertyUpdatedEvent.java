package org.mule.galaxy.events;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.security.User;

public class PropertyUpdatedEvent extends GalaxyEvent {

    private String propertyName;
    private Object newValue;

    public PropertyUpdatedEvent(User user, String message, Artifact artifact, String propertyName, Object newValue) {
        super(artifact, message);
        setUser(user);
        this.propertyName = propertyName;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(final Object newValue) {
        this.newValue = newValue;
    }
}
