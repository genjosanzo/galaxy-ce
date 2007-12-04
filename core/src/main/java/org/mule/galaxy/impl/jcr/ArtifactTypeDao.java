package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.xml.namespace.QName;

import org.mule.galaxy.ArtifactType;

public class ArtifactTypeDao extends AbstractReflectionDao<ArtifactType> {

    public ArtifactTypeDao() throws Exception {
        super(ArtifactType.class, "artifactTypes", true);
    }

    @Override
    protected ArtifactType build(Node node) throws Exception {
        ArtifactType at = super.build(node);
        at.setId(node.getUUID());
        return at;
    }

    protected List<ArtifactType> doListAll(Session session) throws RepositoryException {
        List<ArtifactType> types = super.doListAll(session);
        
        Collections.sort(types, new Comparator<ArtifactType>() {
            public int compare(ArtifactType o1, ArtifactType o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });
        
        return types;
    }
}
