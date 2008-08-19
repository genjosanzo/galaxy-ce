package org.mule.galaxy.impl.workspace;

import org.mule.galaxy.ContentService;
import org.mule.galaxy.Registry;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractWorkspaceManager implements WorkspaceManager {

    private ContentService contentService;
    
    private CommentManager commentManager;

    protected String trimWorkspaceManagerId(String id) {
        int idx = id.indexOf(Registry.WORKSPACE_MANAGER_SEPARATOR);
        if (idx == -1) {
            throw new IllegalStateException("Illegal workspace manager id.");
        }

        return id.substring(idx + 1);
    }
    
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
