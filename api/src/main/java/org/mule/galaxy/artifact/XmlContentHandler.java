package org.mule.galaxy.artifact;

import java.util.List;

import javax.xml.namespace.QName;

public interface XmlContentHandler extends ContentHandler {

    /**
     * Get the name of the root XML element
     * @param o
     * @return
     */
    QName getDocumentType(Object o);
    
    List<QName> getSupportedDocumentTypes();
    
}
