package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.w3c.dom.Document;

public interface Registry {

    Workspace getWorkspace(String id) throws ArtifactException, NotFoundException;
    
    Collection<Workspace> getWorkspaces() throws ArtifactException;
    
    Artifact createArtifact(Workspace workspace, Object o) 
        throws ArtifactException;
    
    Artifact createArtifact(Workspace workspace, String contentType, String name, InputStream inputStream) 
        throws ArtifactException, IOException;
    
    ArtifactVersion newVersion(Artifact doc, String contentType, String name, InputStream inputStream) 
        throws ArtifactException, IOException;

    Collection<Artifact> getArtifacts(Workspace workspace) throws ArtifactException;
    
    Artifact getArtifact(String id) throws NotFoundException;
    
    void delete(Artifact artifact) throws ArtifactException;

}
