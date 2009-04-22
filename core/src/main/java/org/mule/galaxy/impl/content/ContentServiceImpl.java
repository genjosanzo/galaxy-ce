package org.mule.galaxy.impl.content;


import java.util.Collection;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.mule.galaxy.artifact.ContentHandler;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.artifact.XmlContentHandler;

public class ContentServiceImpl implements ContentService {
    private Collection<ContentHandler> contentHandlers;
    private ContentHandler defaultContentHandler = new DefaultContentHandler();
    private MimeType xmlMimeType;
    
    public ContentServiceImpl(Collection<ContentHandler> contentHandlers) throws MimeTypeParseException {
        super();
        this.contentHandlers = contentHandlers;
        xmlMimeType = new MimeType("application/xml");
    }
    

    public ContentHandler getContentHandler(Class<?> c) {
        // Exact match?
        for (ContentHandler ch : contentHandlers) {
        
            for (Class<?> type : ch.getSupportedTypes()) {
                if (type.equals(c)) {
                    return ch;
                }
            }
        }
        
        // Closest match
        for (ContentHandler ch : contentHandlers) {
            
            for (Class<?> type : ch.getSupportedTypes()) {
                if (type.isAssignableFrom(c)) {
                    return ch;
                }
            }
        }
        return defaultContentHandler;
    }

    public ContentHandler getContentHandler(QName documentType) {
        for (ContentHandler ch : contentHandlers) {
            
            if (ch instanceof XmlContentHandler) {
                XmlContentHandler xch = (XmlContentHandler) ch;
                
                if (xch.getSupportedDocumentTypes().contains(documentType)) {
                    return xch;
                }
            }
        }
        return getContentHandler(xmlMimeType);
    }


    public MimeType getContentType(QName name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<ContentHandler> getContentHandlers() {
        return contentHandlers;
    }


    public ContentHandler getContentHandler(MimeType contentType) {
        for (ContentHandler ch : contentHandlers) {
            for (MimeType candidate : ch.getSupportedContentTypes()) {
                if (candidate.match(contentType)) {
                    return ch;
                }
            }
        }
        return defaultContentHandler;
    }
    
    public void registerContentHandler(ContentHandler ch) {
        contentHandlers.add(ch);
    }


    public ContentHandler getDefaultContentHandler() {
        return defaultContentHandler;
    }


    public void setDefaultContentHandler(ContentHandler defaultContentHandler) {
        this.defaultContentHandler = defaultContentHandler;
    }
    
}
