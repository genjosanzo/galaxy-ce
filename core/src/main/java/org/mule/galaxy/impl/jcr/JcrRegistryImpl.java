package org.mule.galaxy.impl.jcr;


import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.LinkType;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Settings;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.EntryMovedEvent;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.WorkspaceCreatedEvent;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.jcr.query.QueryBuilder;
import org.mule.galaxy.impl.jcr.query.SimpleQueryBuilder;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.AbstractFunction;
import org.mule.galaxy.query.FunctionCall;
import org.mule.galaxy.query.FunctionRegistry;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.DateUtil;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.SecurityUtils;
import org.mule.galaxy.workspace.WorkspaceManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.util.ISO9075;
import org.springframework.dao.DataAccessException;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JcrRegistryImpl extends JcrTemplate implements Registry, JcrRegistry {
    private static final String REPOSITORY_LAYOUT_VERSION = "version";

    private final Log log = LogFactory.getLog(getClass());

    private Settings settings;
    
    private ContentService contentService;

    private FunctionRegistry functionRegistry;
    
    private LifecycleManager lifecycleManager;
    
    private PolicyManager policyManager;
    
    private UserManager userManager;

    private Dao<PropertyDescriptor> propertyDescriptorDao;
    
    private Dao<LinkType> linkTypeDao;
    
    private String workspacesId;

    private String indexesId;

    private String artifactTypesId;
    
    private ActivityManager activityManager;
    
    private AccessControlManager accessControlManager;

    private EventManager eventManager;
    
    private String id;
    
    private Map<String, QueryBuilder> queryBuilders;
    
    private SimpleQueryBuilder simpleQueryBuilder = new SimpleQueryBuilder(new String[0], false);

    private JcrWorkspaceManager localWorkspaceManager;
    
    private Map<String, WorkspaceManager> idToWorkspaceManager = new HashMap<String, WorkspaceManager>();
    
    private List<Extension> extensions;
    
    public JcrRegistryImpl() {
        super();
    }

    public String getUUID() {
        return id;
    }

    public Artifact getArtifact(String id) throws NotFoundException, RegistryException, AccessException {
        return getWorkspaceManagerByItemId(id).getArtifact(id);
    }

    public Workspace getWorkspace(String id) throws RegistryException, NotFoundException, AccessException {
        return getWorkspaceManagerByItemId(id).getWorkspace(id);
    }

    public Collection<Workspace> getWorkspaces() throws RegistryException, AccessException {
        return localWorkspaceManager.getWorkspaces();
    }

    private Workspace buildWorkspace(Node node) throws RepositoryException {
        return new JcrWorkspace(localWorkspaceManager, node);
    }

    public Workspace createWorkspace(final String name) throws RegistryException, AccessException, DuplicateItemException {
        // we should throw an error, but lets be defensive for now
        final String escapedName = JcrUtil.escape(name);
        
        accessControlManager.assertAccess(Permission.MODIFY_WORKSPACE);

        return (Workspace) executeAndDewrapWithDuplicate(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node;
                try {
                    node = getWorkspacesNode().addNode(escapedName, "galaxy:workspace");
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                node.addMixin("mix:referenceable");
    
                JcrWorkspace workspace = new JcrWorkspace(localWorkspaceManager, node);
                workspace.setName(escapedName);
                workspace.setDefaultLifecycle(lifecycleManager.getDefaultLifecycle());
                
                Calendar now = DateUtil.getCalendarForNow();
                node.setProperty(JcrWorkspace.CREATED, now);
                node.setProperty(JcrWorkspace.UPDATED, now);
                
                session.save();

                final WorkspaceCreatedEvent event = new WorkspaceCreatedEvent(workspace.getId(), workspace.getPath());
                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return workspace;
            }
        });
    }

    public void save(Workspace w) throws AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_WORKSPACE, w);
        
        execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                session.save();
                return null;
            }
            
        });
    }

    public void save(final Workspace w, final String parentId) 
        throws RegistryException, NotFoundException, AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, w);

        final String trimmedParentId = trimWorkspaceManagerId(parentId);
        
        executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                JcrWorkspace jw = (JcrWorkspace) w;
                Node node = jw.getNode();
                
                if (trimmedParentId != null && !trimmedParentId.equals(node.getParent().getUUID())) {
                    Node parentNode = null;
                    if (parentId != null) {
                        try {
                            parentNode = getNodeByUUID(trimmedParentId);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(new NotFoundException(trimmedParentId));
                        }
                    } else {
                        parentNode = node.getParent();
                    }
                    
                    // ensure the user isn't trying to move this to a child node of the workspace
                    Node checked = parentNode;
                    while (checked != null && checked.getPrimaryNodeType().getName().equals("galaxy:workspace")) {
                        if (checked.equals(node)) {
                            throw new RuntimeException(new RegistryException(new Message("MOVE_ONTO_CHILD", BundleUtils.getBundle(getClass()))));
                        }
                        
                        checked = checked.getParent();
                    }
                    
                    JcrWorkspace toWkspc = new JcrWorkspace(localWorkspaceManager, parentNode);
                    try {
                        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, toWkspc);
                    } catch (AccessException e) {
                        throw new RuntimeException(e);
                    }
                    
                    String dest = parentNode.getPath() + "/" + w.getName();
                    session.move(node.getPath(), dest);
                }
                
                session.save();
                
                return null;
            }
        });
    }


    public Workspace createWorkspace(final Workspace parent, 
                                     final String name) throws DuplicateItemException, RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_WORKSPACE, parent);

        // we should throw an error, but lets be defensive for now
        final String escapedName = JcrUtil.escape(name);

        return (Workspace) executeAndDewrapWithDuplicate(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Collection<Workspace> workspaces = parent.getWorkspaces();
                
                Node parentNode = ((JcrWorkspace) parent).getNode();
                Node node = null;
                try {
                    node = parentNode.addNode(escapedName, "galaxy:workspace");
                } catch (ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                node.addMixin("mix:referenceable");
    
                Calendar now = DateUtil.getCalendarForNow();
                node.setProperty(JcrWorkspace.CREATED, now);
                node.setProperty(JcrWorkspace.UPDATED, now);
                
                JcrWorkspace workspace = new JcrWorkspace(localWorkspaceManager, node);
                workspace.setName(escapedName);
                workspace.setDefaultLifecycle(lifecycleManager.getDefaultLifecycle());
                workspaces.add(workspace);

                session.save();

                final String path = workspace.getPath();
                WorkspaceCreatedEvent event = new WorkspaceCreatedEvent(workspace.getId(), path);
                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return workspace;
            }
        });
    }

    public Artifact resolve(Workspace w, String location) {
        String[] paths = location.split("/");
        
        for (int i = 0; i < paths.length - 1; i++) {
            String p = paths[i];
            
            // TODO: escaping?
            if (p.equals("..")) {
                w = ((Workspace)w.getParent());
            } else if (!p.equals(".")) {
                w = w.getWorkspace(p);
            }
        }

        try {
            return getArtifact(w, paths[paths.length-1]);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    public Node getWorkspacesNode() {
        return getNodeByUUID(workspacesId);
    }

    public Node getIndexNode() {
        return getNodeByUUID(indexesId);
    }
    
    public Node getArtifactTypesNode() {
        return getNodeByUUID(artifactTypesId);
    }
    
    public Collection<Artifact> getArtifacts(Workspace w) throws RegistryException {
        WorkspaceManager wm = getWorkspaceManager(w);
    
        return wm.getArtifacts(w);
    }

    private WorkspaceManager getWorkspaceManager(Item i) {
        return getWorkspaceManagerByItemId(i.getId());
    }

    private WorkspaceManager getWorkspaceManager(String wmId) {
        return idToWorkspaceManager.get(wmId);
    }

    private WorkspaceManager getWorkspaceManagerByItemId(String itemId) {
        int idx = itemId.indexOf('$');

        if (idx == -1) {
            throw new IllegalStateException("Invalid item id: " + itemId);
        }

        return getWorkspaceManager(itemId.substring(0, idx));
    }

    private Artifact buildArtifact(Node node)
        throws ItemNotFoundException, AccessDeniedException, RepositoryException {
        Node wNode = node.getParent();
        JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(localWorkspaceManager, wNode), 
                                               node, localWorkspaceManager);

        setupContentHandler(artifact);

        return artifact;
    }

    public Item getRegistryItem(final String id) throws NotFoundException, RegistryException, AccessException {
        return (Item) executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(trimWorkspaceManagerId(id));
                
                try {
                    if (node.getPrimaryNodeType().getName().equals("galaxy:artifact")) {
                        Artifact a = buildArtifact(node);
    
                        accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
                        
                        return a;
                    } else {
                         Workspace wkspc = buildWorkspace(node);
                         
                         accessControlManager.assertAccess(Permission.READ_WORKSPACE, wkspc);
                         
                         return wkspc;
                    }
                } catch (AccessException e){
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public Item getItemByPath(String path) throws RegistryException, NotFoundException, AccessException {
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
            
            if (type.equals("galaxy:workspace")) {
                Workspace w = buildWorkspace(node);
                accessControlManager.assertAccess(Permission.READ_WORKSPACE, w);
                return w;
            } else if (type.equals("galaxy:artifact")) {
            Artifact a = buildArtifact(node);
                accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
                return a;
            } else if (type.equals("galaxy:artifactVersion")) {
            Artifact a = buildArtifact(node.getParent());
                accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
                return a.getVersion(node.getName());
            }
            
            throw new NotFoundException(path);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(e);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }
    
    
    public ArtifactVersion getArtifactVersion(final String id) throws NotFoundException, RegistryException, AccessException {
        return (ArtifactVersion) executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(trimWorkspaceManagerId(id));
                Node aNode = node.getParent();
                Node wNode = aNode.getParent();
                
                JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(localWorkspaceManager, wNode), 
                                                       aNode, localWorkspaceManager);

                try {
                    accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }
                
                setupContentHandler(artifact);

                EntryVersion av = artifact.getVersion(node.getName());
                if (av == null) {
                    throw new RuntimeException(new NotFoundException(id));
                }
                return av;
            }
        });
    }
    
    protected void setupContentHandler(JcrArtifact artifact) {
        ContentHandler ch = null;
        if (artifact.getDocumentType() != null) {
            ch = contentService.getContentHandler(artifact.getDocumentType());
        } else {
            ch = contentService.getContentHandler(artifact.getContentType());
        }
        artifact.setContentHandler(ch);
    }

    private String trimWorkspaceManagerId(String id) {
        int idx = id.indexOf('$');
        if (idx == -1) {
            throw new IllegalStateException("Illegal workspace manager id.");
        }

        return id.substring(idx + 1);
    }
    
    public Artifact getArtifact(final Workspace w, final String name) throws NotFoundException {
        Artifact a = (Artifact) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = getQueryManager(session);
                StringBuilder sb = new StringBuilder();
                sb.append("//element(*, galaxy:artifact)[@name='")
                  .append(name)
                  .append("']");
                
                Query query = qm.createQuery(sb.toString(), Query.XPATH);
                
                QueryResult result = query.execute();
                NodeIterator nodes = result.getNodes();
                
                if (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    JcrArtifact artifact = new JcrArtifact(w, node, localWorkspaceManager);
                    
                    try {
                        accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
                    } catch (AccessException e) {
                        throw new RuntimeException(e);
                    }
                    
                    artifact.setContentHandler(contentService.getContentHandler(artifact.getContentType()));

                    return artifact;
                }
                return null;
            }
        });
        
        if (a == null) {
            throw new NotFoundException(name);
        }
        
        return a;
    }

    private Object executeAndDewrapWithDuplicate(JcrCallback jcrCallback) 
        throws RegistryException, DuplicateItemException {
        try {
            return execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof DuplicateItemException) {
                throw (DuplicateItemException) cause;
            } else {
                throw e;
            }
        }
    }

    private Object executeWithNotFound(JcrCallback jcrCallback) 
        throws RegistryException, NotFoundException, AccessException {
        try {
            return execute(jcrCallback);
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
    
    private Object executeWithQueryException(JcrCallback jcrCallback) 
        throws RegistryException, QueryException {
        try {
            return execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof QueryException) {
                throw (QueryException) cause;
            } else {
                throw e;
            }
        }
    }
    
    public void save(Artifact artifact) throws RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, artifact);
        
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                // TODO: Fix artifact saving, we should have to call artifact.save().
                session.save();
                return null;
            }
        });
    }

    public void move(final Artifact artifact, final String workspaceId) throws RegistryException, AccessException, NotFoundException {
        final Workspace workspace = getWorkspace(workspaceId);
        
        accessControlManager.assertAccess(Permission.MODIFY_WORKSPACE, workspace);
        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, artifact);
        
        if (artifact.getParent().getId().equals(workspaceId)) {
            return;
        }

        final String oldPath = artifact.getPath();

        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                Node aNode = ((JcrArtifact) artifact).getNode();
                Node wNode = ((JcrWorkspace) workspace).getNode();

                final String newPath = wNode.getPath() + "/" + aNode.getName();
                session.move(aNode.getPath(), newPath);

                session.save();
                ((JcrArtifact) artifact).setWorkspace(workspace);
                return null;
            }
        });

        EntryMovedEvent event = new EntryMovedEvent(oldPath, artifact.getPath());
        event.setUser(SecurityUtils.getCurrentUser());
        eventManager.fireEvent(event);
    }

    
    private QueryManager getQueryManager(Session session) throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }

    public SearchResults search(String queryString, int startOfResults, int maxResults) throws RegistryException, QueryException {
        org.mule.galaxy.query.Query q = org.mule.galaxy.query.Query.fromString(queryString);

        q.setStart(startOfResults);
        q.setMaxResults(maxResults);
        return search(q);
    }

    public SearchResults search(final org.mule.galaxy.query.Query query) 
        throws RegistryException, QueryException {
        return (SearchResults) executeWithQueryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                        
                QueryManager qm = getQueryManager(session);
                
                Set<Object> artifacts = new HashSet<Object>();
                
                boolean av = false;
                Class selectType = query.getSelectType();
                if (selectType.equals(ArtifactVersion.class) || selectType.equals(EntryVersion.class)) {
                    av = true;
                } else if (!selectType.equals(Artifact.class) && !selectType.equals(Entry.class)) {
                    throw new RuntimeException(new QueryException(new Message("INVALID_SELECT_TYPE", BundleUtils.getBundle(getClass()), selectType.getName())));
                }
                
                Map<FunctionCall, AbstractFunction> functions = new HashMap<FunctionCall, AbstractFunction>();
                
                String qstr = null;
                try {
                    qstr = createQueryString(query, av, functions);
                } catch (QueryException e) {
                    // will be dewrapped later
                    throw new RuntimeException(e);
                }
                
                if (log.isDebugEnabled())
                {
                    log.debug("Query: " + qstr.toString());
                }
                
                Query jcrQuery = qm.createQuery(qstr, Query.XPATH);
                
                QueryResult result = jcrQuery.execute();
                NodeIterator nodes = result.getNodes(); 
               
                if (query.getStart() != -1) {
                    if (nodes.getSize() <= query.getStart()) {
                        return new SearchResults(0, artifacts);
                    } else {
                        nodes.skip(query.getStart());
                    }
                }
                
                int max = query.getMaxResults();
                int count = 0;
                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    
                    // UGH: jackrabbit does not support parent::* xpath expressions
                    // so we need to traverse the hierarchy to find the right node
                    if (av) {
                        Node artifactNode = node;
                        while (!artifactNode.getPrimaryNodeType().getName().equals(JcrWorkspaceManager.ARTIFACT_NODE_TYPE)) {
                            artifactNode = node.getParent();
                        }
                        JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(localWorkspaceManager, artifactNode.getParent()), 
                                                               artifactNode, 
                                                               localWorkspaceManager);
                        try {
                            accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);

                            setupContentHandler(artifact);
                            artifacts.add(new JcrVersion(artifact, node));
                        } catch (AccessException e) {
                            // don't include artifacts the user can't read in the search
                        }
                    } else if (Entry.class.equals(query.getSelectType())) {
                	while (!node.getPrimaryNodeType().getName().equals(JcrWorkspaceManager.ENTRY_NODE_TYPE)) {
                            node = node.getParent();
                        }
                	JcrEntry artifact = new JcrEntry(new JcrWorkspace(localWorkspaceManager, node.getParent()),
                                                         node, localWorkspaceManager);

                        try {
                            accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);

                            artifacts.add(artifact);
                        } catch (AccessException e) {
                            // don't include artifacts the user can't read in the search
                        }
                    } else {
                        while (!node.getPrimaryNodeType().getName().equals(JcrWorkspaceManager.ARTIFACT_NODE_TYPE)) {
                            node = node.getParent();
                        }
                        JcrArtifact artifact = new JcrArtifact(
                                                        new JcrWorkspace(localWorkspaceManager, node.getParent()),
                                                        node, localWorkspaceManager);

                        try {
                            accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);

                            setupContentHandler(artifact);
                            artifacts.add(artifact);
                        } catch (AccessException e) {
                            // don't include artifacts the user can't read in the search
                        }
                    }
                    
                    count++;
                    
                    if (count == max) {
                        break;
                    }
                }                                                   

                // TODO Dan, this for doesn't loop
                for (Map.Entry<FunctionCall, AbstractFunction> e : functions.entrySet()) {
                    if (av) {
                        Set<ArtifactVersion> artifacts2 = new HashSet<ArtifactVersion>();
                        for (Object o : artifacts) {
                            artifacts2.add((ArtifactVersion) o);
                        }
                        e.getValue().modifyArtifactVersions(e.getKey().getArguments(), artifacts2);
                        return new SearchResults(artifacts2.size(), artifacts2);
                    } else {
                        Set<Artifact> artifacts2 = new HashSet<Artifact>();
                        for (Object o : artifacts) {
                            artifacts2.add((Artifact) o);
                        }
                        e.getValue().modifyArtifacts(e.getKey().getArguments(), artifacts2);
                        return new SearchResults(artifacts2.size(), artifacts2);
                    }
                }
                return new SearchResults(nodes.getSize(), artifacts);
            }

        });
    }

    protected String createQueryString(final org.mule.galaxy.query.Query query, 
                                       boolean av, 
                                       Map<FunctionCall, AbstractFunction> functions) throws QueryException {
        StringBuilder base = new StringBuilder();
        
        String type = "galaxy:artifact";
        if (query.getSelectType().equals(Entry.class)) {
            type = "galaxy:entry";
        }
        
        // Search by workspace id, workspace path, or any workspace
        if (query.getWorkspaceId() != null) {
            base.append("//*[@jcr:uuid='")
                .append(trimWorkspaceManagerId(query.getWorkspaceId()))
                .append("'][@jcr:primaryType=\"galaxy:workspace\"]");

            if (query.isWorkspaceSearchRecursive()) {
                base.append("//");
            } else {
                base.append("/");
            }
            
            base.append("element(*, ").append(type).append(")");
        } else if (query.getWorkspacePath() != null && !"".equals(query.getWorkspacePath())) {
            String path = query.getWorkspacePath();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            
            base.append("//")
            .append(ISO9075.encode(path))
            .append("[@jcr:primaryType=\"galaxy:workspace\"]");
            
            if (query.isWorkspaceSearchRecursive()) {
                base.append("//");
            } else {
                base.append("/");
            }
            
            base.append("element(*, ").append(type).append(")");
        } else {
            base.append("//element(*, ").append(type).append(")");
        }
        
        StringBuilder avQuery = new StringBuilder();
        StringBuilder artifactQuery = new StringBuilder();

        for (Restriction r : query.getRestrictions()) {
            if (r instanceof OpRestriction) {
                handleOperator((OpRestriction) r, artifactQuery, avQuery);
            } else if (r instanceof FunctionCall) {
                handleFunction((FunctionCall) r, functions, artifactQuery, avQuery);
            }
        }
        
        if (avQuery.length() > 0) avQuery.append("]");
        if (artifactQuery.length() > 0) artifactQuery.append("]");

        // Search the latest if we're searching for artifacts, otherwise
        // search all versions
        if (!av) {
            avQuery.insert(0, "/*[@latest='true']");
        } else {
            avQuery.insert(0, "/*");
        }
        
        base.append(artifactQuery);
        base.append(avQuery);
        
        return base.toString();
    }

    private void handleFunction(FunctionCall r, Map<FunctionCall, AbstractFunction> functions, StringBuilder qstr, StringBuilder propStr) throws QueryException {
        AbstractFunction fn = functionRegistry.getFunction(r.getModule(), r.getName());
        
        functions.put(r, fn);
        
        // Narrow down query if possible
        List<OpRestriction> restrictions = fn.getRestrictions(r.getArguments());
        
        if (restrictions != null && restrictions.size() > 0) {
            for (OpRestriction opR : restrictions) {
                handleOperator(opR, qstr, propStr);
            }
        }
    }

    private void handleOperator(OpRestriction or, StringBuilder artifactQuery, StringBuilder avQuery)
        throws QueryException {
        
        // TODO: NOT, LIKE, OR, etc
        String property = (String) or.getLeft();
        boolean not = false;
        Operator operator = or.getOperator();
        
        if (operator.equals(Operator.NOT)) {
            not = true;
            or = (OpRestriction) or.getRight();
            operator = or.getOperator();
            property = or.getLeft().toString();
        }

        QueryBuilder builder = getQueryBuilder(property);
        StringBuilder query;
        // are we searching a property on the artifact itself or the artifact version?
        if (builder.isArtifactProperty()) {
            query = artifactQuery;
        } else {
            query = avQuery;
        }
        
        if (query.length() == 0) {
            query.append("[");
        } else {
            query.append(" and ");
        }
        
        builder.build(query, property, or.getRight(), not, operator);
    }

    private QueryBuilder getQueryBuilder(String property) throws QueryException {
        QueryBuilder qb = queryBuilders.get(property);
        
        if (qb == null) {
            return simpleQueryBuilder;
        }
        
        return qb;
    }

    @SuppressWarnings("unchecked")
    public Set<Link> getReciprocalLinks(final Item item) 
        throws RegistryException {
        
    final JcrRegistryImpl registry = this;

        return (Set<Link>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                        
                QueryManager qm = getQueryManager(session);
                
                StringBuilder qstr = new StringBuilder();
                qstr.append("//element(*, galaxy:link)[jcr:like(@path, '")
                    .append(item.getPath())
                    .append("%')]");
                
                Set<Link> links = new HashSet<Link>();
                
                Query query = qm.createQuery(qstr.toString(), Query.XPATH);
                
                QueryResult result = query.execute();
                
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    final Node linkNode = nodes.nextNode();
                    
                    // dependencies->version->artifact
                    Node versionNode = linkNode.getParent().getParent();
                    final Node artifactNode = versionNode.getParent();
                    
                    JcrWorkspace workspace = new JcrWorkspace(localWorkspaceManager, artifactNode.getParent());
                    JcrArtifact art = new JcrArtifact(workspace, artifactNode, localWorkspaceManager);
                    JcrVersion version = new JcrVersion(art, versionNode);
                    
                    links.add(new LinkImpl(version, linkNode, registry));
                }
                return links;
            }

        });
    }

    public PropertyDescriptor getPropertyDescriptorByName(final String propertyName) {
        
        return (PropertyDescriptor) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                List<PropertyDescriptor> pds = propertyDescriptorDao.find("property", propertyName);
                
                if (pds.size() == 0) {
                    return null;
                }
                return pds.get(0);
            }
        });
    }

    public PropertyDescriptor getPropertyDescriptor(final String id) throws NotFoundException {
        return propertyDescriptorDao.get(id);
    }
    
    public Collection<PropertyDescriptor> getPropertyDescriptors() throws RegistryException {
        return propertyDescriptorDao.listAll();
    }

    public void savePropertyDescriptor(PropertyDescriptor pd) throws RegistryException, AccessException, DuplicateItemException, NotFoundException {
        accessControlManager.assertAccess(Permission.MANAGE_PROPERTIES);
        propertyDescriptorDao.save(pd);
    }
    
    public void deletePropertyDescriptor(String id) throws RegistryException {
        propertyDescriptorDao.delete(id);
    }
    
    public void initialize() throws Exception {
        
        Session session = getSessionFactory().getSession();
        Node root = session.getRootNode();
        
        Node workspaces = JcrUtil.getOrCreate(root, "workspaces", "galaxy:noSiblings");
        
        workspacesId = workspaces.getUUID();
        indexesId = JcrUtil.getOrCreate(root, "indexes").getUUID();
        artifactTypesId = JcrUtil.getOrCreate(root, "artifactTypes").getUUID();

        NodeIterator nodes = workspaces.getNodes();
        // ignore the system node
        if (nodes.getSize() == 0) {
            Node node = workspaces.addNode(settings.getDefaultWorkspaceName(),
                                           "galaxy:workspace");
            node.addMixin("mix:referenceable");

            JcrWorkspace w = new JcrWorkspace(localWorkspaceManager, node);
            w.setName(settings.getDefaultWorkspaceName());

            workspaces.setProperty(REPOSITORY_LAYOUT_VERSION, "2");
        } 
        id = workspaces.getUUID();
        
        session.save();
        
        for (ContentHandler ch : contentService.getContentHandlers()) {
            ch.setRegistry(this);
        }
        
        for (ArtifactPolicy a : policyManager.getPolicies()) {
            a.setRegistry(this);
        }
        
        session.logout();
        
        idToWorkspaceManager.put(localWorkspaceManager.getId(), localWorkspaceManager);
    }

    public void addLinks(Item artifactVersion, final LinkType type, final Item... toLinkTo)
        throws RegistryException {
        final AbstractJcrItem jcrItem = (AbstractJcrItem) artifactVersion;
        if (toLinkTo != null) {
            execute(new JcrCallback() {
                public Object doInJcr(Session session) throws IOException, RepositoryException {
                    jcrItem.addLinks(toLinkTo, false, type);
                    session.save();
                    return null;
                }
            });
        }
    }
    
    public void removeLinks(final Link... links)
        throws RegistryException {
        if (links != null) {
            execute(new JcrCallback() {
                public Object doInJcr(Session session) throws IOException, RepositoryException {
                    for (Link l : links) {
                    ((LinkImpl) l).getNode().remove();
                    }
                    
                    session.save();
                    return null;
                }
            });
        }
    }
    
    public void setQueryBuilders(List<QueryBuilder> qbs) {
        queryBuilders = new HashMap<String, QueryBuilder>();
        for (QueryBuilder q : qbs) {
            for (String prop : q.getProperties()) {
                queryBuilders.put(prop, q);
            }
        }
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public void setFunctionRegistry(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public LifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }

    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setPropertyDescriptorDao(Dao<PropertyDescriptor> propertyDescriptorDao) {
        this.propertyDescriptorDao = propertyDescriptorDao;
    }

    public Dao<LinkType> getLinkTypeDao() {
        return linkTypeDao;
    }

    public void setLinkTypeDao(Dao<LinkType> linkTypeDao) {
        this.linkTypeDao = linkTypeDao;
    }
    
    public void setLocalWorkspaceManager(JcrWorkspaceManager localWorkspaceManager) {
        this.localWorkspaceManager = localWorkspaceManager;
        localWorkspaceManager.setRegistry(this);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(final EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
