package org.mule.galaxy.events;

import org.mule.galaxy.Workspace;

public class WorkspaceCreatedEvent extends GalaxyEvent {

    public WorkspaceCreatedEvent(Workspace source) {
        super(source);
    }

    @Override
    public Workspace getSource() {
        return (Workspace) source;
    }
}
