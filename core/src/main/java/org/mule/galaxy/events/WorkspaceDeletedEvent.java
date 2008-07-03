package org.mule.galaxy.events;

import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;

public class WorkspaceDeletedEvent extends GalaxyEvent {

    private String message;

    public WorkspaceDeletedEvent(final User currentUser, final String message) {
        super(currentUser);
        this.message = message;
    }

    @Override
    public Workspace getSource() {
        return (Workspace) source;
    }
}