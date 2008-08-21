package org.mule.galaxy;

import java.util.Map;

import org.mule.galaxy.workspace.WorkspaceManager;

public interface AttachedWorkspace extends Workspace {
    WorkspaceManager getWorkspaceManager();
    
    String getWorkspaceManagerFactory();
    
    Map<String, String> getConfiguration();
    
    void setConfiguration(Map<String, String> configuration);
}
