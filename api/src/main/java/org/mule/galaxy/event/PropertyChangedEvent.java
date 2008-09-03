package org.mule.galaxy.event;

import org.mule.galaxy.Item;
import org.mule.galaxy.security.User;

public class PropertyChangedEvent extends ItemEvent {

    private String propertyName;
    private Object newValue;

    public PropertyChangedEvent(User user, Item item, String propertyName, Object newValue) {
	super(item);
        setUser(user);
        this.propertyName = propertyName;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }
}
