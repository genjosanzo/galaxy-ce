package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public interface Registry {
    
    /**
     * Get a universally unique ID for this registry, which can be used for things such as atom feeds.
     * @return
     */
    String getUUID();
    
    Workspace createWorkspace(String name) throws DuplicateItemException, RegistryException, AccessException;

    Workspace createWorkspace(Workspace parent, String name) throws DuplicateItemException, RegistryException, AccessException;
    
    Workspace getWorkspace(String id) throws RegistryException, NotFoundException, AccessException;
    
    void save(Workspace w, String parentId)
        throws RegistryException, NotFoundException, AccessException;

    void save(Workspace w) throws AccessException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException, AccessException;
    
    Item<?> getRegistryItem(String id) throws NotFoundException, RegistryException, AccessException;
    
    Item<?> getItemByPath(String path) throws NotFoundException, RegistryException, AccessException;

    void move(Artifact artifact, String workspaceId) throws RegistryException, AccessException, NotFoundException;
    
    Collection<Artifact> getArtifacts(Workspace workspace) throws RegistryException;
    
    Artifact getArtifact(String id) throws NotFoundException, RegistryException, AccessException;

    ArtifactVersion getArtifactVersion(String id) throws NotFoundException, RegistryException, AccessException;

    void save(Artifact artifact) throws RegistryException, AccessException;

    Artifact getArtifact(Workspace w, String name) throws NotFoundException;

    Artifact resolve(Workspace w, String location);
    
    /* Search functions */

    SearchResults search(String queryString, int start, int maxResults) throws RegistryException, QueryException;

    SearchResults search(Query query) throws RegistryException, QueryException;
    

    /* Property related methods */
     
    Collection<PropertyDescriptor> getPropertyDescriptors() throws RegistryException;

    PropertyDescriptor getPropertyDescriptor(String propertyId) throws RegistryException, NotFoundException;

    void savePropertyDescriptor(PropertyDescriptor pd) throws RegistryException, AccessException, DuplicateItemException, NotFoundException;
    
    void deletePropertyDescriptor(String id) throws RegistryException;
    
    PropertyDescriptor getPropertyDescriptorByName(final String propertyName);
    

    /* Link related operations */
    
    void addLinks(Item<?> item, LinkType type, Item<?>... toLinkTo) throws RegistryException;

    void removeLinks(Link... links) throws RegistryException;
    
    Set<Link> getReciprocalLinks(Item<?> a) throws RegistryException;

}
