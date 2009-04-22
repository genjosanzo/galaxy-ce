package org.mule.galaxy.impl.artifact;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ContentHandler;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.util.QNameUtil;
import org.w3c.dom.Document;

public class ArtifactImpl implements Artifact {
    public final static String CONTENT_TYPE = "contentType";
    public final static String DOCUMENT_TYPE = "documentType";
    private static final String INDEXED = "indexed";
    
    private Object data;
    private ContentHandler contentHandler;
    private ArtifactType artifactType;
    private final Item item;
    private final ContentService contentService;
    private final Node node;

    public ArtifactImpl(Item item, Node node, ContentService contentService) {
        super();
        this.item = item;
        this.node = node;
        this.contentService = contentService;
    }

    public ContentHandler getContentHandler() {
        if (contentHandler == null) {
            if (getDocumentType() != null) {
                contentHandler = contentService.getContentHandler(getDocumentType());
            } else {
                contentHandler = contentService.getContentHandler(getContentType());
            }
        }
        return contentHandler;
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


    /**
     * Get a Java API friendly representation of this document. This may be
     * something like a {@link Document} or a {@link Definition}.
     * 
     * @return
     * @throws IOException 
     */
    public Object getData() throws IOException {
        if (data == null) {
             data = getContentHandler().read(getInputStream(), item.getParent());
        }
        return data;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
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
