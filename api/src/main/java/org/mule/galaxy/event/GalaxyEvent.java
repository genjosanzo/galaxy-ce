package org.mule.galaxy.event;

import org.mule.galaxy.security.User;

public class GalaxyEvent {

    private User user;
    protected String message;

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
