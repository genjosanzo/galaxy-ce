package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public class JcrEntryVersion extends AbstractJcrItem implements EntryVersion {
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

    private JcrEntry parent;
    private User author;
    
    public JcrEntryVersion(JcrEntry parent, Node v) throws RepositoryException  {
        super(v, parent.getManager());
        this.parent = parent;
    }
    
    public String getPath() {
        return getParent().getPath() + "?version=" + getVersionLabel();
    }

    public String getName() {
        return getParent().getName() + " (" + getVersionLabel() + ")";
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
                JcrUtil.setProperty(JcrEntryVersion.LATEST, null, node);
            } else {
                JcrUtil.setProperty(JcrEntryVersion.LATEST, Boolean.TRUE, node);
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
                JcrUtil.setProperty(JcrEntryVersion.INDEX_PROPERTIES_STALE, Boolean.FALSE, node);
            } else {
                JcrUtil.setProperty(JcrEntryVersion.INDEX_PROPERTIES_STALE, Boolean.TRUE, node);
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

    public Entry getParent() {
        return parent;
    }

    public String getVersionLabel() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Calendar getCreated() {
        return getCalendarOrNull(CREATED);
    }
    
    public Calendar getUpdated() {
        return getCalendarOrNull(UPDATED);
    }

    public EntryVersion getPrevious() {
        List<EntryVersion> versions = parent.getVersions();
        
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
    
    public void setAsDefaultVersion() throws RegistryException, PolicyException {
        getManager().setDefaultVersion(this);
    }

    public void setEnabled(boolean enabled) throws RegistryException, PolicyException {
        getManager().setEnabled(this, enabled);
    }

    public void setEnabledInternal(boolean enabled) {
        try {
            JcrUtil.setProperty(JcrEntryVersion.ENABLED, enabled, node);
            
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
}
