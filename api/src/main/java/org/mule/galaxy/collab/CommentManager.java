package org.mule.galaxy.collab;

import java.util.List;

import org.mule.galaxy.NotFoundException;

public interface CommentManager {       
    List<Comment> getComments(final String itemId);
    
    Comment getComment(String commentId) throws NotFoundException;

    void addComment(Comment c);

    List<Comment> getComments(String itemId, boolean includeChildren);
    
    List<Comment> getRecentComments(int maxResults);
}
