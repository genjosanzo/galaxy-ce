package org.mule.galaxy.impl.artifact;

import java.io.IOException;

import javax.wsdl.Definition;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ContentHandler;
import org.mule.galaxy.artifact.ContentService;
import org.w3c.dom.Document;

public abstract class AbstractArtifact implements Artifact {

    protected ContentHandler contentHandler;
    protected ContentService contentService;
    protected Object data;
    protected final Item item;
    
    public AbstractArtifact(Item item, ContentService contentService) {
        super();
        this.item = item;
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

    /**
     * Get a Java API friendly representation of this document. This may be
     * something like a {@link Document} or a {@link Definition}.
     * 
     * @return
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() throws IOException {
        if (data == null) {
             data = getContentHandler().read(getInputStream(), item.getParent());
        }
        return (T) data;
    }

    public ArtifactType getArtifactType() {
        return null;
    }

}