package org.mule.galaxy.impl.artifact;

import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.util.QNameUtil;

public class ArtifactImpl extends AbstractArtifact implements Artifact {
    public final static String CONTENT_TYPE = "contentType";
    public final static String DOCUMENT_TYPE = "documentType";
    private static final String INDEXED = "indexed";
    
    Object data;
    ArtifactType artifactType;
    final Item item;
    private final Node node;

    public ArtifactImpl(Item item, Node node, ContentService contentService) {
        super(item, contentService);
        this.item = item;
        this.node = node;
    }

    public InputStream getInputStream() {
        try {
            return node.getNode("jcr:content").getProperty("jcr:data").getStream();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public MimeType getContentType() {
        String ct = JcrUtil.getStringOrNull(node, CONTENT_TYPE);
        
        if (ct != null) {
            try {
                return new MimeType(ct);
            } catch (MimeTypeParseException e) {
            }
        }
        
        try {
            return new MimeType("application/octet-stream");
        } catch (MimeTypeParseException e) {
            // this can never occur
            throw new RuntimeException(e);
        }
    }

    public QName getDocumentType() {
        return QNameUtil.fromString(JcrUtil.getStringOrNull(node, DOCUMENT_TYPE));
    }


    public boolean isIndexed() {
        return JcrUtil.getBooleanOrNull(node, INDEXED);
    }

    public void setIndexed(boolean indexed) {
        try {
            JcrUtil.setProperty(INDEXED, indexed, node);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Item getItem() {
        return item;
    }
}
