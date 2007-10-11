package org.mule.galaxy.jcr;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Settings;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.JcrUtil;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.QNameUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JcrRegistry implements Registry {
    private Logger LOGGER = LogUtils.getL7dLogger(JcrRegistry.class);

    private Settings settings;
    private ContentService contentService;

    private Repository jcrRepository;
    private Session session;
    private Node root;
    private Credentials credentials;

    public Workspace getWorkspace(String id) throws ArtifactException {
        // TODO: implement a query
        // TODO: possibility for injenction in the id here?

        try {
//            for (NodeIterator itr = root.getNodes(); itr.hasNext();) {
//                System.out.println("Path: " + itr.nextNode().getPath());
//            }

            Node node = root.getNode(id);

            return new JcrWorkspace(node);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(id);
        } catch (ItemNotFoundException e) {
            throw new NotFoundException(id);
        } catch (RepositoryException e) {
            throw new ArtifactException(e);
        }
    }

    public Collection<Workspace> getWorkspaces() throws ArtifactException {
        try {
            Collection<Workspace> workspaces = new ArrayList<Workspace>();
            for (NodeIterator itr = root.getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();

                if (!n.getName().equals("jcr:system")) {
                    workspaces.add(new JcrWorkspace(n));
                }
            }
            return workspaces;
        } catch (RepositoryException e) {
            throw new ArtifactException(e);
        }
    }

    public Collection<Artifact> getArtifacts(Workspace w) throws ArtifactException {
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
            throw new ArtifactException(e);
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

    public Artifact createArtifact(Workspace workspace, Object data) throws ArtifactException {
        ContentHandler ch = contentService.getContentHandler(data.getClass());
        
        if (ch == null) {
            throw new ArtifactException(new Message("UNKNOWN_TYPE", LOGGER, data.getClass()));
        }
        
        String ct = ch.getContentType(data);
        String name = ch.getName(data);
        
        return createArtifact(workspace, data, name, ct);
    }

    public Artifact createArtifact(Workspace workspace, Object data, String name, String contentType)
        throws ArtifactException {

        try {
            Node workspaceNode = ((JcrWorkspace)workspace).getNode();
            Node artifactNode = workspaceNode.addNode("artifact");
            artifactNode.addMixin("mix:versionable");

            ContentHandler ch = contentService.getContentHandler(contentType);
            
            JcrArtifact artifact = new JcrArtifact(workspace, artifactNode, ch);
            artifact.setContentType(contentType);
            artifact.setName(name);

            if (data instanceof Document) {
                artifact.setDocumentType(QNameUtil.getName(((Document)data).getDocumentElement()));
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
            
            session.save();
            artifactNode.checkin();

            JcrVersion next = (JcrVersion)artifact.getVersions().iterator().next();
            next.setData(data);

            return artifact;
        } catch (Exception e) {
            throw new ArtifactException(e);
        }
    }

    public Artifact createArtifact(Workspace workspace, String contentType, String name,
                                   InputStream inputStream) throws ArtifactException, IOException {
        contentType = trimContentType(contentType);

        Object data = getData(contentType, inputStream);

        return createArtifact(workspace, data, name, contentType);
    }

    private Object getData(String contentType, InputStream inputStream) throws ArtifactException, IOException {
        ContentHandler ch = contentService.getContentHandler(contentType);

        if (ch == null) {
            throw new ArtifactException(new Message("UNSUPPORTED_CONTENT_TYPE", LOGGER, contentType));
        }

        Object data = ch.read(inputStream);
        return data;
    }

    public ArtifactVersion newVersion(Artifact artifact, Object data) throws ArtifactException, IOException {
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
            
            session.save();
            
            Version checkin = artifactNode.checkin();
            Node versionNode = checkin.getNodes().nextNode();
            
            JcrVersion next = new JcrVersion(jcrArtifact, versionNode);
            next.setData(data);
            jcrArtifact.getVersions().add(next);
            
            return next;
        } catch (VersionException e) {
            throw new ArtifactException(e);
        } catch (UnsupportedRepositoryOperationException e) {
            throw new ArtifactException(e);
        } catch (InvalidItemStateException e) {
            throw new ArtifactException(e);
        } catch (LockException e) {
            throw new ArtifactException(e);
        } catch (RepositoryException e) {
            throw new ArtifactException(e);
        }
    }

    public ArtifactVersion newVersion(Artifact artifact, 
                                      InputStream inputStream) throws ArtifactException, IOException {
        // TODO: assert artifact is of the same type as the previous revision
//        contentType = trimContentType(contentType);
//
//        Object data = getData(contentType, inputStream);
//        if (data instanceof Document) {
//            artifact.setDocumentType(QNameUtil.getName(((Document)data).getDocumentElement()));
//        }
//
//        LOGGER.info("Created artifact " + artifact.getId());

        return null;
    }

    public void delete(Artifact artifact) {
        throw new UnsupportedOperationException();
    }

    private String trimContentType(String contentType) {
        int comma = contentType.indexOf(';');
        if (comma != -1) {
            contentType = contentType.substring(0, comma);
        }
        return contentType;
    }


    public void initialize() throws Exception {
        session = jcrRepository.login(credentials);
        root = session.getRootNode();

        try {
            root = root.getNode("workspaces");
        } catch (PathNotFoundException e) {

        }

        if (root == null) {
            root = root.addNode("workspaces");
            root.addMixin("mix:referenceable");

            session.save();
        }

        NodeIterator nodes = root.getNodes();
        // ignore the system node
        if (nodes.getSize() == 1) {
            Node node = root.addNode(settings.getDefaultWorkspaceName());
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
