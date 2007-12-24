package org.mule.galaxy.web.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistryServiceAsync extends RemoteService {
    void getWorkspaces(AsyncCallback callback);
    
    void getArtifactTypes(AsyncCallback callback);
    
    void getArtifacts(String workspace, Set artifactTypes, AsyncCallback callback);
    
    void getDependencyInfo(String artifactId, AsyncCallback callback);

    void getArtifact(String artifactId, AsyncCallback abstractCallback);
}
