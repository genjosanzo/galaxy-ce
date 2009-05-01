package org.mule.galaxy.atom.client;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.mule.galaxy.impl.artifact.AbstractArtifact;

public class AtomArtifact extends AbstractArtifact {

    private final Element element;
    private final AtomWorkspaceManager manager;

    public AtomArtifact(AtomItem item, 
                        Element element, 
                        AtomWorkspaceManager workspaceManager) {
        super(item, workspaceManager.getContentService());
        this.element = element;
        this.manager = workspaceManager;
        
    }
    
    public MimeType getContentType() {
        try {
            String val = element.getAttributeValue("mediaType");
            if (val == null) val = "application/octet-stream";
            return new MimeType(val);
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e);
        }
    }

    public QName getDocumentType() {
        String type = element.getAttributeValue("documentType");
        
        if (type != null) {
            return QName.valueOf(type);
        }
        
        return null;
    }

    public InputStream getInputStream() throws IOException {
        IRI src = ((AtomItem)item).getAtomEntry().getContentSrc();
        if (src == null) {
            throw new RuntimeException("Artifact source cannot be null!");
        }
        
        return manager.getStream(src);
    }

    public boolean isIndexed() {
        return true;
    }
}
