package org.mule.galaxy.jcr;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.Credentials;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.xml.namespace.QName;

import net.sf.saxon.javax.xml.xquery.XQConnection;
import net.sf.saxon.javax.xml.xquery.XQDataSource;
import net.sf.saxon.javax.xml.xquery.XQItem;
import net.sf.saxon.javax.xml.xquery.XQItemType;
import net.sf.saxon.javax.xml.xquery.XQPreparedExpression;
import net.sf.saxon.javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Index;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Settings;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.Index.Language;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.JcrUtil;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.QNameUtil;

import org.w3c.dom.Document;

public class JcrRegistry implements Registry {
    private Logger LOGGER = LogUtils.getL7dLogger(JcrRegistry.class);

    private Settings settings;
    private ContentService contentService;

    private Repository jcrRepository;
    private Session session;
    private Credentials credentials;

    private Node root;

    private Node workspaces;

    private Node indices;

    public Workspace getWorkspace(String id) throws RegistryException {
        // TODO: implement a query
        // TODO: possibility for injenction in the id here?
        
        try {
            Node node = workspaces.getNode(id);

            return new JcrWorkspace(node);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(id);
        } catch (ItemNotFoundException e) {
            throw new NotFoundException(id);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }

    public Collection<Workspace> getWorkspaces() throws RegistryException {
        try {
            Collection<Workspace> workspaceCol = new ArrayList<Workspace>();
            for (NodeIterator itr = workspaces.getNodes(); itr.hasNext();) {
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

    public Artifact getArtifact(String id) throws NotFoundException {
        try {
            Node node = session.getNodeByUUID(id);
            Node wNode = node.getParent();
            JcrArtifact artifact = new JcrArtifact(new JcrWorkspace(wNode), node);
            
            artifact.setContentHandler(contentService.getContentHandler(artifact.getContentType()));

            return artifact;
        } catch (ItemNotFoundException e) {
            throw new NotFoundException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Artifact createArtifact(Workspace workspace, Object data) throws RegistryException, MimeTypeParseException {
        ContentHandler ch = contentService.getContentHandler(data.getClass());
        
        if (ch == null) {
            throw new RegistryException(new Message("UNKNOWN_TYPE", LOGGER, data.getClass()));
        }
        
        MimeType ct = ch.getContentType(data);
        String name = ch.getName(data);
        
        return createArtifact(workspace, data, name, ct);
    }

    public Artifact createArtifact(Workspace workspace, Object data, 
                                   String name, MimeType contentType)
        throws RegistryException {

        try {
            Node workspaceNode = ((JcrWorkspace)workspace).getNode();
            Node artifactNode = workspaceNode.addNode("artifact");
            artifactNode.addMixin("mix:versionable");

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
            artifactNode.checkout();
            
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            artifactNode.setProperty(JcrVersion.CREATED, now);
            
            // Store the data
            InputStream s = ch.read(data);
            artifactNode.setProperty(JcrVersion.DATA, s);
            
            JcrVersion jcrVersion = new JcrVersion(artifact, artifactNode);
            jcrVersion.setData(data);
            index(jcrVersion);
            
            session.save();
            artifactNode.checkin();

            JcrVersion next = (JcrVersion)artifact.getVersions().iterator().next();
            next.setData(data);

            LOGGER.info("Created artifact " + artifact.getId());

            return artifact;
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    public Artifact createArtifact(Workspace workspace, String contentType, String name,
                                   InputStream inputStream) throws RegistryException, IOException, MimeTypeParseException {
        contentType = trimContentType(contentType);
        MimeType ct = new MimeType(contentType);

        Object data = getData(ct, inputStream);

        return createArtifact(workspace, data, name, ct);
    }

    private Object getData(MimeType contentType, InputStream inputStream) 
        throws RegistryException, IOException {
        ContentHandler ch = contentService.getContentHandler(contentType);

        if (ch == null) {
            throw new RegistryException(new Message("UNSUPPORTED_CONTENT_TYPE", LOGGER, contentType));
        }

        return ch.read(inputStream);
    }

    public ArtifactVersion newVersion(Artifact artifact, Object data) throws RegistryException, IOException {
        // TODO: Locking
        try {
            JcrArtifact jcrArtifact = (JcrArtifact) artifact;
            Node artifactNode = jcrArtifact.getNode();
            
            ContentHandler ch = contentService.getContentHandler(jcrArtifact.getContentType());
            
            // create an initial version
            artifactNode.checkout();
            
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            artifactNode.setProperty(JcrVersion.CREATED, now);
            
            // Store the data
            InputStream s = ch.read(data);
            artifactNode.setProperty(JcrVersion.DATA, s);

            JcrVersion next = new JcrVersion(jcrArtifact, artifactNode);
            next.setData(data);
            jcrArtifact.getVersions().add(next);
            ch.addMetadata(next);
            
            session.save();
            
            // Commit the node
            Version checkin = artifactNode.checkin();
            Node versionNode = checkin.getNodes().nextNode();

            // UGLY, will explain/clean up later - need to understand versioning better
            next.setNode(versionNode);
            
            return next;
        } catch (VersionException e) {
            throw new RegistryException(e);
        } catch (UnsupportedRepositoryOperationException e) {
            throw new RegistryException(e);
        } catch (InvalidItemStateException e) {
            throw new RegistryException(e);
        } catch (LockException e) {
            throw new RegistryException(e);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }

    public ArtifactVersion newVersion(Artifact artifact, 
                                      InputStream inputStream) throws RegistryException, IOException {
        // TODO: assert artifact is of the same type as the previous revision
        
        Object data = getData(artifact.getContentType(), inputStream);

        return newVersion(artifact, data);
    }

    public void delete(Artifact artifact) {
        throw new UnsupportedOperationException();
    }
    
    public Set<Index> getIndices(QName documentType) throws RegistryException {
        try {
            QueryManager qm = getQueryManager();
            
            Query query = qm.createQuery("//indices/*/documentType[@value='" + documentType.toString() + "']", 
                                         Query.XPATH);
            
            QueryResult result = query.execute();
            
            Set<Index> indices = new HashSet<Index>();
            for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                Node node = nodes.nextNode();
                
                indices.add(createIndexFromNode(node.getParent()));
            }
            return indices;
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }

    private Index createIndexFromNode(Node node) throws RegistryException, RepositoryException {
        JcrIndex idx = new JcrIndex();
        
        idx.setId(JcrUtil.getStringOrNull(node, JcrIndex.ID));
        idx.setExpression(JcrUtil.getStringOrNull(node, JcrIndex.EXPRESSION));
        idx.setLanguage(Language.valueOf(JcrUtil.getStringOrNull(node, JcrIndex.LANGUAGE)));
        idx.setName(JcrUtil.getStringOrNull(node, JcrIndex.NAME));
        
        String qt = JcrUtil.getStringOrNull(node, JcrIndex.QUERY_TYPE);
        try {
            idx.setQueryType(getClass().getClassLoader().loadClass(qt));
        } catch (ClassNotFoundException e) {
            // not gonna happen
            throw new RegistryException(e);
        }
        
        HashSet<QName> docTypes = new HashSet<QName>();
        for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
            Node child = nodes.nextNode();
            
            if (child.getName().equals(JcrIndex.DOCUMENT_TYPE)) {
                String value = JcrUtil.getStringOrNull(child, JcrIndex.DOCUMENT_TYPE_VALUE);
                
                docTypes.add(QNameUtil.fromString(value));
            }
        }
        idx.setDocumentTypes(docTypes);
        
        return idx;
    }

    private QueryManager getQueryManager() throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }

    public Index registerIndex(String indexId, 
                               String displayName, 
                               Index.Language language,
                               Class<?> searchType,
                               String expression, 
                               QName... documentTypes) throws RegistryException {
        try {
            Node idxNode = JcrUtil.getOrCreate(indices, indexId);
            
            idxNode.setProperty(JcrIndex.ID, indexId);
            idxNode.setProperty(JcrIndex.EXPRESSION, expression);
            idxNode.setProperty(JcrIndex.NAME, displayName);
            idxNode.setProperty(JcrIndex.QUERY_TYPE, searchType.getName());
            idxNode.setProperty(JcrIndex.LANGUAGE, language.toString());
            
            JcrUtil.removeChildren(idxNode, JcrIndex.DOCUMENT_TYPE);
            
            Set<QName> typeSet = new HashSet<QName>();
            for (QName q : documentTypes) {
                typeSet.add(q);
                Node typeNode = idxNode.addNode(JcrIndex.DOCUMENT_TYPE);
                typeNode.setProperty(JcrIndex.DOCUMENT_TYPE_VALUE, q.toString());
            }
            
            session.save();
            
            JcrIndex idx = new JcrIndex();
            idx.setId(indexId);
            idx.setName(displayName);
            idx.setLanguage(language);
            idx.setQueryType(searchType);
            idx.setExpression(expression);
            idx.setDocumentTypes(typeSet);
            
            return idx;
        } catch (ItemExistsException e) {
            throw new RegistryException(e);
        } catch (PathNotFoundException e) {
            throw new RegistryException(e);
        } catch (VersionException e) {
            throw new RegistryException(e);
        } catch (ConstraintViolationException e) {
            throw new RegistryException(e);
        } catch (LockException e) {
            throw new RegistryException(e);
        } catch (NoSuchNodeTypeException e) {
            throw new RegistryException(e);
        } catch (RepositoryException e) {
            throw new RegistryException(e);
        }
    }

    public Set<Artifact> search(String index, Object input) {
        // TODO Auto-generated method stub
        return null;
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
            ex.bindNode(new QName("document"), (Document) jcrVersion.getData(), null);
            
            XQResultSequence result = ex.executeQuery();
            
            Node versionNode = jcrVersion.node;
            
            
            Node property = JcrUtil.getOrCreate(versionNode, idx.getId());
            JcrUtil.removeChildren(property);
            
            while (result.next()) {
                XQItem item = result.getItem();

                org.w3c.dom.Node node = item.getNode();
                
                String content = DOMUtils.getContent(node);
                Node valueNode = property.addNode(JcrVersion.VALUE);
                valueNode.setProperty(JcrVersion.VALUE, content);
            }
            
        } catch (Exception e) {
            // TODO: better error handling for frontends
            // We should log this and make the logs retrievable
            // We should also prepare the expressions when the expression is created
            // or on startup
            throw new RegistryException(e);
        }
        
    }

    public void initialize() throws Exception {
        session = jcrRepository.login(credentials);
        root = session.getRootNode();
        
        workspaces = JcrUtil.getOrCreate(root, "workspaces");
        indices = JcrUtil.getOrCreate(root, "indices");

        session.save();
        
        NodeIterator nodes = workspaces.getNodes();
        // ignore the system node
        if (nodes.getSize() == 0) {
            Node node = workspaces.addNode(settings.getDefaultWorkspaceName());
            node.addMixin("mix:referenceable");

            JcrWorkspace w = new JcrWorkspace(node);
            w.setName(settings.getDefaultWorkspaceName());

            session.save();
        } 
    }

    public void destroy() {
        if (session != null) {
            session.logout();
        }
    }

    public void setJcrRepository(Repository jcrRepository) {
        this.jcrRepository = jcrRepository;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }


}
