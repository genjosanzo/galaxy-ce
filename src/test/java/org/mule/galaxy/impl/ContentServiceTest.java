package org.mule.galaxy.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.cxf.helpers.DOMUtils;
import org.junit.Test;
import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ContentServiceTest extends AbstractGalaxyTest {

    ContentService contentService;
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
    }

    @Test
    public void testCHs() throws Exception {
        contentService = (ContentService) applicationContext.getBean("contentService");
        
        ContentHandler ch = contentService.getContentHandler("application/xml");
        
        assertNotNull(ch);
        assertTrue(ch instanceof W3CDocumentContentHandler);
        
        W3CDocumentContentHandler docHandler = (W3CDocumentContentHandler) ch;
        
        Document document = DOMUtils.createDocument();
        Element el = document.createElement("foo");
        document.appendChild(el);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        docHandler.write(document, out);
        
        Document doc2 = (Document) docHandler.read(new ByteArrayInputStream(out.toByteArray()));
        assertNotNull(doc2);
        
        ch = contentService.getContentHandler(Document.class);
        assertNotNull(ch);
        assertTrue(ch instanceof W3CDocumentContentHandler);
    }
}
