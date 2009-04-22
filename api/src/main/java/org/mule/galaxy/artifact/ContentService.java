package org.mule.galaxy.artifact;

import java.util.Collection;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

public interface ContentService {
    /**
     * Get the content type for a specific XML document QName.
     * @param name
     * @return
     */
    MimeType getContentType(QName name);
    
    ContentHandler getContentHandler(MimeType contentType);

    ContentHandler getContentHandler(QName documentType);

    ContentHandler getContentHandler(Class<?> c);
    
    public void registerContentHandler(ContentHandler ch);

    Collection<ContentHandler> getContentHandlers();
}
