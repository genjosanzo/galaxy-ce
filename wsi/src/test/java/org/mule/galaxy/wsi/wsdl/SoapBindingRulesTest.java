package org.mule.galaxy.wsi.wsdl;

import java.util.List;

import org.mule.galaxy.wsi.AbstractWSITest;
import org.mule.galaxy.wsi.Message;
import org.mule.galaxy.wsi.wsdl.soap.EmptySoapBindingTransportAttributeRule;
import org.mule.galaxy.wsi.wsdl.soap.NoEncodingRule;
import org.mule.galaxy.wsi.wsdl.soap.SoapHttpBindingTransportRule;
import org.mule.galaxy.wsi.wsdl.soap.StyleConsistencyRule;

import org.w3c.dom.Document;

public class SoapBindingRulesTest extends AbstractWSITest {

    public void testEmptyTransportR2701() throws Exception {
        EmptySoapBindingTransportAttributeRule rule = new EmptySoapBindingTransportAttributeRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/wsi/soapbinding/r2701.wsdl"));
        
        assertFailedWithMessage(rule, doc, "Binding: ");
    }

    public void testNonSoapHttpTransportR2702() throws Exception {
        SoapHttpBindingTransportRule rule = new SoapHttpBindingTransportRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/wsi/soapbinding/r2701.wsdl"));
        
        assertFailedWithMessage(rule, doc, "Binding: ");
    }
    
    public void testStyleConsistencyR2705() throws Exception {
        StyleConsistencyRule rule = new StyleConsistencyRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/wsi/soapbinding/r2705.wsdl"));
        
        assertFailedWithMessage(rule, doc, "Operation: sayHi");
    }    
    public void testNoEncodingR2706() throws Exception {
        NoEncodingRule rule = new NoEncodingRule();
        
        Document doc = readXml(getResourceAsStream("/wsdl/wsi/soapbinding/r2706.wsdl"));
        
        ValidationResult result = rule.validate(doc, null);
        
        assertTrue(result.isFailed());
        
        List<AssertionResult> results = result.getAssertionResults();
        
        assertEquals(1, results.size());
       
        AssertionResult aResult = results.get(0);
        assertTrue(aResult.isFailed());
        
        List<Message> messages = aResult.getMessages();
        assertEquals(5, messages.size());
        System.out.println(messages);
        boolean found = false;
        for (Message m : messages) {
            if (m.getText().equals("soap:body on operation: sayHi")) {
                found = true;
            }
        }
        assertTrue(found);
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
        assertTrue("Real message was " + m.getText(), m.getText().startsWith(message));
    }
}
