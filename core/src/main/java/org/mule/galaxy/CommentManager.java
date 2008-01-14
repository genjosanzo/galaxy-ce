package org.mule.galaxy;

import java.util.List;

public interface CommentManager {
    List<Comment> getComments(final String artifactId);
    
    Comment getComment(String commentId) throws NotFoundException;

    void addComment(Comment c);

    List<Comment> getComments(String artifactId, boolean includeChildren);
}
