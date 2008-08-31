package org.mule.galaxy.event;

import org.mule.galaxy.security.User;

public class PropertyChangedEvent extends GalaxyEvent {

    private String itemPath;
    private String propertyName;
    private Object newValue;

    public PropertyChangedEvent(User user, String itemPath, String propertyName, Object newValue) {
        setUser(user);
        this.itemPath = itemPath;
        this.propertyName = propertyName;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public String getItemPath() {
        return itemPath;
    }
}
