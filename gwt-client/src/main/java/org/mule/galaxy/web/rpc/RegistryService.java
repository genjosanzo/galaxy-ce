package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

public interface RegistryService extends RemoteService {
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.WWorkspace
     * @return
     * @throws RPCException 
     */
    Collection getWorkspaces() throws RPCException;
    
    void addWorkspace(String parentWorkspaceId, String workspaceName) throws RPCException;

    void updateWorkspace(String workspaceId, String parentWorkspaceId, String workspaceName) throws RPCException;
    
    void deleteWorkspace(String workspaceId) throws RPCException;
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.WArtifactType
     * @return
     */
    Collection getArtifactTypes();

    
    /**
     * @gwt typeArgs org.mule.galaxy.web.client.ArtifactGroup
     * @return
     */
    Collection getArtifacts(String workspace, Set artifactTypes);
    
    /**
     * @gwt typeArgs java.lang.String,java.lang.String
     * @return
     */
    public Map getIndexes();
    
    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.DependencyInfo
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
    

    void deleteProperty(String artifactId, 
                        String propertyName) throws RPCException;
    
    /**
     * @gwt typeArgs java.lang.String,java.lang.String
     * @return
     * @throws Exception 
     */
    Map getProperties() throws RPCException;

    WComment addComment(String artifactId, String parentCommentId, String text) throws RPCException;
    
    void setDescription(String artifactId, String description) throws RPCException;

    WGovernanceInfo getGovernanceInfo(String artifactId) throws RPCException;

    TransitionResponse transition(String artifactId, String nextPhase) throws RPCException;

    /**
     * @gwt typeArgs java.lang.String,java.lang.String
     * @return
     * @throws Exception 
     */
    Collection getArtifactVersions(String artifactId) throws RPCException;
}
