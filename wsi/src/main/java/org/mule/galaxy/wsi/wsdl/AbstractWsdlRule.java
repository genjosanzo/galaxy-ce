package org.mule.galaxy.wsi.wsdl;

import javax.wsdl.Definition;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class AbstractWsdlRule implements WsdlRule {
    
    public static final String SOAP_HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http";
    
    protected String name;

    public AbstractWsdlRule(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String getAttribute(Node element, String attName) {
        NamedNodeMap attrs = element.getAttributes();
        if (attrs == null) {
            return null;
        }
        Node attN = attrs.getNamedItem(attName);
        if (attN == null) {
            return null;
        }
        return attN.getNodeValue();
    }
    
}
