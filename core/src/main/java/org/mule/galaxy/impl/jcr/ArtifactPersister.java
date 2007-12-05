package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Registry;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;

public class ArtifactPersister implements FieldPersister {
    private Registry registry;
    
    public ArtifactPersister(Registry registry) {
        super();
        this.registry = registry;
    }

    public Object build(Node n, String property) throws Exception {
        String val = JcrUtil.getStringOrNull(n, property);
        if (val == null) return null;
        
        return registry.getArtifact(val);
    }

    public void persist(Object o, Node n, String property) throws Exception {
        if (o == null) {
            n.setProperty(property, (String) null);
        } else {
            n.setProperty(property, ((Artifact) o).getId());
        }
    }

}
