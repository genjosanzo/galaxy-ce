package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.ContentHandler;
import org.mule.galaxy.api.PropertyException;
import org.mule.galaxy.api.PropertyInfo;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.lifecycle.Lifecycle;
import org.mule.galaxy.api.lifecycle.Phase;
import org.mule.galaxy.util.QNameUtil;

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
import javax.xml.namespace.QName;

public class JcrArtifact extends AbstractJcrObject implements Artifact
{
    public static final String CONTENT_TYPE = "contentType";
    public static final String CREATED = "created";
    public static final String DESCRIPTION = "description";
    public static final String UPDATED = "updated";
    public static final String NAME = "name";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String LIFECYCLE = "lifecycle";
    public static final String PHASE = "phase";
    
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

    public void setName(String name) {
        try {
            node.setProperty(NAME, name);
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
                    
                    if ("version".equals(node.getName())) {
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

    public ArtifactVersion getActiveVersion() {
        for (ArtifactVersion v : getVersions()) {
            if (v.isActive()) {
                return v;
            }
        }
        return null;
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
    
    public void setVersions(List<ArtifactVersion> versions2) {
        this.versions = versions2;
    }
    

    @Override
    public Object getProperty(String name) {
        return getActiveVersion().getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException
    {
        getActiveVersion().setProperty(name, value);
    }
    
    @Override
    public Iterator<PropertyInfo> getProperties() {
        return getActiveVersion().getProperties();
    }

    @Override
    public PropertyInfo getPropertyInfo(String name) {
        return getActiveVersion().getPropertyInfo(name);
    }

    @Override
    public void setLocked(String name, boolean locked) {
        getActiveVersion().setLocked(name, locked);
    }


    @Override
    public boolean hasProperty(String name) {
        return  getActiveVersion().hasProperty(name);
    }

    @Override
    public void setVisible(String name, boolean visible) {
        getActiveVersion().setVisible(name, visible);
    }

    public Registry getRegistry() {
        return registry;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

}
