package org.mule.galaxy;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

public interface XmlContentHandler extends ContentHandler {

    Object read(Source source) throws Exception;

    /**
     * Get the name of the root XML element
     * @param o
     * @return
     */
    QName getDocumentType(Object o);
    
}
