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
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public class JcrVersion extends JcrEntryVersion implements ArtifactVersion {
    public static final String JCR_DATA = "jcr:data";

    private Object data;

    private final JcrArtifact parent;

    private final Node contentNode;

    public JcrVersion(JcrArtifact parent, 
                      Node v,
                      Node contentNode) throws RepositoryException  {
        super(parent, v);
        this.parent = parent;
        this.contentNode = contentNode;
    }
    
    public JcrVersion(JcrArtifact parent, Node versionNode) throws RepositoryException {
       this(parent, versionNode, versionNode != null ? versionNode.getNode("jcr:content") : null);
    }
    
    public Artifact getParent() {
        return parent;
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

}
