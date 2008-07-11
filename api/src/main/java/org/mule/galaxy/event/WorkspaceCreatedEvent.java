package org.mule.galaxy.event;

public class WorkspaceCreatedEvent extends GalaxyEvent {

    private String workspacePath;

    public WorkspaceCreatedEvent(final String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }
}
