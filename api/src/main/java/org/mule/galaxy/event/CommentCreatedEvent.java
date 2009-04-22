package org.mule.galaxy.event;

import org.mule.galaxy.Item;
import org.mule.galaxy.collab.Comment;

public class CommentCreatedEvent extends ItemEvent {

    private Comment comment;


    public CommentCreatedEvent(Item item,
                                    Comment comment) {
        super(item);
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }


}
