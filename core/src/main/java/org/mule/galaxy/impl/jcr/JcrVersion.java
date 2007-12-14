package org.mule.galaxy.impl.jcr;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;

public class JcrVersion extends AbstractJcrObject implements ArtifactVersion {
    public static final String CREATED = "created";
    public static final String DATA = "data";
    public static final String LABEL = "label";
    public static final String LATEST = "latest";
    public static final String AUTHOR_ID = "authorId";
    public static final String DEPENDENCIES = "dependencies";
    public static final String USER_SPECIFIED = "userSpecified";
    
    private JcrArtifact parent;
    private Object data;
    private User author;
    private boolean latest;
    
    public JcrVersion(JcrArtifact parent, 
                      Node v) throws RepositoryException  {
        super(v);
        this.parent = parent;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public Object getData() {
        return data;
    }

    public Artifact getParent() {
        return parent;
    }

    public String getVersionLabel() {
        return getStringOrNull(LABEL);
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setVersionLabel(String vname) {
        setNodeProperty(LABEL, vname);
    }

    public Calendar getCreated() {
        return getDateOrNull(CREATED);
    }

    public InputStream getStream() {
        try {
            Value v = getValueOrNull(DATA);

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
        // TODO Auto-generated method stub
        return null;
    }

    public User getAuthor() {
        if (author == null) {
            String authId = getStringOrNull(AUTHOR_ID);
            
            if (authId != null) {
                author = parent.getRegistry().getUserManager().get(authId);
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
                Node dep = depsNode.addNode(a.getId());
                dep.setProperty(USER_SPECIFIED, userSpecified);
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
                } catch (NotFoundException e) {
                    dep.remove();
                }
            }
            return deps;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        
    }

}
