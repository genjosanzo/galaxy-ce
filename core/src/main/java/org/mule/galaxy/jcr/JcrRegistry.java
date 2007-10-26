package org.mule.galaxy.jcr;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;

import net.sf.saxon.javax.xml.xquery.XQConnection;
import net.sf.saxon.javax.xml.xquery.XQDataSource;
import net.sf.saxon.javax.xml.xquery.XQItem;
import net.sf.saxon.javax.xml.xquery.XQPreparedExpression;
import net.sf.saxon.javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Index;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.QueryException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Settings;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.Index.Language;
import org.mule.galaxy.impl.IndexImpl;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.JcrUtil;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.QNameUtil;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class JcrRegistry extends JcrTemplate implements Registry {

    private Logger LOGGER = LogUtils.getL7dLogger(JcrRegistry.class);

    private Settings settings;
    
    private ContentService contentService;

    private String workspacesId;

    private String indexesId;

    private String artifactTypesId;
    
    public Workspace getWorkspace(String id) throws RegistryException {
        // TODO: implement a query
        // TODO: possibility for injenction in the id here?
        
        try {
            Node node = getWorkspacesNode().getNode(id);

            return new JcrWorkspace(node);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(id);
        } catch (ItemNotFoundException e) {
            throw new NotFoundException(id);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }

    Node getWorkspacesNode() {
        return getNodeByUUID(workspacesId);
    }

    Node getIndexNode() {
        return getNodeByUUID(indexesId);
    }
    
    Node getArtifactTypesNode() {
        return getNodeByUUID(artifactTypesId);
    }
    
    public Collection<Workspace> getWorkspaces() throws RegistryException {
        try {
            Collection<Workspace> workspaceCol = new ArrayList<Workspace>();
            for (NodeIterator itr = getWorkspacesNode().getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();

                if (!n.getName().equals("jcr:system")) {
                    workspaceCol.add(new JcrWorkspace(n));
                }
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
                JcrArtifact artifact = new JcrArtifact(jw, itr.nextNode());
                artifact.setContentHandler(contentService.getContentHandler(artifact.getContentType()));
                artifacts.add(artifact);
            }
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }

        return artifacts;
    }

    public Artifact getArtifact(final String id) throws NotFoundException {
        return (Artifact) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(id);
                Node wNode = node.getParent();
                JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(wNode), node);
                
                artifact.setContentHandler(contentService.getContentHandler(artifact.getContentType()));

                return artifact;
            }
        });
    }

    public Artifact createArtifact(Workspace workspace, Object data, String versionLabel) throws RegistryException, MimeTypeParseException {
        ContentHandler ch = contentService.getContentHandler(data.getClass());
        
        if (ch == null) {
            throw new RegistryException(new Message("UNKNOWN_TYPE", LOGGER, data.getClass()));
        }
        
        MimeType ct = ch.getContentType(data);
        String name = ch.getName(data);
        
        return createArtifact(workspace, data, name, versionLabel, ct);
    }

    public Artifact createArtifact(final Workspace workspace, 
                                   final Object data, 
                                   final String name, 
                                   final String versionLabel,
                                   final MimeType contentType)
        throws RegistryException {

        return (Artifact) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node workspaceNode = ((JcrWorkspace)workspace).getNode();
                Node artifactNode = workspaceNode.addNode("artifact");
                artifactNode.addMixin("mix:referenceable");
    
                ContentHandler ch = contentService.getContentHandler(contentType);
                
                JcrArtifact artifact = new JcrArtifact(workspace, artifactNode, ch);
                artifact.setContentType(contentType);
                artifact.setName(name);
    
                if (ch instanceof XmlContentHandler) {
                    XmlContentHandler xch = (XmlContentHandler) ch;
                    artifact.setDocumentType(xch.getDocumentType(data));
                }
    
                session.save();
                
                // create an initial version
                Node versionNode = artifactNode.addNode("version");
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                
                // Store the data
                InputStream s = ch.read(data);
                versionNode.setProperty(JcrVersion.DATA, s);
                
                JcrVersion jcrVersion = new JcrVersion(artifact, versionNode);
                jcrVersion.setData(data);
                jcrVersion.setVersionLabel(versionLabel);

                try {
                    index(jcrVersion);
                } catch (RegistryException e) {
                    throw new RuntimeException(e);
                }
                
                session.save();
    
                JcrVersion next = (JcrVersion)artifact.getVersions().iterator().next();
                next.setData(data);
    
                LOGGER.info("Created artifact " + artifact.getId());
    
                return artifact;
            }
        });
    }

    public Artifact createArtifact(Workspace workspace, String contentType, String name,
                                   String versionLabel, InputStream inputStream) throws RegistryException, IOException, MimeTypeParseException {
        contentType = trimContentType(contentType);
        MimeType ct = new MimeType(contentType);

        Object data = getData(ct, inputStream);

        return createArtifact(workspace, data, name, versionLabel, ct);
    }

    private Object getData(MimeType contentType, InputStream inputStream) 
        throws RegistryException, IOException {
        ContentHandler ch = contentService.getContentHandler(contentType);

        if (ch == null) {
            throw new RegistryException(new Message("UNSUPPORTED_CONTENT_TYPE", LOGGER, contentType));
        }

        return ch.read(inputStream);
    }

    public ArtifactVersion newVersion(final Artifact artifact, final Object data, final String versionLabel) throws RegistryException, IOException {
        // TODO: Locking
        return (ArtifactVersion) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                JcrArtifact jcrArtifact = (JcrArtifact) artifact;
                Node artifactNode = jcrArtifact.getNode();
                
                ContentHandler ch = contentService.getContentHandler(jcrArtifact.getContentType());
                
                // create a new version node
                Node versionNode = artifactNode.addNode("version");
                
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                versionNode.setProperty(JcrVersion.CREATED, now);
                
                // Store the data
                InputStream s = ch.read(data);
                versionNode.setProperty(JcrVersion.DATA, s);
    
                JcrVersion next = new JcrVersion(jcrArtifact, versionNode);
                next.setData(data);
                next.setVersionLabel(versionLabel);
                jcrArtifact.getVersions().add(next);
                ch.addMetadata(next);
                
                session.save();
                
                return next;
            }
        });
    }

    public ArtifactVersion newVersion(Artifact artifact, 
                                      InputStream inputStream, 
                                      String versionLabel) throws RegistryException, IOException {
        // TODO: assert artifact is of the same type as the previous revision
        
        Object data = getData(artifact.getContentType(), inputStream);

        return newVersion(artifact, data, versionLabel);
    }

    public void delete(Artifact artifact) {
        throw new UnsupportedOperationException();
    }
    
    @SuppressWarnings("unchecked")
    public Set<Index> getIndices(final QName documentType) throws RegistryException {
        return (Set<Index>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = getQueryManager(session);
                Query query = qm.createQuery("//indexes/*/documentType[@value='" + documentType.toString() + "']", 
                                             Query.XPATH);
                
                QueryResult result = query.execute();
                
                Set<Index> indices = new HashSet<Index>();
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    Node node = nodes.nextNode();
                    
                    indices.add(createIndexFromNode(node.getParent()));
                }
                return indices;
            }
        });
    }

    private Index createIndexFromNode(Node node) throws RepositoryException {
        IndexImpl idx = new IndexImpl();
        
        idx.setId(JcrUtil.getStringOrNull(node, IndexImpl.ID));
        idx.setExpression(JcrUtil.getStringOrNull(node, IndexImpl.EXPRESSION));
        idx.setLanguage(Language.valueOf(JcrUtil.getStringOrNull(node, IndexImpl.LANGUAGE)));
        idx.setName(JcrUtil.getStringOrNull(node, IndexImpl.NAME));
        
        String qt = JcrUtil.getStringOrNull(node, IndexImpl.QUERY_TYPE);
        try {
            idx.setQueryType(getClass().getClassLoader().loadClass(qt));
        } catch (ClassNotFoundException e) {
            // not gonna happen
            throw new RuntimeException(e);
        }
        
        HashSet<QName> docTypes = new HashSet<QName>();
        for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
            Node child = nodes.nextNode();
            
            if (child.getName().equals(IndexImpl.DOCUMENT_TYPE)) {
                String value = JcrUtil.getStringOrNull(child, IndexImpl.DOCUMENT_TYPE_VALUE);
                
                docTypes.add(QNameUtil.fromString(value));
            }
        }
        idx.setDocumentTypes(docTypes);
        
        return idx;
    }

    private QueryManager getQueryManager(Session session) throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }

    public Index registerIndex(final String indexId, 
                               final String displayName, 
                               final Index.Language language,
                               final Class<?> searchType,
                               final String expression, 
                               final QName... documentTypes) throws RegistryException {
        // TODO: check if index name already exists.
        
        return (Index) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                
                Node idxNode = JcrUtil.getOrCreate(getIndexNode(), indexId);
                
                idxNode.setProperty(IndexImpl.ID, indexId);
                idxNode.setProperty(IndexImpl.EXPRESSION, expression);
                idxNode.setProperty(IndexImpl.NAME, displayName);
                idxNode.setProperty(IndexImpl.QUERY_TYPE, searchType.getName());
                idxNode.setProperty(IndexImpl.LANGUAGE, language.toString());
                
                String name = IndexImpl.DOCUMENT_TYPE;
                JcrUtil.removeChildren(idxNode, name);
                
                Set<QName> typeSet = new HashSet<QName>();
                for (QName q : documentTypes) {
                    typeSet.add(q);
                    Node typeNode = idxNode.addNode(name);
                    typeNode.setProperty(IndexImpl.DOCUMENT_TYPE_VALUE, q.toString());
                }
                
                session.save();
                
                IndexImpl idx = new IndexImpl();
                idx.setId(indexId);
                idx.setName(displayName);
                idx.setLanguage(language);
                idx.setQueryType(searchType);
                idx.setExpression(expression);
                idx.setDocumentTypes(typeSet);
                
                return idx;
            }
        });
    }

    public Set search(final org.mule.galaxy.query.Query query) 
        throws RegistryException, QueryException {
        return (Set) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                        
                QueryManager qm = getQueryManager(session);
                StringBuilder qstr = new StringBuilder();
                
                Set<Object> artifacts = new HashSet<Object>();
                
                boolean av = false;
                Class<?> selectType = query.getSelectType();
                if (selectType.equals(ArtifactVersion.class)) {
                    av = true;
                } else if (!selectType.equals(Artifact.class)) {
                    throw new RuntimeException(new QueryException(new Message("INVALID_SELECT_TYPE", LOGGER, selectType.getName())));
                }
                
                for (Restriction r : query.getRestrictions()) {
                    
                    // TODO: NOT, LIKE, OR, etc
                    
                    String property = (String) r.getLeft();
                    if (property.startsWith("artifact.")) {
                        property = property.substring("artifact.".length());
                        
                        qstr.append("//artifact/");
                            
                    } else if (property.startsWith("artifactVersion.")) {
                        property = property.substring("artifactVersion.".length());
                        qstr.append("//artifact/version/");
                    } else {
                        throw new RuntimeException(new QueryException(new Message("INVALID_QUERY_PROPERTY", LOGGER, property)));
                    }
                    
                    qstr.append(property)
                        .append("/")
                        .append(JcrUtil.VALUE)
                        .append("[@")
                        .append(JcrUtil.VALUE)
                        .append("= \"")
                        .append(r.getRight())
                        .append("\"]");
                }
                
                LOGGER.info("Query: " + qstr.toString());
                
                Query jcrQuery = qm.createQuery(qstr.toString(), Query.XPATH);
                
                QueryResult result = jcrQuery.execute();
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    Node node = nodes.nextNode();
                    
                    // UGH: jackrabbit does not support parent::* xpath expressions
                    // so we need to traverse the hierarchy to find the right node
                    if (av) {
                        while (!node.getName().equals("version")) {
                            node = node.getParent();
                        }
                        Node artifactNode = node.getParent();
                        JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(artifactNode.getParent()), artifactNode);
                        artifacts.add(new JcrVersion(artifact, node));
                    } else {
                        while (!node.getName().equals("artifact")) {
                            node = node.getParent();
                        }
                        artifacts.add(new JcrArtifact(new JcrWorkspace(node.getParent()), node));
                    }
                    
                    
                }                                                   
                
                return artifacts;
            }
        });
    }

    private String trimContentType(String contentType) {
        int comma = contentType.indexOf(';');
        if (comma != -1) {
            contentType = contentType.substring(0, comma);
        }
        return contentType;
    }

    private void index(JcrVersion jcrVersion) throws RegistryException {
        Set<Index> indices = getIndices(jcrVersion.getParent().getDocumentType());
        
        for (Index idx : indices) {
            switch (idx.getLanguage()) {
            case XQUERY:
                indexWithXQuery(jcrVersion, idx);
                break;
            default:
                throw new UnsupportedOperationException();
            }
        }
    }

    private void indexWithXQuery(JcrVersion jcrVersion, Index idx) throws RegistryException {
        XQDataSource ds = new SaxonXQDataSource();
        
        try {
            XQConnection conn = ds.getConnection();
            
            XQPreparedExpression ex = conn.prepareExpression(idx.getExpression());
            XmlContentHandler ch = (XmlContentHandler) contentService.getContentHandler(jcrVersion.getParent().getContentType());
            
            ex.bindNode(new QName("document"), ch.getDocument(jcrVersion.getData()), null);
            
            XQResultSequence result = ex.executeQuery();
            
            Node versionNode = jcrVersion.node;
            
            Node property = JcrUtil.getOrCreate(versionNode, idx.getId());
            JcrUtil.removeChildren(property);
            
            List<Object> results = new ArrayList<Object>();
            
            while (result.next()) {
                XQItem item = result.getItem();

                org.w3c.dom.Node node = item.getNode();
                
                Object content = DOMUtils.getContent(node);
              
                if (idx.getQueryType().equals(QName.class)) {
                    results.add(QNameUtil.fromString(content.toString())); 
                } else {
                    results.add(content);
                }
            }
            
            jcrVersion.setProperty(idx.getId(), results);
        } catch (Exception e) {
            // TODO: better error handling for frontends
            // We should log this and make the logs retrievable
            // We should also prepare the expressions when the expression is created
            // or on startup
            throw new RegistryException(e);
        }
        
    }

    public void initialize() throws Exception {
        Session session = getSessionFactory().getSession();
        Node root = session.getRootNode();
        
        Node workspaces = JcrUtil.getOrCreate(root, "workspaces");
        workspacesId = workspaces.getUUID();
        indexesId = JcrUtil.getOrCreate(root, "indexes").getUUID();
        artifactTypesId = JcrUtil.getOrCreate(root, "artifactTypes").getUUID();

        NodeIterator nodes = workspaces.getNodes();
        // ignore the system node
        if (nodes.getSize() == 0) {
            Node node = workspaces.addNode(settings.getDefaultWorkspaceName());
            node.addMixin("mix:referenceable");

            JcrWorkspace w = new JcrWorkspace(node);
            w.setName(settings.getDefaultWorkspaceName());
        } 
        
        session.save();
//        session.logout();
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

}
