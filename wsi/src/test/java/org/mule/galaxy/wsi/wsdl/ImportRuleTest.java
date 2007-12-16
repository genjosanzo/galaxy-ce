package org.mule.galaxy.wsi.wsdl;

import java.util.List;

import org.mule.galaxy.wsi.AbstractWSITest;
import org.mule.galaxy.wsi.Message;

import org.w3c.dom.Document;

public class ImportRuleTest extends AbstractWSITest {

    public void testRelativeNamespaceR2803() throws Exception {
        ImportUriRule rule = new ImportUriRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/wsi/imports/imports.wsdl"));
        
        assertFailedWithMessage(rule, doc, "Failed namespace: ");
    }

    public void testEmptyLocationR2007() throws Exception {
        ImportUriRule rule = new ImportUriRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/wsi/imports/imports.wsdl"));
        
        assertFailedWithMessage(rule, doc, "Failed namespace: ");
    }

    private void assertFailedWithMessage(WsdlRule rule, Document doc, String message) {
        ValidationResult result = rule.validate(doc, null);
        
        assertTrue(result.isFailed());
        
        List<AssertionResult> results = result.getAssertionResults();
        
        assertEquals(1, results.size());
       
        AssertionResult aResult = results.get(0);
        assertTrue(aResult.isFailed());
        
        List<Message> messages = aResult.getMessages();
        
        Message m = messages.get(0); 
        assertTrue(m.getText().startsWith(message));
    }
}
