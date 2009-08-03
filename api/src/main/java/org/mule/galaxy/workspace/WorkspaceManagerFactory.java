package org.mule.galaxy.workspace;

import java.util.Map;

/**
 * Builds a WorkspaceManager from a configuration that is stored in the registry.
 */
public abstract class WorkspaceManagerFactory {
    
    public abstract String getName();
    
    public abstract WorkspaceManager createWorkspaceManager(Map<String, String> configuration);
    
}
