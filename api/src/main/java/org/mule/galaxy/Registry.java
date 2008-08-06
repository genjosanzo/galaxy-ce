package org.mule.galaxy;


import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.AccessException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Registry {

    char WORKSPACE_MANAGER_SEPARATOR = '$';
    
    String PRIMARY_LIFECYCLE = "primary.lifecycle";

    /**
     * Get a universally unique ID for this registry, which can be used for things such as atom feeds.
     * @return
     */
    String getUUID();
    
    Workspace createWorkspace(String name) throws DuplicateItemException, RegistryException, AccessException;

    Workspace createWorkspace(Workspace parent, String name) throws DuplicateItemException, RegistryException, AccessException;

    void save(Workspace w, String parentId)
        throws RegistryException, NotFoundException, AccessException;

    void save(Item item) throws AccessException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException, AccessException;
    
    Item getItemById(String id) throws NotFoundException, RegistryException, AccessException;
    
    Item getItemByPath(String path) throws NotFoundException, RegistryException, AccessException;

    void move(Artifact artifact, String newWorkspaceId, final String newName) throws RegistryException, AccessException, NotFoundException;
    
    Collection<Artifact> getArtifacts(Workspace workspace) throws RegistryException;
    
    Artifact getArtifact(String id) throws NotFoundException, RegistryException, AccessException;

    ArtifactVersion getArtifactVersion(String id) throws NotFoundException, RegistryException, AccessException;

    Artifact getArtifact(Workspace w, String name) throws NotFoundException;

    Item resolve(Item w, String location);
    
    /* Search functions */

    SearchResults search(String queryString, int start, int maxResults) throws RegistryException, QueryException;

    SearchResults search(Query query) throws RegistryException, QueryException;
    
    /* Extensions */
    List<Extension> getExtensions();

    Extension getExtension(String extension);

}
