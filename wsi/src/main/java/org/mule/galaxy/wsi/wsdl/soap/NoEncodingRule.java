package org.mule.galaxy.wsi.wsdl.soap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.mule.galaxy.wsi.wsdl.AbstractXPathRule;
import org.mule.galaxy.wsi.wsdl.AssertionResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * R2706: A wsdl:binding in a DESCRIPTION MUST use the value of "literal" for the 
 * use attribute in all soapbind:body, soapbind:fault, soapbind:header and 
 * soapbind:headerfault elements.
 * 
 */
public class NoEncodingRule extends AbstractXPathRule {

    public NoEncodingRule() throws Exception {
        super("R2706");
        
        expressions.add(xpath.compile("//soap:body"));
        expressions.add(xpath.compile("//soap:header"));
        expressions.add(xpath.compile("//soap:fault"));
        expressions.add(xpath.compile("//soap:headerfault"));
    }

    protected boolean validate(AssertionResult ar, Node n) throws XPathExpressionException {
        String use = getAttribute(n, "use");
        
        if ("encoded".equals(use)) {
            String msg = n.getNodeName() + " on operation: " 
                + getAttribute(n.getParentNode().getParentNode(), "name");
            ar.addMessage(msg);
            
            return false;
        }
        
        return true;
    }


}
