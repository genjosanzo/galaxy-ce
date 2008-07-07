package org.mule.galaxy.event;

import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;

public class WorkspaceDeletedEvent extends GalaxyEvent {

    public WorkspaceDeletedEvent(final User user, final String message) {
        super(user, message);
        setUser(user);
    }

    @Override
    // TODO remove this getSource(), it stands in the way more than it helps
    public Workspace getSource() {
        return (Workspace) source;
    }
}