package org.mule.galaxy.atom;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.mule.galaxy.Registry;

/**
 * This workspace contains collections which map to the Galaxy workspaces.
 * 
 */
public class ArtifactWorkspaceInfo implements WorkspaceInfo {
    private Registry registry;
    private Map<String,CollectionProvider> providers;
    
    public ArtifactWorkspaceInfo() {
        super();
    }
    
    public void initialize() {
        providers = new HashMap<String, CollectionProvider>();
        providers.put("registry", new ArtifactCollectionProvider(registry));
//        providers.put("registry-history", new ArtifactVersionCollectionProvider(registry));
    }

    public CollectionProvider getCollectionProvider(String id) {
        return providers.get(id);
    }

    public Map<String, CollectionProvider> getCollectionProviders() {
        return providers;
    }
    
    public String getName() {
        return "Mule Galaxy Registry & Repository";
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
