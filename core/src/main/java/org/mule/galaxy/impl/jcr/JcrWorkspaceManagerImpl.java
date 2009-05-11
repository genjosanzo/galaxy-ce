package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.AttachedItem;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.artifact.ArtifactTypeDao;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.ItemCreatedEvent;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.impl.lifecycle.LifecycleExtension;
import org.mule.galaxy.impl.workspace.AbstractWorkspaceManager;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JcrWorkspaceManagerImpl extends AbstractWorkspaceManager 
    implements JcrWorkspaceManager {

    public static final String ID = "local";
    
    public static final String ITEM_NODE_TYPE = "galaxy:item";
    public static final String ATTACHED_ITEM_NODE_TYPE = "galaxy:attachedItem";
    
    public static final String LATEST = "latest";
    
    private final Log log = LogFactory.getLog(getClass());

    private LifecycleManager lifecycleManager;
    
    private PolicyManager policyManager;
    
    private UserManager userManager;

    private IndexManager indexManager;

    private ActivityManager activityManager;
    
    private AccessControlManager accessControlManager;

    private EventManager eventManager;
    
    private ArtifactTypeDao artifactTypeDao;
    
    private Registry registry;

    private TypeManager typeManager;
    
    private JcrTemplate template;
    
    public String getId() {
        return "local";
    }

    private Item buildItem(Node node) throws RepositoryException {
        return new JcrItem(node, this);
    }

    public Collection<Item> getWorkspaces() {
        return getWorkspaces(null);
    }

    public Collection<Item> getWorkspaces(Item workspace) {
        try {
            Node workspacesNode;
            if (workspace == null) {
                workspacesNode = getWorkspacesNode();
            } else {
                workspacesNode = ((JcrItem) workspace).getNode();
            }
            ArrayList<Item> workspaceCol = new ArrayList<Item>();
            for (NodeIterator itr = workspacesNode.getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();

                String type = n.getPrimaryNodeType().getName();
                if (type.equals(ITEM_NODE_TYPE)) {
                    workspaceCol.add(buildItem(n));
                } else if (type.equals(ATTACHED_ITEM_NODE_TYPE)) {
                    workspaceCol.add(buildAttachedItem(n));
                }
                
                Collections.sort(workspaceCol, new ItemComparator());
            }
            return workspaceCol;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Item buildAttachedItem(Node n) throws RepositoryException {
        return new JcrAttachedItem(n, this);
    }

    public Item getItemById(final String id) throws NotFoundException, RegistryException, AccessException {
        return (Item) executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Node node = session.getNodeByUUID(trimWorkspaceManagerId(id));
                    
                    String type = node.getPrimaryNodeType().getName();
                    
                    return build(node, type);
                } catch (ItemNotFoundException e){
                    throw new RuntimeException(new NotFoundException(id));
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }

        });
    }

    public Item getItemByPath(String path) throws NotFoundException, RegistryException, AccessException {
        try {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length()-1);
            }
            
            Node wNode = getWorkspacesNode();
            
            try {
                // have to have the catch because jackrabbit is lame...
                if (!wNode.hasNode(path)) throw new NotFoundException(path);
            } catch (RepositoryException e) {
                throw new NotFoundException(path);
            }
            
            Node node = wNode.getNode(path);
            String type = node.getPrimaryNodeType().getName();
            
            return build(node, type);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(e);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }
    
    public Item getItem(final Item w, final String name) throws NotFoundException, RegistryException, AccessException {
        Item a = (Item) executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = ((JcrItem) w).getNode();
                
                try {
                    Node resolved = node.getNode(JcrUtil.escape(name));
                    
                    return build(resolved, resolved.getPrimaryNodeType().getName());
                } catch (PathNotFoundException e) {
                    return null;
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                } catch (RegistryException e) {
                    throw new RuntimeException(e);
                }
                
            }
        });
        
        if (a == null) {
            throw new NotFoundException(name);
        }
        
        return a;
    }

    protected Item build(Node node) throws RepositoryException, ItemNotFoundException, AccessException, RegistryException {
        return build(node, node.getPrimaryNodeType().getName());
    }
    
    protected Item build(Node node, String type) throws RepositoryException, ItemNotFoundException,
        AccessException, RegistryException {
        if (type.equals(ATTACHED_ITEM_NODE_TYPE)) {
            AttachedItem w = new JcrAttachedItem(node, this); 
    
            accessControlManager.assertAccess(Permission.READ_ITEM, w);
            
            return w;
        }  else {
             Item wkspc = buildItem(node);
             
             accessControlManager.assertAccess(Permission.READ_ITEM, wkspc);
             
             return wkspc;
        }
    }
    
    private Node getWorkspacesNode() {
        return ((JcrRegistryImpl) registry).getWorkspacesNode();
    }

    
    protected void copy(Node original, Node parent) throws RepositoryException {
        Node node = parent.addNode(original.getName());
        node.addMixin("mix:referenceable");
        
        for (PropertyIterator props = original.getProperties(); props.hasNext();) {
            Property p = props.nextProperty();
            if (!p.getName().startsWith("jcr:")) {
                node.setProperty(p.getName(), p.getValue());
            }
        }
        
        for (NodeIterator nodes = original.getNodes(); nodes.hasNext();) {
            Node child = nodes.nextNode();
            if (!child.getName().startsWith("jcr:")) {
                copy(child, node);
            }
        }
    }
    
    public NewItemResult newItem(
            final Item parent,
            final String name,
            final Type type,
        final Map<String, Object> initialProperties)
        throws DuplicateItemException, RegistryException, PolicyException, AccessException, PropertyException {
        if (parent != null) {
            accessControlManager.assertAccess(Permission.MODIFY_ITEM, parent);
        } else {
            accessControlManager.assertAccess(Permission.MODIFY_ITEM);
        }
        
        final User user = SecurityUtils.getCurrentUser();

        if (user == null) {
            throw new NullPointerException("User cannot be null.");
        }
        
        if (name == null) {
            throw new NullPointerException("Item name cannot be null.");
        }
        
        final JcrWorkspaceManagerImpl registry = this;
        return (NewItemResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node parentNode;
                if (parent != null) {
                    parentNode = ((JcrItem)parent).getNode();
                } else {
                    parentNode = getWorkspacesNode();
                }
                
                Node itemNode;
                try {
                    itemNode = parentNode.addNode(name, ITEM_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                itemNode.addMixin("mix:referenceable");
                itemNode.setProperty(JcrItem.TYPE, type.getId());
                itemNode.setProperty(JcrItem.AUTHOR, user.getId());
                
                JcrItem item = new JcrItem(itemNode, registry);
                item.setName(name);
                
                // set up the initial version
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                itemNode.setProperty(JcrItem.CREATED, now);
                
                if (log.isDebugEnabled())
                {
                    log.debug("Created item " + item.getId());
                }
                
                try {
                    if (initialProperties != null) {
                        for (Map.Entry<String, Object> e : initialProperties.entrySet()) {
                            item.setProperty(e.getKey(), e.getValue());
                        }
                    }
                    initializeLifecycle(item);
                    
                    item.setType(type);

                    approveChild(parent, item);
                    
                    Map<Item, List<ApprovalMessage>> approvals = policyManager.approve(item);

                    // save this so the indexer will work
                    session.save();
                    
                    for (GalaxyEvent e : item.getSaveEvents()) {
                        eventManager.fireEvent(e);
                    }
                    item.getSaveEvents().clear();
                    
                    // index in a separate thread
                    indexManager.index(item);
                    
                    // save the "we're indexing" flag
                    session.save();
                    
                // fire the event
            NewItemResult result = new NewItemResult(item, approvals);
            ItemCreatedEvent event = new ItemCreatedEvent(result.getItem());
            event.setUser(SecurityUtils.getCurrentUser());
            eventManager.fireEvent(event);

                return result;
                } catch (RuntimeException e) {
                    parentNode.refresh(false);
                    throw e;
                } catch (RegistryException e) {
                    parentNode.refresh(false);
                    throw new RuntimeException(e);
        } catch (PolicyException e) {
                    parentNode.refresh(false);
                    throw new RuntimeException(e);
        } catch (PropertyException e) {
                    parentNode.refresh(false);
                    throw new RuntimeException(e);
                } catch (AccessException e) {
                    parentNode.refresh(false);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void approveChild(final Item parent, Item child) {
        // Ensure that this type is allowed here
        if (parent == null) return;
        
        List<Type> allowedChildren = parent.getType().getAllowedChildren();
        if (allowedChildren != null && allowedChildren.size() > 0) {
            boolean found = false;
            for (Type t : allowedChildren) {
                if (child.getType().inheritsFrom(t.getName())) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                throw new RuntimeException(new PolicyException(child, "This type is not allowed as a child of " + parent.getType().getName()));
            }
        }
    }

    protected void initializeLifecycle(Item item) {
        Lifecycle lifecycle = item.getDefaultLifecycle();

        for (PropertyDescriptor pd : item.getType().getProperties()) {
            if (pd.getExtension() instanceof LifecycleExtension) {
                try {
                    item.setProperty(pd.getProperty(), lifecycle.getInitialPhase());
                } catch (PropertyException e) {
                    throw new RuntimeException(e);
                } catch (PolicyException e) {
                    throw new RuntimeException(e);
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected String escapeNodeName(String right) {
        return ISO9075.encode(right);
    }

    public void delete(final Item item) throws RegistryException, AccessException {
    accessControlManager.assertAccess(Permission.DELETE_ITEM);

        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
            ItemDeletedEvent evt = new ItemDeletedEvent(item);

                Node node = ((JcrItem) item).getNode();
                Node parent = node.getParent();
                node.remove();
                parent.refresh(true);
                
                session.save();

                evt.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(evt);

                return null;
            }
        });
    }
    
    public void save(Item i) throws AccessException, RegistryException, PolicyException, PropertyException {
        accessControlManager.assertAccess(Permission.MODIFY_ITEM, i);
        
        // Check that this is OK with the policies!
        getPolicyManager().approve(i);
        ((JcrItem) i).verifyConformance();
        
        template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                session.save();
                return null;
            }
        });

        JcrItem jcrItem = ((JcrItem) i);
        for (GalaxyEvent e : jcrItem.getSaveEvents()) {
            eventManager.fireEvent(e);
        }
        jcrItem.getSaveEvents().clear();
    }

    public void attachTo(Item workspace) {
        throw new UnsupportedOperationException();
    }

    private Object executeWithRegistryException(JcrCallback jcrCallback) 
        throws RegistryException, AccessException {
        try {
            return template.execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else {
                throw e;
            }
        }
    }
    
    private Object executeWithNotFound(JcrCallback jcrCallback) 
        throws RegistryException, NotFoundException, AccessException {
        try {
            return template.execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof NotFoundException) {
                throw (NotFoundException) cause;
            } else if (cause instanceof AccessException) {
                throw (AccessException) cause;
            } else {
                throw e;
            }
        }
    }
    
    private Object executeAndDewrap(JcrCallback jcrCallback)
        throws RegistryException, PolicyException, DuplicateItemException, PropertyException {
        try {
            return template.execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof DuplicateItemException) {
                throw (DuplicateItemException) cause;
            } else if (cause instanceof PolicyException) {
                throw (PolicyException) cause;
            }  else if (cause instanceof PropertyException) {
                throw (PropertyException) cause;
            } else {
                throw e;
            }
        }
    }
        
    public void validate() throws RegistryException {
    }

    public JcrTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JcrTemplate template) {
        this.template = template;
    }

    public LifecycleManager getLifecycleManager(Item w) {
        return lifecycleManager;
    }

    public LifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public IndexManager getIndexManager() {
        return indexManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public AccessControlManager getAccessControlManager() {
        return accessControlManager;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public ArtifactTypeDao getArtifactTypeDao() {
        return artifactTypeDao;
    }

    public void setArtifactTypeDao(ArtifactTypeDao artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public TypeManager getTypeManager() {
        return typeManager;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public List<Item> getItems(Item w) throws RegistryException {
        List<Item> items = new ArrayList<Item>();
        try {
            NodeIterator nodes = ((JcrItem) w).getNode().getNodes();
            while (nodes.hasNext()) {
                Node n = nodes.nextNode();
                
                try {
                    items.add(build(n));
                } catch (AccessException e) {
                    // skip nodes which the user doesn't have access to
                }
            }
            Collections.sort(items, new ItemComparator());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        } 
        return items;
    }
}
