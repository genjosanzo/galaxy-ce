package org.mule.galaxy.atom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.content.CollectionProvider;
import org.apache.abdera.protocol.server.content.ResponseContextException;
import org.apache.abdera.protocol.server.content.WorkspaceInfo;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;

/**
 * This workspace contains collections which map to the Galaxy workspaces.
 * 
 */
public class ArtifactWorkspaceInfo implements WorkspaceInfo<Artifact> {
    private Registry registry;
    
    public CollectionProvider<Artifact> getCollectionProvider(String id) throws ResponseContextException {
        try {
            Workspace workspace = registry.getWorkspace(id);
            
            return new ArtifactCollectionProvider(registry, workspace);
        } catch (NotFoundException e) {
            throw new ResponseContextException(404);
        } catch (ArtifactException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public Map<String, CollectionProvider<Artifact>> getCollectionProviders() {
        HashMap<String, CollectionProvider<Artifact>> providers = 
            new HashMap<String, CollectionProvider<Artifact>>();
        
        try {
            Collection<Workspace> workspaces = registry.getWorkspaces();
            for (Workspace w : workspaces) {
                providers.put(w.getId(), new ArtifactCollectionProvider(registry, w));
                // TODO: go through child workspaces or have the feed contain other workspaces
            }
        } catch (ArtifactException e) {
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
