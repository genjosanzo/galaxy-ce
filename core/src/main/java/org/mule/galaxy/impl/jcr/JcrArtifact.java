package org.mule.galaxy.impl.jcr;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.util.QNameUtil;

public class JcrArtifact extends AbstractJcrObject implements Artifact {
    private static final String CONTENT_TYPE = "contentType";
    private static final String CREATED = "created";
    private static final String DESCRIPTION = "description";
    private static final String UPDATED = "updated";
    private static final String NAME = "name";
    private static final String QNAME = "qname";
    private static final String LIFECYCLE = "lifecycle";
    private static final String PHASE = "phase";
    
    private Set<ArtifactVersion> versions;
    private Workspace workspace;
    private JcrRegistryImpl registry;
    private ContentHandler contentHandler;
    
    public JcrArtifact(Workspace w, Node node, JcrRegistryImpl registry) 
        throws RepositoryException {
        super(node);
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
    
    public Workspace getWorkspace() {
        return workspace;
    }

    public Calendar getCreated() {
        return getDateOrNull(CREATED);
    }

    public Calendar getUpdated() {
        return getDateOrNull(UPDATED);
    }

    public MimeType getContentType() {
        String ct = getStringOrNull(CONTENT_TYPE);
        
        try {
            return new MimeType(ct);
        } catch (MimeTypeParseException e) {
            // we've already previously validated this, so this can't happen
            throw new RuntimeException(e);
        }
    }

    
    public QName getDocumentType() {
        return QNameUtil.fromString(getStringOrNull(QNAME));
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setDocumentType(QName documentType) {
        try {
            node.setProperty(QNAME, documentType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        try {
            node.setProperty(NAME, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Set<ArtifactVersion> getVersions() {
        if (versions == null) {
            versions = new HashSet<ArtifactVersion>();

            try {
                for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                    Node node = itr.nextNode();
                    
                    if ("version".equals(node.getName())) {
                        versions.add(new JcrVersion(this, node));
                    }
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
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

    public ArtifactVersion getLatestVersion() {
        ArtifactVersion latest = null;
        for (ArtifactVersion v : getVersions()) {
            if (latest == null) {
                latest = v;
            } else if (latest.getCreated().before(v.getCreated())) {
                latest = v;
            }
        }
        return latest;
    }
    
    public Phase getPhase() {
        String lifecycle = getStringOrNull(LIFECYCLE);
        if (lifecycle == null) {
            return null;
        }
        
        String phase = getStringOrNull(PHASE);
        if (phase == null) {
            return null;
        }
        
        Lifecycle l = registry.getLifecycleManager().getLifecycle(lifecycle);
        
        return l.getPhase(phase);
    }
    
    public void setPhase(Phase p) {
        try {
            node.setProperty(LIFECYCLE, p.getLifecycle().getName());
            node.setProperty(PHASE, p.getName());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setVersions(Set<ArtifactVersion> versions2) {
        this.versions = versions2;
    }
    

    @Override
    public Object getProperty(String name) {
        return getLatestVersion().getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) {
        getLatestVersion().setProperty(name, value);
    }
    
    public JcrRegistryImpl getRegistry() {
        return registry;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

}
