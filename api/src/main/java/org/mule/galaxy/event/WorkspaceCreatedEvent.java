package org.mule.galaxy.event;

public class WorkspaceCreatedEvent extends GalaxyEvent {

    private String workspacePath;
    private String itemId;

    public WorkspaceCreatedEvent(final String itemId, final String workspacePath) {
        this.itemId = itemId;
        this.workspacePath = workspacePath;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getItemId() {
        return itemId;
    }
}
