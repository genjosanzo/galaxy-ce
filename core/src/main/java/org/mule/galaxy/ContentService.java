package org.mule.galaxy;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

public interface ContentService {
    /**
     * Get the content type for a specific XML document QName.
     * @param name
     * @return
     */
    MimeType getContentType(QName name);
    
    ContentHandler getContentHandler(MimeType contentType);
    
    ContentHandler getContentHandler(Class<?> c);
    
    public void registerContentHandler(ContentHandler ch);
}
