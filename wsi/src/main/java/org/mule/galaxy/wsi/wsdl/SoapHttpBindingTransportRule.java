package org.mule.galaxy.wsi.wsdl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SoapHttpBindingTransportRule extends AbstractXPathRule {

    public SoapHttpBindingTransportRule() throws Exception {
        super("R2701");
        expressions.add(xpath.compile("//soap:binding"));
    }

    protected boolean validate(AssertionResult ar, Node n) {
        String value = getAttribute(n, "transport");
            
        if (value != null && !"".equals(value)) {
            return true;
        }

        ar.addMessage("Binding: " + getBindingName(n));
        
        return false;
    }

    private String getBindingName(Node n) {
        Element parent = (Element) n.getParentNode();
        
        return getAttribute(parent, "name");
    }

}
