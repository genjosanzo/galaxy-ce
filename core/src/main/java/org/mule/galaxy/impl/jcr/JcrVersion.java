package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.LinkType;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public class JcrVersion extends AbstractJcrItem implements ArtifactVersion {
    public static final String CREATED = "created";
    public static final String JCR_DATA = "jcr:data";
    public static final String LATEST = "latest";
    public static final String DEFAULT = "default";
    public static final String ENABLED = "enabled";
    public static final String AUTHOR_ID = "authorId";
    public static final String INDEX_PROPERTIES_STALE = "indexedPropertiesStale";

    public static final String LIFECYCLE = "lifecycle";
    public static final String PHASE = "phase";
    public static final String VERSION = "version";

    
    private JcrArtifact parent;
    private Object data;
    private User author;
    private Node contentNode;
    
    public JcrVersion(Node v,
                      JcrWorkspaceManager manager) throws RepositoryException  {
        super(v, manager);
    }
    
    public JcrVersion(JcrArtifact parent, 
                      Node v,
                      Node contentNode) throws RepositoryException  {
        super(v, parent != null ? parent.getManager() : null);
        this.parent = parent;
        this.contentNode = contentNode;
    }
    
    public JcrVersion(JcrArtifact parent, Node versionNode) throws RepositoryException {
       this(parent, versionNode, versionNode.getNode("jcr:content"));
    }

    public String getId() {
        try {
            return node.getUUID();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getPath() {
	return getParent().getPath() + "?version=" + getVersionLabel();
    }

    public void setPhase(Phase p) {
        try {
            node.setProperty(LIFECYCLE, p.getLifecycle().getId());
            node.setProperty(PHASE, p.getId());
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Phase getPhase() {
        String phase = getStringOrNull(PHASE);
        if (phase == null) {
            return null;
        }
        
        Phase p = parent.getManager().getLifecycleManager().getPhaseById(phase);
        
        return p;
    }
    
    public boolean isLatest() {
        Boolean b = JcrUtil.getBooleanOrNull(node, LATEST);
        if (b == null) {
            return false;
        }
        return b;
    }
    
    public boolean isEnabled() {
        Boolean b = JcrUtil.getBooleanOrNull(node, ENABLED);
        if (b == null) {
            return false;
        }
        return b;
    }

    public void setLatest(boolean latest) {
        try {
            if (!latest) {
                JcrUtil.setProperty(JcrVersion.LATEST, null, node);
            } else {
                JcrUtil.setProperty(JcrVersion.LATEST, Boolean.TRUE, node);
            }
            
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean isIndexedPropertiesStale() {
        Boolean b = JcrUtil.getBooleanOrNull(node, INDEX_PROPERTIES_STALE);
        
        if (b == null) {
            return false;
        }
        
        return b;
    }

    public void setIndexedPropertiesStale(boolean stale) {
        try {
            if (!stale) {
                JcrUtil.setProperty(JcrVersion.INDEX_PROPERTIES_STALE, Boolean.FALSE, node);
            } else {
                JcrUtil.setProperty(JcrVersion.INDEX_PROPERTIES_STALE, Boolean.TRUE, node);
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isDefault() {
        Boolean b = JcrUtil.getBooleanOrNull(node, DEFAULT);
        if (b == null) {
            return false;
        }
        return b;
    }

    public void setDefault(boolean active) {
        try {
            JcrUtil.setProperty(DEFAULT, active, node);
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getData() {
        if (data == null) {
            try {
                data = parent.getContentHandler().read(getStream(), parent.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return data;
    }

    public Artifact getParent() {
        return parent;
    }

    public String getVersionLabel() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Calendar getCreated() {
        return getCalendarOrNull(CREATED);
    }
    
    public Calendar getUpdated() {
        return getCalendarOrNull(UPDATED);
    }

    public InputStream getStream() {
        try {
            Value v = JcrUtil.getValueOrNull(contentNode, JCR_DATA);

            if (v != null) {
                return v.getStream();
            }

            return null;
        } catch (PathNotFoundException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public ArtifactVersion getPrevious() {
        List<ArtifactVersion> versions = parent.getVersions();
        
        int i = versions.indexOf(this);
        
        if (i > 0) {
            return versions.get(i-1);
        }
        return null;
    }

    public User getAuthor() {
        if (author == null) {
            String authId = getStringOrNull(AUTHOR_ID);
            
            if (authId != null) {
                try {
                    author = parent.getManager().getUserManager().get(authId);
                } catch (NotFoundException e) {
                    // TODO
                }
            }
        }
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
        
        setNodeProperty(AUTHOR_ID, author.getId());
    }

    public void delete() throws RegistryException,
	    AccessException {
	parent.getManager().delete(this);
    }

    public void setAsDefaultVersion()
	    throws RegistryException, ArtifactPolicyException {
	parent.getManager().setDefaultVersion(this);
    }

    public void setEnabled(boolean enabled)
	    throws RegistryException, ArtifactPolicyException {
	parent.getManager().setEnabled(this, enabled);
    }

    public void setEnabledInternal(boolean enabled) {
        try {
            JcrUtil.setProperty(JcrVersion.ENABLED, enabled, node);
            
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
}
