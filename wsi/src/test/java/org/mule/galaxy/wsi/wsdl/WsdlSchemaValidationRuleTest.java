package org.mule.galaxy.wsi.wsdl;

import java.util.List;

import org.mule.galaxy.wsi.AbstractWSITest;
import org.mule.galaxy.wsi.Message;

import org.w3c.dom.Document;

public class WsdlSchemaValidationRuleTest extends AbstractWSITest {

    public void testManager() throws Exception {
        WsdlSchemaValidationRule rule = new WsdlSchemaValidationRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/hello-invalid.wsdl"));
        
        ValidationResult result = rule.validate(doc, null);
        
        assertTrue(result.isFailed());
        
        List<AssertionResult> results = result.getAssertionResults();
        
        assertEquals(1, results.size());
       
        AssertionResult aResult = results.get(0);
        assertTrue(aResult.isFailed());
        
        List<Message> messages = aResult.getMessages();
        
        Message m = messages.get(0); 
        assertTrue(m.getText().startsWith("cvc-complex-type.2.4.a: Invalid content"));
//        assertTrue(m.getLineNumber() > 0);
//        assertTrue(m.getColumnNumber() > 0);
//        System.out.println(m.getSystemId());
    }
}
