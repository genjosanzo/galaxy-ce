package org.mule.galaxy.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.activation.MimeType;
import javax.wsdl.Definition;

import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.DOMUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ContentServiceTest extends AbstractGalaxyTest {

    ContentService contentService;
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
    }

    public void testCHs() throws Exception {
        contentService = (ContentService) applicationContext.getBean("contentService");
        
        ContentHandler ch = contentService.getContentHandler(new MimeType("application/xml"));
        
        assertNotNull(ch);
        assertTrue(ch instanceof XmlDocumentContentHandler);
        
        XmlDocumentContentHandler docHandler = (XmlDocumentContentHandler) ch;
        
        Document document = DOMUtils.createDocument();
        Element el = document.createElement("foo");
        document.appendChild(el);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        docHandler.write(document, out);
        
        Document doc2 = (Document) docHandler.read(new ByteArrayInputStream(out.toByteArray()), null);
        assertNotNull(doc2);
        
        ch = contentService.getContentHandler(Document.class);
        assertNotNull(ch);
        assertTrue(ch instanceof XmlDocumentContentHandler);
        
        ch = contentService.getContentHandler(new MimeType("application/wsdl+xml"));
        assertNotNull(ch);
        
        ch = contentService.getContentHandler(Definition.class);
        assertNotNull(ch);
        
        
    }
}
