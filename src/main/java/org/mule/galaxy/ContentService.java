package org.mule.galaxy;

import javax.xml.namespace.QName;

public interface ContentService {
    String getContentType(QName name);
    ContentHandler getContentHandler(String contentType);
    ContentHandler getContentHandler(Class<?> c);
    public void registerContentHandler(String contentType, ContentHandler ch);
    public void registerContentHandler(Class<?> cls, ContentHandler ch);
}
