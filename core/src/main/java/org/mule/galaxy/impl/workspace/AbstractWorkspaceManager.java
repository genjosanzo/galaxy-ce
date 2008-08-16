package org.mule.galaxy.impl.workspace;

import org.mule.galaxy.ContentService;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractWorkspaceManager implements WorkspaceManager {

    
    private ContentService contentService;
    
    private CommentManager commentManager;

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public CommentManager getCommentManager() {
        return commentManager;
    }

    public void setCommentManager(CommentManager commentManager) {
        this.commentManager = commentManager;
    }
    
}
