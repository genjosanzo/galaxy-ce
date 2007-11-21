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
import org.mule.galaxy.util.JcrUtil;

public class ArtifactTypeDao extends AbstractDao<ArtifactType> {

    private static final String DESCRIPTION = "__description";

    private static final String CONTENT_TYPE = "__contentType";

    private static final String DOCUMENT_TYPES = "__documentTypes";

    protected void doSave(ArtifactType t, Session session) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException, AccessDeniedException,
        ItemExistsException, InvalidItemStateException, NoSuchNodeTypeException {
        
        Node node = null;
        if (t.getId() == null) {
            node = registry.getArtifactTypesNode().addNode("artifactType");
            node.addMixin("mix:referenceable");
            
            t.setId(node.getUUID());
        } else {
            node = registry.getNodeByUUID(t.getId());
        }
        
        node.setProperty(DESCRIPTION, t.getDescription());
        node.setProperty(CONTENT_TYPE, t.getContentType());
        JcrUtil.setProperty(DOCUMENT_TYPES, t.getDocumentTypes(), node);
        session.save();
    }


    @Override
    protected ArtifactType doGet(String id, Session session) throws RepositoryException {
        Node node = registry.getNodeByUUID(id);
        
        return createArtifact(node);
    }

    @SuppressWarnings("unchecked")
    private ArtifactType createArtifact(Node node) throws RepositoryException {
        ArtifactType a = new ArtifactType();
        a.setId(node.getUUID());
        a.setContentType((String) JcrUtil.getProperty(CONTENT_TYPE, node));
        a.setDescription((String) JcrUtil.getProperty(DESCRIPTION, node));
       
        a.setDocumentTypes((Set<QName>) JcrUtil.getProperty(DOCUMENT_TYPES, node));
        
        return a;
    }

    protected List<ArtifactType> doListAll(Session session) throws RepositoryException {
        List<ArtifactType> types = new ArrayList<ArtifactType>();
        
        for (NodeIterator itr = registry.getArtifactTypesNode().getNodes(); itr.hasNext();) {
            types.add(createArtifact(itr.nextNode()));
        }
        
        Collections.sort(types, new Comparator<ArtifactType>() {
            public int compare(ArtifactType o1, ArtifactType o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });
        
        return types;
    }

    
}
