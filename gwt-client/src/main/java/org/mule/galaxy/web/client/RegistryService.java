package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;

public interface RegistryService extends RemoteService {
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.WorkspaceInfo
     * @return
     */
    public Collection getWorkspaces();
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.ArtifactTypeInfo
     * @return
     */
    public Collection getArtifactTypes();

    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.ArtifactGroup
     * @return
     */
    public Collection getArtifacts(String workspace);
}
