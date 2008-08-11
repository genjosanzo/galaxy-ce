package org.mule.galaxy.event;

import org.mule.galaxy.Item;

public class WorkspaceDeletedEvent extends ItemEvent {

    private String comment;

    public WorkspaceDeletedEvent(Item item) {
	super(item);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}