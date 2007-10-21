package org.mule.galaxy.jcr;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;

public class JcrVersion extends AbstractJcrObject implements ArtifactVersion {
    public static final String CREATED = "created";
    public static final String DATA = "data";
    public static final String LABEL = "label";
    private JcrArtifact parent;
    private Object data;
    
    public JcrVersion(JcrArtifact parent, Node v) {
        super(v);
        this.parent = parent;
    }

    public Object getData() {
        return data;
    }

    public Artifact getParent() {
        return parent;
    }

    public String getLabel() {
        return getStringOrNull(LABEL);
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setVersion(String vname) {
        setProperty(LABEL, vname);
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
}
