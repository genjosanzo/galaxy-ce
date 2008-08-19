package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ArtifactTypeDao;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.EntryCreatedEvent;
import org.mule.galaxy.event.EntryDeletedEvent;
import org.mule.galaxy.event.EntryVersionCreatedEvent;
import org.mule.galaxy.event.EntryVersionDeletedEvent;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.WorkspaceCreatedEvent;
import org.mule.galaxy.event.WorkspaceDeletedEvent;
import org.mule.galaxy.impl.lifecycle.LifecycleExtension;
import org.mule.galaxy.impl.link.LinkExtension;
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
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.DateUtil;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.SecurityUtils;
import org.mule.galaxy.workspace.WorkspaceManager;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JcrWorkspaceManager extends AbstractWorkspaceManager implements WorkspaceManager {

    public static final String ID = "local";
    
    public static final String ARTIFACT_NODE_TYPE = "galaxy:artifact";
    
    public static final String ARTIFACT_VERSION_NODE_TYPE = "galaxy:artifactVersion";

    public static final String ENTRY_NODE_TYPE = "galaxy:entry";
    
    public static final String ENTRY_VERSION_NODE_TYPE = "galaxy:entryVersion";

    public static final String WORKSPACE_NODE_TYPE = "galaxy:workspace";
    
    public static final String ATTACHED_WORKSPACE_NODE_TYPE = "galaxy:attachedWorkspace";

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

    private Workspace buildWorkspace(Node node) throws RepositoryException {
        return new JcrWorkspace(this, node);
    }

    protected Artifact buildArtifact(Node node)
        throws ItemNotFoundException, AccessDeniedException, RepositoryException {
        Node wNode = node.getParent();
        
        return new JcrArtifact(new JcrWorkspace(this, wNode), node, this);
    }
    
    public Collection<Workspace> getWorkspaces() {
        return getWorkspaces(null);
    }

    public Collection<Workspace> getWorkspaces(Workspace workspace) {
        try {
            Node workspacesNode;
            if (workspace == null) {
                workspacesNode = getWorkspacesNode();
            } else {
                workspacesNode = ((JcrWorkspace) workspace).getNode();
            }
            ArrayList<Workspace> workspaceCol = new ArrayList<Workspace>();
            for (NodeIterator itr = workspacesNode.getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();

                String type = n.getPrimaryNodeType().getName();
                if (type.equals(WORKSPACE_NODE_TYPE)) {
                    workspaceCol.add(buildWorkspace(n));
                } else if (type.equals(ATTACHED_WORKSPACE_NODE_TYPE)) {
                    workspaceCol.add(buildAttachedWorkspace(n));
                }
                
                Collections.sort(workspaceCol, new ItemComparator());
            }
            return workspaceCol;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Workspace buildAttachedWorkspace(Node n) throws RepositoryException {
        return new AttachedWorkspace(n, this, null);
    }

    public Item getItemById(final String id) throws NotFoundException, RegistryException, AccessException {
        return (Item) executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(trimWorkspaceManagerId(id));
                
                try {
                    String type = node.getPrimaryNodeType().getName();
                    
                    return build(node, type);
                } catch (AccessException e){
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

    protected Item build(Node node) throws RepositoryException, ItemNotFoundException, AccessException {
        return build(node, node.getPrimaryNodeType().getName());
    }
    
    protected Item build(Node node, String type) throws RepositoryException, ItemNotFoundException,
        AccessException {
        if (type.equals("galaxy:artifact")) {
            Artifact a = buildArtifact(node);
    
            accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
            
            return a;
        } else if (type.equals("galaxy:artifactVersion")) {
            Artifact a = buildArtifact(node.getParent());
    
            accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
            
            return a.getVersion(node.getName());
        } else if (type.equals("galaxy:entry")) {
            Entry a = buildEntry(node);
    
            accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
            
            return a;
        } else if (type.equals("galaxy:entryVersion")) {
            Entry a = buildEntry(node.getParent());
    
            accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
            
            return a.getVersion(node.getName());
        } else {
             Workspace wkspc = buildWorkspace(node);
             
             accessControlManager.assertAccess(Permission.READ_WORKSPACE, wkspc);
             
             return wkspc;
        }
    }
    
    protected JcrEntry buildEntry(Node node) throws RepositoryException, ItemNotFoundException,
        AccessDeniedException {
        return new JcrEntry(new JcrWorkspace(this, node.getParent()), node, this);
    }
    
    private Node getWorkspacesNode() {
        return ((JcrRegistryImpl) registry).getWorkspacesNode();
    }

    public EntryResult newVersion(Artifact artifact, 
                                     Object data, 
                                     String versionLabel)
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return newVersion(artifact, null, data, versionLabel);
    }

    public EntryResult newVersion(final Artifact artifact, 
                                     final InputStream inputStream, 
                                     final String versionLabel) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return newVersion(artifact, inputStream, null, versionLabel);
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
    
    protected EntryResult newVersion(final Artifact artifact, 
                                        final InputStream inputStream, 
                                        final Object data,
                                        final String versionLabel) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, artifact);
        
        final User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new NullPointerException("User cannot be null!");
        }
        
        return (EntryResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                JcrArtifact jcrArtifact = (JcrArtifact) artifact;
                Node artifactNode = jcrArtifact.getNode();
                artifactNode.refresh(false);
                JcrVersion previousLatest = ((JcrVersion)jcrArtifact.getDefaultOrLastVersion());
                Node previousNode = previousLatest.getNode();
                
                previousLatest.setDefault(false);
                previousLatest.setLatest(false);

                ContentHandler ch = getContentService().getContentHandler(jcrArtifact.getContentType());
                
                // create a new version node
                Node versionNode;
                try {
                    versionNode = artifactNode.addNode(versionLabel, ARTIFACT_VERSION_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(versionLabel));
                }
                versionNode.addMixin("mix:referenceable");

                // set the version as a property so we can search via it as local-name() isn't supported.
                // See JCR-696
                versionNode.setProperty(JcrVersion.VERSION, versionLabel);
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.ENABLED, true);
                
                Node resNode = createVersionContentNode(versionNode, 
                                                        inputStream, 
                                                        jcrArtifact.getContentType(), 
                                                        now);
                
                JcrVersion next = new JcrVersion(jcrArtifact, versionNode, resNode);
                next.setDefault(true);
                next.setLatest(true);
                
                try {
                    // Store the data
                    if (inputStream != null) {
                        InputStream s = next.getStream();
                        Object data = getData((Workspace)artifact.getParent(), artifact.getContentType(), s);
                        next.setData(data);
                    } else {
                        next.setData(data);
                        resNode.setProperty(JcrVersion.JCR_DATA, ch.read(data));
                    }
                    
                    next.setAuthor(user);
                    next.setLatest(true);
                    
                    // Add it as the most recent version
                    jcrArtifact.getVersions().add(next);
                    
                    initializeLifecycle(jcrArtifact, next);        
                    
                    ch.addMetadata(next);
                    
                    copyProperties(previousNode, versionNode);

                    EntryResult result = approve(session, artifact, previousLatest, next, user);

                    // fire the event
                    final EntryVersion entryVersion = result.getEntryVersion();
                    EntryVersionCreatedEvent event = new EntryVersionCreatedEvent(entryVersion);
                    event.setUser(SecurityUtils.getCurrentUser());
                    eventManager.fireEvent(event);

                    return result;
                } catch (RegistryException e) {
                    // this will get dewrapped
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected void initializeLifecycle(Entry entry, EntryVersion v) {
        Lifecycle lifecycle = entry.getParent().getDefaultLifecycle();
        
        for (PropertyDescriptor pd : entry.getType().getProperties()) {
            if (pd.getExtension() instanceof LifecycleExtension) {
                try {
                    v.setProperty(pd.getProperty(), lifecycle.getInitialPhase());
                } catch (PropertyException e) {
                    throw new RuntimeException(e);
                } catch (PolicyException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public EntryResult newVersion(final Entry entry, final String versionLabel) 
    	throws RegistryException, PolicyException, DuplicateItemException, AccessException {
	accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, entry);
        
	final User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new NullPointerException("User cannot be null!");
        }
        
        return (EntryResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                JcrEntry jcrArtifact = (JcrEntry) entry;
                Node artifactNode = jcrArtifact.getNode();
                artifactNode.refresh(false);
                JcrEntryVersion previousLatest = ((JcrEntryVersion)jcrArtifact.getDefaultOrLastVersion());
                Node previousNode = previousLatest.getNode();
                
                previousLatest.setDefault(false);
                previousLatest.setLatest(false);

                // create a new version node
                Node versionNode;
                try {
                    versionNode = artifactNode.addNode(versionLabel, ENTRY_VERSION_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(versionLabel));
                }
                versionNode.addMixin("mix:referenceable");

                // set the version as a property so we can search via it as local-name() isn't supported.
                // See JCR-696
                versionNode.setProperty(JcrVersion.VERSION, versionLabel);
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.ENABLED, true);
                
                JcrEntryVersion next = new JcrEntryVersion(jcrArtifact, versionNode);
                next.setDefault(true);
                next.setLatest(true);
                next.setAuthor(user);
                next.setLatest(true);
                
                // Add it as the most recent version
                jcrArtifact.getVersions().add(next);
                
                initializeLifecycle(entry, next);
                
                copyProperties(previousNode, versionNode);
                
                Map<Item, List<ApprovalMessage>> approvals = approve(next);

                session.save();

                EntryResult result = new EntryResult(entry, next, approvals);

                // fire the event
                final EntryVersion entryVersion = result.getEntryVersion();
                EntryVersionCreatedEvent event = new EntryVersionCreatedEvent(entryVersion);
                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return result;
            }
        });
    }
    
    public EntryResult createArtifact(Workspace workspace, 
                                         String contentType, 
                                         String name,
                                         String versionLabel, 
                                         InputStream inputStream) 
        throws RegistryException, PolicyException, IOException, MimeTypeParseException, DuplicateItemException, AccessException {
        accessControlManager.assertAccess(Permission.READ_ARTIFACT);
        contentType = trimContentType(contentType);
        MimeType ct = new MimeType(contentType);

        return createArtifact(workspace, inputStream, null, name, versionLabel, ct);
    }

    public EntryResult createArtifact(Workspace workspace, Object data, String versionLabel) 
        throws RegistryException, PolicyException, MimeTypeParseException, DuplicateItemException, AccessException {
        accessControlManager.assertAccess(Permission.READ_ARTIFACT);

        ContentHandler ch = getContentService().getContentHandler(data.getClass());
        
        if (ch == null) {
            throw new RegistryException(new Message("UNKNOWN_TYPE", BundleUtils.getBundle(getClass()), data.getClass()));
        }
        
        MimeType ct = ch.getContentType(data);
        String name = ch.getName(data);
        
        return createArtifact(workspace, null, data, name, versionLabel, ct);
    }

    public EntryResult createArtifact(final Workspace workspace, 
                                      final InputStream is, 
                                      final Object data,
                                      final String name, 
                                      final String versionLabel,
                                      final MimeType contentType)
        throws RegistryException, PolicyException, DuplicateItemException {
        
        final User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new NullPointerException("User cannot be null.");
        }
        if (name == null) {
            throw new NullPointerException("Artifact name cannot be null.");
        }
        
        final JcrWorkspaceManager wm = this;
        return (EntryResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node workspaceNode = ((JcrWorkspace)workspace).getNode();
                Node artifactNode;
                try {
                    artifactNode = workspaceNode.addNode(name, ARTIFACT_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                artifactNode.addMixin("mix:referenceable");

                Node versionNode = artifactNode.addNode(versionLabel, ARTIFACT_VERSION_NODE_TYPE);
                versionNode.addMixin("mix:referenceable");

                // set the version as a property so we can search via it as local-name() isn't supported.
                // See JCR-696
                versionNode.setProperty(JcrVersion.VERSION, versionLabel);
                
                JcrArtifact artifact = new JcrArtifact(workspace, artifactNode, wm);
                artifact.setName(name);
                artifact.setType(typeManager.getDefaultType());
                
                ContentHandler ch = initializeContentHandler(artifact, name, contentType);
                
                // set up the initial version
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                artifactNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.LATEST, true);
                versionNode.setProperty(JcrVersion.ENABLED, true);
                
                Node resNode = createVersionContentNode(versionNode, is, contentType, now);
                
                JcrVersion jcrVersion = new JcrVersion(artifact, versionNode, resNode);

                // Store the data
                Object loadedData = null;
                if (data != null) {
                    jcrVersion.setData(data);
                    InputStream dataStream = ch.read(data);
                    resNode.setProperty("jcr:data", dataStream);
                    loadedData = data;
                } else {
                    InputStream dataStream = jcrVersion.getStream();
                    jcrVersion.setData(ch.read(dataStream, workspace));
                    loadedData = jcrVersion.getData();
                }
                
                if (ch instanceof XmlContentHandler) {
                    XmlContentHandler xch = (XmlContentHandler) ch;
                    artifact.setDocumentType(xch.getDocumentType(loadedData));
                    ch = getContentService().getContentHandler(artifact.getDocumentType());
                }
    
                jcrVersion.setAuthor(user);
                jcrVersion.setLatest(true);
                jcrVersion.setDefault(true);
                
                try {
                    Set<String> dependencies = ch.detectDependencies(loadedData, workspace);
                    Set<Link> links = new HashSet<Link>();
                    for (String p : dependencies) {
                        links.add(new Link(jcrVersion, null, p, true));
                    }
                    
                    if (links.size() > 0) {
                        jcrVersion.setProperty(LinkExtension.DEPENDS, links);
                    }
                    initializeLifecycle(artifact, jcrVersion);            
                    
                    List<EntryVersion> versions = new ArrayList<EntryVersion>();
                    versions.add(jcrVersion);
                    artifact.setVersions(versions);

                    if (log.isDebugEnabled())
                    {
                        log.debug("Created artifact " + artifact.getId());
                    }

                    EntryResult result = approve(session, artifact, null, jcrVersion, user);

                    // fire the event
                    EntryCreatedEvent event = new EntryCreatedEvent(result.getEntry());
                    event.setUser(SecurityUtils.getCurrentUser());
                    eventManager.fireEvent(event);

                    return result;
                } catch (RegistryException e) {
                    // gets unwrapped by executeAndDewrap
                    throw new RuntimeException(e);
                } catch (PropertyException e) {
                    // won't happen
                    throw new RuntimeException(e);
                } catch (PolicyException e) {
                    // won't happen
                    throw new RuntimeException(e);
                }
            }

        });
    }

    protected Node createVersionContentNode(Node versionNode, final InputStream is,
                                            final MimeType contentType, Calendar now)
        throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException,
        VersionException, ConstraintViolationException, RepositoryException, ValueFormatException {
        // these are required since we inherit from nt:file
        Node resNode = versionNode.addNode("jcr:content", "nt:resource");
        resNode.setProperty("jcr:mimeType", contentType.toString());
//        resNode.setProperty("jcr:encoding", "");
        resNode.setProperty("jcr:lastModified", now);

        if (is != null) {
            resNode.setProperty(JcrVersion.JCR_DATA, is);
        }
        return resNode;
    }

    protected ContentHandler initializeContentHandler(JcrArtifact artifact, 
                                                      final String name,
                                                      MimeType contentType) {
        ContentHandler ch = null;
        if ("application/octet-stream".equals(contentType.toString())) {
            String ext = getExtension(name);
            ArtifactType type = artifactTypeDao.getArtifactType(ext);
            
            try {
                if (type == null && "xml".equals(ext)) {
                    contentType = new MimeType("application/xml");
                } else if (type != null) {
                    contentType = new MimeType(type.getContentType());
                    
                    if (type.getDocumentTypes() != null && type.getDocumentTypes().size() > 0) {
                        for (QName q : type.getDocumentTypes()) {
                            ch = getContentService().getContentHandler(q);
                            if (ch != null) {
                                break;
                            }
                        }
                    }
                }
            } catch (MimeTypeParseException e) {
                throw new RuntimeException(e);
            }
        } 
        
        if (ch == null) {
            ch = getContentService().getContentHandler(contentType);
        }
        
        artifact.setContentType(contentType);
        artifact.setContentHandler(ch);
        
        return ch;
    }

    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            return name.substring(idx+1);
        }
        
        return "";
    }

    private Object getData(Workspace workspace, MimeType contentType, InputStream inputStream) 
        throws RegistryException, IOException {
        ContentHandler ch = getContentService().getContentHandler(contentType);

        if (ch == null) {
            throw new RegistryException(new Message("UNSUPPORTED_CONTENT_TYPE", BundleUtils.getBundle(getClass()), contentType));
        }

        return ch.read(inputStream, workspace);
    }

    protected Map<Item, List<ApprovalMessage>> approve(Item item) {
        try {
            return policyManager.approve(item);
        } catch (PolicyException e) {
            throw new RuntimeException(e);
        }
    }

    public EntryResult newEntry(final Workspace workspace, final String name, final String versionLabel)
        throws RegistryException, PolicyException, DuplicateItemException {
        
        final User user = SecurityUtils.getCurrentUser();

        if (user == null) {
            throw new NullPointerException("User cannot be null.");
        }
        if (name == null) {
            throw new NullPointerException("Entry name cannot be null.");
        }
        
        final JcrWorkspaceManager registry = this;
        return (EntryResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node workspaceNode = ((JcrWorkspace)workspace).getNode();
                Node entryNode;
                try {
                    entryNode = workspaceNode.addNode(name, ENTRY_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                entryNode.addMixin("mix:referenceable");
                Node versionNode = entryNode.addNode(versionLabel, ENTRY_VERSION_NODE_TYPE);
                versionNode.addMixin("mix:referenceable");

                // set the version as a property so we can search via it as local-name() isn't supported.
                // See JCR-696
                versionNode.setProperty(JcrVersion.VERSION, versionLabel);
                
                JcrEntry entry = new JcrEntry(workspace, entryNode, registry);
                entry.setName(name);
                entry.setType(typeManager.getDefaultType());
                
                // set up the initial version
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                entryNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.LATEST, true);
                versionNode.setProperty(JcrVersion.ENABLED, true);
                
                JcrEntryVersion version = new JcrEntryVersion(entry, versionNode);
    
                version.setAuthor(user);
                version.setLatest(true);
                version.setDefault(true);
                
                initializeLifecycle(entry, version);
                
                List<EntryVersion> versions = new ArrayList<EntryVersion>();
                versions.add(version);
                entry.setVersions(versions);

                if (log.isDebugEnabled())
                {
                    log.debug("Created artifact " + entry.getId());
                }

                Map<Item, List<ApprovalMessage>> approvals = approve(version);

                // save this so the indexer will work
                session.save();

                // fire the event
                EntryResult result = new EntryResult(entry, version, approvals);
                EntryCreatedEvent event = new EntryCreatedEvent(result.getEntry());
                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return result;
            }
        });
    }

    private EntryResult approve(Session session, 
                                Artifact artifact, 
                                JcrVersion previous, 
                                JcrVersion next,
                                User user)
        throws RegistryException, RepositoryException {
        Map<Item, List<ApprovalMessage>> approvals = approve(next);

        // save this so the indexer will work
        session.save();
        
        // index in a separate thread
        indexManager.index(next);
        
        // save the "we're indexing" flag
        session.save();
        
        return new EntryResult(artifact, next, approvals);
    }

    protected String escapeNodeName(String right) {
        return ISO9075.encode(right);
    }

    private String trimContentType(String contentType) {
        int comma = contentType.indexOf(';');
        if (comma != -1) {
            contentType = contentType.substring(0, comma);
        }
        return contentType;
    }

    public Workspace newWorkspace(final Workspace parent, 
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
                
                JcrWorkspace workspace = new JcrWorkspace(JcrWorkspaceManager.this, node);
                workspace.setName(escapedName);
                workspace.setDefaultLifecycle(lifecycleManager.getDefaultLifecycle());
                workspaces.add(workspace);

                WorkspaceCreatedEvent event = new WorkspaceCreatedEvent(workspace);
                session.save();

                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return workspace;
            }
        });
    }
    public Workspace newWorkspace(final String name) throws DuplicateItemException, RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_WORKSPACE);
        
        return (Workspace) executeAndDewrapWithDuplicate(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node;
                try {
                    node = getWorkspacesNode().addNode(name, "galaxy:workspace");
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                node.addMixin("mix:referenceable");
    
                JcrWorkspace workspace = new JcrWorkspace(JcrWorkspaceManager.this, node);
                workspace.setName(name);
                workspace.setDefaultLifecycle(lifecycleManager.getDefaultLifecycle());
                
                Calendar now = DateUtil.getCalendarForNow();
                node.setProperty(JcrWorkspace.CREATED, now);
                node.setProperty(JcrWorkspace.UPDATED, now);
                
                session.save();

                final WorkspaceCreatedEvent event = new WorkspaceCreatedEvent(workspace);
                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return workspace;
            }
        });
    }

    private Object executeAndDewrapWithDuplicate(JcrCallback jcrCallback) 
        throws RegistryException, DuplicateItemException {
        try {
            return template.execute(jcrCallback);
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
    public void delete(Item item) throws RegistryException, AccessException {
        if (item instanceof EntryVersion) {
            delete((EntryVersion)item);
        } else if (item instanceof Entry) {
            delete((Entry)item);
        } else if (item instanceof Workspace) {
            delete((Workspace)item);
        }
    }

    public void delete(final EntryVersion version) throws RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.DELETE_ARTIFACT, version.getParent());

        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Entry entry = version.getParent();

                    if (entry.getVersions().size() == 1) {
                        delete(entry);
                        return null;
                    }
                    
                    entry.getVersions().remove(version);
                    
                    if (((JcrVersion)version).isLatest()) {
                        JcrVersion newLatest = (JcrVersion) entry.getVersions().get(0);
                        
                        newLatest.setLatest(true);
                    }
                    
                    EntryVersionDeletedEvent event = new EntryVersionDeletedEvent(version);
                    
                    ((JcrVersion) version).getNode().remove();
    
                    session.save();

                    event.setUser(SecurityUtils.getCurrentUser());
                    eventManager.fireEvent(event);

                    return null;

                } catch (RegistryException e) {
                    throw new RuntimeException(e);
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void delete(final Entry entry) throws RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.DELETE_ARTIFACT, entry);

        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                EntryDeletedEvent event = new EntryDeletedEvent(entry);

                ((JcrEntry) entry).getNode().remove();

                session.save();

                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return null;
            }
        });
    }
    
    public void save(Item i) throws AccessException {
        if (i instanceof Workspace) {
            accessControlManager.assertAccess(Permission.MODIFY_WORKSPACE, i);
        } else {
            accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, i);
        }
        
        template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                session.save();
                return null;
            }
            
        });
    }

    public void setEnabled(final EntryVersion version, 
                           final boolean enabled) throws RegistryException,
        PolicyException {
        executeWithPolicy(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                if (enabled && version instanceof EntryVersion) {
                    approve((EntryVersion) version);
                }
                
                ((JcrVersion) version).setEnabledInternal(enabled);
                
                session.save();
                return null;
            }
        });
        
    }

    public void setDefaultVersion(final EntryVersion version) throws RegistryException,
        PolicyException {
        template.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                EntryVersion oldDefault = ((Entry)version.getParent()).getDefaultOrLastVersion();
                
                ((JcrVersion) oldDefault).setDefault(false);
                ((JcrVersion) version).setDefault(true);
                
                session.save();
                return null;
            }
        });
    }
    
    public void delete(final Workspace wkspc) throws RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.DELETE_WORKSPACE);

        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                WorkspaceDeletedEvent evt = new WorkspaceDeletedEvent(wkspc);

                ((JcrWorkspace) wkspc).getNode().remove();

                session.save();

                evt.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(evt);

                return null;
            }
        });
    }
    
    public void attachTo(Workspace workspace) {
        throw new UnsupportedOperationException();
    }

    private Object executeWithPolicy(JcrCallback jcrCallback) 
        throws RegistryException, PolicyException {
        try {
            return template.execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof PolicyException) {
                throw (PolicyException) cause;
            } else {
                throw e;
            }
        }
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
        throws RegistryException, PolicyException, DuplicateItemException {
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
            } else {
                throw e;
            }
        }
    }
        
    public JcrTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JcrTemplate template) {
        this.template = template;
    }

    public LifecycleManager getLifecycleManager(Workspace w) {
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

    private void copyProperties(Node previousNode, Node versionNode) throws RepositoryException,
        ValueFormatException, VersionException, LockException, ConstraintViolationException {
        try {
            Property pNames = previousNode.getProperty(AbstractJcrItem.PROPERTIES);
        
            for (Value name : pNames.getValues()) {
                Property prop = previousNode.getProperty(name.getString());
                
                if (prop.getDefinition().isMultiple()) {
                    versionNode.setProperty(prop.getName(), prop.getValues());
                } else {
                    versionNode.setProperty(prop.getName(), prop.getValue());
                }
            }
            
            versionNode.setProperty(pNames.getName(), pNames.getValues());
        } catch (PathNotFoundException e) {
            // ignore?
        }
    }

    public List<Item> getItems(Workspace w) {
        List<Item> items = new ArrayList<Item>();
        try {
            NodeIterator nodes = ((JcrWorkspace) w).getNode().getNodes();
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
