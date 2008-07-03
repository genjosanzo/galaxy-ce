package org.mule.galaxy.events;

import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;

public class WorkspaceDeletedEvent extends GalaxyEvent {

    private String message;
    private User user;

    public WorkspaceDeletedEvent(final User user, final String message) {
        super(user);
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @Override
    // TODO remove this getSource(), it stands in the way more than it helps
    public Workspace getSource() {
        return (Workspace) source;
    }
}