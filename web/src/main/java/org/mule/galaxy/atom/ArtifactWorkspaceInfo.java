package org.mule.galaxy.atom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;

/**
 * This workspace contains collections which map to the Galaxy workspaces.
 * 
 */
public class ArtifactWorkspaceInfo implements WorkspaceInfo {
    private Registry registry;
    private Map<String,CollectionProvider> providers;
    
    public ArtifactWorkspaceInfo() {
        super();
        providers = new HashMap<String, CollectionProvider>();
    }
    
    public void initialize() {
        providers.put("repository", new ArtifactCollectionProvider(registry));
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
