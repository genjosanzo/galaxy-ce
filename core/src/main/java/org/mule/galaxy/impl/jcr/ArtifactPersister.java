package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Registry;
import org.mule.galaxy.impl.jcr.onm.FieldDescriptor;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;

public class ArtifactPersister implements FieldPersister {
    private Registry registry;

    public ArtifactPersister(Registry registry) {
        super();
        this.registry = registry;
    }

    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        String val = JcrUtil.getStringOrNull(n, fd.getName());
        if (val == null) return null;
        
        return registry.getArtifact(val);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        if (o == null) {
            n.setProperty(fd.getName(), (String) null);
        } else {
            n.setProperty(fd.getName(), ((Artifact) o).getId());
        }
    }

}
