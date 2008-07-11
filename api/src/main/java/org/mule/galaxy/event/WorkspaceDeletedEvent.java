package org.mule.galaxy.event;

public class WorkspaceDeletedEvent extends GalaxyEvent {

    private String comment;

    private String workspacePath;

    public WorkspaceDeletedEvent(final String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }
}