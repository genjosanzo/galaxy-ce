package org.mule.galaxy.web.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistryServiceAsync extends RemoteService {
    public void getWorkspaces(AsyncCallback callback);
    public void getArtifactTypes(AsyncCallback callback);
    
    public void getArtifacts(String workspace, Set artifactTypes, AsyncCallback callback);
}
