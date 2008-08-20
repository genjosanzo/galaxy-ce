package org.mule.galaxy;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;
import org.mule.galaxy.workspace.WorkspaceManagerFactory;

public interface Registry {

    char WORKSPACE_MANAGER_SEPARATOR = '$';
    
    String PRIMARY_LIFECYCLE = "primary.lifecycle";

    /**
     * Get a universally unique ID for this registry, which can be used for things such as atom feeds.
     * @return
     */
    String getUUID();
    
    Collection<Workspace> getWorkspaces() throws RegistryException, AccessException;
    
    Workspace newWorkspace(String name) throws DuplicateItemException, RegistryException, AccessException;

    void save(Workspace w, String parentId)
        throws RegistryException, NotFoundException, AccessException, DuplicateItemException;

    void save(Item item) throws AccessException, RegistryException;
    
    /**
     * The WorkspaceManagers that have been configured and attached inside this Registry.
     * 
     */
    Collection<WorkspaceManager> getWorkspaceManagers();
    
    /**
     * Create a "virtual" workspace which is attached to a parent workspace. The name
     * of the WorkspaceManagerFactory and the configuration will be stored and used to 
     * reattach the virtual workspace when the Registry starts again.
     * 
     * @param parent The workspace the virtual workspace is attached to.
     * @param name The name of the virtual workspace.
     * @param factory 
     * @param configuration
     * @return
     * @throws RegistryException
     */
    Workspace attachWorkspace(
	    Workspace parent, 
	    String name, 
	    WorkspaceManagerFactory factory, 
	    Map<String, String> configuration) throws RegistryException;
    
    Item getItemById(String id) throws NotFoundException, RegistryException, AccessException;
    
    Item getItemByPath(String path) throws NotFoundException, RegistryException, AccessException;

    Item resolve(Item w, String location);
    
    void move(Entry item, String newWorkspaceId, final String newName) throws RegistryException, AccessException, NotFoundException;

    
    /* Search functions */

    SearchResults search(String queryString, int start, int maxResults) throws RegistryException, QueryException;

    SearchResults search(Query query) throws RegistryException, QueryException;
    
    SearchResults suggest(final String path, final int maxResults, final String excludePath, Class... types)
    	throws RegistryException, QueryException;
    
    /**
     * Get a Map of properties which can be used inside queries. The key will be the property
     * name and the value will be the human readable version.
     * @return
     */
    Map<String, String> getQueryProperties();
    
    /* Extensions */
    List<Extension> getExtensions();

    Extension getExtension(String extension);

}
