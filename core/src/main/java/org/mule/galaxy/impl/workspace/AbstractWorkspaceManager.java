package org.mule.galaxy.impl.workspace;

import org.mule.galaxy.ContentService;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractWorkspaceManager implements WorkspaceManager {

    
    private ContentService contentService;

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
    
}
