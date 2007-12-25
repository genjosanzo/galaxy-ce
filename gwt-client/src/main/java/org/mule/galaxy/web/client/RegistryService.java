package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Map;
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
    
    
    public Map getIndexes();
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.DependencyInfo
     * @return
     * @throws Exception 
     */
    Collection getDependencyInfo(String artifactId) throws RPCException;
    
    ArtifactGroup getArtifact(String artifactId) throws RPCException;
    
    void newPropertyDescriptor(String name, 
                               String description, 
                               boolean multivalued) throws RPCException;
    
    void setProperty(String artifactId, 
                     String propertyName, 
                     String propertyValue) throws RPCException;
    
    WComment addComment(String artifactId, String parentCommentId, String text) throws RPCException;
    
    void setDescription(String artifactId, String description) throws RPCException;

}
