package org.mule.galaxy;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.type.Type;

public interface Item {

    String getId();

    Item getParent();
    
    String getName();
    
    void setName(String name);

    Type getType();
    
    void setType(Type type) throws PropertyException;
    
    boolean isLocal();
    
    /**
     * Whether or not this item is for Galaxy's internal use only. This in essence means
     * that there is another way of interacting with it which is not through the repository UI.
     * @return
     */
    boolean isInternal();
    
    void setInternal(boolean internal);
    
    String getPath();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    User getAuthor();
    
    /**
     * Set the property value. May be intercepted/validated by an Extension.
     * @param name
     * @param value
     * @throws PropertyException
     * @throws PolicyException Thrown if this is not a valid value.
     */
    void setProperty(String name, Object value) throws PropertyException, PolicyException, AccessException;;

    /**
     * Set the property value direct - skipping any extensions. Extension.validate() is still called.
     * @param name
     * @param value
     * @throws PropertyException
     * @throws PolicyException
     */
    void setInternalProperty(String name, Object value) throws PropertyException, PolicyException, AccessException;;
    
    <T> T getProperty(String name);

    <T> T getInternalProperty(String name);
    
    Collection<PropertyInfo> getProperties();
    
    PropertyInfo getPropertyInfo(String name);

    void setLocked(String name, boolean locked);

    void setVisible(String property, boolean visible);

    void delete() throws RegistryException, AccessException;
    
    /**
     * Get the default lifecycle for this workspace. If there has been
     * no lifecycle explicitly set, it will use the parent's lifecycle.
     * If it gets to the top level workspace and there is no lifecycle
     * set, it will use the lifecycle from 
     * <code>LifecycleManager.getDefaultLifecycle()</code>.
     * 
     * @return
     */
    Lifecycle getDefaultLifecycle();
    
    void setDefaultLifecycle(Lifecycle l);

    LifecycleManager getLifecycleManager();
    
    CommentManager getCommentManager();    

    List<Item> getItems() throws RegistryException;

    /**
     * Get's the item that was added most recently to the collection.
     * @return
     * @throws RegistryException
     */
    Item getLatestItem() throws RegistryException;
    
    Item getItem(String name) throws RegistryException, NotFoundException, AccessException;
    
    NewItemResult newItem(String name, Type type)
        throws DuplicateItemException, RegistryException, PolicyException, PropertyException, AccessException;

    NewItemResult newItem(String name, Type type, Map<String,Object> initialProperties)
        throws DuplicateItemException, RegistryException, PolicyException, PropertyException, AccessException;

    Item getPrevious() throws RegistryException;

}
