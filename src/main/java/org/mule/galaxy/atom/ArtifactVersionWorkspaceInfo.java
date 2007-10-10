package org.mule.galaxy.atom;


import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.content.CollectionProvider;
import org.apache.abdera.protocol.server.content.WorkspaceInfo;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;

public class ArtifactVersionWorkspaceInfo implements WorkspaceInfo {
    private Registry registry;
    
    public Map<String, CollectionProvider> getCollectionProviders() {
        return new HashMap<String, CollectionProvider>();
    }

    public CollectionProvider getCollectionProvider(String id) {
       
        try {
            Artifact doc = registry.getArtifact(id);
            
            return new ArtifactVersionContentProvider(doc, registry);
        } catch (NotFoundException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return "versions";
    }

    public String getName() {
        return "Galaxy Document Versions";
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
