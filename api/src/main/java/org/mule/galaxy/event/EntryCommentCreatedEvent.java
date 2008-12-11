package org.mule.galaxy.event;

import org.mule.galaxy.Item;
import org.mule.galaxy.collab.Comment;

public class EntryCommentCreatedEvent extends ItemEvent {

    private Comment comment;


    public EntryCommentCreatedEvent(Item item,
                                    Comment comment) {
        super(item);
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }


}
