package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;

public interface RegistryService extends RemoteService {
    
    public Collection getWorkspaces();
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.ArtifactTypeInfo
     * @return
     */
    public Collection getArtifactTypes();
   
}
