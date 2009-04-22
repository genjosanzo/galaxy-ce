package org.mule.galaxy.artifact;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.mule.galaxy.Item;
import org.w3c.dom.Document;

public interface XmlContentHandler extends ContentHandler {

    Object read(Source source, Item workspace) throws Exception;

    Document getDocument(Object o) throws IOException;
    
    /**
     * Get the name of the root XML element
     * @param o
     * @return
     */
    QName getDocumentType(Object o);
    
    List<QName> getSupportedDocumentTypes();
    
}
