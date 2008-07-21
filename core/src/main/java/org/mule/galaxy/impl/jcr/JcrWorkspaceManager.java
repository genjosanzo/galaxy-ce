package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ArtifactTypeDao;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.LinkType;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.ItemCreatedEvent;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.event.ItemVersionCreatedEvent;
import org.mule.galaxy.event.ItemVersionDeletedEvent;
import org.mule.galaxy.event.WorkspaceDeletedEvent;
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
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.SecurityUtils;
import org.mule.galaxy.workspace.WorkspaceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JcrWorkspaceManager extends JcrTemplate implements WorkspaceManager {

    public static final String ID = "local";
    
    public static final String ARTIFACT_NODE_TYPE = "galaxy:artifact";
    
    public static final String ARTIFACT_VERSION_NODE_TYPE = "galaxy:artifactVersion";

    public static final String ENTRY_NODE_TYPE = "galaxy:entry";
    
    public static final String ENTRY_VERSION_NODE_TYPE = "galaxy:entryVersion";
    
    public static final String LATEST = "latest";
    
    private final Log log = LogFactory.getLog(getClass());
    
    private CommentManager commentManager;
    
    private ContentService contentService;

    private LifecycleManager lifecycleManager;
    
    private PolicyManager policyManager;
    
    private UserManager userManager;

    private IndexManager indexManager;
    
    private Dao<LinkType> linkTypeDao;

    private ActivityManager activityManager;
    
    private AccessControlManager accessControlManager;

    private EventManager eventManager;
    
    private ArtifactTypeDao artifactTypeDao;
    
    private Registry registry;

    public String getId() {
        return "local";
    }

    public Workspace getWorkspace(String id) throws RegistryException, AccessException {
        try {
            if (id == null) {
                throw new NullPointerException("Workspace ID cannot be null.");
            }
            
            id = trimWorkspaceManagerId(id);
            
            Node node = getNodeByUUID(id);

            Workspace w = buildWorkspace(node);
            
            accessControlManager.assertAccess(Permission.READ_WORKSPACE, w);
            
            return w;
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }

    private String trimWorkspaceManagerId(String id) {
        int idx = id.indexOf('$');
        if (idx == -1) {
            throw new IllegalStateException("Illegal workspace manager id.");
        }

        return id.substring(idx + 1);
    }

    private Workspace buildWorkspace(Node node) throws RepositoryException {
        return new JcrWorkspace(this, node);
    }

    public Artifact getArtifact(final String id) throws NotFoundException, RegistryException, AccessException {
        if (id == null) {
            throw new NotFoundException("No id specified.");
        }
        return (Artifact) executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(trimWorkspaceManagerId(id));
                Artifact a = buildArtifact(node);
                
                try {
                    accessControlManager.assertAccess(Permission.READ_ARTIFACT, a);
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }

                return a;
            }
        });
    }

    protected Artifact buildArtifact(Node node)
        throws ItemNotFoundException, AccessDeniedException, RepositoryException {
        Node wNode = node.getParent();
        JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(this, wNode), node, this);

        setupContentHandler(artifact);

        return artifact;
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
    
    public Collection<Workspace> getWorkspaces() throws RegistryException, AccessException {
        try {
            List<Workspace> workspaceCol = new ArrayList<Workspace>();
            for (NodeIterator itr = getWorkspacesNode().getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();

                if (!n.getName().equals("jcr:system")) {

                    JcrWorkspace wkspc = new JcrWorkspace(this, n);
                    accessControlManager.assertAccess(Permission.READ_WORKSPACE, wkspc);
                    
                    workspaceCol.add(wkspc);
                }
                
                Collections.sort(workspaceCol, new WorkspaceComparator());
            }
            return workspaceCol;
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }
    
    public Collection<Artifact> getArtifacts(Workspace w) throws RegistryException {
        JcrWorkspace jw = (JcrWorkspace)w;

        Node node = jw.getNode();

        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        try {
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                JcrArtifact artifact = new JcrArtifact(jw, itr.nextNode(), this);
                
                try {
                    accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
                    
                    artifact.setContentHandler(contentService.getContentHandler(artifact.getContentType()));
                    artifacts.add(artifact);
                } catch (AccessException e) {
                    // don't list artifacts which the user doesn't have perms for
                }
            }
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }

        return artifacts;
    }

    private Node getWorkspacesNode() {
        return ((JcrRegistryImpl) registry).getWorkspacesNode();
    }

    public EntryResult newVersion(Artifact artifact, 
                                     Object data, 
                                     String versionLabel, 
                                     User user)
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return newVersion(artifact, null, data, versionLabel, user);
    }

    public EntryResult newVersion(final Artifact artifact, 
                                     final InputStream inputStream, 
                                     final String versionLabel, 
                                     final User user) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return newVersion(artifact, inputStream, null, versionLabel, user);
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
                                        final String versionLabel, 
                                        final User user) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, artifact);
        
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

                ContentHandler ch = contentService.getContentHandler(jcrArtifact.getContentType());
                
                // create a new version node
                Node versionNode = artifactNode.addNode(versionLabel, ARTIFACT_VERSION_NODE_TYPE);
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
                    
                    Lifecycle lifecycle = jcrArtifact.getParent().getDefaultLifecycle();
                    next.setPhase(lifecycle.getInitialPhase());        
                    
                    ch.addMetadata(next);
                    
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

                    EntryResult result = approve(session, artifact, previousLatest, next, user);

                    // fire the event
                    ItemVersionCreatedEvent event = new ItemVersionCreatedEvent(
                            result.getEntry().getPath(), result.getEntryVersion().getVersionLabel());
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
    
    public EntryResult createArtifact(Workspace workspace, 
                                         String contentType, 
                                         String name,
                                         String versionLabel, 
                                         InputStream inputStream, 
                                         User user) 
        throws RegistryException, PolicyException, IOException, MimeTypeParseException, DuplicateItemException, AccessException {
        accessControlManager.assertAccess(Permission.READ_ARTIFACT);
        contentType = trimContentType(contentType);
        MimeType ct = new MimeType(contentType);

        return createArtifact(workspace, inputStream, null, name, versionLabel, ct, user);
    }

    public EntryResult createArtifact(Workspace workspace, Object data, String versionLabel, User user) 
        throws RegistryException, PolicyException, MimeTypeParseException, DuplicateItemException, AccessException {
        accessControlManager.assertAccess(Permission.READ_ARTIFACT);

        ContentHandler ch = contentService.getContentHandler(data.getClass());
        
        if (ch == null) {
            throw new RegistryException(new Message("UNKNOWN_TYPE", BundleUtils.getBundle(getClass()), data.getClass()));
        }
        
        MimeType ct = ch.getContentType(data);
        String name = ch.getName(data);
        
        return createArtifact(workspace, null, data, name, versionLabel, ct, user);
    }

    public EntryResult createArtifact(final Workspace workspace, 
                                      final InputStream is, 
                                      final Object data,
                                      final String name, 
                                      final String versionLabel,
                                      final MimeType contentType,
                                      final User user)
        throws RegistryException, PolicyException, DuplicateItemException {
        
        if (user == null) {
            throw new NullPointerException("User cannot be null.");
        }
        if (name == null) {
            throw new NullPointerException("Artifact name cannot be null.");
        }
        
        final JcrWorkspaceManager registry = this;
        return (EntryResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node workspaceNode = ((JcrWorkspace)workspace).getNode();
                Node artifactNode;
                try {
                    artifactNode = workspaceNode.addNode(ISO9075.encode(name), ARTIFACT_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                artifactNode.addMixin("mix:referenceable");
                Node versionNode = artifactNode.addNode(versionLabel, ARTIFACT_VERSION_NODE_TYPE);
                versionNode.addMixin("mix:referenceable");

                // set the version as a property so we can search via it as local-name() isn't supported.
                // See JCR-696
                versionNode.setProperty(JcrVersion.VERSION, versionLabel);
                
                JcrArtifact artifact = new JcrArtifact(workspace, artifactNode, registry);
                artifact.setName(name);
                
                ContentHandler ch = initializeContentHandler(artifact, name, contentType);
                
                // set up the initial version
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
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
                    ch = contentService.getContentHandler(artifact.getDocumentType());
                }
    
                jcrVersion.setAuthor(user);
                jcrVersion.setLatest(true);
                jcrVersion.setDefault(true);
                
                try {
                    Set<Item> dependencies = ch.detectDependencies(loadedData, workspace);
                    
                    jcrVersion.addLinks(dependencies, true, linkTypeDao.get(LinkType.DEPENDS));
                    
                    Lifecycle lifecycle = workspace.getDefaultLifecycle();
                    jcrVersion.setPhase(lifecycle.getInitialPhase());                    
                    
                    List<EntryVersion> versions = new ArrayList<EntryVersion>();
                    versions.add(jcrVersion);
                    artifact.setVersions(versions);

                    if (log.isDebugEnabled())
                    {
                        log.debug("Created artifact " + artifact.getId());
                    }

                    EntryResult result = approve(session, artifact, null, jcrVersion, user);

                    // fire the event
                    ItemCreatedEvent event = new ItemCreatedEvent(result.getEntryVersion().getPath());
                    event.setUser(SecurityUtils.getCurrentUser());
                    eventManager.fireEvent(event);

                    return result;
                } catch (RegistryException e) {
                    // gets unwrapped by executeAndDewrap
                    throw new RuntimeException(e);
                } catch (NotFoundException e) {
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
                            ch = contentService.getContentHandler(q);
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
            ch = contentService.getContentHandler(contentType);
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
        ContentHandler ch = contentService.getContentHandler(contentType);

        if (ch == null) {
            throw new RegistryException(new Message("UNSUPPORTED_CONTENT_TYPE", BundleUtils.getBundle(getClass()), contentType));
        }

        return ch.read(inputStream, workspace);
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
                Node artifactNode;
                try {
                    artifactNode = workspaceNode.addNode(ISO9075.encode(name), ENTRY_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                artifactNode.addMixin("mix:referenceable");
                Node versionNode = artifactNode.addNode(versionLabel, ENTRY_VERSION_NODE_TYPE);
                versionNode.addMixin("mix:referenceable");

                // set the version as a property so we can search via it as local-name() isn't supported.
                // See JCR-696
                versionNode.setProperty(JcrVersion.VERSION, versionLabel);
                
                JcrArtifact artifact = new JcrArtifact(workspace, artifactNode, registry);
                artifact.setName(name);
                
                // set up the initial version
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.LATEST, true);
                versionNode.setProperty(JcrVersion.ENABLED, true);
                
                JcrEntryVersion version = new JcrEntryVersion(artifact, versionNode);
    
                version.setAuthor(user);
                version.setLatest(true);
                version.setDefault(true);
                
                Lifecycle lifecycle = workspace.getDefaultLifecycle();
                version.setPhase(lifecycle.getInitialPhase());                    
                
                List<EntryVersion> versions = new ArrayList<EntryVersion>();
                versions.add(version);
                artifact.setVersions(versions);

                if (log.isDebugEnabled())
                {
                    log.debug("Created artifact " + artifact.getId());
                }

                List<ApprovalMessage> approvals = approve(null, version);

                // save this so the indexer will work
                session.save();

                // fire the event
//                    ItemCreatedEvent event = new ItemCreatedEvent(result.getEntryVersion().getPath());
//                    event.setUser(SecurityUtils.getCurrentUser());
//                    eventManager.fireEvent(event);

                return new EntryResult(artifact, version, approvals);
            }
        });
    }

    private EntryResult approve(Session session, 
                                Artifact artifact, 
                                JcrVersion previous, 
                                JcrVersion next,
                                User user)
        throws RegistryException, RepositoryException {
        List<ApprovalMessage> approvals = approve(previous, next);

        // save this so the indexer will work
        session.save();
        
        // index in a separate thread
        indexManager.index(next);
        
        // save the "we're indexing" flag
        session.save();
        
        return new EntryResult(artifact, next, approvals);
    }

    private List<ApprovalMessage> approve(EntryVersion previous, EntryVersion next) {
        boolean approved = true;
        
        List<ApprovalMessage> approvals = policyManager.approve(previous, next);
        for (ApprovalMessage a : approvals) {
            if (!a.isWarning()) {
                approved = false;
                break;
            }
        }
        
        if (!approved) {
            throw new RuntimeException(new PolicyException(approvals));
        }
        return approvals;
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
    

    public void delete(Item item) throws RegistryException, AccessException {
        if (item instanceof ArtifactVersion) {
            delete((ArtifactVersion) item);
        } else
            if (item instanceof Artifact) {
                delete((Artifact) item);
            } else
                if (item instanceof Workspace) {
                    delete((Workspace) item);
                } else
                    if (item instanceof Entry) {
                        //delete((ArtifactVersion) item);
                    } else
                        if (item instanceof EntryVersion) {
                            //delete((ArtifactVersion) item);
                        }
    }

    public void delete(final ArtifactVersion version) throws RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.DELETE_ARTIFACT, version.getParent());

        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Artifact artifact = (Artifact) version.getParent();

                    if (artifact.getVersions().size() == 1) {
                        delete(artifact);
                        return null;
                    }
                    
                    artifact.getVersions().remove(version);
                    
                    if (((JcrVersion)version).isLatest()) {
                        JcrVersion newLatest = (JcrVersion) artifact.getVersions().get(0);
                        
                        newLatest.setLatest(true);
                    }
                    
                    String label = version.getVersionLabel();
    
                    ((JcrVersion) version).getNode().remove();

                    final String path = artifact.getPath();
    
                    session.save();

                    ItemVersionDeletedEvent event = new ItemVersionDeletedEvent(path, label);
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

    public void delete(final Artifact artifact) throws RegistryException, AccessException {
        accessControlManager.assertAccess(Permission.DELETE_ARTIFACT, artifact);

        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                String path = artifact.getPath();
                ((JcrArtifact) artifact).getNode().remove();

                session.save();

                ItemDeletedEvent event = new ItemDeletedEvent(path);
                event.setUser(SecurityUtils.getCurrentUser());
                eventManager.fireEvent(event);

                return null;
            }
        });
    }
    
    public void setEnabled(final EntryVersion version, 
                           final boolean enabled) throws RegistryException,
        PolicyException {
        executeWithPolicy(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                if (enabled && version instanceof ArtifactVersion) {
                    approve((ArtifactVersion) version.getPrevious(), (ArtifactVersion) version);
                }
                
                ((JcrVersion) version).setEnabledInternal(enabled);
                
                session.save();
                return null;
            }
        });
        
    }

    public void setDefaultVersion(final EntryVersion version) throws RegistryException,
        PolicyException {
        execute(new JcrCallback() {
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
                String path = wkspc.getPath();

                ((JcrWorkspace) wkspc).getNode().remove();

                session.save();

                WorkspaceDeletedEvent evt = new WorkspaceDeletedEvent(path);
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
            return execute(jcrCallback);
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
            return execute(jcrCallback);
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
    
    private Object executeAndDewrap(JcrCallback jcrCallback)
        throws RegistryException, PolicyException, DuplicateItemException {
        try {
            return execute(jcrCallback);
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
        
    public LifecycleManager getLifecycleManager(Workspace w) {
        return lifecycleManager;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
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

    public CommentManager getCommentManager() {
        return commentManager;
    }

    public void setCommentManager(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    public Dao<LinkType> getLinkTypeDao() {
        return linkTypeDao;
    }

    public void setLinkTypeDao(Dao<LinkType> linkTypeDao) {
        this.linkTypeDao = linkTypeDao;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
