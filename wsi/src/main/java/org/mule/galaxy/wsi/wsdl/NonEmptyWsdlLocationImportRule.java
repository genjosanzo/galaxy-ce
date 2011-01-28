package org.mule.galaxy.wsi.wsdl;

import org.w3c.dom.Node;

public class NonEmptyWsdlLocationImportRule extends AbstractXPathRule {

    public NonEmptyWsdlLocationImportRule() throws Exception {
        super("R2007");
        expressions.add(xpath.compile("/wsdl:definitions/wsdl:import/@location"));
    }

    @Override
    protected boolean validate(AssertionResult ar, Node n) {
        String ns = n.getNodeValue();
        
        if (ns == null && "".equals(ns)) {
            ar.addMessage("Failed namespace: " + ns);
            return false;
        }
        
        return true;
    }

}
