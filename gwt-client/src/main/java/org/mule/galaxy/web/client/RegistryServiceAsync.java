package org.mule.galaxy.web.client;

import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistryServiceAsync extends RemoteService {
    void getWorkspaces(AsyncCallback callback);
    
    void getArtifactTypes(AsyncCallback callback);
    
    void getArtifacts(String workspace, Set artifactTypes, AsyncCallback callback);
    
    void getIndexes(AsyncCallback callback);
    
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
    
    void addComment(String artifactId, String parentCommentId, String text, AsyncCallback callback);
    
    void setDescription(String artifactId, String description, AsyncCallback callback);
}
