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
    
    public CollectionProvider getCollectionProvider(String id) throws ResponseContextException {
        try {
            Workspace workspace = registry.getWorkspace(id);
            
            return new ArtifactCollectionProvider(registry, workspace);
        } catch (NotFoundException e) {
            System.out.println("No such workspace " + id);
            throw new ResponseContextException(404);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public Map<String, CollectionProvider> getCollectionProviders() {
        HashMap<String, CollectionProvider> providers = 
            new HashMap<String, CollectionProvider>();
        
        try {
            Collection<Workspace> workspaces = registry.getWorkspaces();
            for (Workspace w : workspaces) {
                providers.put(w.getId(), new ArtifactCollectionProvider(registry, w));
                // TODO: go through child workspaces or have the feed contain other workspaces
            }
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
        
        return providers;
    }

    public String getId() {
        return "workspaces";
    }

    public String getName() {
        return "Galaxy Registry & Repository";
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
