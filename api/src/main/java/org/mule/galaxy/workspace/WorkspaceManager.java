package org.mule.galaxy.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;

/**
 * WorkspaceManagers perform the underlying operations on Items in the registry. This makes it 
 * easy for different backends to be replaced. For instance, there could be Atom backends
 * or SVN or UDDI. Not all methods may be supported and may therefore throw UnsupportedOperationExceptions.
 * @author Dan
 */
public interface WorkspaceManager {
    /**
     * A UUID for this workspace manager.
     */
    String getId();
    
    Collection<Workspace> getWorkspaces() throws AccessException;

    Collection<Workspace> getWorkspaces(Workspace workspace);

    Workspace newWorkspace(final String name) 
    	throws DuplicateItemException, RegistryException, AccessException;
    
    Workspace newWorkspace(Workspace parent, String name) 
    	throws DuplicateItemException, RegistryException, AccessException;
                                     
    void delete(Item item) throws RegistryException, AccessException;

    void save(Item item) throws RegistryException, AccessException;
    
    EntryResult newVersion(Artifact artifact, 
            Object data, 
            String versionLabel)
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException;
    
    EntryResult newVersion(final Artifact artifact, 
            final InputStream inputStream, 
            final String versionLabel) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException;
    
    EntryResult createArtifact(Workspace workspace, Object data, String versionLabel) 
        throws RegistryException, PolicyException, MimeTypeParseException, DuplicateItemException, AccessException;
    
    EntryResult createArtifact(Workspace workspace, 
            String contentType, 
            String name,
            String versionLabel, 
            InputStream inputStream) 
        throws RegistryException, PolicyException, IOException, MimeTypeParseException, DuplicateItemException, AccessException;
    
    EntryResult newEntry(Workspace workspace, String name, String versionLabel)
       throws DuplicateItemException, RegistryException, PolicyException, AccessException;
    
    EntryResult newVersion(Entry entry, String versionLabel)
    	throws DuplicateItemException, RegistryException, PolicyException, AccessException;

    List<Item> getItems(Workspace w);
    
    Item getItemById(final String id) throws NotFoundException, RegistryException, AccessException;
    
    Item getItemByPath(final String path) throws NotFoundException, RegistryException, AccessException;
    /**
     * Attach this manager to the specified workspace.
     * @param workspace
     */
    void attachTo(Workspace workspace);

    ContentService getContentService();

    CommentManager getCommentManager();
}
