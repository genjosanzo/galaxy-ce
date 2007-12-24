package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Set;

public interface RegistryService extends RemoteService {
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.WorkspaceInfo
     * @return
     */
    Collection getWorkspaces();
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.ArtifactTypeInfo
     * @return
     */
    Collection getArtifactTypes();

    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.ArtifactGroup
     * @return
     */
    Collection getArtifacts(String workspace, Set artifactTypes);

    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.DependencyInfo
     * @return
     * @throws Exception 
     */
    Collection getDependencyInfo(String artifactId) throws Exception;
    
    ArtifactGroup getArtifact(String artifactId) throws Exception;
}
