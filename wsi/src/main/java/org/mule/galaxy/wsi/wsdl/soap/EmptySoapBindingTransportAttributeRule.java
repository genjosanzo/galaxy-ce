package org.mule.galaxy.wsi.wsdl.soap;

import org.mule.galaxy.wsi.wsdl.AbstractXPathRule;
import org.mule.galaxy.wsi.wsdl.AssertionResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EmptySoapBindingTransportAttributeRule extends AbstractXPathRule {

    public EmptySoapBindingTransportAttributeRule() throws Exception {
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
