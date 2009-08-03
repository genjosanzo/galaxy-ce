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

public class StyleConsistencyRule extends AbstractXPathRule {

    private XPathExpression soapBindingExpr;
    private XPathExpression soapOperationExpr;

    public StyleConsistencyRule() throws Exception {
        super("R2705");
        expressions.add(xpath.compile("//wsdl:binding"));
        
        soapBindingExpr = xpath.compile("//soap:binding/@style");
        soapOperationExpr = xpath.compile("//soap:operation");
    }

    protected boolean validate(AssertionResult ar, Node n) throws XPathExpressionException {
        String style = soapBindingExpr.evaluate(n);
        boolean validated = true;
        
        if (style != null && !"".equals(style)) {
            NodeList styles = (NodeList) soapOperationExpr.evaluate(n, XPathConstants.NODESET);
            
            for (int i = 0; i < styles.getLength(); i++) {
                Node opNode = styles.item(i);
                String opStyle = getAttribute(opNode, "style");
                
                if (!style.equals(opStyle)) {
                    ar.addMessage("Operation: " + getAttribute(opNode.getParentNode(), "name"));
                    validated = false;
                }
            }
        }
        
        
        return validated;
    }


}
