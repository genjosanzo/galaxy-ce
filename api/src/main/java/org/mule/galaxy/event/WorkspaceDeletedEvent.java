package org.mule.galaxy.event;

public class WorkspaceDeletedEvent extends GalaxyEvent {

    private String comment;

    private String workspaceName;

    public WorkspaceDeletedEvent(final String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }
}