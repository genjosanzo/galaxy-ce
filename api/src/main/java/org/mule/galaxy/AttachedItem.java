package org.mule.galaxy;

import java.util.Map;

import org.mule.galaxy.workspace.WorkspaceManager;

public interface AttachedItem extends Item {
    WorkspaceManager getWorkspaceManager();
    
    String getWorkspaceManagerFactory();
    
    Map<String, String> getConfiguration();
    
    void setConfiguration(Map<String, String> configuration);
}
