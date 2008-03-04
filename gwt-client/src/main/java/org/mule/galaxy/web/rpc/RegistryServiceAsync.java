package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public interface RegistryServiceAsync extends RemoteService {
    void getWorkspaces(AsyncCallback callback);
    
    void addWorkspace(String parentWorkspaceId, 
                      String workspaceName,
                      String lifecycleId,
                      AsyncCallback callback);
    
    void updateWorkspace(String workspaceId, 
                         String parentWorkspaceId, 
                         String workspaceName,
                         String lifecycleId,
                         AsyncCallback callback);

    void deleteWorkspace(String workspaceId,
                         AsyncCallback callback);
    
    void getArtifactTypes(AsyncCallback callback);
    
    void saveArtifactType(WArtifactType artifactType, 
                      AsyncCallback callback);
    
    void deleteArtifactType(String id, 
                            AsyncCallback callback);
    
    void getArtifacts(String workspace, Set artifactTypes, 
                      Set searchPredicates, String freeformQuery, 
                      int start, int maxResults, 
                      AsyncCallback callback);
    
    void getIndexes(AsyncCallback callback);

    void getIndex(String id, AsyncCallback c);
    
    void saveIndex(WIndex index, AsyncCallback callback);
    
    void getDependencyInfo(String artifactId, AsyncCallback callback);

    void getArtifact(String artifactId, AsyncCallback callback);
    

    void newPropertyDescriptor(String name, 
                               String description, 
                               boolean multivalued, 
                               AsyncCallback callback);
    
    void setProperty(String artifactId, 
                     String propertyName, 
                     String propertyValue,
                     AsyncCallback callback);
    
    void deleteProperty(String artifactId, 
                        String propertyName, 
                        AsyncCallback callback);
    
    void addComment(String artifactId, String parentCommentId, String text, AsyncCallback callback);
    
    void setDescription(String artifactId, String description, AsyncCallback callback);
    
    void getPropertyList(AsyncCallback c);
    
    void getProperties(AsyncCallback c);
    
    void getGovernanceInfo(String artifactId, AsyncCallback c);
    
    void transition(String artifactId, String nextPhaseId, AsyncCallback c);

    void setActive(String artifactId, String versionLabel, AsyncCallback c);
    
    void getArtifactVersions(String artifactId, AsyncCallback c);

    void move(String artifactId, String workspaceId, String name, AsyncCallback c);
    
    void delete(String artifactId, AsyncCallback c);
    
    void getPolicies(AsyncCallback c);

    void getLifecycles(AsyncCallback c);

    void saveLifecycle(WLifecycle l, AsyncCallback c);
    
    void getActivePoliciesForLifecycle(String name, String workspaceId, AsyncCallback c);
    
    void getActivePoliciesForPhase(String lifecycle, String phase, String workspaceId, AsyncCallback c);

    void setActivePolicies(String workspace, String lifecycle, String phase, Collection ids, AsyncCallback c);
    
    void getActivities(Date from, Date to, String user, String eventType, int start, int results, boolean ascending, AsyncCallback c);
    
    void getUserInfo(AsyncCallback c);

    void deleteLifecycle(String id, AsyncCallback abstractCallback);
}