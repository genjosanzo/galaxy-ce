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
import org.mule.galaxy.util.JcrUtil;

public class JcrVersion extends AbstractJcrObject implements ArtifactVersion {
    public static final String CREATED = "created";
    public static final String DATA = "data";
    public static final String LABEL = "label";
    public static final String LATEST = "latest";

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

    public String getVersionLabel() {
        return getStringOrNull(LABEL);
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setVersionLabel(String vname) {
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
