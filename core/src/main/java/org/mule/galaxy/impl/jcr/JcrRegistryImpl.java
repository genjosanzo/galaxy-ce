package org.mule.galaxy.impl.jcr;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.xml.NodeTypeReader;
import org.apache.jackrabbit.name.QName;
import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.ActivityManager;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Settings;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.ActivityManager.EventType;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.query.Restriction.Operator;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.util.DateUtil;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.UserUtils;
import org.springframework.dao.DataAccessException;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JcrRegistryImpl extends JcrTemplate implements Registry, JcrRegistry {

    private static final String ARTIFACT_NODE_TYPE = "galaxy:artifact";
    public static final String LATEST = "latest";
    private static final String REPOSITORY_LAYOUT_VERSION = "version";
    private static final String NAMESPACE = "http://galaxy.mule.org";

    private Logger LOGGER = LogUtils.getL7dLogger(JcrRegistryImpl.class);
    
    private Settings settings;
    
    private ContentService contentService;

    private LifecycleManager lifecycleManager;
    
    private PolicyManager policyManager;
    
    private UserManager userManager;

    private IndexManager indexManager;
    
    private Dao<PropertyDescriptor> propertyDescriptorDao;
    
    private String workspacesId;

    private String indexesId;

    private String artifactTypesId;
    
    private Session openSession;
    
    private ActivityManager activityManager;
    private String id;
    
    public JcrRegistryImpl() {
        super();
    }

    public String getUUID() {
        return id;
    }

    public Workspace getWorkspace(String id) throws RegistryException {
        try {
            if (id == null) {
                throw new NullPointerException("Workspace ID cannot be null.");
            }
            
            Node node = getNodeByUUID(id);

            return new JcrWorkspace(this, lifecycleManager, node);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }


    public Workspace getWorkspaceByPath(String path) throws RegistryException, NotFoundException {
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
            } catch (RegistryException e) {
                throw new NotFoundException(path);
            }
            
            Node node = wNode.getNode(path);

            if (node.getPrimaryNodeType().getName().equals("galaxy:workspace")) {
                return new JcrWorkspace(this, lifecycleManager, node);
            }
            
            throw new NotFoundException(path);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(e);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }
    
    public Workspace createWorkspace(final String name) throws RegistryException {
        // we should throw an error, but lets be defensive for now
        final String escapedName = JcrUtil.escape(name);
        final JcrRegistryImpl registry = this;
        
        return (Workspace) executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node;
                try {
                    node = getWorkspacesNode().addNode(escapedName, "galaxy:workspace");
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                node.addMixin("mix:referenceable");
    
                JcrWorkspace workspace = new JcrWorkspace(registry, lifecycleManager, node);
                workspace.setName(escapedName);
                workspace.setDefaultLifecycle(lifecycleManager.getDefaultLifecycle());
                
                Calendar now = DateUtil.getCalendarForNow();
                node.setProperty(JcrWorkspace.CREATED, now);
                node.setProperty(JcrWorkspace.UPDATED, now);
                
                session.save();
                
                activityManager.logActivity(UserUtils.getCurrentUser(),
                                            "Workspace " + workspace.getPath() + " was created", 
                                            EventType.INFO);
                return workspace;
            }
        });
    }

    public void save(Workspace w) {
        execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                session.save();
                return null;
            }
            
        });
    }

    public void save(final Workspace w, final String parentId) 
        throws RegistryException, NotFoundException {
        executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                JcrWorkspace jw = (JcrWorkspace) w;
                Node node = jw.getNode();
                
                if (parentId != null && !parentId.equals(node.getParent().getUUID())) {
                    Node parentNode = null;
                    if (parentId != null) {
                        try {
                            parentNode = getNodeByUUID(parentId);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(new NotFoundException(parentId));
                        }
                    } else {
                        parentNode = node.getParent();
                    }
                    
                    // ensure the user isn't trying to move this to a child node of the workspace
                    Node checked = parentNode;
                    while (checked != null && checked.getPrimaryNodeType().getName().equals("galaxy:workspace")) {
                        if (checked.equals(node)) {
                            throw new RuntimeException(new RegistryException(new Message("MOVE_ONTO_CHILD", LOGGER)));
                        }
                        
                        checked = checked.getParent();
                    }
                    
                    String dest = parentNode.getPath() + "/" + w.getName();
                    session.move(node.getPath(), dest);
                }
                
                session.save();
                
                return null;
            }
        });
    }

    public void deleteWorkspace(final String id) throws RegistryException, NotFoundException {
        final JcrRegistryImpl registry = this;
        executeWithNotFound(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                try {
                    Node node = getNodeByUUID(id);

                    JcrWorkspace wkspc = new JcrWorkspace(registry, lifecycleManager, node);
                    String path = wkspc.getPath();
                    
                    node.remove();

                    activityManager.logActivity(UserUtils.getCurrentUser(),
                                                "Workspace " + path + " was deleted", 
                                                EventType.INFO);
                    session.save();
                    
                } catch (ItemNotFoundException e) {
                    throw new RuntimeException(new NotFoundException(id));
                }
                return null;
            }
        });
    }

    public Workspace createWorkspace(final Workspace parent, 
                                     final String name) throws DuplicateItemException, RegistryException {
        // we should throw an error, but lets be defensive for now
        final String escapedName = JcrUtil.escape(name);

        final JcrRegistryImpl registry = this;
        
        return (Workspace) executeWithRegistryException(new JcrCallback() {
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
                
                JcrWorkspace workspace = new JcrWorkspace(registry, lifecycleManager, node);
                workspace.setName(escapedName);
                workspace.setDefaultLifecycle(lifecycleManager.getDefaultLifecycle());
                workspaces.add(workspace);

                session.save();
                
                activityManager.logActivity(UserUtils.getCurrentUser(),
                                            "Workspace " + workspace.getPath() + " was created", 
                                            EventType.INFO);
                
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
                w = w.getParent();
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
    
    public Collection<Workspace> getWorkspaces() throws RegistryException {
        try {
            List<Workspace> workspaceCol = new ArrayList<Workspace>();
            for (NodeIterator itr = getWorkspacesNode().getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();

                if (!n.getName().equals("jcr:system")) {
                    workspaceCol.add(new JcrWorkspace(this, lifecycleManager, n));
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
                artifact.setContentHandler(contentService.getContentHandler(artifact.getContentType()));
                artifacts.add(artifact);
            }
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }

        return artifacts;
    }

    public Artifact getArtifact(final String id) throws NotFoundException {
        final JcrRegistryImpl registry = this;
        return (Artifact) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(id);
                Node wNode = node.getParent();
                JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(registry, lifecycleManager, wNode), 
                                                       node, registry);

                setupContentHandler(artifact);

                return artifact;
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
    
    public Artifact getArtifact(final Workspace w, final  String name) throws NotFoundException {
        final JcrRegistryImpl registry = this;
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
                    JcrArtifact artifact = new JcrArtifact(w, node, registry);
                    
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

    public ArtifactResult createArtifact(Workspace workspace, Object data, String versionLabel, User user) 
        throws RegistryException, ArtifactPolicyException, MimeTypeParseException, DuplicateItemException {
        ContentHandler ch = contentService.getContentHandler(data.getClass());
        
        if (ch == null) {
            throw new RegistryException(new Message("UNKNOWN_TYPE", LOGGER, data.getClass()));
        }
        
        MimeType ct = ch.getContentType(data);
        String name = ch.getName(data);
        
        return createArtifact(workspace, null, data, name, versionLabel, ct, user);
    }

    public ArtifactResult createArtifact(final Workspace workspace, 
                                         final InputStream is, 
                                         final Object data,
                                         final String name, 
                                         final String versionLabel,
                                         final MimeType contentType,
                                         final User user)
        throws RegistryException, ArtifactPolicyException, DuplicateItemException {
        
        if (user == null) {
            throw new NullPointerException("User cannot be null.");
        }
        if (name == null) {
            throw new NullPointerException("Artifact name cannot be null.");
        }
        
        final JcrRegistryImpl registry = this;
        return (ArtifactResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node workspaceNode = ((JcrWorkspace)workspace).getNode();
                Node artifactNode;
                try {
                    artifactNode = workspaceNode.addNode(ISO9075.encode(name), ARTIFACT_NODE_TYPE);
                } catch (javax.jcr.ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(name));
                }
                
                artifactNode.addMixin("mix:referenceable");
                Node versionNode = artifactNode.addNode("version");
                versionNode.addMixin("mix:referenceable");
                if (is != null) {
                    versionNode.setProperty(JcrVersion.DATA, is);
                }
                
                ContentHandler ch = contentService.getContentHandler(contentType);
                
                JcrArtifact artifact = new JcrArtifact(workspace, artifactNode, registry);
                artifact.setContentType(contentType);
                artifact.setName(name);
                artifact.setContentHandler(ch);
                
                // set up the initial version
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                versionNode.setProperty(JcrVersion.LATEST, true);
                
                JcrVersion jcrVersion = new JcrVersion(artifact, versionNode);
                
                // Store the data
                Object loadedData = null;
                if (data != null) {
                    jcrVersion.setData(data);
                    InputStream dataStream = ch.read(data);
                    versionNode.setProperty(JcrVersion.DATA, dataStream);
                    loadedData = data;
                } else {
                    InputStream dataStream = versionNode.getProperty(JcrVersion.DATA).getStream();
                    jcrVersion.setData(ch.read(dataStream, workspace));
                    loadedData = jcrVersion.getData();
                }
                
                if (ch instanceof XmlContentHandler) {
                    XmlContentHandler xch = (XmlContentHandler) ch;
                    artifact.setDocumentType(xch.getDocumentType(loadedData));
                    ch = contentService.getContentHandler(artifact.getDocumentType());
                }
    
                jcrVersion.setVersionLabel(versionLabel);
                jcrVersion.setAuthor(user);
                jcrVersion.setLatest(true);
                jcrVersion.setActive(true);
                
                try {
                    Set<Artifact> dependencies = ch.detectDependencies(loadedData, workspace);
                    jcrVersion.addDependencies(dependencies, false);
                    
                    Lifecycle lifecycle = workspace.getDefaultLifecycle();
                    artifact.setPhase(lifecycle.getInitialPhase());                    
                    
                    List<ArtifactVersion> versions = new ArrayList<ArtifactVersion>();
                    versions.add(jcrVersion);
                    artifact.setVersions(versions);
                    
                    LOGGER.log(Level.FINE, "Created artifact " + artifact.getId());
                    return approve(session, artifact, null, jcrVersion, user);
                } catch (RegistryException e) {
                    // gets unwrapped by executeAndDewrap
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private Object executeAndDewrap(JcrCallback jcrCallback) 
        throws RegistryException, ArtifactPolicyException, DuplicateItemException {
        try {
            return execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof DuplicateItemException) {
                throw (DuplicateItemException) cause;
            } else if (cause instanceof ArtifactPolicyException) {
                throw (ArtifactPolicyException) cause;
            } else {
                throw e;
            }
        }
    }

    private Object executeWithNotFound(JcrCallback jcrCallback) 
        throws RegistryException, NotFoundException {
        try {
            return execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RegistryException) {
                throw (RegistryException) cause;
            } else if (cause instanceof NotFoundException) {
                throw (NotFoundException) cause;
            } else {
                throw e;
            }
        }
    }
    
    private Object executeWithRegistryException(JcrCallback jcrCallback) 
        throws RegistryException {
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

    private ArtifactResult approve(Session session, Artifact artifact, 
                                   JcrVersion previous, 
                                   JcrVersion next,
                                   User user)
        throws RegistryException, RepositoryException {
        boolean approved = true;
        
        List<ApprovalMessage> approvals = policyManager.approve(previous, next);
        for (ApprovalMessage a : approvals) {
            if (!a.isWarning()) {
                approved = false;
                break;
            }
        }
        
        if (!approved) {
            throw new RuntimeException(new ArtifactPolicyException(approvals));
        }

        indexManager.index(next);
        
        session.save();
        
        if (previous == null) {
            activityManager.logActivity(user, "Artifact " + artifact.getName() + " was created in workspace "
                                              + artifact.getWorkspace().getPath() + ".", EventType.INFO);
        } else {
            activityManager.logActivity(user, "Version " + next.getVersionLabel() 
                                        + " of artifact " + artifact.getPath() + " was created.", EventType.INFO);
        }
        
        
        return new ArtifactResult(artifact, next, approvals);
    }
    
    public ArtifactResult createArtifact(Workspace workspace, 
                                         String contentType, 
                                         String name,
                                         String versionLabel, 
                                         InputStream inputStream, 
                                         User user) 
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException, DuplicateItemException {
        contentType = trimContentType(contentType);
        MimeType ct = new MimeType(contentType);

        return createArtifact(workspace, inputStream, null, name, versionLabel, ct, user);
    }

    private Object getData(Workspace workspace, MimeType contentType, InputStream inputStream) 
        throws RegistryException, IOException {
        ContentHandler ch = contentService.getContentHandler(contentType);

        if (ch == null) {
            throw new RegistryException(new Message("UNSUPPORTED_CONTENT_TYPE", LOGGER, contentType));
        }

        return ch.read(inputStream, workspace);
    }


    public ArtifactResult newVersion(Artifact artifact, 
                                     Object data, 
                                     String versionLabel, 
                                     User user)
        throws RegistryException, ArtifactPolicyException, IOException, DuplicateItemException {
        return newVersion(artifact, null, data, versionLabel, user);
    }

    public ArtifactResult newVersion(final Artifact artifact, 
                                     final InputStream inputStream, 
                                     final String versionLabel, 
                                     final User user) 
        throws RegistryException, ArtifactPolicyException, IOException, DuplicateItemException {
        return newVersion(artifact, inputStream, null, versionLabel, user);
    }
    
    protected ArtifactResult newVersion(final Artifact artifact, 
                                        final InputStream inputStream, 
                                        final Object data,
                                        final String versionLabel, 
                                        final User user) 
        throws RegistryException, ArtifactPolicyException, IOException, DuplicateItemException {
       
        if (user == null) {
            throw new NullPointerException("User cannot be null!");
        }
        
        return (ArtifactResult) executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                JcrArtifact jcrArtifact = (JcrArtifact) artifact;
                Node artifactNode = jcrArtifact.getNode();
                artifactNode.refresh(false);
                JcrVersion previousLatest = ((JcrVersion)jcrArtifact.getActiveVersion());
                Node previousNode = previousLatest.getNode();
                
                previousLatest.setActive(false);
                previousLatest.setLatest(false);

                ContentHandler ch = contentService.getContentHandler(jcrArtifact.getContentType());
                
                // create a new version node
                Node versionNode = artifactNode.addNode("version");
                versionNode.addMixin("mix:referenceable");
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                
                
                JcrVersion next = new JcrVersion(jcrArtifact, versionNode);
                next.setActive(true);
                next.setLatest(true);
                
                try {
                    // Store the data
                    if (inputStream != null) {
                        versionNode.setProperty(JcrVersion.DATA, inputStream);
                        
                        InputStream s = versionNode.getProperty(JcrVersion.DATA).getStream();
                        Object data = getData(artifact.getWorkspace(), artifact.getContentType(), s);
                        next.setData(data);
                    } else {
                        next.setData(data);
                        versionNode.setProperty(JcrVersion.DATA, ch.read(data));
                    }
                    
                    next.setVersionLabel(versionLabel);
                    next.setAuthor(user);
                    next.setLatest(true);
                    
                    ((List<ArtifactVersion>)jcrArtifact.getVersions()).add(0, next);
                    
                    ch.addMetadata(next);
                    
                    try {
                        Property pNames = previousNode.getProperty(AbstractJcrObject.PROPERTIES);
                    
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
                    }
                    return approve(session, artifact, previousLatest, next, user);
                } catch (RegistryException e) {
                    // this will get dewrapped
                    throw new RuntimeException(e);
                }
            }
        });
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
    
    public void setActiveVersion(final Artifact artifact, 
                                 final String version, 
                                 final User user) throws RegistryException,
        ArtifactPolicyException {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                ArtifactVersion av = artifact.getActiveVersion();
                ArtifactVersion newAV = artifact.getVersion(version);
                
                ((JcrVersion) av).setActive(false);
                ((JcrVersion) newAV).setActive(true);
                
                session.save();
                return null;
            }
        });
        
    }

    public void save(Artifact artifact) throws RegistryException {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                // TODO: Fix artifact saving, we should have to call artifact.save().
                session.save();
                return null;
            }
        });
    }

    public void move(final Artifact artifact, final String workspaceId) throws RegistryException {
        final Workspace workspace = getWorkspace(workspaceId);
        
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                String p1 = artifact.getPath();
                Node aNode = ((JcrArtifact) artifact).getNode();
                Node wNode = ((JcrWorkspace) workspace).getNode();
                
                session.move(aNode.getPath(), wNode.getPath() + "/" + aNode.getName());

                activityManager.logActivity(UserUtils.getCurrentUser(),
                                            "Workspace " + p1 + " was moved to " + artifact.getPath(), 
                                            EventType.INFO);
                
                session.save();
                return null;
            }
        });
    }

    public void delete(final Artifact artifact) throws RegistryException {
        executeWithRegistryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Set<Dependency> deps;
                try {
                    deps = getDependedOnBy(artifact);
                } catch (QueryException e) {
                    throw new RuntimeException(e);
                } catch (RegistryException e) {
                    throw new RuntimeException(e);
                }
                
                for (Dependency d : deps) {
                    ((JcrDependency) d).getDependencyNode().remove();
                }
                String path = artifact.getPath();
                ((JcrArtifact) artifact).getNode().remove();

                activityManager.logActivity(UserUtils.getCurrentUser(),
                                            "Artifact " + path + " was deleted", 
                                            EventType.INFO);
                
                session.save();
                return null;
            }
        });
    }
    
    private QueryManager getQueryManager(Session session) throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }

    public SearchResults search(String queryString, int startOfResults, int maxResults) throws RegistryException, QueryException {
        List<String> tokens = new ArrayList<String>();
        int start = 0;
        for (int i = 0; i < queryString.length(); i++) {
            char c = queryString.charAt(i);
            switch (c) {
            case ' ':
                if (start != i) {
                    tokens.add(queryString.substring(start, i));
                }
                start = i + 1;
                break;
            case '=':
                 tokens.add("=");
                 start = i+1;
                 break;
            case '<':
                if (queryString.charAt(i+1) == '=') {
                    i++;
                    tokens.add("<=");
                } else {
                    tokens.add("<");
                }
                start = i+1;  
                break;
            }
        }
        
        if (start != queryString.length()) {
            tokens.add(queryString.substring(start));
        }

        Iterator<String> itr = tokens.iterator(); 
        if (!itr.hasNext()) {
            throw new QueryException(new Message("EMPTY_QUERY_STRING", LOGGER));
        }
        
        if (!itr.next().toLowerCase().equals("select")){
            throw new QueryException(new Message("EXPECTED_SELECT", LOGGER));
        }
        
        if (!itr.hasNext()) {
            throw new QueryException(new Message("EXPECTED_SELECT_TYPE", LOGGER));
        }
        
        Class<?> selectTypeCls = null;
        String selectType = itr.next();
        if (selectType.equals("artifact")) {
            selectTypeCls = Artifact.class;
        } else if (selectType.equals("artifactVersion")) {
            selectTypeCls = ArtifactVersion.class;
        } else {
            throw new QueryException(new Message("UNKNOWN_SELECT_TYPE", LOGGER, selectType));
        }
        
        if (!itr.hasNext()){
            return search(new org.mule.galaxy.query.Query(selectTypeCls));
        }
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query(selectTypeCls);
        q.setStart(startOfResults);
        q.setMaxResults(maxResults);
        
        String next = itr.next();
        if ("from".equals(next.toLowerCase())) {
            if (!itr.hasNext()) throw new QueryException(new Message("EXPECTED_FROM", LOGGER));
            
            q.workspacePath(dequote(itr.next(), itr));
            
            if (!itr.hasNext()) {
                throw new QueryException(new Message("EXPECTED_WHERE", LOGGER));
            }
            
            next = itr.next();
        }
        
        if (!next.toLowerCase().equals("where")) {
            throw new QueryException(new Message("EXPECTED_WHERE_BUT_FOUND", LOGGER, next));
        }
        
        boolean firstRestriction = true;
        while (itr.hasNext()) {
            if (firstRestriction) {
                firstRestriction = false;
            } else {
                if (!itr.hasNext()) {
                    throw new QueryException(new Message("EXPECTED_AND", LOGGER));
                }
                String t = itr.next();
                if (!"and".equals(t.toLowerCase())) {
                    throw new QueryException(new Message("EXPECTED_AND", LOGGER));
                }
            }
            
            String left = itr.next();
            
            if (!itr.hasNext()) {
                throw new QueryException(new Message("EXPECTED_COMPARATOR", LOGGER));
            }
            
            String compare = itr.next();
            
            if (!itr.hasNext()) {
                throw new QueryException(new Message("EXPECTED_RIGHT", LOGGER));
            }
            
            Restriction r = null;
            if (compare.equals("=")) {
                r = Restriction.eq(left, dequote(itr.next(), itr));
            } else if (compare.equals("like")) {
                r = Restriction.like(left, dequote(itr.next(), itr));
            } else if (compare.equals("!=")) {
                r = Restriction.not(Restriction.eq(left, dequote(itr.next(), itr)));
            } else if (compare.equals("in")) {
                if (!itr.hasNext()) {
                    throw new QueryException(new Message("EXPECTED_IN_TOKEN", LOGGER));
                }
                
                ArrayList<String> in = new ArrayList<String>();
                String first = itr.next();
                boolean end = false;
                if (first.startsWith("(")) {
                    if (first.endsWith(")")) {
                        end = true;
                        first = first.substring(1, first.length() - 1);
                    } else {
                        first = first.substring(1);
                    }
                        
                    if (first.endsWith(",")) {
                        first = first.substring(0, first.length() - 1);
                    }
                    in.add(dequote(first, itr));
                } else {
                    throw new QueryException(new Message("EXPECTED_IN_LEFT_PARENS", LOGGER, first));
                }
                
                while (!end && itr.hasNext()) {
                    String nextIn = itr.next();
                    if (nextIn.endsWith(")")) {
                        in.add(dequote(nextIn.substring(0, nextIn.length()-1), itr));
                        break;
                    } else {
                        in.add(dequote(nextIn, itr));
                    }
                }
                r = Restriction.in(left, in);
            } else {
                new QueryException(new Message("UNKNOWN_COMPARATOR", LOGGER, left));
            }
            
            q.add(r);
        }
        
        return search(q);
    }


    private String dequote(String s, Iterator itr) {
        if (s.startsWith("'")) {
            s = dequote2(s, "'", itr);
        } else if (s.startsWith("\"")) {
            s = dequote2(s, "\"", itr);
        }
        return s;
    }

    private String dequote2(String s, String quote, Iterator itr) {
        if (!s.endsWith(quote)) {
            StringBuilder sb = new StringBuilder();
            sb.append(s.substring(1));
            while (itr.hasNext()) {
                sb.append(" ");
                String next = (String) itr.next();
                
                if (next.endsWith(quote)) {
                    sb.append(next.substring(0, next.length()-1));
                    break;
                } else {
                    sb.append(next);
                }
            }
            return sb.toString();
        } else {
            return s.substring(1, s.length()-1);
        }
    }

    public SearchResults search(final org.mule.galaxy.query.Query query) 
        throws RegistryException, QueryException {
        final JcrRegistryImpl registry = this;
        return (SearchResults) executeWithQueryException(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                        
                QueryManager qm = getQueryManager(session);
                
                Set<Object> artifacts = new HashSet<Object>();
                
                boolean av = false;
                Class<?> selectType = query.getSelectType();
                if (selectType.equals(ArtifactVersion.class)) {
                    av = true;
                } else if (!selectType.equals(Artifact.class)) {
                    throw new RuntimeException(new QueryException(new Message("INVALID_SELECT_TYPE", LOGGER, selectType.getName())));
                }
                
                String qstr = null;
                try {
                    qstr = createQueryString(query, av);
                } catch (QueryException e) {
                    // will be dewrapped later
                    throw new RuntimeException(e);
                }
                
                LOGGER.log(Level.FINE, "Query: " + qstr.toString());
                
                Query jcrQuery = qm.createQuery(qstr, Query.XPATH);
                
                QueryResult result = jcrQuery.execute();
                NodeIterator nodes = result.getNodes(); 
                if (query.getStart() != -1) {
                    nodes.skip(query.getStart());
                }
                
                int max = query.getMaxResults();
                int count = 0;
                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    
                    // UGH: jackrabbit does not support parent::* xpath expressions
                    // so we need to traverse the hierarchy to find the right node
                    if (av) {
                        Node artifactNode = node;
                        if (!artifactNode.getPrimaryNodeType().getName().equals(ARTIFACT_NODE_TYPE)) {
                            while (!node.getName().equals("version")) {
                                node = node.getParent();
                            }
                            artifactNode = node.getParent(); 
                        }
                        JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(registry, lifecycleManager, artifactNode.getParent()), 
                                                               artifactNode, 
                                                               registry);
                        setupContentHandler(artifact);
                        artifacts.add(new JcrVersion(artifact, node));
                    } else {
                        while (!node.getPrimaryNodeType().getName().equals(ARTIFACT_NODE_TYPE)) {
                            node = node.getParent();
                        }
                        JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(registry, lifecycleManager, node.getParent()), node,
                                                               registry);
                        setupContentHandler(artifact);
                        artifacts.add(artifact);
                    }
                    
                    count++;
                    
                    if (count == max) {
                        break;
                    }
                }                                                   
                
                return new SearchResults(nodes.getSize(), artifacts);
            }

        });
    }

    protected String createQueryString(final org.mule.galaxy.query.Query query, boolean av) throws QueryException {
        StringBuilder qstr = new StringBuilder();
        if (query.getWorkspaceId() != null) {
            qstr.append("//*[@jcr:uuid='")
                .append(query.getWorkspaceId())
                .append("'][@jcr:primaryType=\"galaxy:workspace\"]/element(*, galaxy:artifact)");
        } else if (query.getWorkspacePath() != null) {
            String path = query.getWorkspacePath();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            
            qstr.append("//")
            .append(ISO9075.encode(path))
            .append("[@jcr:primaryType=\"galaxy:workspace\"]/element(*, galaxy:artifact)");
        } else {
            qstr.append("//element(*, galaxy:artifact)");
        }
        
        StringBuilder propStr = new StringBuilder();

        // Search the latest if we're searching for artifacts, otherwise
        // search all versions
        if (!av) {
            propStr.append("/version[@latest='true']");
        } else {
            propStr.append("/version");
        }
        
        boolean first = true;
        for (Restriction r : query.getRestrictions()) {

            // TODO: NOT, LIKE, OR, etc
            String property = (String) r.getLeft();
            boolean not = false;
            Operator operator = r.getOperator();
            
            if (operator.equals(Operator.NOT)) {
                not = true;
                r = (Restriction) r.getRight();
                operator = r.getOperator();
                property = r.getLeft().toString();
            }
            
            if ("phase".equals(property)) {
                if (operator.equals(Operator.IN)) {
                    Collection<?> right = (Collection<?>) r.getRight();
                    boolean firstPhase = true;
                    for (Object o : right) {
                        
                        
                        if (firstPhase) {
                            qstr.append("[");
                            firstPhase = false;
                        } else {
                            qstr.append(" or ");
                        }
                        
                        createLifecycleAndPhasePropertySearch(qstr, property, o, not, operator);
                    }
                    
                    if (!firstPhase) {
                        qstr.append("]");
                    }
                } else {
                    String right = r.getRight().toString();
                    
                    qstr.append("[");
                    createLifecycleAndPhasePropertySearch(qstr, property, right, not, operator);
                    qstr.append("]");
                }
            } else {
                // this is a normal property
                if (operator.equals(Operator.IN)) {
                    Collection<?> right = (Collection<?>) r.getRight();
                    for (Object o : right) {

                        String rightVal = o == null ? "" : o.toString();
                        if ("lifecycle".equals(property)) {
                            Lifecycle l = lifecycleManager.getLifecycle(rightVal);
                            if (l == null) {
                                continue;
                            } else {
                                rightVal = l.getId();
                            }
                        }

                        first = appendPropertySearch(qstr, propStr, first,
                                                     rightVal, property,
                                                     not, false, Operator.EQUALS);
                    }
                } else {
                    String right = r.getRight().toString();

                    if ("lifecycle".equals(property)) {
                        Lifecycle l = lifecycleManager.getLifecycle(right);
                        
                        right = l.getId();
                    }
                    
                    first = appendPropertySearch(qstr, propStr, first, right, 
                                                  property, not, true, operator);
                }
            }
        }
        
        // No search criteria
        if (qstr.length() == 0) {
            qstr.append("//element(*, galaxy:artifact)");
        } else {
            if (!first) propStr.append("]");
            
            qstr.append(propStr);
        }
        return qstr.toString();
    }

    private void createLifecycleAndPhasePropertySearch(StringBuilder qstr, String property, Object right,
                                                       boolean not, Operator operator) throws QueryException {
        String[] lp = right.toString().split(":");
        if (lp.length != 2) {
            throw new QueryException(new Message("INVALID_PHASE_FORMAT", LOGGER, right.toString()));
        }
        // phase = ...
        Lifecycle l = lifecycleManager.getLifecycle(lp[0]);
        String pid = "invalid";
        if (l != null) {
            Phase p = l.getPhase(lp[1]);
            
            if (p != null) pid = p.getId();
        }
        
        createPropertySearch(qstr, "phase", pid, operator, not, false);
    }

    protected boolean appendPropertySearch(StringBuilder qstr, 
                                          StringBuilder propStr, 
                                          boolean first,
                                          String right, 
                                          String property, 
                                          boolean not,
                                          boolean and,
                                          Operator operator) {
        
        if (property.equals(JcrArtifact.PHASE)
            || property.equals(JcrArtifact.DOCUMENT_TYPE)
            || property.equals(JcrArtifact.CONTENT_TYPE)
            || property.equals(JcrArtifact.NAME)
            || property.equals(JcrArtifact.LIFECYCLE)
            || property.equals(JcrArtifact.DESCRIPTION)) {
            createPropertySearch(qstr, property, right, operator, not, true);
        } else {
            if (first) {
                first = false;
                propStr.append("[");
            } else if (and) {
                propStr.append(" and ");
            } else {
                propStr.append(" or ");
            }
            
            createPropertySearch(propStr, property, right, operator, not, false);
        }
        return first;
    }

    @SuppressWarnings("unchecked")
    public Set<Dependency> getDependedOnBy(final Artifact artifact) 
        throws RegistryException, QueryException {
        final JcrRegistryImpl registry = this;
        
        return (Set<Dependency>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                        
                QueryManager qm = getQueryManager(session);
                
                StringBuilder qstr = new StringBuilder();
                qstr.append("//element(*, galaxy:artifact)/version/")
                    .append(JcrVersion.DEPENDENCIES)
                    .append("/")
                    .append(ISO9075.encode(artifact.getId()))
                    .append("");
                
                Set<Dependency> artifacts = new HashSet<Dependency>();
                
                Query query = qm.createQuery(qstr.toString(), Query.XPATH);
                
                QueryResult result = query.execute();
                
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    final Node depNode = nodes.nextNode();
                    
                    final Node node = depNode.getParent().getParent().getParent();
                    
                    JcrWorkspace workspace = new JcrWorkspace(registry, lifecycleManager, node.getParent());
                    final JcrArtifact artDep = new JcrArtifact(workspace, node, registry);
                    
                    Dependency dependency = new JcrDependency(artDep, depNode);
                    artifacts.add(dependency);
                }
                return artifacts;
            }

        });
    }

    @SuppressWarnings("unchecked")
    public Object getPropertyDescriptorOrIndex(final String propertyName) {
        
        return execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    return indexManager.getIndex(propertyName);
                } catch (NotFoundException e) {
                    return getPropertyDescriptor(propertyName);
                }
            }


        });
    }

    public PropertyDescriptor getPropertyDescriptor(final String propertyName) {
        List<PropertyDescriptor> results = propertyDescriptorDao.find("property", propertyName);
        
        if (results.size() == 0) {
            return null;
        }
        
        return results.get(0);
    }
    
    public Collection<PropertyDescriptor> getPropertyDescriptors() throws RegistryException {
        return propertyDescriptorDao.listAll();
    }

    public void savePropertyDescriptor(PropertyDescriptor pd) throws RegistryException {
        propertyDescriptorDao.save(pd);
    }
    
    public void deletePropertyDescriptor(String id) throws RegistryException {
        propertyDescriptorDao.delete(id);
    }
    protected String escapeNodeName(String right) {
        return ISO9075.encode(right);
    }

    private void createPropertySearch(StringBuilder qstr, String property, 
                                      String right, Operator operator, 
                                      boolean not, boolean appendBrackets) {
        if (appendBrackets) {
            qstr.append("[");
        }
        
        if (not) {
            qstr.append("not(");
        }
        
        if (operator.equals(Operator.LIKE)) {
            qstr.append("jcr:like(@")
            .append(property)
            .append(", '%")
            .append(right)
            .append("%')");
        } else {
            qstr.append("@")
                .append(property)
                .append("='")
                .append(right)
                .append("'");
        }
        
        if (not) {
            qstr.append(")");
        }
        
        if (appendBrackets) {
            qstr.append("]");
        }
    }

    private String trimContentType(String contentType) {
        int comma = contentType.indexOf(';');
        if (comma != -1) {
            contentType = contentType.substring(0, comma);
        }
        return contentType;
    }

    public void initialize() throws Exception {
        // Keep a session open so the transient repository doesn't shutdown
        openSession = getSessionFactory().getSession();
        
        Session session = getSessionFactory().getSession();
        Node root = session.getRootNode();
        
        
        // UGH, Jackrabbit specific code
        javax.jcr.Workspace workspace = session.getWorkspace();
        try {
            workspace.getNamespaceRegistry().getPrefix(NAMESPACE);
        } catch (NamespaceException e) {
            workspace.getNamespaceRegistry().registerNamespace("galaxy", NAMESPACE);
        }

        NodeTypeDef[] nodeTypes = NodeTypeReader.read(getClass()
            .getResourceAsStream("/org/mule/galaxy/impl/jcr/nodeTypes.xml"));

        // Get the NodeTypeManager from the Workspace.
        // Note that it must be cast from the generic JCR NodeTypeManager to the
        // Jackrabbit-specific implementation.

        NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl)workspace.getNodeTypeManager();

        // Acquire the NodeTypeRegistry
        NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();        

        // Loop through the prepared NodeTypeDefs
        for (NodeTypeDef ntd : nodeTypes) {
            // ...and register it
            if (!ntreg.isRegistered(ntd.getName())) {
                ntreg.registerNodeType(ntd);
            }
                
            
        }
//        ntreg.dump(System.out);

        
        Node workspaces = JcrUtil.getOrCreate(root, "workspaces");
        workspacesId = workspaces.getUUID();
        indexesId = JcrUtil.getOrCreate(root, "indexes").getUUID();
        artifactTypesId = JcrUtil.getOrCreate(root, "artifactTypes").getUUID();

        NodeIterator nodes = workspaces.getNodes();
        // ignore the system node
        if (nodes.getSize() == 0) {
            Node node = workspaces.addNode(settings.getDefaultWorkspaceName(),
                                           "galaxy:workspace");
            node.addMixin("mix:referenceable");

            JcrWorkspace w = new JcrWorkspace(this, lifecycleManager, node);
            w.setName(settings.getDefaultWorkspaceName());

            System.setProperty("initializeOnce", "true");
            workspaces.setProperty(REPOSITORY_LAYOUT_VERSION, "1");
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
    }
    
    public void destroy() throws Exception {
        openSession.logout();
    }

    public void addDependencies(ArtifactVersion artifactVersion, final Artifact... dependencies)
        throws RegistryException {
        final JcrVersion jcrVersion = (JcrVersion) artifactVersion;
        
        if (dependencies != null) {
            execute(new JcrCallback() {
                public Object doInJcr(Session session) throws IOException, RepositoryException {
                    jcrVersion.addDependencies(dependencies, true);
                    session.save();
                    return null;
                }
            });
        }
    }
    
    public void removeDependencies(ArtifactVersion artifactVersion, final Artifact... dependencies)
        throws RegistryException {
        final JcrVersion jcrVersion = (JcrVersion) artifactVersion;
        
        if (dependencies != null) {
            execute(new JcrCallback() {
                public Object doInJcr(Session session) throws IOException, RepositoryException {
                    Set<String> ids = new HashSet<String>();
                    for (Artifact a : dependencies) {
                        ids.add(a.getId());
                    }

                    Node depsNode = JcrUtil.getOrCreate(jcrVersion.node, JcrVersion.DEPENDENCIES);
                    for (NodeIterator nodes = depsNode.getNodes(); nodes.hasNext();) {
                        Node dep = nodes.nextNode();
                        boolean user = JcrUtil.getBooleanOrNull(dep, JcrVersion.USER_SPECIFIED);
                        if (user && ids.contains(dep.getName())) {
                            dep.remove();
                        }
                    }
                    session.save();
                    return null;
                }
            });
        }
    }
    
    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
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

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
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

    
}
