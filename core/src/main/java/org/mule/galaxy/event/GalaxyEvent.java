package org.mule.galaxy.event;

import org.mule.galaxy.security.User;

import java.util.EventObject;

public class GalaxyEvent extends EventObject {

    private User user;
    protected String message;

    public GalaxyEvent(final Object source) {
        super(source);
    }

    public GalaxyEvent(Object source, final String message) {
        super(source);
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
