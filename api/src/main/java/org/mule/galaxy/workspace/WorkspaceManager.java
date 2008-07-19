package org.mule.galaxy.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.PolicyException;
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

    void delete(Item item) throws RegistryException, AccessException;
    
    void setEnabled(final EntryVersion version, 
            final boolean enabled) throws RegistryException, PolicyException;

    EntryResult newVersion(Artifact artifact, 
            Object data, 
            String versionLabel, 
            User user)
    	throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException;
    
    EntryResult newVersion(final Artifact artifact, 
            final InputStream inputStream, 
            final String versionLabel, 
            final User user) 
    	throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException;
    
    EntryResult createArtifact(Workspace workspace, Object data, String versionLabel, User user) 
    	throws RegistryException, PolicyException, MimeTypeParseException, DuplicateItemException, AccessException;
    
    EntryResult createArtifact(Workspace workspace, 
            String contentType, 
            String name,
            String versionLabel, 
            InputStream inputStream, 
            User user) 
    	throws RegistryException, PolicyException, IOException, MimeTypeParseException, DuplicateItemException, AccessException;
    
    EntryResult newEntry(Workspace workspace, String name, String versionLabel)
   	throws DuplicateItemException, RegistryException, PolicyException, AccessException;
    
    /**
     * Attach this manager to the specified workspace.
     * @param workspace
     */
    void attachTo(Workspace workspace);
}
