package org.mule.galaxy.artifact;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;

public interface Artifact {

    public ContentHandler getContentHandler();

    public MimeType getContentType();
    
    /**
     * If this is an XML document, get the root QName.
     * 
     * @return
     */
    public QName getDocumentType();

    /**
     * Get a Java API friendly representation of this document. This may be
     * something like a {@link Document} or a {@link Definition}.
     * 
     * @return
     * @throws IOException
     */
    public Object getData() throws IOException;

    public InputStream getInputStream() throws IOException;

    public ArtifactType getArtifactType();

    /**
     * Whether or not this artifact has been indexed or not.
     * 
     * @return
     */
    public boolean isIndexed();
}
