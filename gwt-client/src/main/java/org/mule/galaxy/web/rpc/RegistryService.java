package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

public interface RegistryService extends RemoteService {
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WWorkspace>
     * @return
     * @throws RPCException 
     */
    Collection getWorkspaces() throws RPCException;
    
    void addWorkspace(String parentWorkspaceId, String workspaceName) throws RPCException;

    void updateWorkspace(String workspaceId, String parentWorkspaceId, String workspaceName) throws RPCException;
    
    void deleteWorkspace(String workspaceId) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactType>
     * @return
     */
    Collection getArtifactTypes();

    
    /**
     * @gwt.typeArgs searchPredicates <org.mule.galaxy.web.rpc.SearchPredicate>
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.ArtifactGroup>
     * @return 
     * @throws RPCException 
     */
    Collection getArtifacts(String workspace, Set artifactTypes, Set searchPredicates, String freeformQuery) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WIndex>
     * @return
     */
    public Collection getIndexes();
    
    public WIndex getIndex(String id) throws RPCException;
    
    public void saveIndex(WIndex index) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.DependencyInfo>
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
    
    void move(String artifactId, String workspaceId, String name) throws RPCException;
    
    void delete(String artifactId) throws RPCException;
    
    Map getPropertyList() throws RPCException;
    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     * @return
     * @throws Exception 
     */
    Map getProperties() throws RPCException;

    WComment addComment(String artifactId, String parentCommentId, String text) throws RPCException;
    
    void setDescription(String artifactId, String description) throws RPCException;

    WGovernanceInfo getGovernanceInfo(String artifactId) throws RPCException;

    TransitionResponse setActive(String artifactId, String versionLabel) throws RPCException;
    
    TransitionResponse transition(String artifactId, String nextPhase) throws RPCException;

    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     * @return
     * @throws Exception 
     */
    Collection getArtifactVersions(String artifactId) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactPolicy>
     * @return
     * @throws Exception 
     */
    Collection getPolicies() throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactPolicy>
     * @return
     * @throws Exception 
     */
    Collection getLifecycles() throws RPCException;
    
    /**
     * @gwt.typeArgs <java.lang.String>
     * @return
     * @throws RPCException 
     */
    Collection getActivePoliciesForLifecycle(String lifecycle) throws RPCException;
    
    /**
     * @gwt.typeArgs <java.lang.String>
     * @return
     * @throws RPCException 
     */
    Collection getActivePoliciesForPhase(String lifecycle, String phase) throws RPCException;

    /**
     * @gwt.typeArgs ids <java.lang.String>
     */
    void setActivePolicies(String workspace, String lifecycle, String phase, Collection ids) throws RPCException;

    /**
     * @gwt.typeArgs ids <org.mule.galaxy.web.rpc.WActivity>
     */
    Collection getActivities(Date from, Date to, String user, String eventType, int start, int results, boolean ascending) throws RPCException;
}
