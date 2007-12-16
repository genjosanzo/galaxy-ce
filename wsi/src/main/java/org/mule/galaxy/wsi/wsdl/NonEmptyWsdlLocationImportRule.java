package org.mule.galaxy.wsi.wsdl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NonEmptyWsdlLocationImportRule extends AbstractXPathRule {

    public NonEmptyWsdlLocationImportRule() throws Exception {
        super("2007");
        
        expressions.add(xpath.compile("/wsdl:definitions/wsdl:import/@location"));
    }

    @Override
    protected void validate(NodeList nodeset, Document document, ValidationResult result) {
        boolean failed = false;
        
        AssertionResult ar = new AssertionResult(getName(), true);
        
        for (int i = 0; i < nodeset.getLength(); i++) {
            Node n = nodeset.item(i);
            
            String ns = n.getNodeValue();
            
            if (ns == null && "".equals(ns)) {
                ar.addMessage("Failed namespace: " + ns);
                failed = true;
            }
        }
        
        if (failed) {
            result.addAssertionResult(ar);
        }
    }

}
