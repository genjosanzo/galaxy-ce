package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.util.DateUtil;

public class JcrVersion extends AbstractJcrItem implements ArtifactVersion {
    public static final String CREATED = "created";
    public static final String JCR_DATA = "jcr:data";
    public static final String LATEST = "latest";
    public static final String DEFAULT = "default";
    public static final String ENABLED = "enabled";
    public static final String AUTHOR_ID = "authorId";
    public static final String DEPENDENCIES = "dependencies";
    public static final String USER_SPECIFIED = "userSpecified";
    public static final String INDEX_PROPERTIES_STALE = "indexedPropertiesStale";

    public static final String LIFECYCLE = "lifecycle";
    public static final String PHASE = "phase";
    
    private JcrArtifact parent;
    private Object data;
    private User author;
    private Node contentNode;
    
    public JcrVersion(JcrArtifact parent, 
                      Node v,
                      Node contentNode) throws RepositoryException  {
        super(v, parent.getRegistry());
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
        
        Phase p = parent.getRegistry().getLifecycleManager().getPhaseById(phase);
        
        return p;
    }
    
    public boolean isLatest() {
        return JcrUtil.getBooleanOrNull(node, LATEST);
    }
    
    public boolean isEnabled() {
        return JcrUtil.getBooleanOrNull(node, ENABLED);
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

    public void setEnabled(boolean enabled) {
        try {
            JcrUtil.setProperty(JcrVersion.ENABLED, enabled, node);
            
            update();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean isIndexedPropertiesStale() {
        return JcrUtil.getBooleanOrNull(node, INDEX_PROPERTIES_STALE);
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
        return JcrUtil.getBooleanOrNull(node, DEFAULT);
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
                    author = parent.getRegistry().getUserManager().get(authId);
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
    
    public void addDependencies(Set<Artifact> dependencies, boolean userSpecified) {
        try {
            Node depsNode = JcrUtil.getOrCreate(node, DEPENDENCIES);
            
            for (Artifact a : dependencies) {
                addDependency(userSpecified, depsNode, a);
            }

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void addDependency(boolean userSpecified, Node depsNode, Artifact a) throws RepositoryException {
        Node dep = depsNode.addNode(a.getId());
        dep.addMixin("mix:referenceable");
        dep.setProperty(USER_SPECIFIED, userSpecified);
    }
    
    public void addDependencies(Artifact[] dependencies, boolean userSpecified) {
        try {
            Node depsNode = JcrUtil.getOrCreate(node, DEPENDENCIES);
            
            for (Artifact a : dependencies) {
                addDependency(userSpecified, depsNode, a);
            }

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Set<Dependency> getDependencies() {
        try {
            Node depsNode = JcrUtil.getOrCreate(node, DEPENDENCIES);
            Set<Dependency> deps = new HashSet<Dependency>();
            for (NodeIterator nodes = depsNode.getNodes(); nodes.hasNext();) {
                Node dep = nodes.nextNode();
                final boolean user = JcrUtil.getBooleanOrNull(dep, USER_SPECIFIED);
                try {
                    final Artifact a = parent.getRegistry().getArtifact(dep.getName());
                    deps.add(new Dependency() {

                        public Artifact getArtifact() {
                            return a;
                        }

                        public boolean isUserSpecified() {
                            return user;
                        }
                        
                    });
                } catch (AccessException e) {
                    // don't list dependencies which the user shouldn't see
                } catch (NotFoundException e) {
                    dep.remove();
                } catch (RegistryException e) {
                    throw new RuntimeException(e);
                }
            }
            return deps;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

}
