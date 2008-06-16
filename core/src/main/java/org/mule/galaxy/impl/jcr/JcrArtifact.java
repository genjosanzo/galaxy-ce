package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.QNameUtil;
import org.springmodules.jcr.JcrCallback;

public class JcrArtifact extends AbstractJcrItem implements Artifact {
    public static final String CONTENT_TYPE = "contentType";
    public static final String CREATED = "created";
    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";
    public static final String DOCUMENT_TYPE = "documentType";
    
    private List<ArtifactVersion> versions;
    private Workspace workspace;
    private JcrRegistryImpl registry;
    private ContentHandler contentHandler;
    
    public JcrArtifact(Workspace w, Node node, JcrRegistryImpl registry) 
        throws RepositoryException {
        super(node, registry);
        this.workspace = w;
        this.registry = registry;
    }

    public String getId() {
        try {
            return node.getUUID();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getPath() {
        StringBuilder sb = getBasePath();
        
        sb.append(getName());
        return sb.toString();
    }
    
    private StringBuilder getBasePath() {
        StringBuilder sb = new StringBuilder();
        
        Workspace w = workspace;
        while (w != null) {
            sb.insert(0, '/');
            sb.insert(0, w.getName());
            w = w.getParent();
        }
        sb.insert(0, '/');
        return sb;
    }
    
    public Workspace getParent() {
        return workspace;
    }

    public Calendar getCreated() {
        return getCalendarOrNull(CREATED);
    }

    public MimeType getContentType() {
        String ct = getStringOrNull(CONTENT_TYPE);
        
        if (ct == null) {
            return null;
        }
        
        try {
            return new MimeType(ct);
        } catch (MimeTypeParseException e) {
            // we've already previously validated this, so this can't happen
            throw new RuntimeException(e);
        }
    }

    
    public QName getDocumentType() {
        return QNameUtil.fromString(getStringOrNull(DOCUMENT_TYPE));
    }

    public String getName() {
        return getStringOrNull(NAME);
    }
    
    public String getDescription() {
        return getStringOrNull(DESCRIPTION);
    }
    
    public void setContentType(MimeType contentType) {
        try {
            node.setProperty(CONTENT_TYPE, contentType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setDescription(String desc) {
        try {
            node.setProperty(DESCRIPTION, desc);
            update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setDocumentType(QName documentType) {
        try {
            node.setProperty(DOCUMENT_TYPE, documentType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(final String name) {
        try {
            
            if (!node.getName().equals(name)) {
                registry.execute(new JcrCallback() {
    
                    public Object doInJcr(Session session) throws IOException, RepositoryException {
                        String dest = node.getParent().getPath() + "/" + name;
                        session.move(node.getPath(), dest);
                        return null;
                    }
                    
                });
            }
            node.setProperty(NAME, name);
            update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<ArtifactVersion> getVersions() {
        if (versions == null) {
            versions = new ArrayList<ArtifactVersion>();
            
            try {
                for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                    Node node = itr.nextNode();
                    
                    if (node.getPrimaryNodeType().getName().equals(JcrRegistryImpl.ARTIFACT_VERSION_NODE_TYPE)) {
                        versions.add(new JcrVersion(this, node));
                    }
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
            
            Collections.sort(versions, new Comparator<ArtifactVersion>() {

                public int compare(ArtifactVersion o1, ArtifactVersion o2) {
                    return - o1.getCreated().getTime().compareTo(o2.getCreated().getTime());
                }
                
            });
        }
            
        return versions;
    }

    public ArtifactVersion getVersion(String versionName) {
        for (ArtifactVersion v : getVersions()) {
            if (v.getVersionLabel().equals(versionName)) {
                return v;
            }
        }
        return null;
    }

    public Node getNode() {
        return node;
    }

    public ArtifactVersion getDefaultVersion() {
        for (ArtifactVersion v : getVersions()) {
            if (v.isDefault()) {
                return v;
            }
        }
        // return the latest artifact if there is no default
        return getVersions().get(0);
    }
    
    public void setVersions(List<ArtifactVersion> versions2) {
        this.versions = versions2;
    }

    @Override
    public Object getProperty(String name) {
        return getDefaultVersion().getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        getDefaultVersion().setProperty(name, value);
    }
    
    @Override
    public Iterator<PropertyInfo> getProperties() {
        return getDefaultVersion().getProperties();
    }

    @Override
    public PropertyInfo getPropertyInfo(String name) {
        return getDefaultVersion().getPropertyInfo(name);
    }

    @Override
    public void setLocked(String name, boolean locked) {
        update();
        getDefaultVersion().setLocked(name, locked);
    }


    @Override
    public boolean hasProperty(String name) {
        update();
        return getDefaultVersion().hasProperty(name);
    }

    @Override
    public void setVisible(String name, boolean visible) {
        update();
        getDefaultVersion().setVisible(name, visible);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public JcrRegistryImpl getRegistry() {
        return registry;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

}
