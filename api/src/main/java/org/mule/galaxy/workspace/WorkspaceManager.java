package org.mule.galaxy.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public interface WorkspaceManager {
    /**
     * A UUID for this workspace manager.
     */
    String getId();

    Workspace getWorkspace(String id) throws RegistryException, NotFoundException, AccessException;

    Artifact getArtifact(String id) throws NotFoundException, RegistryException, AccessException;
    
    Collection<Artifact> getArtifacts(Workspace workspace) throws RegistryException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException, AccessException;

    void delete(Workspace workspace) throws RegistryException, AccessException;
    	
    void delete(Artifact artifact) throws RegistryException, AccessException;
    	
    void delete(ArtifactVersion version) throws RegistryException, AccessException;
    
    ArtifactResult newVersion(Artifact artifact, 
            Object data, 
            String versionLabel, 
            User user)
    	throws RegistryException, ArtifactPolicyException, IOException, DuplicateItemException, AccessException;
    
    ArtifactResult newVersion(final Artifact artifact, 
            final InputStream inputStream, 
            final String versionLabel, 
            final User user) 
    	throws RegistryException, ArtifactPolicyException, IOException, DuplicateItemException, AccessException;
    
    /**
     * Attach this manager to the specified workspace.
     * @param workspace
     */
    void attachTo(Workspace workspace);
}
