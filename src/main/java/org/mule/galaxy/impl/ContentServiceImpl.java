package org.mule.galaxy.impl;


import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;

import org.w3c.dom.Document;

public class ContentServiceImpl implements ContentService {
    private Map<String, ContentHandler> ct2ch = new HashMap<String, ContentHandler>();
    private Map<Class, ContentHandler> cls2ch = new HashMap<Class, ContentHandler>();

    public ContentServiceImpl() {
        super();
        W3CDocumentContentHandler w3c = new W3CDocumentContentHandler();
        ct2ch.put("application/xml", w3c);
        cls2ch.put(Document.class, w3c);
    }
    

    public ContentHandler getContentHandler(Class<?> c) {
        ContentHandler ch = cls2ch.get(c);
        
        if (ch == null) {
            for (Class<?> key : cls2ch.keySet()) {
                if (key.isAssignableFrom(c)) {
                    return cls2ch.get(key);
                }
            }
        }
        
        return ch;
    }

    public String describe(Object o) {
        // TODO Auto-generated method stub
        return null;
    }


    public String desribeDifferences(Object v1, Object v2) {
        // TODO Auto-generated method stub
        return null;
    }


    public String getContentType(QName name) {
        // TODO Auto-generated method stub
        return null;
    }


    public ContentHandler getContentHandler(String contentType) {
        return ct2ch.get(contentType);
    }
    
    public void registerContentHandler(String contentType, ContentHandler ch) {
        ct2ch.put(contentType, ch);
    }
    
    public void registerContentHandler(Class cls, ContentHandler ch) {
        cls2ch.put(cls, ch);
    }
}
