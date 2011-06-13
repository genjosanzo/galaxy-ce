package org.mule.galaxy.workspace;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

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
    
    Collection<Item> getWorkspaces() throws AccessException, RegistryException;

    void delete(Item item) throws RegistryException, AccessException, PolicyException;

    void save(Item item) throws RegistryException, AccessException, PolicyException, PropertyException;

    NewItemResult newItem(Item parent, String name, Type type, Map<String,Object> initialProperties)
        throws DuplicateItemException, RegistryException, PolicyException, AccessException, PropertyException;
    
    List<Item> getItems(Item w) throws RegistryException;
    
    Item getLatestItem(Item w) throws RegistryException;
    
    Item getItemById(final String id) throws NotFoundException, RegistryException, AccessException;
    
    Item getItemByPath(final String path) throws NotFoundException, RegistryException, AccessException;
    
    /**
     * Attach this manager to the specified workspace.
     * @param workspace
     */
    void attachTo(Item workspace);

    ContentService getContentService();

    CommentManager getCommentManager();
    
    void validate() throws RegistryException;

    Item getItem(Item w, String name) throws RegistryException, NotFoundException, AccessException;

    TypeManager getTypeManager();
}
