package org.mule.galaxy.wsi.wsdl;

import org.w3c.dom.Node;

public class ImportUriRule extends AbstractXPathRule {

    public ImportUriRule() throws Exception {
        super("R2803");
        expressions.add(xpath.compile("/wsdl:definitions/wsdl:import/@namespace"));
    }

    @Override
    protected boolean validate(AssertionResult ar, Node n) {
        String ns = n.getNodeValue();
        
        if (!ns.startsWith("public:") && !ns.startsWith("urn:") 
            && ns.indexOf("://") == -1) {
            ar.addMessage("Failed namespace: " + ns);
            return false;
        }
        return true;
    }
    
}
